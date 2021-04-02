package dam95.android.uk.firstbyte.model.tables.components

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import dam95.android.uk.firstbyte.datasource.WRITABLE_DATA
import dam95.android.uk.firstbyte.model.tables.FirstByteSQLConstants
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

const val MAX_COMPARE_SIZE = 5
class ComponentHandler(
    private val dbHandler: SQLiteDatabase
) {
    private val typeQueries: ComponentExtraQueries = ComponentExtraQueries()


    /**
     *
     */
    fun insertHardware(component: Component) {
        Log.i("TEST", component.type)
        val loadColumns: List<String> = when (component.type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> FirstByteSQLConstants.GraphicsCards.COLUMN_LIST
            ComponentsEnum.CPU.toString() -> FirstByteSQLConstants.Processors.COLUMN_LIST
            ComponentsEnum.RAM.toString() -> FirstByteSQLConstants.RamSticks.COLUMN_LIST
            ComponentsEnum.PSU.toString() -> FirstByteSQLConstants.PowerSupplys.COLUMN_LIST
            ComponentsEnum.STORAGE.toString() -> FirstByteSQLConstants.StorageList.COLUMN_LIST
            ComponentsEnum.MOTHERBOARD.toString() -> FirstByteSQLConstants.Motherboards.COLUMN_LIST
            ComponentsEnum.CASES.toString() -> FirstByteSQLConstants.Cases.COLUMN_LIST
            ComponentsEnum.HEATSINK.toString() -> FirstByteSQLConstants.Heatsinks.COLUMN_LIST
            ComponentsEnum.FAN.toString() -> FirstByteSQLConstants.Fans.COLUMN_LIST
            else -> return
        }
        typeQueries.batchValueInsert(component, loadColumns, dbHandler)
    }

    /**
     * Remove hardware from the database
     */
    fun removeHardware(name: String) {
        //Find the hardware's name in Components Table and delete it,
        //which initiates cascade delete to any created Foreign Keys
        val whereClause = "${FirstByteSQLConstants.Components.COMPONENT_NAME} =? AND ${FirstByteSQLConstants.Components.IS_DELETABLE} =$WRITABLE_DATA"
        val result = dbHandler.delete(
            FirstByteSQLConstants.Components.TABLE, whereClause,
            arrayOf(name)
        )
        if (result == -1) {
            Log.e("FAILED REMOVAL", result.toString())
        } else {
            Log.i("REMOVED", name)
        }
    }

    /**
     *
     */
    fun getHardware(hardwareName: String, hardwareType: String): Component {
        Log.i("HARDWARE_SEARCH", "Getting $hardwareName's details...")

        //Full details query
        val queryString =
            "SELECT ${FirstByteSQLConstants.Components.TABLE}.*, $hardwareType.* FROM ${FirstByteSQLConstants.Components.TABLE} INNER JOIN $hardwareType " +
                    "ON ${FirstByteSQLConstants.Components.TABLE}.${FirstByteSQLConstants.Components.COMPONENT_NAME} = $hardwareType.${hardwareType}_name " +
                    "WHERE ${FirstByteSQLConstants.Components.COMPONENT_NAME} =?"

        val loadColumns: List<String> = when (hardwareType.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> FirstByteSQLConstants.GraphicsCards.COLUMN_LIST
            ComponentsEnum.CPU.toString() -> FirstByteSQLConstants.Processors.COLUMN_LIST
            ComponentsEnum.RAM.toString() -> FirstByteSQLConstants.RamSticks.COLUMN_LIST
            ComponentsEnum.PSU.toString() -> FirstByteSQLConstants.PowerSupplys.COLUMN_LIST
            ComponentsEnum.STORAGE.toString() -> FirstByteSQLConstants.StorageList.COLUMN_LIST
            ComponentsEnum.MOTHERBOARD.toString() -> FirstByteSQLConstants.Motherboards.COLUMN_LIST
            ComponentsEnum.CASES.toString() -> FirstByteSQLConstants.Cases.COLUMN_LIST
            ComponentsEnum.HEATSINK.toString() -> FirstByteSQLConstants.Heatsinks.COLUMN_LIST
            ComponentsEnum.FAN.toString() -> FirstByteSQLConstants.Fans.COLUMN_LIST
            else -> emptyList()
        }

        //Implement error check
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(hardwareName))
        val componentResult: Component =
            typeQueries.buildTheComponent(cursor, hardwareType, loadColumns)
        cursor.close()
        return componentResult
    }

    fun hardwareExists(name: String): Int {
        val queryString =
            "SELECT component_name FROM component WHERE component_name =?"
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(name))
        val result: Int = cursor.count
        cursor.close()
        //If 1, then true, otherwise false
        return result
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
            "SELECT ${FirstByteSQLConstants.Components.COMPONENT_NAME}, ${FirstByteSQLConstants.Components.COMPONENT_TYPE}, " +
                    "${FirstByteSQLConstants.Components.COMPONENT_IMAGE}, ${FirstByteSQLConstants.Components.RRP_PRICE} FROM ${FirstByteSQLConstants.Components.TABLE}"


        //If a specific category is being searched, append the specific category onto the MySQL query
        queryString += if (category != "all") {
            " WHERE ${FirstByteSQLConstants.Components.COMPONENT_TYPE} LIKE '$category' " +
                    "AND ${FirstByteSQLConstants.Components.COMPONENT_NAME} LIKE '%$searchQuery%'"
        } else {
            " WHERE ${FirstByteSQLConstants.Components.COMPONENT_NAME} LIKE '%$searchQuery%'"
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
                "SELECT ${FirstByteSQLConstants.Components.COMPONENT_NAME}, ${FirstByteSQLConstants.Components.COMPONENT_TYPE}," +
                        "${FirstByteSQLConstants.Components.COMPONENT_IMAGE}, ${FirstByteSQLConstants.Components.RRP_PRICE} FROM ${FirstByteSQLConstants.Components.TABLE}"

            val cursor: Cursor
            //If a specific category is being searched, append the specific category onto the MySQL query
            if (category != "all") {
                queryString += " WHERE ${FirstByteSQLConstants.Components.COMPONENT_TYPE} =?"
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
    fun retrieveImageURL(componentName: String): String? {
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
     * Create a new ID entry for the compared components table.
     */
    fun createComparedList(typeID: String) {
        val cv = ContentValues()
        cv.put(FirstByteSQLConstants.Compare.ID, typeID)
        dbHandler.insert(FirstByteSQLConstants.Compare.TABLE, null, cv)
    }

    @Throws(SQLiteException::class)
    fun doesComparedListExist(typeID: String): Int {

        return try {
            val cursor = dbHandler.rawQuery("SELECT * FROM ${FirstByteSQLConstants.Compare.TABLE} WHERE ${FirstByteSQLConstants.Compare.ID} =?",
                arrayOf(typeID)
            )
            val result: Int = cursor.count
            cursor.close()
            Log.i("COMPARE_TABLE_EXISTS", cursor.count.toString())
            //If 1, then true, otherwise false
            result
        } catch (exception: Exception){
            //For human readability
            val theTableDoesNotExist = 0
            theTableDoesNotExist
        }
    }

    /**
     *
     */
    fun retrieveComparedList(typeID: String): List<String?> {
        val loadedComponentNames = mutableListOf<String?>()

        return try {
            val cursor = dbHandler.rawQuery(
                "SELECT ${FirstByteSQLConstants.CompareStats.COMPONENT} FROM ${FirstByteSQLConstants.CompareStats.TABLE} WHERE ${FirstByteSQLConstants.CompareStats.ID} =?",
                arrayOf(typeID)
            )
            cursor.moveToFirst()
            for (i in 0 until MAX_COMPARE_SIZE) {
                Log.i("ROW_VALUE", cursor.getString(0))
                loadedComponentNames.add(cursor.getString(0))
                cursor.moveToNext()
            }
            cursor.close()
            //Return the loaded list
            loadedComponentNames
        } catch (exception: Exception) {
            //Return null
            for (i in loadedComponentNames.size until MAX_COMPARE_SIZE) loadedComponentNames.add(null)
            loadedComponentNames
        }
    }

    @Throws(SQLiteException::class)
    fun isComponentInComparedTable(componentName: String): Int {
        return try {
            val cursor = dbHandler.rawQuery("SELECT ${FirstByteSQLConstants.CompareStats.COMPONENT} FROM ${FirstByteSQLConstants.CompareStats.TABLE} WHERE ${FirstByteSQLConstants.CompareStats.COMPONENT} =?",
                arrayOf(componentName)
            )
            val result: Int = cursor.count
            cursor.close()
            Log.i("COMPONENT_IN_TABLE?", cursor.count.toString())
            //If 1, then true, otherwise false
            result
        } catch (exception: Exception){
            //For human readability
            val theComponentIsNotInTheList = 0
            theComponentIsNotInTheList
        }
    }

    /**
     * Insert the compared component table by saving/updating the newly added component into a dynamic slot that the user has chosen.
     *
     * @param typeID
     * @param savedComponent
     */
    fun saveComparedComponent(typeID: String, savedComponent: String) {

        val cv = ContentValues()
        cv.put(FirstByteSQLConstants.CompareStats.ID, typeID)
        cv.put(FirstByteSQLConstants.CompareStats.COMPONENT, savedComponent)
        dbHandler.insert(FirstByteSQLConstants.CompareStats.TABLE, null, cv)
    }

    /**
     * Remove the compared stats row that has the removed component name.
     *
     * @param componentName
     */
    fun deleteComparedComponent(componentName: String) =
        dbHandler.delete(FirstByteSQLConstants.CompareStats.TABLE, "${FirstByteSQLConstants.CompareStats.COMPONENT} =?",
            arrayOf(componentName)
        )
}