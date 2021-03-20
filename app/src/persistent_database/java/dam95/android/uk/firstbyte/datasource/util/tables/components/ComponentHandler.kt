package dam95.android.uk.firstbyte.datasource.util.tables.components

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import dam95.android.uk.firstbyte.datasource.util.SQLComponentConstants
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ComponentHandler(private val dbHandler: SQLiteDatabase
) {
    private val typeQueries: ComponentExtraQueries = ComponentExtraQueries()


    /**
     *
     */
    fun insertHardware(component: Component) {
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
            else -> return
        }
        typeQueries.batchValueInsert(component, loadColumns, dbHandler)
    }

    /**
     * Remove hardware from the database
     * TODO(Add that only components with "deletable" = 1 can be deleted) and figure out why all hardware is being set to 0
     */
    fun removeHardware(name: String) {
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
}