package dam95.android.uk.firstbyte.datasource.util

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.pcbuilds.PCBuild
import java.util.*

/*If you are going to add or remove  values to the Component Interface class,
 * then add/remove columns in the components table, in the Component Database.
 * END_OF_COMPONENTS_TABLE will = 8, because we have 9 values, but subtract 1 to make it array friendly.
 */
private const val END_OF_COMPONENTS_TABLE = 8
private const val INTEGER_RES = 0x00000001
private const val FLOAT_RES = 0x00000002
private const val STRING_RES = 0x00000003

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
        val currentTableColumns: List<String> =
            SQLComponentConstants.Components.COLUMN_LIST + tableColumns

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
                    dbHandler.insert(SQLComponentConstants.Components.TABLE, null, cv)
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
    fun buildTheComponent(cursor: Cursor, type: String): Component {

        //Load default values for a component
        val component: Component = when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> Gpu(
                "",
                "",
                "",
                0,
                0,
                0,
                0,
                "",
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.CPU.toString() -> Cpu(
                "",
                "",
                "",
                0.0,
                0,
                0,
                "",
                0,
                0,
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.RAM.toString() -> Ram(
                "",
                "",
                "",
                0,
                0,
                "",
                0,
                0.0,
                0.0,
                null,
                null,
                null
            )
            ComponentsEnum.PSU.toString() -> Psu("", "", "", 0, "", 0, 0.0, null, null, null, null)
            ComponentsEnum.STORAGE.toString() -> Storage(
                "",
                "",
                "",
                "",
                0,
                0,
                0,
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.MOTHERBOARD.toString() -> Motherboard(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                0.0,
                0,
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.CASES.toString() -> Case(
                "",
                "",
                "",
                0,
                0,
                "",
                "",
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.HEATSINK.toString() -> Heatsink(
                "",
                "",
                "",
                0,
                null,
                null,
                null,
                null,
                "",
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.FAN.toString() -> Fan("", "", "", 0, 0, 0.0, null, null, null, null)
            else -> null
        } ?: throw java.lang.NullPointerException()

        cursor.moveToFirst()
        val listComponent = component.getDetails().toMutableList()
        for (i in listComponent.indices) {
            //Skip adding the duplicate component name from the relational table
            Log.i("INDEX", i.toString())
            Log.i("INDEX_COLUMN", cursor.getColumnName(i))
            when (cursor.getType(i)) {
                STRING_RES -> listComponent[i] = cursor.getString(i)
                FLOAT_RES -> listComponent[i] = cursor.getDouble(i)
                INTEGER_RES -> {
                    if (listComponent[i] is Boolean) {
                        listComponent[i] = (cursor.getInt(i) == 1)
                    } else {
                        listComponent[i] = cursor.getInt(i)
                    }
                }
                else -> listComponent[i] = null
            }
        }
        //Assign details to component
        component.setDetails(listComponent)
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

    /**
     *
     */
    fun getPcRelationalData(loadPC: PCBuild, pcID: Int, dbHandler: SQLiteDatabase) {

        //Load Names of Ram in the PC
        var reusableCursor: Cursor = dbHandler.rawQuery(
            "SELECT ram_in_pc.ram_name FROM ram_in_pc WHERE pc_id = $pcID",
            null
        )
        loadPC.ramList = relationalPCLoop(reusableCursor)

        //Load Names of Storage in the PC
        reusableCursor = dbHandler.rawQuery(
            "SELECT storage_in_pc.storage_name FROM storage_in_pc WHERE pc_id = $pcID",
            null
        )
        loadPC.storageList = relationalPCLoop(reusableCursor)

        //Load Names of Fans in the PC
        reusableCursor = dbHandler.rawQuery(
            "SELECT fans_in_pc.fan_name FROM fans_in_pc WHERE pc_id = $pcID",
            null
        )
        loadPC.fanList = relationalPCLoop(reusableCursor)
    }

    /**
     *
     */
    fun insertPcRelationalData(cv: ContentValues, pcID: Int, tableColumns: List<String>, nameList:List<String?>, dbHandler: SQLiteDatabase): Long{
        cv.clear()
        for (i in nameList.indices) {
            cv.put(tableColumns[0], pcID)
            cv.put(tableColumns[1], nameList[i])
        }
        return dbHandler.insert(SQLComponentConstants.PcBuild.TABLE, null, cv)
    }

    /**
     *
     */
    private fun relationalPCLoop(cursor: Cursor): List<String> {
        val nameList = mutableListOf<String>()
        cursor.moveToFirst()
        for (i in 0 until cursor.count) nameList[i] = cursor.getString(i)
        return nameList
    }
}