package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.model.tables.components.ComponentHandler
import dam95.android.uk.firstbyte.model.tables.pcbuilds.PCBuildHandler
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.tables.FK_ON
import dam95.android.uk.firstbyte.model.tables.FirstByteSQLConstants
import kotlinx.coroutines.*

const val PC_ID_COLUMN = 0
const val WRITABLE_DATA = 1
const val MAX_PC_LIST_SIZE = 10
const val NULL_RES = 0x00000000
const val INTEGER_RES = 0x00000001
const val FLOAT_RES = 0x00000002
const val STRING_RES = 0x00000003
private const val IS_DELETABLE = 1
/**
 *
 */
class FirstByteDBAccess(
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    private val database: FirstByteDBHandler = FirstByteDBHandler(context)
    private val dbHandler: SQLiteDatabase = database.writableDatabase
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    private val componentQueries: ComponentHandler = ComponentHandler(dbHandler)
    private val pcBuildQueries: PCBuildHandler = PCBuildHandler(componentQueries, dbHandler)

    init {
        dbHandler.execSQL(FK_ON)
    }

    companion object Static {
        private var dbController: FirstByteDBAccess? = null

        /**
         *
         */
        fun dbInstance(context: Context, coroutineDispatcher: CoroutineDispatcher): FirstByteDBAccess? {
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
     *
     */
    fun closeDatabase() {
        dbHandler.close()
        Log.i("DATABASE_CLOSED", "Database closed")
        dbController = null
    }

    /**
     *
     */
    fun insertHardware(component: Component) {
        coroutineScope.launch(coroutineDispatcher) {
            componentQueries.insertHardware(component)
        }
    }

    /**
     * Remove hardware from the database
     */
    fun removeHardware(name: String) {
        componentQueries.removeHardware(name)
    }

    /**
     *
     */
    fun retrieveHardware(hardwareName: String, hardwareType: String): Component =
        componentQueries.getHardware(hardwareName, hardwareType)

    /**
     *
     */
    fun retrieveImageURL(componentName: String): String? =
        componentQueries.retrieveImageURL(componentName)

    /**
     *
     */
    fun hardwareExists(name: String): Int = componentQueries.hardwareExists(name)

    /**
     *
     */
    suspend fun retrieveCategorySearch(
        category: String,
        searchQuery: String
    ): LiveData<List<SearchedHardwareItem>>? =
        componentQueries.getCategorySearch(category, searchQuery)

    /**
     *
     */
    suspend fun retrieveCategory(category: String): LiveData<List<SearchedHardwareItem>>? =
        componentQueries.getCategory(category)

    /**
     *
     */
    fun checkIfComponentIsAnyPC(componentName: String, categoryType: String): Int = pcBuildQueries.isHardwareInBuilds(componentName, categoryType)

    /**
     *
     */
    fun removeComponentFromAllPCs(componentName: String, categoryType: String, rrpPrice: Double) = pcBuildQueries.removeHardwareFromBuilds(componentName, categoryType, rrpPrice)

    /**
     *
     */
    fun createPC(personalPC: PCBuild): Int = pcBuildQueries.createPersonalPC(personalPC)

    /**
     *
     */
    fun deletePC(pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.deletePC(pcID)
        }
    }

    /**
     *
     */
    fun savePCPart(name: String, type: String, pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.savePCPart(name, type, pcID)
        }
    }

    /**
     * Moves request to pcBuildQueries, which will remove a component from the pc table or relational table.
     *
     * @param type string value which determines the type of pc part to remove.
     * @param pcID an int value determining which pc build will be altered.
     */
    fun removePCPart(type: String, pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.removePCPart(type, pcID)
        }
    }

    /**
     * Moves request to pcBuildQueries, which will remove a component from the pc table or relational table.
     *
     * @param type string value which determines the type of pc part to remove.
     * @param partName a string value which determines the name of a relational pc part to remove.
     * @param pcID an int value determining which pc build in a relational table will be altered.
     */
    fun removeRelationalPCPart(type: String, pcID: Int, relativePos: Int) = pcBuildQueries.removeRelationalPCPart(type,  pcID, relativePos)
    /**
     *
     */
    fun trimFanList(type: String, pcID: Int, numberOfFans: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.updateFansInPC(type, pcID, numberOfFans)
        }
    }

    /**
     *
     */
    fun updatePCPrice(newPrice: Double, pcID: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.updatePCTotalPrice(newPrice, pcID)
        }
    }

    /**
     *
     */
    fun changePCName(pcID: Int, pcName: String) = pcBuildQueries.updatePCName(pcID, pcName)

    /**
     *
     */
    fun pcUpdateCompletedValue(pc: PCBuild) = pcBuildQueries.pcUpdateIsCompleted(pc)

    /**
     *
     */
    fun retrievePC(pcID: Int): MutableLiveData<PCBuild> = pcBuildQueries.loadPersonalPC(pcID)

    /**
     *
     */
    fun retrievePCList(): LiveData<List<PCBuild?>> = pcBuildQueries.loadPersonalPCList()

    /**
     *
     */
    fun retrievePCComponents(
        pcID: Int,
        componentType: String,
        componentNameList: List<String?>?,
        numberOfComponents: Int
    ): List<Pair<Component?, String>> =
        pcBuildQueries.loadComponentInPc(
            pcID,
            componentType,
            componentNameList,
            numberOfComponents
        )

    /**
     * Create a new compared components list which holds up to 5 unique component references for data retrieval.
     *
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     */
    fun createComparedComponents(typeID: String) = componentQueries.createComparedList(typeID)

    /**
     *
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     */
    fun checkIfComparedTableExists(typeID: String): Int = componentQueries.doesComparedListExist(typeID)

    /**
     * Retrieve the desired compared components list from the database and return the result to the caller.
     *
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     */
    fun retrieveComparedComponents(typeID: String): List<String?> = componentQueries.retrieveComparedList(typeID)

    fun checkIfComponentIsInComparedTable(componentName: String) : Int = componentQueries.isComponentInComparedTable(componentName)

    /**
     * Save the altered list to the correct compared components table.
     *
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     * @param savedComponent a reference name of the component that will be compared.
     */
    fun saveComparedComponent(typeID: String, savedComponent: String) = componentQueries.saveComparedComponent(typeID, savedComponent)

    /**
     *
     */
    fun removeComparedComponent(componentName: String) = componentQueries.deleteComparedComponent(componentName)

    /**
     * Delete all records from the database, used as a factory data reset.
     */
    fun resetDatabase(){
        dbHandler.delete(FirstByteSQLConstants.Components.TABLE, "${FirstByteSQLConstants.Components.IS_DELETABLE} =$IS_DELETABLE", null)
        dbHandler.delete(FirstByteSQLConstants.PcBuild.TABLE,"${FirstByteSQLConstants.PcBuild.PC_IS_DELETABLE} =$IS_DELETABLE", null)
    }
}