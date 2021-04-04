package dam95.android.uk.firstbyte.model.tables.pcbuilds

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.model.tables.components.ComponentHandler
import dam95.android.uk.firstbyte.datasource.MAX_PC_LIST_SIZE
import dam95.android.uk.firstbyte.datasource.WRITABLE_DATA
import dam95.android.uk.firstbyte.model.tables.FirstByteSQLConstants
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.components.Case
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Heatsink
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*

/**
 * @author David Mckee
 * @Version 1.0
 *
 * Handles all pc build related queries in the app's database.
 * Deals with creating, deleting and retrieving a pc to/from the database and retrieving a list (Max size 10) of pc's from the database.
 * Save and remove all pc parts. Update fan slots and parts depending on the added/remove pc component.
 * Update pc details such as it's name, total price and completion tag.
 * @param componentQueries Instance of the class that deals with component queries, used to load details of pc parts.
 * @param dbHandler Instance of the app's SQLite Database
 */
class PCBuildHandler(
    private val componentQueries: ComponentHandler,
    private val dbHandler: SQLiteDatabase
) {

    private val pcBuildExtraExtraQueries: PCBuildExtraQueries = PCBuildExtraQueries(dbHandler)

    /**
     * Create a new blank pc and add it tp the database. If there are already 10 Created Personal PC's (Writable PC's) then do not create the PC.
     * @param personalPC A new blank pc
     * @return Returns a value of -1 if it can't be created or there was an error, otherwise 0+
     */
    fun createPersonalPC(personalPC: PCBuild): Int {
        var cursor = dbHandler.rawQuery(
            "SELECT ${FirstByteSQLConstants.PcBuild.PC_ID} FROM ${FirstByteSQLConstants.PcBuild.TABLE} " +
                    "WHERE ${FirstByteSQLConstants.PcBuild.PC_IS_DELETABLE} = $WRITABLE_DATA", null
        )
        //We only allow 10 Personal Builds created at a time, if a new PC is being created...
        //...and there is already 10 PC's, then don't create the PC and return -1
        if (cursor.count >= 10) {
            cursor.close()
            return -1
        }
        cursor.close()

        //Input values into the correct component table.
        val result = pcBuildExtraExtraQueries.insertPCDetails(personalPC)
        //If there was an error, exit out of this insertion.
        if (result == (-1).toLong()) {
            Log.e("FAILED INSERT", result.toString())
            return -1
        } else {
            cursor = dbHandler.rawQuery(
                "SELECT ${FirstByteSQLConstants.PcBuild.PC_ID} FROM ${FirstByteSQLConstants.PcBuild.TABLE} " +
                        "WHERE ${FirstByteSQLConstants.PcBuild.PC_ID} =(SELECT MAX(${FirstByteSQLConstants.PcBuild.PC_ID}) " +
                        "FROM ${FirstByteSQLConstants.PcBuild.TABLE})", null
            )

            cursor.moveToFirst()
            val mostRecentID = cursor.getInt(0)
            //Setup for relational table insertion.
            //2 Ram Slots
            //3 Storage slots
            //n amount of fan slots
            personalPC.ramList?.let {
                for (i in it.indices) personalPC.ramList!![i]?.let { it1 ->
                    pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                        it1,
                        ComponentsEnum.RAM.toString().toLowerCase(Locale.ROOT),
                        mostRecentID
                    )
                }
            }
            personalPC.storageList?.let {
                for (i in it.indices) personalPC.storageList!![i]?.let { it1 ->
                    pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                        it1,
                        ComponentsEnum.STORAGE.toString().toLowerCase(Locale.ROOT),
                        mostRecentID
                    )
                }
            }
            personalPC.fanList?.let {
                for (i in it.indices) personalPC.fanList!![i]?.let { it1 ->
                    pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                        it1,
                        ComponentsEnum.FAN.toString().toLowerCase(Locale.ROOT),
                        mostRecentID
                    )
                }
            }
            cursor.close()
            return mostRecentID
        }
    }

    /**
     * Remove a PC from the database, cascade delete will remove all relational pc parts [RAM, STORAGE, FAN] in the many-to-many tables.
     * @param pcID PC Primary Key ID
     */
    fun deletePC(pcID: Int) {
        val result = dbHandler.delete(
            FirstByteSQLConstants.PcBuild.TABLE, "${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID " +
                    "AND ${FirstByteSQLConstants.PcBuild.PC_IS_DELETABLE} = $WRITABLE_DATA", null
        )
        if (result == -1) {
            Log.e("FAILED REMOVAL", result.toString())
        }
    }

    /**
     * Update the PCBuild table and save the component into it's PC Part slot. If the component is [RAM, STORAGE, FAN],
     * then add it to the many-to-many relationship table.
     * @param name name of the component being saved
     * @param type type of component being saved, identifies if it's a part of a many-to-many relationship or 0..1
     * and what slot to put the new PC Part into
     * @param pcID Primary Key ID of PC
     */
    fun savePCPart(name: String, type: String, pcID: Int) {
        //Determine if the component belongs in a many-to-many table or is directly referenced in the PC build table.
        when (type.toUpperCase(Locale.ROOT)) {
            //Insert component name it into many-to-many relational table.
            ComponentsEnum.RAM.toString() ->
                pcBuildExtraExtraQueries.insertPCBuildRelationTables(name, type, pcID)
            ComponentsEnum.STORAGE.toString() ->
                pcBuildExtraExtraQueries.insertPCBuildRelationTables(name, type, pcID)
            ComponentsEnum.FAN.toString() ->
                pcBuildExtraExtraQueries.insertPCBuildRelationTables(name, type, pcID)
            else -> {
                val cv = ContentValues()
                //Insert component name into it's designated pc Part slot.
                cv.put("${type}_name", name)
                dbHandler.update(
                    FirstByteSQLConstants.PcBuild.TABLE, cv,
                    "${FirstByteSQLConstants.PcBuild.PC_ID} =?", arrayOf(pcID.toString())
                )
            }
        }
    }

    /**
     * Remove a pc part from the currently loaded pc build. This pc part is a named reference to a component record.
     * @param type string value which determines the type of pc part to remove.
     * @param pcID Primary Key value determining the pc that will have the component removed from.
     */
    fun removePCPart(type: String, pcID: Int) {
        val cv = ContentValues()
        cv.putNull("${type}_name")

        //Update the pc part to hold null, removing the component reference but keeping a slot for further insertion/updating,
        val result = dbHandler.update(
            FirstByteSQLConstants.PcBuild.TABLE,
            cv,
            "${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID",
            null
        )
        //If unsuccessful, Log an error
        if (result == -1) {
            Log.e("FAILED REMOVAL", result.toString())
        } else {
            Log.i("REMOVED_PART", "$type deleted")
        }
    }

    /**
     * Finds the specific PC Part that is being removed from the many-to-many relational table in the current PC and deletes it.
     *
     * @param type identifies what many-to-many relationship table to remove the pc part from.
     * @param pcID pcID Primary Key value determining which pc build in a many-to-many relational table will be altered.
     * @param relativePos position of the component to be removed that's attached to the PC's ID in the relational table.
     * E.g. The user removed storage slot 2 from the PC Build, so we only remove Slot 2 and slide slot 3 down to slot 2.
     */
    fun removeRelationalPCPart(type: String, pcID: Int, relativePos: Int) {

        //Retrieve all many-to-many components and insert them into a temporary mutable list.
        val cursor =
            dbHandler.rawQuery(
                "SELECT ${type}_name FROM ${type}_in_pc WHERE ${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID",
                null
            )
        val tempList: MutableList<String> =
            pcBuildExtraExtraQueries.relationalPCLoop(cursor).toMutableList()

        //Deletes the table that the data was retrieved from
        dbHandler.delete("${type}_in_pc", "${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID", null)
        cursor.close()
        //Remove the desired component at it's relative position in this list.
        tempList.removeAt(relativePos)
        //Recreate the updated table.
        for (i in 0 until tempList.size) pcBuildExtraExtraQueries.insertPCBuildRelationTables(
            tempList[i],
            type,
            pcID
        )
    }

    /**
     * Trims off an overflow amount of fan slots from the PC whenever a heatsink or case is removed from the PC Build.
     * If there are fans in these slots, they just get removed. It only removes n amount of fans from the end of the list.
     * @param type identifies what many-to-many relationship table to remove the pc part from.
     * @param pcID pcID Primary Key value determining which pc build in a many-to-many relational table will be altered.
     * @param numberOfFans Number of fans that are in the PC.
     * @throws NoSuchElementException Ignored
     */
    @Throws(NoSuchElementException::class)
    fun updateFansInPC(type: String, pcID: Int, numberOfFans: Int) {
        //Retrieve all fans in a pc build and insert them into a temporary mutable list.
        val cursor =
            dbHandler.rawQuery(
                "SELECT ${type}_name FROM ${type}_in_pc WHERE ${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID",
                null
            )
        val tempList: MutableList<String> =
            pcBuildExtraExtraQueries.relationalPCLoop(cursor).toMutableList()

        //Deletes the table that the data was retrieved from
        dbHandler.delete("${type}_in_pc", "${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID", null)
        cursor.close()

        try {
            //Remove the amount of fans slots that the PC Build loses when a case or heatsink is removed
            for (i in 0 until numberOfFans) tempList.removeLast()

            //If the temporary list holding fan data of pc still has fan slots in it, then re-add it to the fan_in_pc table
            //Creating an updated table.
            if (tempList.isNotEmpty()) {
                for (i in 0 until tempList.size) pcBuildExtraExtraQueries.insertPCBuildRelationTables(
                    tempList[i],
                    type,
                    pcID
                )
            }
        } catch (exception: Exception) {
            //Ignore
        }
    }

    /**
     * Updates the Current PC's total price when hardware is added or removed from it.
     * @param newPrice new total price value of the PC Build.
     * @param pcID pcID Primary Key value to update the correct PC.
     */
    fun updatePCTotalPrice(newPrice: Double, pcID: Int) {
        val cv = ContentValues()
        //Put the new total price of the PC into the Content values and overwrite PC Price in the database.
        cv.put(FirstByteSQLConstants.PcBuild.PC_RRP_PRICE, newPrice)
        dbHandler.update(
            FirstByteSQLConstants.PcBuild.TABLE,
            cv,
            "${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID",
            null
        )
    }

    /**
     * Updates the PC's name when the user changes it.
     * @param pcID pcID Primary Key value to update the correct PC.
     * @param pcName new name given to the PC.
     */
    fun updatePCName(pcID: Int, pcName: String) {
        val cv = ContentValues()
        //Put the new name into the Content values and overwrite PC Name in the database.
        cv.put(FirstByteSQLConstants.PcBuild.PC_NAME, pcName)
        dbHandler.update(
            FirstByteSQLConstants.PcBuild.TABLE,
            cv,
            "${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID",
            null
        )
    }

    /**
     * Updates if the PC is functional/completed.
     * @param pc The most recent Personal PC Build the user interacted with.
     */
    fun pcUpdateIsCompleted(pc: PCBuild) {
        val cv = ContentValues()
        //Convert the value into tinyInt for SQL
        val booleanToTinyInt = if (pc.isPcCompleted) 1 else 0
        //Put the completed check into the Content values and overwrite the previous value in the database
        cv.put("is_pc_completed", booleanToTinyInt)
        dbHandler.update(
            FirstByteSQLConstants.PcBuild.TABLE,
            cv,
            "${FirstByteSQLConstants.PcBuild.PC_ID} =?",
            arrayOf(pc.pcID.toString())
        )
    }

    /**
     * Load a specific writable Personal PC Build from the database and use it in the PersonalBuild Fragment.
     * @param pcID pcID Primary Key value to update the correct PC.
     * @return A MutableLiveData instance of a PCBuild
     */
    fun loadPersonalPC(pcID: Int): MutableLiveData<PCBuild> {
        val currentTableColumns: List<String> = FirstByteSQLConstants.PcBuild.COLUMN_LIST
        //Finds the correct PC
        val queryString =
            "SELECT * FROM ${FirstByteSQLConstants.PcBuild.TABLE} WHERE ${FirstByteSQLConstants.PcBuild.PC_ID} = $pcID"
        val cursor = dbHandler.rawQuery(queryString, null)
        Log.i("GET_PC", pcID.toString())
        cursor.moveToFirst()
        //Assigns the loaded values into a newly created PCBuild Object
        val loadPC = pcBuildExtraExtraQueries.getPCDetails(currentTableColumns, cursor)
        cursor.close()
        return loadPC
    }


    /**
     * Loads a list that has 10 slots of PCBuilds. For any slots that do not have a PCBuild, it will contain a null.
     * This allows the recycler list to display the slot and have the user create a new pc by pressing on the "empty" slot.
     * @return LiveData PCBuild? List. The list has 10 slots, and will only either contain a PCBuild, or a null.
     */
    fun loadPersonalPCList(): LiveData<List<PCBuild?>> {

        val currentTableColumns: List<String> = FirstByteSQLConstants.PcBuild.COLUMN_LIST
        val pcDisplayList = mutableListOf<PCBuild?>()
        val liveData = MutableLiveData<List<PCBuild?>>()

        val queryString =
            "SELECT * FROM ${FirstByteSQLConstants.PcBuild.TABLE} WHERE ${FirstByteSQLConstants.PcBuild.PC_IS_DELETABLE} =$WRITABLE_DATA"
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
                    pcBuildExtraExtraQueries.getPCDetails(currentTableColumns, cursor)
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
     * Loads the name of each component assigned to a PC that is in the PC build many-to-many relationship table.
     * It then places the loaded component name into a pair along with an identifier category tag, which identifies the slot.
     *
     * @param pcID the ID of the PC that is currently being loaded.
     * @param componentType type of component that this method is loading.
     * @param componentNameList current list of component names in the PCBuild's list value E.G. [RAM, STORAGE, FAN] list.
     * @param numberOfComponents number of components/slots that is being loaded/created.
     * @return a Paired List of component names and their category identifiers.
     * @throws ArrayIndexOutOfBoundsException If the slot is empty and this throws an exception, add a Pair of a null and a identifier on the slot the component holds.
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun loadRelationalComponentInPc(
        pcID: Int,
        componentType: String,
        componentNameList: List<String?>?,
        numberOfComponents: Int
    ): List<Pair<Component?, String>> {

        //Find all components within the many-to-many relational table that is assigned to the current PC.
        val cursor = dbHandler.rawQuery(
            "SELECT ${componentType}_name FROM ${componentType}_in_pc WHERE ${FirstByteSQLConstants.PcBuild.PC_ID} =?",
            arrayOf(pcID.toString())
        )
        val relationalComponentList = mutableListOf<Pair<Component?, String>>()

        //Put each loaded relational component into a Pair (Component name, Component type identifier)
        // e.g. Pair(component name, Ram) or Pair(component name, Storage)
        for (index in 0..numberOfComponents) {
            try {
                componentNameList?.get(index)?.let { name ->
                    relationalComponentList.add(
                        Pair(componentQueries.getHardware(name, componentType),
                            componentType.capitalize(Locale.ROOT)))
                    //If the slot is empty, add a Pair of a null and a identifier on the slot the component holds.
                } ?: relationalComponentList.add(Pair(null, componentType.capitalize(Locale.ROOT)))

            } catch (exception: Exception) {
                //If the slot is empty and this throws an exception, add a Pair of a null and a identifier on the slot the component holds.
                relationalComponentList.add(Pair(null, componentType.capitalize(Locale.ROOT)))
            }
        }
        cursor.close()
        return relationalComponentList
    }

    /**
     * Check if a Writable Component is a part of any PC in the database and return n > 0 if true.
     * This is used to check if components need removed from PC's before the component is removed from the database.
     * @param componentName Searched component name.
     * @param categoryType determines what table is to be checked.
     * @return 0 if component is not in any PC, n > 0 if the component is in PC(s).
     */
    fun isHardwareInBuilds(componentName: String, categoryType: String): Int {

        //Check if the hardware needs to be checked in a many-to-many relational table
        val desiredTable = when (categoryType.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.RAM.toString() -> "${categoryType}_in_pc"
            ComponentsEnum.STORAGE.toString() -> "${categoryType}_in_pc"
            ComponentsEnum.FAN.toString() -> "${categoryType}_in_pc"
            else -> FirstByteSQLConstants.PcBuild.TABLE
        }
        //Check if the component exists in any PC's
        val cursor = dbHandler.rawQuery(
            "SELECT ${categoryType}_name FROM $desiredTable WHERE ${categoryType}_name =?",
            arrayOf(componentName)
        )
        val result = cursor.count
        cursor.close()
        return result
    }

    /**
     * When the user removes a Component from the database (From Hardware Details Fragment), then also remove the same PC part from any PC
     * before the app deletes the Component. When removing the PC part(s), update the pricing of the PC the part(s) belonged to.
     *
     * @param componentName the soon to be deleted Component/PC Part.
     * @param categoryType what category the Component/PC Part is.
     * @param rrpPrice the rrpPrice of the Component/PC Part being removed, which will be taken away from the PCBuild(s) n amount of times.
     */
    fun removeHardwareFromBuilds(componentName: String, categoryType: String, rrpPrice: Double) {
        //If the current removed component is a heatsink or case, load the details so that we can find the number of fans that need to be trimmed off the PC.
        var component: Component? = null

        //Check if the hardware needs to be checked in a many-to-many relational table or is a case and heatsink
        val singlePart = when (categoryType.toUpperCase(Locale.ROOT)) {
            //Many-to-many relational tables
            ComponentsEnum.RAM.toString() -> false
            ComponentsEnum.STORAGE.toString() -> false
            ComponentsEnum.FAN.toString() -> false
            //If the removed hardware is a heatsink or case, inform this method that fans need to be trimmed and their prices removed from the pc
            ComponentsEnum.HEATSINK.toString() -> {
                component = componentQueries.getHardware(componentName, categoryType)
                true
            }
            ComponentsEnum.CASES.toString() -> {
                component = componentQueries.getHardware(componentName, categoryType)
                true
            }
            else -> {
                true
            }
        }
        //Trim of excess fans and remove their price from the PC
        if (component != null) trimOffFans(component)

        val cursor = if (!singlePart) {
            //Get all unique PC's that contain the relational part that's being removed
            dbHandler.rawQuery(
                "SELECT DISTINCT ${FirstByteSQLConstants.PcBuild.TABLE}.${FirstByteSQLConstants.PcBuild.PC_ID} ,${FirstByteSQLConstants.PcBuild.PC_RRP_PRICE} " +
                        "FROM ${FirstByteSQLConstants.PcBuild.TABLE} JOIN ${categoryType}_in_pc ON ${categoryType}_in_pc.${FirstByteSQLConstants.PcBuild.PC_ID} " +
                        "WHERE ${categoryType}_name =?",
                arrayOf(componentName)
            )
        } else {
            //Get all unique PC's that contain the current part that's being removed
            dbHandler.rawQuery(
                "SELECT ${FirstByteSQLConstants.PcBuild.PC_ID} ,${FirstByteSQLConstants.PcBuild.PC_RRP_PRICE} " +
                        "FROM ${FirstByteSQLConstants.PcBuild.TABLE} WHERE ${categoryType}_name =?",
                arrayOf(componentName)
            )
        }

        //update pc prices
        pcBuildExtraExtraQueries.removeComponentPriceFromPCs(
            cursor,
            categoryType,
            rrpPrice,
            singlePart
        )

        //Remove the component from all pc's
        val cv = ContentValues()
        if (!singlePart) {
            //Handle deletion for multiple slot pc parts
            dbHandler.delete(
                "${categoryType}_in_pc", "${categoryType}_name =?",
                arrayOf(componentName)
            )
        } else {
            //Handle deletion (change slot to null) for single slot pc parts
            cv.clear()
            cv.putNull("${categoryType}_name")
            dbHandler.update(
                FirstByteSQLConstants.PcBuild.TABLE,
                cv,
                "${categoryType}_name =?",
                arrayOf(componentName)
            )
        }
    }

    /**
     * When a heatsink or case is removed from the database, before it's removal trim off any fan slots from PC's
     * that contain this component and update the PC Price.
     * @param component the Heatsink or Case.
     */
    private fun trimOffFans(component: Component) {
        var pcID: Int
        //Find all pc's that contain the heatsink or case.
        val cursor = dbHandler.rawQuery(
            "SELECT ${FirstByteSQLConstants.PcBuild.PC_ID}, ${FirstByteSQLConstants.PcBuild.PC_RRP_PRICE} " +
                    "FROM ${FirstByteSQLConstants.PcBuild.TABLE} WHERE ${component.type}_name =?",
            arrayOf(component.name)
        )
        cursor.moveToFirst()
        // For each PC that contains the component, get all Fans in that PC, along with their price.
        // Remove the price of each fan that's being trimmed in the PC from the total price of the pc and trim off the fans.
        // Repeat until all PC's with this component has been updated.
        for (i in 0 until cursor.count) {
            pcID = cursor.getInt(0)
            /*
            The below SQL Query in human readable format. The bonus of having it done below, is that if the referenced variable is "updated"/changed,
            then this query will also be updated/changed with it.
            SELECT DISTINCT pcbuild.pc_id, fan_in_pc.fan_name, pcbuild.pc_price
            FROM pcbuild JOIN fan_in_pc ON fan_in_pc.pc_id
            WHERE fan_in_pc.pc_id = $pcID AND pcbuild.pc_id = $pcID
             */
            //Get all unique PC's that contain the relational part
            val fanCursor = dbHandler.rawQuery(
                "SELECT DISTINCT ${FirstByteSQLConstants.PcBuild.TABLE}.${FirstByteSQLConstants.PcBuild.PC_ID}, " +
                        "${FirstByteSQLConstants.FansInPc.TABLE}.${FirstByteSQLConstants.FansInPc.PC_FAN_NAME}, " +
                        "${FirstByteSQLConstants.PcBuild.TABLE}.${FirstByteSQLConstants.PcBuild.PC_RRP_PRICE} " +
                        "FROM ${FirstByteSQLConstants.PcBuild.TABLE} " +
                        "JOIN ${FirstByteSQLConstants.FansInPc.TABLE} " +
                        "ON ${FirstByteSQLConstants.FansInPc.TABLE}.${FirstByteSQLConstants.PcBuild.PC_ID} " +
                        "WHERE ${FirstByteSQLConstants.FansInPc.TABLE}.${FirstByteSQLConstants.PcBuild.PC_ID} =$pcID " +
                        "AND ${FirstByteSQLConstants.PcBuild.TABLE}.${FirstByteSQLConstants.PcBuild.PC_ID} =$pcID",
                null
            )
            //Update PC Price
            correctFanRemovalPrices(fanCursor, pcID)
            //Trim off the fans in the PC
            if (component.type.toUpperCase(Locale.ROOT) == ComponentsEnum.HEATSINK.toString()) {
                component as Heatsink
                updateFansInPC("fan", pcID, component.fan_slots)
            } else {
                component as Case
                updateFansInPC("fan", pcID, component.case_fan_slots)
            }
            cursor.moveToNext()
        }
        cursor.close()
    }

    /**
     * Update the PC total price be removing the fans rrp price that are being trimmed off this PC.
     * @param cursor Cursor is used to iterate through and retrieve the values from the database.
     * @param pcID pcID Primary Key value to update the correct PC.
     */
    private fun correctFanRemovalPrices(
        cursor: Cursor,
        pcID: Int
    ) {
        val cv = ContentValues()
        cursor.moveToFirst()
        if (cursor.count > 0) {
            val fanNameIndex = 1
            val pcPriceIndex = 2
            //Get PC's current total price
            var totalPrice = cursor.getDouble(pcPriceIndex)
            //Update the total price in all pc's that has the component
            for (index in 0 until cursor.count) {
                val fan = componentQueries.getHardware(cursor.getString(fanNameIndex), "fan")
                //Remove the fan's rrp price from the pc
                totalPrice -= fan.rrpPrice
                //Prepare for next iteration
                cursor.moveToNext()
            }
            //Update the pc
            cv.put(FirstByteSQLConstants.PcBuild.PC_RRP_PRICE, totalPrice)
            dbHandler.update(
                FirstByteSQLConstants.PcBuild.TABLE,
                cv,
                "${FirstByteSQLConstants.PcBuild.PC_ID} =?",
                arrayOf(pcID.toString())
            )
        }
        cursor.close()
    }
}