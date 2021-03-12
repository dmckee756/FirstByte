package dam95.android.uk.firstbyte.datasource

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ComponentDBAccess(private var context: Context) {

    private var database: ComponentDBHandler = ComponentDBHandler(context)
    private var dbHandler: SQLiteDatabase = database.writableDatabase
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

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
            val loadDetails = SQLComponentTypeQueries()
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
            loadDetails.batchValueInsert(component, loadColumns, dbHandler)
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
                SQLComponentConstants.Components.COMPONENT_TABLE, whereClause,
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
    fun getHardware(hardwareName: String, hardwareType: String): Component? {
        Log.i("HARDWARE_SEARCH", "Getting $hardwareName's details...");
        val compTable = SQLComponentConstants.Components.COMPONENT_TABLE

        //Full details query
        val queryString =
            "SELECT $compTable.*, $hardwareType.* FROM $compTable INNER JOIN $hardwareType " +
                    "ON $compTable.${SQLComponentConstants.Components.COMPONENT_NAME} = $hardwareType.${hardwareType}_name " +
                    "WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} =?"
        //Implement error check
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(hardwareName))
        val createComponent = SQLComponentTypeQueries()
        val componentResult: Component = createComponent.buildTheComponent(cursor, hardwareType)
        cursor.close()
        return componentResult
    }

    fun hardwareExists(name: String): Int {
        val queryString =
            "SELECT ${SQLComponentConstants.Components.COMPONENT_NAME} FROM ${SQLComponentConstants.Components.COMPONENT_TABLE} WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} =?"
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(name))
        //Implement error check
        val result: Int = cursor.count
        cursor.close()
        Log.i("HARDWARE_EXIST?", cursor.count.toString())
        //If 1, then true, otherwise false
        return result
    }

    /**
     *
     */
    fun getCategorySearch(
        category: String,
        searchQuery: String
    ): LiveData<List<SearchedHardwareItem>>? {

        Log.i("CATEGORY_SEARCH", "Searched category: $category");
        Log.i("NAME_QUERY_SEARCH", "Searched string: $searchQuery");

        //Retrieve components search details
        var queryString =
            "SELECT ${SQLComponentConstants.Components.COMPONENT_TABLE}.* FROM components"

        //If a specific category is being searched, append the specific category onto the MySQL query
        if (category != "all") {
            queryString += " WHERE ${SQLComponentConstants.Components.COMPONENT_TYPE} LIKE '$category' " +
                    "AND ${SQLComponentConstants.Components.COMPONENT_NAME} LIKE '%$searchQuery%';"
            Log.i("CATEGORY_QUERY", "Category Searched...")
        } else {
            queryString += " WHERE ${SQLComponentConstants.Components.COMPONENT_NAME} LIKE '%$searchQuery%';";
        }
        //Implement error check
        return TODO()
    }

    /**
     *
     */
    fun getCategory(category: String): LiveData<List<SearchedHardwareItem>>? {

        Log.i("CATEGORY_SEARCH", "Searched category: $category");

        //Retrieve components search details
        var queryString =
            "SELECT ${SQLComponentConstants.Components.COMPONENT_TABLE}.* FROM components"

        //If a specific category is being searched, append the specific category onto the MySQL query
        if (category != "all") {
            queryString += " WHERE ${SQLComponentConstants.Components.COMPONENT_TYPE} LIKE '$category';"
            Log.i("CATEGORY_QUERY", "Category $category Searched...")
        }
        //Implement error check
        return TODO()
    }
}