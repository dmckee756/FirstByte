package dam95.android.uk.firstbyte.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.components.*
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.lang.Exception
import java.lang.NullPointerException

private const val READ_ONLY_CPU = "ReadOnlyCpus.json"
private const val READ_ONLY_GPU = "ReadOnlyGpus.json"
private const val READ_ONLY_MOTHERBOARD = "ReadOnlyMotherboards.json"
private const val READ_ONLY_RAM = "ReadOnlyRam.json"
private const val READ_ONLY_PSU = "ReadOnlyPsus.json"
private const val READ_ONLY_STORAGE = "ReadOnlyStorage.json"
private const val READ_ONLY_CASES = "ReadOnlyCases.json"
private const val READ_ONLY_HEATSINK = "ReadOnlyHeatsinks.json"
private const val READ_ONLY_FAN = "ReadOnlyFans.json"
private const val READ_ONLY_PC_BUILDS = "ReadOnlyPCBuilds.json"

class SetupReadOnlyData(application: Application, private val context: Context) :
    AndroidViewModel(application) {

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
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    /**
     *
     */
    fun loadReadOnlyValues() {

        val gson = GsonBuilder().create()
        val fbHardwareDB = FirstByteDBAccess.dbInstance(context, Dispatchers.Main)

        //Have the gson build a mutable list of read only Components
        saveReadOnlyComponents(gson, fbHardwareDB)

        //Have the gson build a mutable list of read only PC Builds
        val loadedPCBuilds = loadFile(READ_ONLY_PC_BUILDS)
        val stringReader = StringReader(loadedPCBuilds)
        //Load all read only PC's into the database
        val readOnlyPCBuildsList: List<PCBuild> =
            gson.fromJson(stringReader, Array<PCBuild>::class.java).toMutableList()
        for (index in readOnlyPCBuildsList.indices) fbHardwareDB!!.createPC(readOnlyPCBuildsList[index])
    }

    @Throws(NullPointerException::class)
    private fun saveReadOnlyComponents(
        gson: Gson,
        fbHardwareDB: FirstByteDBAccess?
    ) {
        val componentFileList = listOf(
            READ_ONLY_CPU, READ_ONLY_GPU, READ_ONLY_MOTHERBOARD, READ_ONLY_RAM, READ_ONLY_PSU,
            READ_ONLY_STORAGE, READ_ONLY_CASES, READ_ONLY_HEATSINK, READ_ONLY_FAN
        )
        var stringReader: StringReader
        var componentList: List<Component>?
        for (index in componentFileList.indices) {
            val loadFile = loadFile(componentFileList[index]) ?: continue
            stringReader = StringReader(loadFile)
            componentList = when (componentFileList[index]) {
                READ_ONLY_CPU -> gson.fromJson(stringReader, Array<Cpu>::class.java).toList()
                READ_ONLY_GPU -> gson.fromJson(stringReader, Array<Gpu>::class.java).toList()
                READ_ONLY_MOTHERBOARD -> gson.fromJson(stringReader, Array<Motherboard>::class.java)
                    .toList()
                READ_ONLY_RAM -> gson.fromJson(stringReader, Array<Ram>::class.java).toList()
                READ_ONLY_PSU -> gson.fromJson(stringReader, Array<Psu>::class.java).toList()
                READ_ONLY_STORAGE -> gson.fromJson(stringReader, Array<Storage>::class.java)
                    .toList()
                READ_ONLY_CASES -> gson.fromJson(stringReader, Array<Case>::class.java).toList()
                READ_ONLY_HEATSINK -> gson.fromJson(stringReader, Array<Heatsink>::class.java)
                    .toList()
                READ_ONLY_FAN -> gson.fromJson(stringReader, Array<Fan>::class.java).toList()
                else -> null
            }

            componentList?.let { savedList ->
                for (component in savedList.indices)
                    fbHardwareDB!!.insertHardware(savedList[component])
            }
        }
    }
}