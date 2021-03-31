package dam95.android.uk.firstbyte.model.tables.pcbuilds

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.datasource.*
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.tables.FirstByteSQLConstants
import java.lang.IndexOutOfBoundsException

class PCBuildExtraQueries(private val dbHandler: SQLiteDatabase) {

    fun insertPCDetails(personalPC: PCBuild): Long{
        val currentTableColumns: List<String> = FirstByteSQLConstants.PcBuild.COLUMN_LIST
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
        cursor: Cursor
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
        getPcRelationalData(loadPC)
        mutableLiveData.value = loadPC
        return mutableLiveData
    }

    /**
     *
     */
    fun getPcRelationalData(loadPC: PCBuild) {
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
        pcID: Int
    ) {
        val cv = ContentValues()
        cv.put("pc_id", pcID)
        cv.put("${type}_name", name)
        dbHandler.insert("${type}_in_pc", null, cv)
    }

    fun removeComponentPriceFromPCs(
        cursor: Cursor,
        categoryType: String,
        rrpPrice: Double,
        singlePart: Boolean
    ) {
        var totalPrice: Double
        var pcID: Int
        var relationalCursor: Cursor
        var numberOfSlots = 1
        val cv = ContentValues()
        cursor.moveToFirst()
        //Update the total price in all pc's that has the component
        for (index in 0 until cursor.count){
            //Get pc ID
            pcID = cursor.getInt(0)
            totalPrice = cursor.getDouble(1)

            //If this is a relational part, then get the number of this components in the PC
            if (!singlePart){
               relationalCursor = dbHandler.rawQuery("SELECT pc_id FROM ${categoryType}_in_pc WHERE pc_id =?",
                    arrayOf(pcID.toString())
                )
                numberOfSlots = relationalCursor.count
                relationalCursor.close()
            }

            for (slots in 0 until numberOfSlots) {
                //Remove the component's rrp price from the pc
                totalPrice -= rrpPrice
            }
            //Update the pc
            cv.put(FirstByteSQLConstants.PcBuild.PC_RRP_PRICE, totalPrice)
            dbHandler.update(FirstByteSQLConstants.PcBuild.TABLE, cv, "${FirstByteSQLConstants.PcBuild.PC_ID} =?",
                arrayOf(pcID.toString()))
            //Prepare for next iteration
            cv.clear()
            cursor.moveToNext()
        }
        cursor.close()
    }
}