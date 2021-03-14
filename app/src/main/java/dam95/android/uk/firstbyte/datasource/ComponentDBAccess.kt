package dam95.android.uk.firstbyte.datasource

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import dam95.android.uk.firstbyte.datasource.util.FK_ON
import dam95.android.uk.firstbyte.datasource.util.SQLComponentConstants
import dam95.android.uk.firstbyte.datasource.util.SQLComponentTypeQueries
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.pcbuilds.PCBuild
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
    suspend fun getHardware(hardwareName: String, hardwareType: String): Component =
        withContext(Dispatchers.Main) {
            Log.i("HARDWARE_SEARCH", "Getting $hardwareName's details...")
            val compTable = SQLComponentConstants.Components.TABLE

            //Full details query
            val queryString =
                "SELECT $compTable.*, $hardwareType.* FROM $compTable INNER JOIN $hardwareType " +
                        "ON $compTable.${SQLComponentConstants.Components.COMPONENT_NAME} = $hardwareType.${hardwareType}_name " +
                        "WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} =?"
            //Implement error check
            val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(hardwareName))
            val componentResult: Component = typeQueries.buildTheComponent(cursor, hardwareType)
            cursor.close()
            return@withContext componentResult
        }

    suspend fun hardwareExists(name: String): Int = withContext(Dispatchers.Main) {
        val queryString =
            "SELECT ${SQLComponentConstants.Components.COMPONENT_NAME} FROM ${SQLComponentConstants.Components.TABLE} WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} =?"
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
            "SELECT ${SQLComponentConstants.Components.TABLE}.component_name, " +
                    "${SQLComponentConstants.Components.TABLE}.component_type, " +
                    "${SQLComponentConstants.Components.TABLE}.image_link, " +
                    "${SQLComponentConstants.Components.TABLE}.rrp_price FROM ${SQLComponentConstants.Components.TABLE}"


        //If a specific category is being searched, append the specific category onto the MySQL query
        queryString += if (category != "all") {
            " WHERE ${SQLComponentConstants.Components.COMPONENT_TYPE} LIKE '$category' " +
                    "AND ${SQLComponentConstants.Components.COMPONENT_NAME} LIKE '%$searchQuery%'"
        } else {
            " WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} LIKE '%$searchQuery%'"
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
                "SELECT ${SQLComponentConstants.Components.TABLE}.component_name," +
                        "${SQLComponentConstants.Components.TABLE}.component_type," +
                        "${SQLComponentConstants.Components.TABLE}.image_link," +
                        "${SQLComponentConstants.Components.TABLE}.rrp_price FROM ${SQLComponentConstants.Components.TABLE}"

            val cursor: Cursor
            //If a specific category is being searched, append the specific category onto the MySQL query
            if (category != "all") {
                queryString += " WHERE ${SQLComponentConstants.Components.COMPONENT_TYPE} =?"
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
            "SELECT ${SQLComponentConstants.Components.COMPONENT_IMAGE} FROM ${SQLComponentConstants.Components.TABLE} WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} =?"
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
    fun savePersonalPC(personalPC: PCBuild): Boolean {

        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        //
        val cv = ContentValues()
        var booleanToTinyInt: Int
        val pcDetails = personalPC.getPrimitiveDetails()
        //Input values into the correct component table.
        for (i in currentTableColumns.indices) {
            if (i == PC_ID_COLUMN) continue
            //Load the correct type of variable and put it into the ContentValue Hash map.
            when (pcDetails[i - 1]) {
                is String -> cv.put(currentTableColumns[i], pcDetails[i - 1] as String)
                is Double -> cv.put(currentTableColumns[i], pcDetails[i - 1] as Double)
                is Int -> cv.put(currentTableColumns[i], pcDetails[i - 1] as Int)
                is Boolean -> {
                    //Convert booleans
                    booleanToTinyInt = if (pcDetails[i - 1] as Boolean) 1 else 0
                    cv.put(currentTableColumns[i], booleanToTinyInt)
                }
            }
            Log.i("DETAIL", pcDetails[i - 1].toString())
        }
        //Add all primitive values to table
        val result = dbHandler.insert(SQLComponentConstants.PcBuild.TABLE, null, cv)
        //If there was an error, exit out of this insertion.
        if (result == (-1).toLong()) {
            Log.e("FAILED INSERT", result.toString())
            return false
        } else {
            val cursor = dbHandler.rawQuery(
                "SELECT pc_id FROM ${SQLComponentConstants.PcBuild.TABLE} WHERE pc_id =(SELECT MAX(pc_id) FROM ${SQLComponentConstants.PcBuild.TABLE})",
                null
            )

            //Setup for relational table insertion.
            personalPC.ramList?.let {
                typeQueries.insertPcRelationalData(
                    cv, cursor.getInt(0), SQLComponentConstants.RamInPc.COLUMN_LIST,
                    it, dbHandler
                )
            }
            personalPC.storageList?.let {
                typeQueries.insertPcRelationalData(
                    cv, cursor.getInt(0), SQLComponentConstants.StorageInPc.COLUMN_LIST,
                    it, dbHandler
                )
            }
            personalPC.fanList?.let {
                typeQueries.insertPcRelationalData(
                    cv, cursor.getInt(0), SQLComponentConstants.FansInPc.COLUMN_LIST,
                    it, dbHandler
                )
            }
            cursor.close()
            return true
        }
    }


    /**
     *
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun getPersonalPCList(): List<PCBuild?> {

        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        val pcDisplayList = mutableListOf<PCBuild?>()

        val queryString =
            "SELECT * FROM ${SQLComponentConstants.PcBuild.TABLE} WHERE ${SQLComponentConstants.PcBuild.PC_IS_DELETABLE} = $WRITABLE_DATA"
        val cursor = dbHandler.rawQuery(queryString, null)

        val loadPC = PCBuild()
        val listDetail = loadPC.getPrimitiveDetails().toMutableList()
        var pcID: Int
        cursor.moveToFirst()
        //Will be reworked later to be less hardcoded, it is currently computationally expensive
        for (i in 0 until MAX_PC_LIST_SIZE) {
            //
            if (cursor.count > i) {
                pcID = cursor.getInt(PC_ID_COLUMN)
                for (j in currentTableColumns.indices) {
                    Log.i("TEST_COL", cursor.getColumnName(j))
                    if (j == PC_ID_COLUMN) continue
                    //
                    when (cursor.getType(j)) {
                        STRING_RES -> listDetail[j - 1] = cursor.getString(j)
                        INTEGER_RES -> listDetail[j - 1] = {
                            if (listDetail[j - 1] is Boolean) {
                                listDetail[j - 1] = cursor.getInt(j) != 0
                            } else {
                                listDetail[j - 1] = cursor.getInt(j)
                            }
                        }
                        FLOAT_RES -> listDetail[j - 1] = cursor.getDouble(j)
                        NULL_RES -> listDetail[j - 1] = null
                    }
                    Log.i("TEST_DETAIL", listDetail[j - 1].toString())
                }
                typeQueries.getPcRelationalData(loadPC, pcID, dbHandler)
                loadPC.setPrimitiveDetails(listDetail)
                pcDisplayList.add(loadPC)
            } else {
                pcDisplayList.add(null)
            }
        }
        cursor.close()
        return pcDisplayList
    }
}