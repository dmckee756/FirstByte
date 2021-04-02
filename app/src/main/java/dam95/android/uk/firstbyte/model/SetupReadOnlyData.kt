package dam95.android.uk.firstbyte.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.view.KeyEventDispatcher
import androidx.lifecycle.AndroidViewModel
import com.google.gson.GsonBuilder
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.components.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.lang.Exception

private const val READ_ONLY_COMPONENTS = "ReadOnlyComponents"
private const val READ_ONLY_PC_BUILDS = "ReadOnlyPCBuilds"
class SetupReadOnlyData(application: Application, private val context: Context) : AndroidViewModel(application) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     *
     * Load either the read only recommended pc builds or read only components.
     * This is done at the initial launch of the app and will be stored within the database with deletable values set to 0.
     * @throws IOException if the file cannot be found, then throw missing file exception with a stack trace
     * @param fileName the desired json file that will be loaded
     * @return the result of the loaded in file in text format, or null if file was not found.
     */
    @Throws(IOException::class)
    fun loadFile(fileName: String): String?{
        return try {
            //Load file if found
            val inputStream: InputStream = context.assets.open(fileName)
            //Put file into a string format
            inputStream.bufferedReader().use { it.readText() }
            //If file not found
        } catch (exception: Exception){
            exception.printStackTrace()
           null
        }
    }

    /**
     *
     */
    fun loadReadOnlyValues() {

        val loadedComponents = loadFile(READ_ONLY_COMPONENTS)
        val loadedPCBuilds = loadFile(READ_ONLY_PC_BUILDS)

        val gson = GsonBuilder().create()
        val fbHardwareDB = FirstByteDBAccess.dbInstance(context, Dispatchers.Main)

        //Have the gson build a mutable list of read only Components
        var stringReader = StringReader(loadedComponents)
        val readOnlyComponentsList: List<Component> = gson.fromJson(stringReader, Array<Component>::class.java).toMutableList()
        //Load all read only components into the database
        for (index in readOnlyComponentsList.indices) fbHardwareDB!!.insertHardware(readOnlyComponentsList[index])

        //Have the gson build a mutable list of read only PC Builds
        stringReader = StringReader(loadedPCBuilds)
        //Load all read only PC's into the database
        val readOnlyPCBuildsList: List<PCBuild> = gson.fromJson(stringReader, Array<PCBuild>::class.java).toMutableList()
        coroutineScope.launch {
            for (index in readOnlyPCBuildsList.indices) fbHardwareDB!!.createPC(readOnlyPCBuildsList[index])
        }
    }
}