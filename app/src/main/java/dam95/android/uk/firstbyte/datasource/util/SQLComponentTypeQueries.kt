package dam95.android.uk.firstbyte.datasource.util

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import java.util.*

/*If you are going to add or remove  values to the Component Interface class,
 * then add/remove columns in the components table, in the Component Database.
 * END_OF_COMPONENTS_TABLE will = 8, because we have 9 values, but subtract 1 to make it array friendly.
 */
private const val END_OF_COMPONENTS_TABLE = 8

class SQLComponentTypeQueries {

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
        val currentTableColumns: List<String> = SQLComponentConstants.Components.COLUMN_LIST + tableColumns

        //Utilising ContentValues to safely put data into the database and...
        //...minimise the possibility of sql injections being successful.
        val cv = ContentValues()
        var booleanToTinyInt: Int
        var result: Long
        val listComponent = component.getDetails()
        //Input values into the correct component table.
        for (i in currentTableColumns.indices) {
            Log.i("DETAIL", listComponent[i].toString())
            //Load the correct type of variable and put it into the ContentValue Hash map.
            when (listComponent[i]) {
                is String -> cv.put(currentTableColumns[i], listComponent[i] as String)
                is Double -> cv.put(currentTableColumns[i], listComponent[i] as Double)
                is Int -> cv.put(currentTableColumns[i], listComponent[i] as Int)
                is Boolean -> {
                    //Convert booleans
                    booleanToTinyInt = if (listComponent[i] as Boolean) 1 else 0
                    cv.put(currentTableColumns[i], booleanToTinyInt)
                }
            }
            //Once this hits the end of the components table, switch over to the specific hardware details such as the gpu or cpu etc.
            if (i == END_OF_COMPONENTS_TABLE) {
                result =
                    dbHandler.insert(SQLComponentConstants.Components.COMPONENT_TABLE, null, cv)
                //If there was an error, exit out of this insertion.
                if (result == (-1).toLong()) {
                    Log.e("FAILED INSERT", result.toString())
                    return false
                }
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
    @Throws(NullPointerException::class)
    fun buildTheComponent(cursor: Cursor, type:String): Component{

        val component: Component = when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> Gpu("","","", 0,0,0,0,"",0.0,null, null, null, null)
            ComponentsEnum.CPU.toString() -> Cpu("","","", 0.0,0,0,"",0,0,0.0, null, null, null, null)
            ComponentsEnum.RAM.toString() -> Ram("","","", 0,0,"",0,0.0,0.0,null, null, null)
            ComponentsEnum.PSU.toString() -> Psu("","","", 0,"",0,0.0,null, null, null, null)
            ComponentsEnum.STORAGE.toString() -> Storage("","","", "",0,0,0,0.0,null, null, null, null)
            ComponentsEnum.MOTHERBOARD.toString() -> Motherboard("","","", "","","","",0,0,0.0, 0, 0.0,null,null,null, null)
            ComponentsEnum.CASES.toString() -> Case("","","", 0,0,"","",0.0,null, null, null, null)
            ComponentsEnum.HEATSINK.toString() -> Heatsink("","","", 0,"","","","","", 0.0,null, null, null, null)
            ComponentsEnum.FAN.toString() -> Fan("","","", 0,0,0.0,null, null, null, null)
            else -> null
        } ?: throw java.lang.NullPointerException()

        cursor.moveToFirst()
        val listComponent = component.getDetails().toMutableList()
        for (i in listComponent.indices){
            //Skip adding the duplicate component name from the relational table
            Log.i("INDEX", i.toString())
            Log.i("INDEX_COLUMN", cursor.getColumnName(i))
            when (listComponent[i]) {
                is String -> listComponent[i] = cursor.getString(i)
                is Double -> listComponent[i] = cursor.getDouble(i)
                is Int -> listComponent[i] = cursor.getInt(i)
                is Boolean -> listComponent[i] = cursor.getInt(i) == 1
                else -> listComponent[i] = null
            }
        }
        //Assign details to component
        component.setDetails(listComponent)
        Log.i("BUILT_VALUES", component.toString())
        return component
    }

    fun buildSearchItemList(cursor: Cursor): LiveData<List<SearchedHardwareItem>>?{
/*
        do {

        } while ()*/
        return TODO()
    }
}