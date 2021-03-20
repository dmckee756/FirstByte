package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam95.android.uk.firstbyte.datasource.util.tables.components.ComponentHandler
import dam95.android.uk.firstbyte.datasource.util.tables.pcbuilds.PCBuildHandler
import dam95.android.uk.firstbyte.datasource.util.FK_ON
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val PC_ID_COLUMN = 0
const val WRITABLE_DATA = 1
const val MAX_PC_LIST_SIZE = 10
const val NULL_RES = 0x00000000
const val INTEGER_RES = 0x00000001
const val FLOAT_RES = 0x00000002
const val STRING_RES = 0x00000003

/**
 *
 */
class ComponentDBAccess(context: Context) {

    private val database: FirstByteDBHandler = FirstByteDBHandler(context)
    private val dbHandler: SQLiteDatabase = database.writableDatabase
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    private val componentQueries: ComponentHandler = ComponentHandler(dbHandler)
    private val pcBuildQueries: PCBuildHandler = PCBuildHandler(componentQueries ,dbHandler)

    init {
        dbHandler.execSQL(FK_ON)
    }

    companion object Static {
        private var dbController: ComponentDBAccess? = null

        /**
         *
         */
        fun dbInstance(context: Context): ComponentDBAccess? {
            when (dbController) {
                null -> {
                    dbController = ComponentDBAccess(context)
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
        coroutineScope.launch {
            componentQueries.insertHardware(component)
        }
    }

    /**
     * Remove hardware from the database
     */
    fun removeHardware(name: String) {
        coroutineScope.launch {
            componentQueries.removeHardware(name)
        }
    }

    /**
     *
     */
    fun retrieveHardware(hardwareName: String, hardwareType: String): Component  = componentQueries.getHardware(hardwareName, hardwareType)

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
        coroutineScope.launch {
            pcBuildQueries.savePCPart(name, type, pcID)
        }
    }

    /**
     * Moves request to pcBuildQueries, which will remove a component from the pc table or relational table.
     *
     * @param type string value which determines the type of pc part to remove.
     * @param partName a string? value which determines the name of a relational pc part to remove.
     * @param pcID an int? value determining which pc build in a relational table will be altered.
     */
    fun removePCPart(type: String, partName: String?, pcID: Int?) {
        coroutineScope.launch {
            pcBuildQueries.removePCPart(type, partName, pcID)
        }
    }

    /**
     *
     */
    fun trimFanList(type: String, pcID: Int, numberOfFans: Int){
        coroutineScope.launch {
            pcBuildQueries.updateFansInPC(type, pcID, numberOfFans)
        }
    }

    /**
     *
     */
    fun updatePCPrice(newPrice: Double, pcID: Int){
        coroutineScope.launch {
            pcBuildQueries.updatePCTotalPrice(newPrice, pcID)
        }
    }

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
}