package dam95.android.uk.firstbyte.model.tables.pcbuilds

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.datasource.*
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.tables.SQLComponentConstants
import java.lang.IndexOutOfBoundsException

class PCBuildExtraQueries {

    fun insertPCDetails(personalPC: PCBuild, dbHandler: SQLiteDatabase): Long{
        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
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
        return dbHandler.insert("pcbuild", null, cv)
    }

    /**
     *
     */
    fun getPCDetails(
        currentTableColumns: List<String>,
        cursor: Cursor,
        dbHandler: SQLiteDatabase
    ): MutableLiveData<PCBuild> {
        val listDetail = mutableListOf<Any?>()
        val mutableLiveData: MutableLiveData<PCBuild> = MutableLiveData()
        val loadPC = PCBuild()
        for (j in currentTableColumns.indices) {
            //
            when (cursor.getType(j)) {
                STRING_RES -> listDetail.add(cursor.getString(j))
                INTEGER_RES -> listDetail.add(cursor.getInt(j))
                FLOAT_RES -> listDetail.add(cursor.getDouble(j))
                NULL_RES -> listDetail.add(null)
            }
        }
        loadPC.setPrimitiveDetails(listDetail)
        getPcRelationalData(loadPC, dbHandler)
        mutableLiveData.value = loadPC
        return mutableLiveData
    }

    /**
     *
     */
    fun getPcRelationalData(loadPC: PCBuild, dbHandler: SQLiteDatabase) {
        Log.i("PC_ID", "${loadPC.pcID}")
        //Load Names of Ram in the PC
        var reusableCursor: Cursor = dbHandler.rawQuery(
            "SELECT ram_name FROM ram_in_pc WHERE pc_id = ${loadPC.pcID}",
            null
        )
        loadPC.ramList = relationalPCLoop(reusableCursor)

        //Load Names of Storage in the PC
        reusableCursor = dbHandler.rawQuery(
            "SELECT storage_name FROM storage_in_pc WHERE pc_id = ${loadPC.pcID}",
            null
        )
        loadPC.storageList = relationalPCLoop(reusableCursor)

        //Load Names of Fans in the PC
        reusableCursor = dbHandler.rawQuery(
            "SELECT fan_name FROM fan_in_pc WHERE pc_id = ${loadPC.pcID}",
            null
        )
        loadPC.fanList = relationalPCLoop(reusableCursor)
    }

    /**
     *
     */
    @Throws(IndexOutOfBoundsException::class)
    fun relationalPCLoop(cursor: Cursor): List<String> {
        val nameList = mutableListOf<String>()
        cursor.moveToFirst()
        //Add all PC parts attached to the current loaded computer's ID
        for (i in 0 until cursor.count) {
            nameList.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return nameList
    }

    /**
     *
     */
    fun insertPCBuildRelationTables(
        name: String,
        type: String,
        pcID: Int,
        dbHandler: SQLiteDatabase
    ) {
        val cv = ContentValues()
        cv.put("pc_id", pcID)
        cv.put("${type}_name", name)
        dbHandler.insert("${type}_in_pc", null, cv)
    }

    /**
     * This will retrieve all pc parts in a many to many relationship table into a mutable list
     * (depending on passed in category [Ram, Storage, Fan]) and delete the records attached to the pc builds id.
     * After this it will remove one selected pc part from the list and then insert all the records back into the relational table.
     *
     * This is not an optimal solution, but rather a solution that shows the flaw of both my schema designs. This is something that would be...
     * ...reworked if I decide to further support this project after the final year project deadline.
     *
     * @param name the component that will be removed.
     * @param pcID the pc that will have the component removed from.
     * @param dbHandler is the access to the database.
     */
    fun removeRelationalPart(name: String, type: String, pcID: Int, dbHandler: SQLiteDatabase){

        val cursor = dbHandler.rawQuery("SELECT ${type}_name FROM ${type}_in_pc WHERE pc_id = $pcID", null)
        val tempList: MutableList<String> = relationalPCLoop(cursor).toMutableList()

        dbHandler.delete("${type}_in_pc", "pc_id = $pcID", null)
        cursor.close()
        tempList.remove(name)

        for (i in 0 until tempList.size) insertPCBuildRelationTables(name, type, pcID, dbHandler)
    }
}