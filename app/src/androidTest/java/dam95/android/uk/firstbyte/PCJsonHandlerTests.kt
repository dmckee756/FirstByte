package dam95.android.uk.firstbyte

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.GsonBuilder
import dam95.android.uk.firstbyte.model.PCBuild
import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.StringReader
import java.lang.Exception

class PCJsonHandlerTests {



    private fun createTestFile() {
        val PC_BUILDS_TEST = "PCBuildsTESTLIST"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        //Load the template pc build file
        try {
            val inputStream: String =
                context.assets.open(PC_BUILDS_TEST).bufferedReader().use { it.readText() }

            //Create a writable app specific json file to store up to 10 PC builds
            val newFile = File(context.filesDir, PC_BUILDS_TEST)
            val fileWriter = FileWriter(newFile)
            val bufferedWrite = BufferedWriter(fileWriter)
            bufferedWrite.write(inputStream)
            bufferedWrite.close()
            //If the file isn't found, throw an exception
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun deleteTestFile() {
        val PC_BUILDS_TEST = "PCBuildsTESTLIST"
        val context = InstrumentationRegistry.getInstrumentation().targetContext

    }

    @Test
    fun doesCreatedFileExist() {
        val PC_BUILDS_TEST = "PCBuildsTESTLIST"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        createTestFile()
        assert(File(context.filesDir, PC_BUILDS_TEST).exists())
        deleteTestFile()
    }

    @Test
    fun canIReadFromCreatedFile() {
        val PC_BUILDS_TEST = "PCBuildsTESTLIST"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        createTestFile()

        deleteTestFile()
    }

    @Test
    fun canISaveAPCBuildToCreatedFile() {
        val PC_BUILDS_TEST = "PCBuildsTESTLIST"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        createTestFile()

        deleteTestFile()
    }

    @Test
    fun canIReadTheHomeEnthusiastRecommendedPC() {
        val PC_BUILDS_TEST = "PCBuildsTESTLIST"
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        var result = false
        val inputStream = context.assets.open("RecommendedPCBuilds").bufferedReader().use { it.readText() }
        val stringReader = StringReader(inputStream)
        val gson = GsonBuilder().create()

        var pcBuild: PCBuild
        for (i in 0..10) {
            pcBuild = gson.fromJson(stringReader, PCBuild::class.java)
            if (pcBuild.pc_name == "home_enthusiast") {
                //The  "home_enthusiast" recommended pc ID = 4
                if (pcBuild.pcID == 4) result = true
            }
        }
        assert(result)
    }

}