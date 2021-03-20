package dam95.android.uk.firstbyte.datasource.util.tables.components

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.datasource.FLOAT_RES
import dam95.android.uk.firstbyte.datasource.INTEGER_RES
import dam95.android.uk.firstbyte.datasource.NULL_RES
import dam95.android.uk.firstbyte.datasource.STRING_RES
import dam95.android.uk.firstbyte.datasource.util.SQLComponentConstants
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.DataClassTemplate

class ComponentExtraQueries {

    private val componentsTableSize = SQLComponentConstants.Components.COLUMN_LIST.size - 1

    /**
     *
     */
    fun batchValueInsert(
        component: Component,
        tableColumns: List<String>,
        dbHandler: SQLiteDatabase
    ): Boolean {
        //First add details to the components table, if all values are added successfully,
        //then it will switch over to specific components.
        val currentTableColumns: List<String> =
            SQLComponentConstants.Components.COLUMN_LIST + tableColumns

        //When the loop skips the duplicate name, make this value minus 1
        // meaning that none of details being inserted into the database from the component will be skipped
        var correctListAlign: Int = 0

        //Utilising ContentValues to safely put data into the database and...
        //...minimise the possibility of sql injections being successful.
        val cv = ContentValues()
        var booleanToTinyInt: Int
        var result: Long
        val listComponent = component.getDetails()
        //Input values into the correct component table.
        for (i in currentTableColumns.indices) {
            //When the iteration starts the component's category table, insert the first value in list Component,
            // which should be name (if not, fix that in the component's class "getDetails" method)
            if (i == (componentsTableSize + 1)) {
                cv.put(currentTableColumns[i], listComponent[0] as String)
                correctListAlign--
                continue
            }

            Log.i("DETAIL", listComponent[i + correctListAlign].toString())
            //Load the correct type of variable and put it into the ContentValue Hash map.
            when (listComponent[i + correctListAlign]) {
                is String -> cv.put(
                    currentTableColumns[i],
                    listComponent[i + correctListAlign] as String
                )
                is Double -> cv.put(
                    currentTableColumns[i],
                    listComponent[i + correctListAlign] as Double
                )
                is Int -> cv.put(currentTableColumns[i], listComponent[i + correctListAlign] as Int)
                is Boolean -> {
                    //Convert booleans
                    booleanToTinyInt = if (listComponent[i + correctListAlign] as Boolean) 1 else 0
                    cv.put(currentTableColumns[i], booleanToTinyInt)
                }
            }
            //Once this hits the end of the components table, switch over to the specific hardware details such as the gpu or cpu etc.
            if (i == componentsTableSize) {
                result =
                    dbHandler.insert(SQLComponentConstants.Components.TABLE, null, cv)
                //If there was an error, exit out of this insertion.
                if (result == (-1).toLong()) {
                    Log.e("FAILED INSERT", result.toString())
                    return false
                }
                Log.i("COMPONENT_TABLE_INSERT", "Successfully inserted details into component")
                //Setup for relational table insertion.
                cv.clear()
            }
        }
        result = dbHandler.insert(component.type, null, cv)
        //Clear for next insertion, otherwise it will keep the previous columns
        return if (result == (-1).toLong()) {
            Log.e("FAILED INSERT", result.toString())
            false
        } else {
            true
        }
    }

    /**
     *
     */
    fun buildTheComponent(cursor: Cursor, type: String, tableColumns: List<String>): Component {

        //Load default values for a component
        val component = DataClassTemplate.createTemplateObject(type)
        val currentTableColumns: List<String> =
            SQLComponentConstants.Components.COLUMN_LIST + tableColumns

        cursor.moveToFirst()
        /*
         * Get a size of all variables in the component, and add 1 to the value. We add 1 because the cursor has a duplicate instance of the components name...
         * The first name it loads is from the components table and the second from it's own category table (Name is the primary Key).
         * Therefore we iterate through all the columns and once it reaches the end of the components table, skip the next iteration holding the duplicate name.
         */
        val allDetails = mutableListOf<Any?>()
        for (i in currentTableColumns.indices) {
            //Skip adding the duplicate component name from the relational table
            if (i == (componentsTableSize + 1)) continue
            Log.i("INDEX_COLUMN", cursor.getColumnName(i))
            when (cursor.getType(i)) {
                STRING_RES -> allDetails.add(cursor.getString(i))
                FLOAT_RES -> allDetails.add(cursor.getDouble(i))
                INTEGER_RES -> allDetails.add(cursor.getInt(i))
                NULL_RES -> allDetails.add(null)
            }
        }
        //Assign details to component
        component.setAllDetails(allDetails)
        return component
    }

    /**
     *
     */
    @Throws(NullPointerException::class)
    fun buildSearchItemList(cursor: Cursor): LiveData<List<SearchedHardwareItem>> {
        val buildDisplayList: MutableList<SearchedHardwareItem> = mutableListOf()
        val liveDataList: MutableLiveData<List<SearchedHardwareItem>> = MutableLiveData()

        cursor.moveToFirst()
        //Load all Display Items
        for (i in 0 until cursor.count) {
            val displayItem = SearchedHardwareItem(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getDouble(3)
            )
            buildDisplayList.add(displayItem)
            cursor.moveToNext()
        }

        //Put the built display list into a Mutable Live Data List of Display Items and return it as LiveData
        liveDataList.value = buildDisplayList.toList()
        return liveDataList
    }
}