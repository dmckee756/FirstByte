package dam95.android.uk.firstbyte.datasource

import android.app.Application

/**
 *
 */
class FirstByteRepository(application: Application) {
   /* private val pcBuildList = PersonalPCListInjection.getPCDatabase(application).pcBuildDao()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    //PCBuilds Database Commands
    /**
     *
     */
    fun insertNewPCBuild(pc: PCbuild){
        coroutineScope.launch {
            pcBuildList.insertBuild(pc)
        }
    }

    /**
     *
     */
    fun deletePC(pc: PCbuild){
        coroutineScope.launch {
            pcBuildList.deleteBuild(pc)
        }
    }

    /**
     *
     */
    fun clearAllPCs(){
        coroutineScope.launch {
            pcBuildList.deleteAllBuilds()
        }
    }

    /**
     *
     */
    fun retrieveAllPCs() = pcBuildList.retrieveAllPCs()
*/
}