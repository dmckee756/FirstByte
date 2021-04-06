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
import java.lang.IndexOutOfBoundsException
import java.util.*

const val MAX_COMPARE_SIZE = 5

/**
 * @author David Mckee
 * @Version 1.0
 *
 * Handles all component related queries in the app's database. Deals with inserting and retrieving components to/from the database.
 * Checking if components exist in the database.
 * Retrieving Display items for HardwareList fragment.
 * Adding, removing and retrieving components that are being compared to each other.
 * @param dbHandler Instance of the app's SQLite Database
 */
class ComponentHandler(
    private val dbHandler: SQLiteDatabase
) {
    private val typeQueries: ComponentExtraQueries = ComponentExtraQueries()


    /**
     * Find the correct columns/tables that the component will be saved into and then adds the component into the database.
     * @param component the component object being saved into the database.
     */
    fun insertHardware(component: Component) {
        //Find the correct amount of columns depending on the object type  [Gpu, Cpu, ... , Fan]
        //So that we have the correct amount of iterations to save all the data.
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
        //Insert the component into the database.
        typeQueries.batchValueInsert(component, loadColumns, dbHandler)
    }

    /**
     * Removes the hardware from the database by deleting it's Primary Key in the component table, initiating SQL's cascade delete.
     * @param name The name/key of the component being deleted.
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
     * Retrieves a component from the database, builds it and then returns it to the caller.
     * Will find the type of component being loaded, create a template and then load the values into said template.
     * @param hardwareName component name that is being loaded.
     * @param hardwareType the category of component that is being loaded.
     * @return The rebuilt loaded child component object.
     */
    fun getHardware(hardwareName: String, hardwareType: String): Component {
        Log.i("HARDWARE_SEARCH", "Getting $hardwareName's details...")

        //Full details query
        val queryString =
            "SELECT ${FirstByteSQLConstants.Components.TABLE}.*, $hardwareType.* FROM ${FirstByteSQLConstants.Components.TABLE} INNER JOIN $hardwareType " +
                    "ON ${FirstByteSQLConstants.Components.TABLE}.${FirstByteSQLConstants.Components.COMPONENT_NAME} = $hardwareType.${hardwareType}_name " +
                    "WHERE ${FirstByteSQLConstants.Components.COMPONENT_NAME} =?"

        //Find the correct amount of columns depending on the object type  [Gpu, Cpu, ... , Fan]
        //So that we have the correct amount of iterations to save all the data.
        //I don't use the cursor for this purpose as I want to assure the duplicate name column is included so that it can be skipped in the iteration.
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

    /**
     * Check the database component table if a component exists.
     * @param name name of component
     * @return Returns 1 if component exists, 0 if it doesn't
     */
    fun hardwareExists(name: String): Int {
        //Attempt to find the hardware's name in the component table
        val queryString =
            "SELECT component_name FROM component WHERE component_name =?"
        val cursor: Cursor = dbHandler.rawQuery(queryString, arrayOf(name))
        val result: Int = cursor.count
        cursor.close()
        //If 1, then true, otherwise false
        return result
    }

    /**
     * Queries the database to retrieve all display information of the desired category. This can be all components or a specific type of component.
     * I.e Load only Gpu's for display, or load all Components for display.
     * @param category Indicated if all display component items should be loaded if it has a value of "all" or if a specific category is to be loaded e.g. "cpu"
     * @return LiveData instance of a SearchedHardwareItem List
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
     * Retrieve a saved component's URL. Used in cases where we only want the URL and not wanting to waste time loading the rest of the component.
     * @param componentName Name of the component that's URL is being retrieved.
     * @return URL of the component's image, that will be converted into an image using Picasso.
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
     * @param typeID Named Primary Key value on table creation for one of the following:
     * GPU, CPU OR RAM compare lists.
     */
    fun createComparedList(typeID: String) {
        val cv = ContentValues()
        cv.put(FirstByteSQLConstants.Compare.ID, typeID)
        dbHandler.insert(FirstByteSQLConstants.Compare.TABLE, null, cv)
    }

    /**
     * Check if the compared identifier has been created yet, if not then inform the caller and create the identifier table.
     * @param typeID The component Category that is used in the compare table's Primary Key
     * @return If the compared key exist/has been created then return 1, otherwise return 0
     * @throws SQLiteException If the compared key hasn't been created yet, catch the exception and return 0
     */
    @Throws(SQLiteException::class)
    fun doesComparedListExist(typeID: String): Int {

        return try {
            //Search for the compare identifier table
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
     * Retrieve all the names of compared components in a specific category [GPU, CPU, RAM].
     * If there are no components being compared, return a list of nulls.
     * There can only be 5 slots maximum for components to be compared to another.
     * @param typeID used in finding the correct relational compared table
     * @return a list of the names referencing compared objects.
     * @throws IndexOutOfBoundsException If there are no current objects being compared,
     * return a list of 5 null values so that the recycler adapter can slots that can be added to.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun retrieveComparedList(typeID: String): List<String?> {
        val loadedComponentNames = mutableListOf<String?>()

        return try {
            // Find the components in a compared list [GPU, CPU, RAM]
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


    /**
     * This method is called when a component is being removed from the database,
     * this way we avoid cascade deleting from potentially removing clumps of data that is not relevant to the component.
     * If the component is being compared, notify the caller, otherwise return 0 and ignore.
     *
     * @param componentName The name of a specific component that is being removed from the database
     * @return If the component is being compared then return 1, otherwise return 0
     * @throws SQLiteException If the compared doesn't exist in the table, catch the exception and return 0
     */
    @Throws(SQLiteException::class)
    fun isComponentInComparedTable(componentName: String): Int {
        return try {
            //Check if a component is in the compared table.
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
     * @param typeID compare table identifier, assigns component to correct foreign key identifier.
     * @param savedComponent name of the component being compared. Used as a reference to load component details when needed.
     */
    fun saveComparedComponent(typeID: String, savedComponent: String) {
        val cv = ContentValues()
        //Put in compare table identifier, to inform database what category this compared component belongs to.
        cv.put(FirstByteSQLConstants.CompareStats.ID, typeID)
        //Put in component's name, to be used as a reference when loading the compared components details.
        cv.put(FirstByteSQLConstants.CompareStats.COMPONENT, savedComponent)
        val result = dbHandler.insert(FirstByteSQLConstants.CompareStats.TABLE, null, cv)
        if (result == (-1).toLong()){
            Log.i("ERROR_SAVING_COMPARE", "Error saving component to it's compared table.")
        }
    }

    /**
     * Remove the component from the compared table in the database.
     * @param componentName the removed components name.
     */
    fun deleteComparedComponent(componentName: String) =
        dbHandler.delete(FirstByteSQLConstants.CompareStats.TABLE, "${FirstByteSQLConstants.CompareStats.COMPONENT} =?",
            arrayOf(componentName)
        )
}