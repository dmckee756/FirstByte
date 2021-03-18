package dam95.android.uk.firstbyte.datasource

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.datasource.util.FK_ON
import dam95.android.uk.firstbyte.datasource.util.SQLComponentConstants
import dam95.android.uk.firstbyte.datasource.util.SQLComponentTypeQueries
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val PC_ID_COLUMN = 0
private const val WRITABLE_DATA = 1
private const val MAX_PC_LIST_SIZE = 10
private const val NULL_RES = 0x00000000
private const val INTEGER_RES = 0x00000001
private const val FLOAT_RES = 0x00000002
private const val STRING_RES = 0x00000003

/**
 *
 */
class ComponentDBAccess(context: Context) {

    private var database: ComponentDBHandler = ComponentDBHandler(context)
    private var dbHandler: SQLiteDatabase = database.writableDatabase
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var typeQueries = SQLComponentTypeQueries()

    init {
        dbHandler.execSQL(FK_ON)
    }

    companion object Static {
        private var dbController: ComponentDBAccess? = null

        /**
         *
         */
        fun dbInstance(context: Context): ComponentDBAccess? {
            when (dbController) {
                null -> {
                    dbController = ComponentDBAccess(context)
                }
                else -> dbController
            }
            return dbController
        }
    }


    /**
     *
     */
    fun closeDatabase() {
        dbHandler.close()
        Log.i("DATABASE_CLOSED", "Database closed")
        dbController = null
    }

    /**
     * Ask questions on tuesday about optimising this method, but it will suffice for the mid project demo TODO
     */
    fun insertHardware(component: Component) {
        coroutineScope.launch {
            val loadColumns: List<String> = when (component.type.toUpperCase(Locale.ROOT)) {
                ComponentsEnum.GPU.toString() -> SQLComponentConstants.GraphicsCards.COLUMN_LIST
                ComponentsEnum.CPU.toString() -> SQLComponentConstants.Processors.COLUMN_LIST
                ComponentsEnum.RAM.toString() -> SQLComponentConstants.RamSticks.COLUMN_LIST
                ComponentsEnum.PSU.toString() -> SQLComponentConstants.PowerSupplys.COLUMN_LIST
                ComponentsEnum.STORAGE.toString() -> SQLComponentConstants.StorageList.COLUMN_LIST
                ComponentsEnum.MOTHERBOARD.toString() -> SQLComponentConstants.Motherboards.COLUMN_LIST
                ComponentsEnum.CASES.toString() -> SQLComponentConstants.Cases.COLUMN_LIST
                ComponentsEnum.HEATSINK.toString() -> SQLComponentConstants.Heatsinks.COLUMN_LIST
                ComponentsEnum.FAN.toString() -> SQLComponentConstants.Fans.COLUMN_LIST
                else -> return@launch
            }
            typeQueries.batchValueInsert(component, loadColumns, dbHandler)
        }
    }

    /**
     * Remove hardware from the database
     * TODO(Add that only components with "deletable" = 1 can be deleted) and figure out why all hardware is being set to 0
     */
    fun removeHardware(name: String) {
        coroutineScope.launch {
            //Find the hardware's name in Components Table and delete it,
            //which initiates cascade delete to any created Foreign Keys
            val whereClause = "${SQLComponentConstants.Components.COMPONENT_NAME} =?"
            val result = dbHandler.delete(
                SQLComponentConstants.Components.TABLE, whereClause,
                arrayOf(name)
            )
            if (result == -1) {
                Log.e("FAILED REMOVAL", result.toString())
            } else {
                Log.i("REMOVED", name)
            }
        }
    }

    /**
     *
     */
    fun getHardware(hardwareName: String, hardwareType: String): Component {
        Log.i("HARDWARE_SEARCH", "Getting $hardwareName's details...")

        //Full details query
        val queryString =
            "SELECT component.*, $hardwareType.* FROM component INNER JOIN $hardwareType " +
                    "ON component.component_name = $hardwareType.${hardwareType}_name " +
                    "WHERE component_name =?"

        val loadColumns: List<String> = when (hardwareType.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> SQLComponentConstants.GraphicsCards.COLUMN_LIST
            ComponentsEnum.CPU.toString() -> SQLComponentConstants.Processors.COLUMN_LIST
            ComponentsEnum.RAM.toString() -> SQLComponentConstants.RamSticks.COLUMN_LIST
            ComponentsEnum.PSU.toString() -> SQLComponentConstants.PowerSupplys.COLUMN_LIST
            ComponentsEnum.STORAGE.toString() -> SQLComponentConstants.StorageList.COLUMN_LIST
            ComponentsEnum.MOTHERBOARD.toString() -> SQLComponentConstants.Motherboards.COLUMN_LIST
            ComponentsEnum.CASES.toString() -> SQLComponentConstants.Cases.COLUMN_LIST
            ComponentsEnum.HEATSINK.toString() -> SQLComponentConstants.Heatsinks.COLUMN_LIST
            ComponentsEnum.FAN.toString() -> SQLComponentConstants.Fans.COLUMN_LIST
            else -> emptyList()
        }

        //Implement error check
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(hardwareName))
        val componentResult: Component =
            typeQueries.buildTheComponent(cursor, hardwareType, loadColumns)
        cursor.close()
        return componentResult
    }

    suspend fun hardwareExists(name: String): Int = withContext(Dispatchers.Main) {
        val queryString =
            "SELECT component_name FROM component WHERE component_name =?"
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(name))
        //Implement error check
        val result: Int = cursor.count
        cursor.close()
        Log.i("HARDWARE_EXIST?", cursor.count.toString())
        //If 1, then true, otherwise false
        return@withContext result
    }

    /**
     *
     */
    suspend fun getCategorySearch(
        category: String,
        searchQuery: String
    ): LiveData<List<SearchedHardwareItem>>? = withContext(Dispatchers.Main) {

//Retrieve components search details
        var queryString =
            "SELECT component.component_name, component.component_type, " +
                    "component.image_link, component.rrp_price FROM component"


        //If a specific category is being searched, append the specific category onto the MySQL query
        queryString += if (category != "all") {
            " WHERE component_type LIKE '$category' " +
                    "AND component_name LIKE '%$searchQuery%'"
        } else {
            " WHERE component_name LIKE '%$searchQuery%'"
        }

        val cursor = dbHandler.rawQuery(queryString, null)
        if (cursor.count > 0) {
            val categoryResults: LiveData<List<SearchedHardwareItem>> =
                typeQueries.buildSearchItemList(cursor)
            cursor.close()
            //Implement error check
            return@withContext categoryResults
        } else {
            cursor.close()
            return@withContext null
        }
    }

    /**
     *
     */
    suspend fun getCategory(category: String): LiveData<List<SearchedHardwareItem>>? =
        withContext(Dispatchers.Main) {

            //Retrieve components search details
            var queryString =
                "SELECT component.component_name, component.component_type," +
                        "component.image_link, component.rrp_price FROM component"

            val cursor: Cursor
            //If a specific category is being searched, append the specific category onto the MySQL query
            if (category != "all") {
                queryString += " WHERE component_type =?"
                cursor = dbHandler.rawQuery(queryString, arrayOf(category))
            } else {
                cursor = dbHandler.rawQuery(queryString, null)
            }

            if (cursor.count > 0) {
                val categoryResults: LiveData<List<SearchedHardwareItem>> =
                    typeQueries.buildSearchItemList(cursor)
                cursor.close()
                //Implement error check
                return@withContext categoryResults
            } else {
                cursor.close()
                return@withContext null
            }
        }

    /**
     *
     */
    fun getImageLink(componentName: String): String? {
        //Retrieve component image
        val queryString =
            "SELECT image_link FROM component WHERE component_name =?"
        val cursor = dbHandler.rawQuery(queryString, arrayOf(componentName))
        //If a specific category is being searched, append the specific category onto the MySQL query
        return if (cursor.count > 0) {
            cursor.moveToFirst()
            val result: String = cursor.getString(0)
            cursor.close()
            result
        } else {
            cursor.close()
            null
        }
    }

    /**
     *
     */
    fun savePCPart(name: String, type: String, pcID: Int) {
        coroutineScope.launch {
            when (type.toUpperCase(Locale.ROOT)) {
                ComponentsEnum.RAM.toString() -> typeQueries.updatePCBuildRelationTables(
                    name,
                    type,
                    pcID,
                    dbHandler
                )
                ComponentsEnum.STORAGE.toString() -> typeQueries.updatePCBuildRelationTables(
                    name,
                    type,
                    pcID,
                    dbHandler
                )
                ComponentsEnum.FAN.toString() -> typeQueries.updatePCBuildRelationTables(
                    name,
                    type,
                    pcID,
                    dbHandler
                )
                else -> {
                    val cv = ContentValues()
                    cv.put("${type}_name", name)
                    dbHandler.update("pcbuild", cv, "pc_id =?", arrayOf(pcID.toString()))
                }
            }
        }
    }

    fun removePCPart(type: String, pcID: Int){
        coroutineScope.launch {
            val cv = ContentValues()
            cv.putNull("${type}_name")
            val result = dbHandler.update("pcbuild", cv, null, null)
            if (result == -1) {
                Log.e("FAILED REMOVAL", result.toString())
            } else {
                Log.i("REMOVED_PC", "PC ID: $pcID deleted")
            }
        }
    }

    fun getPersonalPC(pcID: Int): MutableLiveData<PCBuild> {
        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        val queryString =
            "SELECT * FROM pcbuild WHERE pc_id = $pcID"
        val cursor = dbHandler.rawQuery(queryString, null)
        Log.i("GET_PC", pcID.toString())
        cursor.moveToFirst()
        val loadPC = typeQueries.getPCDetails(currentTableColumns, cursor, dbHandler)
        cursor.close()
        return loadPC
    }

    /**
     *
     */
    fun createPersonalPC(personalPC: PCBuild): Int {

        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        //
        val cv = ContentValues()
        var booleanToTinyInt: Int
        val pcDetails = personalPC.getPrimitiveDetails()
        //Input values into the correct component table.
        for (i in currentTableColumns.indices) {
            if (i == PC_ID_COLUMN) continue
            //Load the correct type of variable and put it into the ContentValue Hash map.
            when (pcDetails[i]) {
                is String -> cv.put(currentTableColumns[i], pcDetails[i] as String)
                is Double -> cv.put(currentTableColumns[i], pcDetails[i] as Double)
                is Int -> cv.put(currentTableColumns[i], pcDetails[i] as Int)
                is Boolean -> {
                    //Convert booleans
                    booleanToTinyInt = if (pcDetails[i] as Boolean) 1 else 0
                    cv.put(currentTableColumns[i], booleanToTinyInt)
                }
            }
            Log.i("DETAIL", pcDetails[i].toString())
        }
        //Add all primitive values to table
        val result = dbHandler.insert("pcbuild", null, cv)
        //If there was an error, exit out of this insertion.
        if (result == (-1).toLong()) {
            Log.e("FAILED INSERT", result.toString())
            return -1
        } else {
            val cursor = dbHandler.rawQuery(
                "SELECT pc_id FROM pcbuild WHERE pc_id =(SELECT MAX(pc_id) FROM pcbuild)",
                null
            )

            cursor.moveToFirst()
            val mostRecentID = cursor.getInt(0)
            //Setup for relational table insertion.
            personalPC.ramList?.let {
                typeQueries.insertPcRelationalData(
                    cv, mostRecentID, SQLComponentConstants.RamInPc.COLUMN_LIST,
                    it, dbHandler
                )
            }
            personalPC.storageList?.let {
                typeQueries.insertPcRelationalData(
                    cv, mostRecentID, SQLComponentConstants.StorageInPc.COLUMN_LIST,
                    it, dbHandler
                )
            }
            personalPC.fanList?.let {
                typeQueries.insertPcRelationalData(
                    cv, mostRecentID, SQLComponentConstants.FansInPc.COLUMN_LIST,
                    it, dbHandler
                )
            }
            cursor.close()
            return mostRecentID
        }
    }

    fun deletePC(pcID: Int) {
        coroutineScope.launch {

            val result = dbHandler.delete(
                "pcbuild", "pc_id =?",
                arrayOf(pcID.toString())
            )
            if (result == -1) {
                Log.e("FAILED REMOVAL", result.toString())
            }
        }
    }

    /**
     *
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun getPersonalPCList(): LiveData<List<PCBuild?>> {

        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        val pcDisplayList = mutableListOf<PCBuild?>()
        val liveData = MutableLiveData<List<PCBuild?>>()

        val queryString =
            "SELECT * FROM pcbuild WHERE deletable = $WRITABLE_DATA"
        val cursor = dbHandler.rawQuery(queryString, null)

        cursor.moveToFirst()
        //Load in 10 PCBuild Slots when the user clicks on the pc build list screen.
        for (i in 0 until MAX_PC_LIST_SIZE) {
            //If there are pc builds that aren't recommended builds currently existing in the database,
            //load all of them until, when there is no more pc builds to load, stop.
            //If there is no more pc builds to be loaded and there are still slots left,
            // full the slots in for nulls to allow users to create a new pc build in the future.
            if (cursor.count > i) {
                val mutableLiveData = typeQueries.getPCDetails(currentTableColumns, cursor, dbHandler)
                pcDisplayList.add(mutableLiveData.value)
            } else {
                pcDisplayList.add(null)
            }
        }
        cursor.close()
        liveData.value = pcDisplayList
        return liveData
    }

    /**
     *
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun getComponentListsInPc(
        pcID: Int,
        componentType: String,
        componentNameList: List<String?>?,
        numberOfComponents: Int
    ): List<Pair<Component?, String>> {

        val cursor = dbHandler.rawQuery(
            "SELECT ${componentType}_name FROM ${componentType}_in_pc WHERE pc_id =?",
            arrayOf(pcID.toString())
        )
        val relationalComponentList = mutableListOf<Pair<Component?, String>>()

        for (index in 0..numberOfComponents) {
            try {
                componentNameList?.get(index)?.let { name ->
                    relationalComponentList.add(
                        Pair(
                            getHardware(name, componentType),
                            componentType.capitalize(Locale.ROOT)
                        )
                    )
                } ?: relationalComponentList.add(Pair(null, componentType.capitalize(Locale.ROOT)))
            } catch (exception: Exception) {
                relationalComponentList.add(Pair(null, componentType.capitalize(Locale.ROOT)))
            }
        }
        cursor.close()
        return relationalComponentList
    }
}