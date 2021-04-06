package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.model.tables.FK_ON
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.tables.FirstByteSQLConstants
import dam95.android.uk.firstbyte.model.tables.components.ComponentHandler
import dam95.android.uk.firstbyte.model.tables.pcbuilds.PCBuildHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val PC_ID_COLUMN = 0
const val WRITABLE_DATA = 1
const val MAX_PC_LIST_SIZE = 10
const val NULL_RES = 0x00000000
const val INTEGER_RES = 0x00000001
const val FLOAT_RES = 0x00000002
const val STRING_RES = 0x00000003
private const val DATABASE_NAME = "FB_TEST_DATABASE"

/**
 * @author David Mckee
 * @Version 1.0
 * This is the handler class for the in-memory database for the FirstByte app.
 * All SQLite database commands pass through this class to either the PCBuildHandler SQLite query class or the Component Handler SQLite query class.
 * @param context Application context
 * @param coroutineDispatcher Injected CoroutineDispatcher, allows developer to change the thread that handles some interactions with the database.
 */
class FirstByteDBAccess(
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val database: FirstByteDBHandler = FirstByteDBHandler(context)
    private val dbHandler: SQLiteDatabase = database.writableDatabase
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    private val componentQueries: ComponentHandler = ComponentHandler(dbHandler)
    private val pcBuildQueries: PCBuildHandler = PCBuildHandler(componentQueries, dbHandler)

    init {
        dbHandler.execSQL(FK_ON)
    }

    /**
     * This "Static" object ensures only one dbInstance is ever created in the app and is shared across all object/references of this DB Access class
     */
    companion object Static {
        private var dbController: FirstByteDBAccess? = null

        /**
         * If there is no current instance of FirstByteDBAccess, create it here and pass in the values needed to allow app and SQLite database communication.
         * @param context Passes the context into this DBAccess class
         * @param coroutineDispatcher Inject a chosen Dispatcher into the coroutine scopes of this DB Access class
         * @return An instance of the FirstByteDBAccess class, allowing SQLite interaction.
         */
        fun dbInstance(
            context: Context,
            coroutineDispatcher: CoroutineDispatcher
        ): FirstByteDBAccess? {
            when (dbController) {
                null -> {
                    dbController = FirstByteDBAccess(context, coroutineDispatcher)
                }
                else -> dbController
            }
            return dbController
        }
    }


    /**
     * When the app is finished with the database, close it...
     * But in this version the database is completely deleted. To simulate in memory.
     */
    fun closeDatabase() {
        dbHandler.close()
        // Since this is actually not in memory, when we close the database on the app's exit,
        // delete the database, this way we simulate an in-memory database without any bugs of
        // fragments not seeing tables in the database.
        context.deleteDatabase(DATABASE_NAME)
        Log.i("DATABASE_CLOSED", "Database closed")
        dbController = null
    }

    /**
     * Find the correct columns/tables that the component will be saved into and then adds the component into the database.
     * @param component the component object being saved into the database.
     */
    fun insertHardware(component: Component) {
        componentQueries.insertHardware(component)
    }

    /**
     * Removes the hardware from the database by deleting it's Primary Key in the component table, initiating SQL's cascade delete.
     * @param name The name/key of the component being deleted.
     */
    fun removeHardware(name: String) {
        componentQueries.removeHardware(name)
    }

    /**
     * Retrieves a component from the database, builds it and then returns it to the caller.
     * Will find the type of component being loaded, create a template and then load the values into said template.
     * @param hardwareName component name that is being loaded.
     * @param hardwareType the category of component that is being loaded.
     * @return The rebuilt loaded child component object.
     */
    fun retrieveHardware(hardwareName: String, hardwareType: String): Component =
        componentQueries.getHardware(hardwareName, hardwareType)

    /**
     * Check the database component table if a component exists.
     * @param name name of component
     * @return Returns 1 if component exists, 0 if it doesn't
     */
    fun hardwareExists(name: String): Int = componentQueries.hardwareExists(name)

    /**
     * Queries the database to retrieve all display information of the desired category. This can be all components or a specific type of component.
     * I.e Load only Gpu's for display, or load all Components for display.
     * @param category Indicated if all display component items should be loaded if it has a value of "all" or if a specific category is to be loaded e.g. "cpu"
     * @return LiveData instance of a SearchedHardwareItem List
     */
    suspend fun retrieveCategory(category: String): LiveData<List<SearchedHardwareItem>>? = componentQueries.getCategory(category)


    /**
     * Retrieve a saved component's URL. Used in cases where we only want the URL and not wanting to waste time loading the rest of the component.
     * @param componentName Name of the component that's URL is being retrieved.
     * @return URL of the component's image, that will be converted into an image using Picasso.
     */
    fun retrieveImageURL(componentName: String): String? =
        componentQueries.retrieveImageURL(componentName)

    /**
     * Create a new compared components list which holds up to 5 unique component references for data retrieval.
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     */
    fun createComparedComponents(typeID: String) = componentQueries.createComparedList(typeID)

    /**
     * Check if the compared identifier has been created yet, if not then inform the caller and create the identifier table.
     * @param typeID The component Category that is used in the compare table's Primary Key
     * @return If the compared key exist/has been created then return 1, otherwise return 0
     */
    fun checkIfComparedTableExists(typeID: String): Int = componentQueries.doesComparedListExist(typeID)

    /**
     * Retrieve the desired compared components list from the database and return the result to the caller.
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     * @return a list of the names referencing compared objects, or a list size of 5 nulls if there are no components being compared.
     */
    fun retrieveComparedComponents(typeID: String): List<String?> = componentQueries.retrieveComparedList(typeID)

    /**
     * This method is called when a component is being removed from the database,
     * this way we avoid cascade deleting from potentially removing clumps of data that is not relevant to the component.
     * If the component is being compared, notify the caller, otherwise return 0 and ignore.
     *
     * @param componentName The name of a specific component that is being removed from the database
     * @return If the component is being compared then return 1, otherwise return 0
     */
    fun checkIfComponentIsInComparedTable(componentName: String) : Int = componentQueries.isComponentInComparedTable(componentName)

    /**
     * Save the altered list to the correct compared components table.
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     * @param savedComponent a reference name of the component that will be compared.
     */
    fun saveComparedComponent(typeID: String, savedComponent: String) = componentQueries.saveComparedComponent(typeID, savedComponent)

    /**
     * Remove the component from the compared table in the database.
     * @param componentName the removed components name.
     */
    fun removeComparedComponent(componentName: String) = componentQueries.deleteComparedComponent(componentName)

    /**
     * Create a new blank pc and add it tp the database. If there are already 10 Created Personal PC's (Writable PC's) then do not create the PC.
     * @param personalPC A new blank pc
     * @return Returns a value of -1 if it can't be created or there was an error, otherwise 0+
     */
    fun createPC(personalPC: PCBuild): Int = pcBuildQueries.createPersonalPC(personalPC)

    /**
     * Remove a PC from the database, cascade delete will remove all relational pc parts [RAM, STORAGE, FAN] in the many-to-many tables.
     * @param pcID PC Primary Key ID
     */
    fun deletePC(pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.deletePC(pcID)
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
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.savePCPart(name, type, pcID)
        }
    }

    /**
     * Remove a pc part from the currently loaded pc build. This pc part is a named reference to a component record.
     * @param type string value which determines the type of pc part to remove.
     * @param pcID Primary Key value determining the pc that will have the component removed from.
     */
    fun removePCPart(type: String, pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.removePCPart(type, pcID)
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
    fun removeRelationalPCPart(type: String, pcID: Int, relativePos: Int) = pcBuildQueries.removeRelationalPCPart(type,  pcID, relativePos)


    /**
     * Trims off an overflow amount of fan slots from the PC whenever a heatsink or case is removed from the PC Build.
     * If there are fans in these slots, they just get removed. It only removes n amount of fans from the end of the list.
     * @param type identifies what many-to-many relationship table to remove the pc part from.
     * @param pcID pcID Primary Key value determining which pc build in a many-to-many relational table will be altered.
     * @param numberOfFans Number of fans that are in the PC.
     */
    fun trimFanList(type: String, pcID: Int, numberOfFans: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.updateFansInPC(type, pcID, numberOfFans)
        }
    }

    /**
     * Updates the Current PC's total price when hardware is added or removed from it.
     * @param newPrice new total price value of the PC Build.
     * @param pcID pcID Primary Key value to update the correct PC.
     */
    fun updatePCPrice(newPrice: Double, pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.updatePCTotalPrice(newPrice, pcID)
        }
    }

    /**
     * Updates the PC's name when the user changes it.
     * @param pcID pcID Primary Key value to update the correct PC.
     * @param pcName new name given to the PC.
     */
    fun changePCName(pcID: Int, pcName: String) = pcBuildQueries.updatePCName(pcID, pcName)

    /**
     * Updates if the PC is functional/completed.
     * @param pc The most recent Personal PC Build the user interacted with.
     */
    fun pcUpdateCompletedValue(pc: PCBuild) = pcBuildQueries.pcUpdateIsCompleted(pc)

    /**
     * Load a specific writable Personal PC Build from the database and use it in the PersonalBuild Fragment.
     * @param pcID pcID Primary Key value to update the correct PC.
     * @return A MutableLiveData instance of a PCBuild
     */
    fun retrievePC(pcID: Int): MutableLiveData<PCBuild> = pcBuildQueries.loadPersonalPC(pcID)

    /**
     * Loads a list that has 10 slots of PCBuilds. For any slots that do not have a PCBuild, it will contain a null.
     * This allows the recycler list to display the slot and have the user create a new pc by pressing on the "empty" slot.
     * @return LiveData PCBuild? List. The list has 10 slots, and will only either contain a PCBuild, or a null.
     */
    fun retrievePCList(): LiveData<List<PCBuild?>> = pcBuildQueries.loadPersonalPCList()

    /**
     * Loads the name of each component assigned to a PC that is in the PC build many-to-many relationship table.
     * It then places the loaded component name into a pair along with an identifier category tag, which identifies the slot.
     *
     * @param pcID the ID of the PC that is currently being loaded.
     * @param componentType type of component that this method is loading.
     * @param componentNameList current list of component names in the PCBuild's list value E.G. [RAM, STORAGE, FAN] list.
     * @param numberOfComponents number of components/slots that is being loaded/created.
     * @return a Paired List of component names and their category identifiers.
     */
    fun retrievePCComponents(
        pcID: Int,
        componentType: String,
        componentNameList: List<String?>?,
        numberOfComponents: Int
    ): List<Pair<Component?, String>> =
        pcBuildQueries.loadRelationalComponentInPc(
            pcID,
            componentType,
            componentNameList,
            numberOfComponents
        )

    /**
     * Check if a Writable Component is a part of any PC in the database and return n > 0 if true.
     * This is used to check if components need removed from PC's before the component is removed from the database.
     * @param componentName Searched component name.
     * @param categoryType determines what table is to be checked.
     * @return 0 if component is not in any PC, n > 0 if the component is in PC(s).
     */
    fun checkIfComponentIsInAnyPC(componentName: String, categoryType: String): Int = pcBuildQueries.isHardwareInBuilds(componentName, categoryType)

    /**
     * When the user removes a Component from the database (From Hardware Details Fragment), then also remove the same PC part from any PC
     * before the app deletes the Component. When removing the PC part(s), update the pricing of the PC the part(s) belonged to.
     *
     * @param componentName the soon to be deleted Component/PC Part.
     * @param categoryType what category the Component/PC Part is.
     * @param rrpPrice the rrpPrice of the Component/PC Part being removed, which will be taken away from the PCBuild(s) n amount of times.
     */
    fun removeComponentFromAllPCs(componentName: String, categoryType: String, rrpPrice: Double) = pcBuildQueries.removeHardwareFromBuilds(componentName, categoryType, rrpPrice)

    /**
     * Delete all records from the database excluding the Read Only Components and Recommended builds.
     * Only executed when the user presses ok/yes when resetting data in the settings fragment.
     */
    fun resetDatabase(){
        dbHandler.delete(FirstByteSQLConstants.Components.TABLE, "${FirstByteSQLConstants.Components.IS_DELETABLE} =$WRITABLE_DATA", null)
        dbHandler.delete(FirstByteSQLConstants.PcBuild.TABLE,"${FirstByteSQLConstants.PcBuild.PC_IS_DELETABLE} =$WRITABLE_DATA", null)
    }
}