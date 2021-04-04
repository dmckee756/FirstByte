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

/**
 * @author David Mckee
 * @Version 1.0
 * During the app's first time setup and before the user can do anything, load in all Read Only values into the database.
 * These values are several components of each category that are used in the 12 recommended pc builds.
 * None of these values can be deleted from the app. This class is only called once, during the first time setup.
 *
 * @param application Application is passed to the AndroidViewModel parent class
 * @param context Context is used to open the assets folder and retrieve all json files holding ReadOnly Components and PC's
 */
class SetupReadOnlyData(application: Application, private val context: Context) :
    AndroidViewModel(application) {


    /**
     * Load in all ReadOnly component values from the json files in assets and load them into the database.
     * After the components are saved into the database, load the recommended PC json file and saved them into the database.
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

    /**
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
     * Reads in each Read Only component json file and loads them into the database.
     * @param gson Gson parsing
     * @param fbHardwareDB Instance of the app's SQLite Database
     */
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

        //Just like in the API process, each Component Object have to be loaded nad parsed separately.
        //For each ReadOnly json component file load the correct file and determine the type of component that is being held within the json file.
        //Once identified, parse the json objects to the correct component object as a list and then send it over to be added into the database.
        //Currently this loops 9 times.
        for (index in componentFileList.indices) {
            //Find file, if file can't be found/doesn't exist then it skips this loop.
            val loadFile = loadFile(componentFileList[index]) ?: continue

            //Find and parse correct object list
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

            //If a component list was successfully loaded and parsed, then load all of it's components into the database.
            componentList?.let { savedList ->
                for (component in savedList.indices)
                    fbHardwareDB!!.insertHardware(savedList[component])
            }
        }
    }
}