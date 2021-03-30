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
 * This is the handler class for the in-memory database for the FirstByte app.
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

    companion object Static {
        private var dbController: FirstByteDBAccess? = null

        /**
         *
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
     *
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
        coroutineScope.launch(coroutineDispatcher) {
            componentQueries.removeHardware(name)
        }
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
    suspend fun hardwareExists(name: String): Int = componentQueries.hardwareExists(name)

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
    fun createPC(personalPC: PCBuild): Int = pcBuildQueries.createPersonalPC(personalPC)

    /**
     *
     */
    fun deletePC(pcID: Int) {
        coroutineScope.launch {
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
    fun removeRelationalPCPart(type: String, pcID: Int, relativePos: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pcBuildQueries.removeRelationalPCPart(type, pcID, relativePos)
        }
    }

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
     * Retrieve the desired compared components list from the database and return the result to the caller.
     *
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     */
    fun retrieveComparedComponents(typeID: String): List<String> = componentQueries.retrieveComparedList(typeID)

    /**
     * Save the altered list to the correct compared components table.
     *
     * @param typeID the unique name given to the compared table, indicating what type of component references are stored in it.
     * @param savedComponents the altered/updated list being saved to the compared table
     */
    fun saveComparedComponents(typeID: String, savedComponents: List<String>)= componentQueries.updateComparedList(typeID, savedComponents)
}