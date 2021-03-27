package dam95.android.uk.firstbyte.model.tables.pcbuilds

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.model.tables.components.ComponentHandler
import dam95.android.uk.firstbyte.datasource.MAX_PC_LIST_SIZE
import dam95.android.uk.firstbyte.datasource.PC_ID_COLUMN
import dam95.android.uk.firstbyte.datasource.WRITABLE_DATA
import dam95.android.uk.firstbyte.model.tables.SQLComponentConstants
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*

class PCBuildHandler(
    private val componentQueries: ComponentHandler,
    private val dbHandler: SQLiteDatabase
) {

    private val pcBuildExtraExtraQueries: PCBuildExtraQueries = PCBuildExtraQueries()

    /**
     *
     */
    fun createPersonalPC(personalPC: PCBuild): Int {
        //Input values into the correct component table.
        val result = pcBuildExtraExtraQueries.insertPCDetails(personalPC, dbHandler)
        //If there was an error, exit out of this insertion.
        if (result == (-1).toLong()) {
            Log.e("FAILED INSERT", result.toString())
            return -1
        } else {
            val cursor = dbHandler.rawQuery(
                "SELECT pc_id FROM pcbuild WHERE pc_id =(SELECT MAX(pc_id) FROM pcbuild)", null
            )

            cursor.moveToFirst()
            val mostRecentID = cursor.getInt(0)
            //Setup for relational table insertion.
            personalPC.ramList?.let {
                for (i in it.indices) personalPC.ramList!![i]?.let { it1 ->
                    pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                        it1,
                        ComponentsEnum.RAM.toString().toLowerCase(Locale.ROOT),
                        mostRecentID,
                        dbHandler
                    )
                }
            }
            personalPC.storageList?.let {
                for (i in it.indices) personalPC.storageList!![i]?.let { it1 ->
                    pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                        it1,
                        ComponentsEnum.STORAGE.toString().toLowerCase(Locale.ROOT),
                        mostRecentID,
                        dbHandler
                    )
                }
            }
            personalPC.fanList?.let {
                for (i in it.indices) personalPC.fanList!![i]?.let { it1 ->
                    pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                        it1,
                        ComponentsEnum.FAN.toString().toLowerCase(Locale.ROOT),
                        mostRecentID,
                        dbHandler
                    )
                }
            }
            cursor.close()
            return mostRecentID
        }
    }

    /**
     *
     */
    fun deletePC(pcID: Int) {
        val result = dbHandler.delete(
            "pcbuild", "pc_id = $pcID AND deletable = 1", null
        )
        if (result == -1) {
            Log.e("FAILED REMOVAL", result.toString())
        }
    }

    /**
     *
     * @param pc the personal pc build the user just exited/paused from
     */
    fun pcUpdateIsCompleted(pc: PCBuild) {
        val cv = ContentValues()
        val booleanToTinyInt = if (pc.isPcCompleted) 1 else 0
        cv.put("is_pc_completed", booleanToTinyInt)
        dbHandler.update("pcbuild", cv, "pc_id =?", arrayOf(pc.pcID.toString()))
    }

    /**
     *
     */
    fun savePCPart(name: String, type: String, pcID: Int) {
        when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.RAM.toString() -> pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                name,
                type,
                pcID,
                dbHandler
            )
            ComponentsEnum.STORAGE.toString() -> pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                name,
                type,
                pcID,
                dbHandler
            )
            ComponentsEnum.FAN.toString() -> pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                name,
                type,
                pcID,
                dbHandler
            )
            else -> {
                val cv = ContentValues()
                cv.put("${type}_name", name)
                dbHandler.update("pcbuild", cv, "pc_id =?", arrayOf(pcID.toString()))
            }
        }
    }

    /**
     * Remove a pc part from the currently loaded pc build. This pc part is a named reference to a component record.
     * @param type string value which determines the type of pc part to remove.
     * @param pcID an int? value determining which pc build in a relational table will be altered.
     */
    @Throws(NoSuchElementException::class)
    fun removePCPart(type: String, pcID: Int) {
        val cv = ContentValues()
        cv.putNull("${type}_name")

        //Update the pc part to hold null, removing the component reference but keeping a slot for further insertion/updating,
        val result = dbHandler.update("pcbuild", cv, "pc_id = $pcID", null)
        //If unsuccessful, Log an error
        if (result == -1) {
            Log.e("FAILED REMOVAL", result.toString())
        } else {
            Log.i("REMOVED_PART", "$type deleted")
        }
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
    @Throws(NoSuchElementException::class)
    fun removeRelationalPCPart(type: String, pcID: Int, relativePos: Int) {

        val cursor = dbHandler.rawQuery("SELECT ${type}_name FROM ${type}_in_pc WHERE pc_id = $pcID", null)
        val tempList: MutableList<String> = pcBuildExtraExtraQueries.relationalPCLoop(cursor).toMutableList()

        dbHandler.delete("${type}_in_pc", "pc_id = $pcID", null)
        cursor.close()
        tempList.removeAt(relativePos)

        for (i in 0 until tempList.size) pcBuildExtraExtraQueries.insertPCBuildRelationTables(tempList[i], type, pcID, dbHandler)
    }

    /**
     *
     */
    @Throws(NoSuchElementException::class)
    fun updateFansInPC(type: String, pcID: Int, numberOfFans: Int) {

        val cursor =
            dbHandler.rawQuery("SELECT ${type}_name FROM ${type}_in_pc WHERE pc_id = $pcID", null)
        val tempList: MutableList<String> =
            pcBuildExtraExtraQueries.relationalPCLoop(cursor).toMutableList()

        dbHandler.delete("${type}_in_pc", "pc_id = $pcID", null)
        cursor.close()
        try {
            for (i in 0 until numberOfFans) tempList.removeLast()

            if (tempList.isNotEmpty()) {
                for (i in 0 until tempList.size) pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                    tempList[i],
                    type,
                    pcID,
                    dbHandler
                )
            }
        } catch (exception: Exception) {

        }
    }

    fun updatePCTotalPrice(newPrice: Double, pcID: Int) {
        val cv = ContentValues()
        cv.put("pc_price", newPrice)
        dbHandler.update("pcbuild", cv, "pc_id = $pcID", null)
    }

    fun updatePCName(pcID: Int, pcName: String){
        val cv = ContentValues()
        cv.put("pc_name", pcName)
        dbHandler.update("pcbuild", cv, "pc_id = $pcID", null)
    }

    /**
     *
     */
    fun loadPersonalPC(pcID: Int): MutableLiveData<PCBuild> {
        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        val queryString =
            "SELECT * FROM pcbuild WHERE pc_id = $pcID"
        val cursor = dbHandler.rawQuery(queryString, null)
        Log.i("GET_PC", pcID.toString())
        cursor.moveToFirst()
        val loadPC = pcBuildExtraExtraQueries.getPCDetails(currentTableColumns, cursor, dbHandler)
        cursor.close()
        return loadPC
    }


    /**
     *
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun loadPersonalPCList(): LiveData<List<PCBuild?>> {

        val currentTableColumns: List<String> = SQLComponentConstants.PcBuild.COLUMN_LIST
        val pcDisplayList = mutableListOf<PCBuild?>()
        val liveData = MutableLiveData<List<PCBuild?>>()

        val queryString =
            "SELECT * FROM pcbuild WHERE deletable = $WRITABLE_DATA"
        val cursor = dbHandler.rawQuery(queryString, null)

        cursor.moveToFirst()
        //Load in 10 PCBuild Slots when the user clicks on the pc build list screen.
        for (i in 0 until MAX_PC_LIST_SIZE) {
            //If there are pc builds that aren't recommended builds currently existing in the database,
            //load all of them until, when there is no more pc builds to load, stop.
            //If there is no more pc builds to be loaded and there are still slots left,
            // full the slots in for nulls to allow users to create a new pc build in the future.
            if (cursor.count > i) {
                val mutableLiveData =
                    pcBuildExtraExtraQueries.getPCDetails(currentTableColumns, cursor, dbHandler)
                pcDisplayList.add(mutableLiveData.value)
            } else {
                pcDisplayList.add(null)
            }
            cursor.moveToNext()
        }
        cursor.close()
        liveData.value = pcDisplayList
        return liveData
    }

    /**
     *
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun loadComponentInPc(
        pcID: Int,
        componentType: String,
        componentNameList: List<String?>?,
        numberOfComponents: Int
    ): List<Pair<Component?, String>> {

        val cursor = dbHandler.rawQuery(
            "SELECT ${componentType}_name FROM ${componentType}_in_pc WHERE pc_id =?",
            arrayOf(pcID.toString())
        )
        val relationalComponentList = mutableListOf<Pair<Component?, String>>()

        for (index in 0..numberOfComponents) {
            try {
                componentNameList?.get(index)?.let { name ->
                    relationalComponentList.add(
                        Pair(
                            componentQueries.getHardware(name, componentType),
                            componentType.capitalize(Locale.ROOT)
                        )
                    )
                } ?: relationalComponentList.add(Pair(null, componentType.capitalize(Locale.ROOT)))
            } catch (exception: Exception) {
                relationalComponentList.add(Pair(null, componentType.capitalize(Locale.ROOT)))
            }
        }
        cursor.close()
        return relationalComponentList
    }
}