package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import dam95.android.uk.firstbyte.model.SearchedHardwareItem

class ComponentDBAccess(private var context: Context) {
    private var componentDB: ComponentDBAccess? = ComponentDBAccess(context)
    private var database: ComponentDBHelper = ComponentDBHelper(context)
    private var componentController: SQLiteDatabase = database.writableDatabase

    /**
     *
     */
    fun getComponentsInstance(): ComponentDBAccess? {
        when (componentDB) {
            null -> componentDB = ComponentDBAccess(context)
            else -> componentDB
        }
        return componentDB
    }

    /**
     *
     */
    fun closeDatabase() {
        componentController.close()
        componentDB = null
    }

    /**
     *
     */
    fun getHardware(hardwareName: String, hardwareType: String) {

        Log.i("HARDWARE_SEARCH", "Getting $hardwareName's details...");

        //Full details query
        val queryString =
            "SELECT components.component_type, components.image_link, $hardwareType.* FROM components " +
                    "INNER JOIN prices ON components.component_name = prices.name " +
                    "INNER JOIN $hardwareType ON components.component_name = ${hardwareName}_name " +
                    "WHERE component_name = '$hardwareName';"
        //DATABASE.FINDHARDWARE
    }

    /**
     *
     */
    fun getCategorySearch(category: String, searchQuery: String): LiveData<List<SearchedHardwareItem>> {

        Log.i("CATEGORY_SEARCH", "Searched category: $category");
        Log.i("NAME_QUERY_SEARCH", "Searched string: $searchQuery");

        //Retrieve components search details with rrp price
        var queryString =
            "SELECT components.*, prices.rrp_price FROM components " +
                    "INNER JOIN prices ON components.component_name = prices.name";

        //If a specific category is being searched, append the specific category onto the MySQL query
        if (category !== "all") {
            queryString += " WHERE component_type LIKE '$category' AND component_name LIKE '%$searchQuery%';"
            Log.i("CATEGORY_QUERY", "Category Searched...")
        } else {
            queryString += " WHERE component_name LIKE '%$searchQuery%';";
        }
        return TODO()
    }

    /**
     *
     */
    fun getCategory(category: String): LiveData<List<SearchedHardwareItem>> {

        Log.i("CATEGORY_SEARCH", "Searched category: $category");

        //Retrieve components search details with rrp price
        var queryString =
            "SELECT components.*, prices.rrp_price FROM components " +
                    "INNER JOIN prices ON components.component_name = prices.name";

        //If a specific category is being searched, append the specific category onto the MySQL query
        if (category !== "all") {
            queryString += " WHERE component_type LIKE '$category';"
            Log.i("CATEGORY_QUERY", "Category Searched...")
        }
        return TODO()
    }
}