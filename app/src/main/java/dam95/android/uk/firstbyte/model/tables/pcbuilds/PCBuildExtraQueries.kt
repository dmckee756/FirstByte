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

/**
 * @author David Mckee
 * @Version 1.0
 * Used to split up the methods from the PCBuildHandler class and avoid overcrowding.
 * This class then handles with saving and retrieval PC Details.
 * Saving and retrieving relational PC parts.
 * @param dbHandler Instance of the app's SQLite Database
 */
class PCBuildExtraQueries(private val dbHandler: SQLiteDatabase) {

    /**
     * Saves all of the PC's details into the pc builds table.
     * @param personalPC The PC that is being saved the to database
     * @return Returns a long indicating if the insertion was successful or not.
     */
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
     * Retrieves all details belonging to a PC, loads it into a newly created PCBuild object and puts it inside Mutable Live Data.
     * @param currentTableColumns
     * @param cursor Cursor is used to iterate through and retrieve the values from the database.
     * @return Returns MutableLiveData PCBuild for us in PersonalBuild Fragment.
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
     * Saves name of many-to-many PC Part into it's table. [RAM, STORAGE, FAN]
     * @param name Name of component.
     * @param type Type of Component, indicates what table the component is being saved to.
     * @param pcID The PC the component is being saved to.
     */
    fun insertPCBuildRelationTables(
        name: String,
        type: String,
        pcID: Int
    ) {
        val cv = ContentValues()
        //Insert a name reference of a component [Storage, Ram, Fan] into the...
        //corresponding many-to-many relational table between the PC build and the component.
        cv.put("pc_id", pcID)
        cv.put("${type}_name", name)
        dbHandler.insert("${type}_in_pc", null, cv)
    }

    /**
     * Retrieves the data stored in this PC's many-to-many tables [RAM, STORAGE, FANS]
     * and stores them into the PC Builds List values.
     * @param loadPC The PC that the data will be saved into.
     */
    private fun getPcRelationalData(loadPC: PCBuild) {
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
     * Refactored method used to load the names of the components in the many-to-many tables.
     * @param cursor Cursor is used to iterate through and retrieve the values from the database.
     * @return List of component names
     */
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
     * Removes PC Part(s) from the PC before a component is deleted from the database. Updates the PC price.
     * @param cursor Cursor is used to iterate through and retrieve the values from the database.
     * @param categoryType Only used when Ram, Storage Or Fan object is being removed from the database, to find the correct table.
     * @param rrpPrice Price of the component that will be removed from the total price of the PC.
     * @param singlePart A boolean check to determine if the PC is having Ram, Storage Or Fan objects removed.
     */
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