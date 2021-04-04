package dam95.android.uk.firstbyte.junit

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.components.Case
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Cpu
import dam95.android.uk.firstbyte.model.components.Gpu
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TestDatabasePCBuilds {

    private lateinit var instrumentContext: Context
    private lateinit var inMemoryDatabase: FirstByteDBAccess
    private lateinit var gpu_1660_Ti_specifications: Gpu
    private lateinit var amd_ryzen_5_3600: Cpu
    private lateinit var cooler_Master_MasterBox_MB511: Case

    @JvmField
    @Rule
    val synchronousTaskExecution = InstantTaskExecutorRule()

    @Before
    fun initialise() {
        instrumentContext = InstrumentationRegistry.getInstrumentation().targetContext
        inMemoryDatabase = FirstByteDBAccess(instrumentContext, Dispatchers.Main)
        gpu_1660_Ti_specifications = Gpu(
            type = "gpu",
            imageLink = "https://www.scan.co.uk/images/products/super/3125547-l-a.jpg",
            name = "ASUS NVIDIA GeForce GTX 1660 Ti",
            core_speed_mhz = 1500,
            memory_size_gb = 6,
            memory_speed_mhz = 12000,
            wattage = 120,
            dimensions = "20.5x4.6x12.3",
            rrpPrice = 202.0,
            amazonPrice = null,
            amazonLink = null,
            scanPrice = 287.99,
            scanLink = "https://www.scan.co.uk/products/asus-geforce-gtx-1660-ti-tuf-gaming-oc-6gb-gddr6-vr-ready-graphics-card-1536-core"
        )

        amd_ryzen_5_3600 = Cpu(
            type = "cpu",
            imageLink = "https://images-na.ssl-images-amazon.com/images/I/71WPGXQLcLL._AC_SL1384_.jpg",
            name = "AMD Ryzen 5 3600",
            core_speed_ghz = 4.2,
            core_count = 6,
            isMultiThreaded = 1,
            cpu_socket = "AM4",
            cpu_wattage = 65,
            hasHeatsink = 1,
            rrpPrice = 199.99,
            amazonPrice = 180.0,
            amazonLink = "https://www.amazon.co.uk/dp/B07STGGQ18/ref=twister_B081TLS828?_encoding=UTF8&psc=1",
            scanPrice = 189.98,
            scanLink = "https://www.scan.co.uk/products/amd-ryzen-5-3600-am4-zen-2-6-core-12-thread-36ghz-42ghz-turbo-32mb-l3-pcie-40-65w-with-wraith-stealt"
        )

        cooler_Master_MasterBox_MB511 = Case(
            type = "cases",
            imageLink = "https://images-na.ssl-images-amazon.com/images/I/81VEEUFQ%2BmL._AC_SL1500_.jpg",
            name = "Cooler Master MasterBox MB511",
            case_fan_slots = 2,
            case_fan_sizes_mm = 120,
            case_motherboard = "ATX",
            case_dimensions = "52.3x27.6x56.8",
            rrpPrice = 105.83,
            amazonPrice = 105.83,
            amazonLink = "https://www.amazon.co.uk/Cooler-Master-MCB-B511D-KANN-S00-MasterBox-systems/dp/B07CYBQWYQ/ref=sr_1_7?dchild=1&keywords=Cooler+master+case&qid=1613168549&s=computers&sr=1-7",
            scanPrice = null,
            scanLink = null
        )

        //Insert the gpu into the database
        inMemoryDatabase.insertHardware(gpu_1660_Ti_specifications)
        //Insert the cpu into the database
        inMemoryDatabase.insertHardware(amd_ryzen_5_3600)
        //Insert the case into the database
        inMemoryDatabase.insertHardware(cooler_Master_MasterBox_MB511)
    }

    @After
    fun close() {
        inMemoryDatabase.closeDatabase()
    }


    @Test
    fun testThat_Personal_PC_IS_Saved() = runBlocking(Dispatchers.Main) {

        val newPC = PCBuild()
        newPC.pcName = "TEST_PC"
        inMemoryDatabase.createPC(newPC)
        val nameResult = inMemoryDatabase.retrievePCList().value?.get(0)?.pcName
        if (nameResult != null) {
            Log.i("EXPECTED_RESULT", "TEST_PC")
            Log.i("ACTUAL_RESULT", nameResult)
            Assert.assertEquals(
                newPC.pcName,
                nameResult
            )
        } else {
            assert(false)
        }
    }

    @Test
    fun testThat_Personal_PC_Be_Deleted() = runBlocking(Dispatchers.Main) {

        //Create 2 new pc's
        val newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        val newPC2 = PCBuild()
        newPC2.pcName = "TEST_PC_2"

        //Add both new pc's to the database
        inMemoryDatabase.createPC(newPC1)
        inMemoryDatabase.createPC(newPC2)

        //Get the list containing both pc's

        var sizeResult = inMemoryDatabase.retrievePCList().value

        if (sizeResult != null) {

            //Check if both new pc's are in the database
            Assert.assertEquals(newPC1.pcName, sizeResult[0]!!.pcName)
            Assert.assertEquals(newPC2.pcName, sizeResult[1]!!.pcName)

            //Delete the first pc "TEST_PC_1"
            inMemoryDatabase.deletePC(sizeResult[0]!!.pcID!!)
            //I put this delay here because no matter what I try, I cannot block the coroutine within deletePC.
            //I would love to know why I can't, but I can't find any research online as to why.
            delay(200)
            //Recall the database list
            sizeResult = inMemoryDatabase.retrievePCList().value

            if (sizeResult != null) {
                //Check if the "TEST_PC_2" has moved into slot one and that slot 2 is now null
                sizeResult[0]?.pcName?.let { Log.i("TEST", it) }
                Assert.assertEquals(newPC2.pcName, sizeResult[0]?.pcName)
                Assert.assertEquals(null, sizeResult[1])
            } else {
                assert(false)
            }
        } else {
            assert(false)
        }
    }

    @Test
    fun testThat_PC_Has_Hardware_Saved() = runBlocking(Dispatchers.Main) {

        //Create a new PC and insert it into the database
        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        inMemoryDatabase.createPC(newPC1)

        //Retrieve the accessible PCList which contains the new pc in slot 1
        newPC1 = inMemoryDatabase.retrievePCList().value!![0]!!

        //Save the gpu's name into the pc build
        inMemoryDatabase.savePCPart(
            gpu_1660_Ti_specifications.name,
            gpu_1660_Ti_specifications.type,
            newPC1.pcID!!
        )
        delay(100)
        //Load the single pc build from the database
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!
        //Check if the pc build has the same gpuName as the inserted gpu component
        Assert.assertEquals(gpu_1660_Ti_specifications.name, newPC1.gpuName)
    }

    @Test
    fun testThat_PC_Has_Hardware_Removed() = runBlocking(Dispatchers.Main) {

        //Create a new PC and insert it into the database
        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        inMemoryDatabase.createPC(newPC1)

        //Retrieve the accessible PCList which contains the new pc in slot 1
        newPC1 = inMemoryDatabase.retrievePCList().value!![0]!!

        //Save the gpu's name into the pc build
        inMemoryDatabase.savePCPart(
            gpu_1660_Ti_specifications.name,
            gpu_1660_Ti_specifications.type,
            newPC1.pcID!!
        )
        delay(100)
        //Load the single pc build from the database
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!
        //Check if the pc build has the same gpuName as the inserted gpu component
        Assert.assertEquals(gpu_1660_Ti_specifications.name, newPC1.gpuName)

        //Remove the gpu component from the pc build
        inMemoryDatabase.removePCPart(gpu_1660_Ti_specifications.type, newPC1.pcID!!)
        delay(100)
        //Get an updated instance of the pcBuild
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!
        delay(50)
        //Check if the gpu name was removed from the pc build
        Assert.assertEquals(null, newPC1.gpuName)
    }

    @Test
    fun testThat_Total_PC_Price_Is_Updated_On_Hardware_Add() = runBlocking(Dispatchers.Main) {

        //Create a new PC and insert it into the database
        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        inMemoryDatabase.createPC(newPC1)

        //Retrieve the accessible PCList which contains the new pc in slot 1
        newPC1 = inMemoryDatabase.retrievePCList().value!![0]!!

        //Save the gpu's name into the pc build
        inMemoryDatabase.savePCPart(
            gpu_1660_Ti_specifications.name,
            gpu_1660_Ti_specifications.type,
            newPC1.pcID!!
        )
        delay(100)
        Assert.assertEquals(0.0, newPC1.pcPrice, 0.0)
        //Load the single pc build from the database
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!
        val hardwareGpu = inMemoryDatabase.retrieveHardware(
            gpu_1660_Ti_specifications.name,
            gpu_1660_Ti_specifications.type
        )
        newPC1.pcPrice += hardwareGpu.rrpPrice

        //Update the pc price in the database
        inMemoryDatabase.updatePCPrice(newPC1.pcPrice, newPC1.pcID!!)
        delay(100)
        //Load the updated single pc build from the database
        val updatedPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!

        Assert.assertEquals(hardwareGpu.rrpPrice, updatedPC1.pcPrice, 0.0)
    }

    @Test
    fun testThat_Total_PC_Price_Is_Updated_On_Hardware_Remove() = runBlocking(Dispatchers.Main) {

        //Create a new PC and insert it into the database
        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        inMemoryDatabase.createPC(newPC1)

        //Retrieve the accessible PCList which contains the new pc in slot 1
        newPC1 = inMemoryDatabase.retrievePCList().value!![0]!!

        //Save the gpu's name into the pc build
        inMemoryDatabase.savePCPart(
            gpu_1660_Ti_specifications.name,
            gpu_1660_Ti_specifications.type,
            newPC1.pcID!!
        )

        //Load the single pc build from the database
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!

        //Get an instance of the gpu and add it's rrp price to the pcBuilds total price
        val hardwareGpu = inMemoryDatabase.retrieveHardware(
            gpu_1660_Ti_specifications.name,
            gpu_1660_Ti_specifications.type
        )
        newPC1.pcPrice += hardwareGpu.rrpPrice

        //Update the pc price in the database
        inMemoryDatabase.updatePCPrice(newPC1.pcPrice, newPC1.pcID!!)
        delay(100)
        //Load the updated single pc build from the database
        var updatedPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!
        Assert.assertEquals(hardwareGpu.rrpPrice, updatedPC1.pcPrice, 0.0)
        //Remove the gpu price before removing the pc part
        updatedPC1.pcPrice -= hardwareGpu.rrpPrice
        inMemoryDatabase.updatePCPrice(updatedPC1.pcPrice, updatedPC1.pcID!!)
        //Remove the gpu component from the pc build
        inMemoryDatabase.removePCPart(gpu_1660_Ti_specifications.type, newPC1.pcID!!)
        delay(100)
        //Get an updated instance of the updated pcBuild
        updatedPC1 = inMemoryDatabase.retrievePC(updatedPC1.pcID!!).value!!
        delay(50)
        //Check if the gpu name was removed from the pc build
        Assert.assertEquals(null, newPC1.gpuName)
        Assert.assertEquals(0.0, updatedPC1.pcPrice, 0.0)
    }

    @Test
    fun testThat_Adding_Case_To_PC_Increases_Fan_Slots() = runBlocking(Dispatchers.Main) {
        //Create a new PC and insert it into the database
        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        inMemoryDatabase.createPC(newPC1)
        //Retrieve the accessible PCList which contains the new pc in slot 1
        newPC1 = inMemoryDatabase.retrievePCList().value!![0]!!

        Assert.assertEquals(newPC1.fanList!!.size, 0)

        inMemoryDatabase.savePCPart(
            cooler_Master_MasterBox_MB511.name,
            cooler_Master_MasterBox_MB511.type,
            newPC1.pcID!!
        )
        delay(200)
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!

        val fansPair = inMemoryDatabase.retrievePCComponents(
            newPC1.pcID!!,
            ComponentsEnum.FAN.toString().toLowerCase(Locale.ROOT),
            newPC1.fanList,
            cooler_Master_MasterBox_MB511.case_fan_slots - 1
        )

        val tempList = mutableListOf<String?>()
        for (i in cooler_Master_MasterBox_MB511.case_fan_slots - 1 downTo 0) {
            fansPair[fansPair.lastIndex - i].first?.let { tempList.add(it.name) }
                ?: tempList.add(null)
        }
        newPC1.fanList = tempList

        Assert.assertEquals(cooler_Master_MasterBox_MB511.case_fan_slots, newPC1.fanList!!.size)
    }

    @Test
    fun testThat_Removing_Case_From_PC_Decreases_Fan_Slots() = runBlocking(Dispatchers.Main) {
        //Create a new PC and insert it into the database
        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        inMemoryDatabase.createPC(newPC1)
        //Retrieve the accessible PCList which contains the new pc in slot 1
        newPC1 = inMemoryDatabase.retrievePCList().value!![0]!!

        inMemoryDatabase.savePCPart(
            cooler_Master_MasterBox_MB511.name,
            cooler_Master_MasterBox_MB511.type,
            newPC1.pcID!!
        )
        delay(100)
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!

        val fansPair = inMemoryDatabase.retrievePCComponents(
            newPC1.pcID!!,
            ComponentsEnum.FAN.toString().toLowerCase(Locale.ROOT),
            newPC1.fanList,
            cooler_Master_MasterBox_MB511.case_fan_slots - 1
        )

        val tempList = mutableListOf<String?>()
        for (i in cooler_Master_MasterBox_MB511.case_fan_slots - 1 downTo 0) {
            fansPair[fansPair.lastIndex - i].first?.let { tempList.add(it.name) }
                ?: tempList.add(null)
        }
        newPC1.fanList = tempList

        Assert.assertEquals(cooler_Master_MasterBox_MB511.case_fan_slots, newPC1.fanList!!.size)

        //Remove the relational database pc part

        inMemoryDatabase.removeRelationalPCPart(
            ComponentsEnum.FAN.toString().toLowerCase(Locale.ROOT),
            newPC1.pcID!!,
            0
        )
        delay(100)
        newPC1 = inMemoryDatabase.retrievePC(newPC1.pcID!!).value!!
        Assert.assertEquals(0, newPC1.fanList!!.size)
    }

    @Test
    fun testThat_ReadOnly_PCs_Cant_Be_Loaded_In_List() = runBlocking(Dispatchers.Main) {

        var newPC1 = PCBuild()
        newPC1.pcName = "TEST_PC_1"
        newPC1.deletable = false
        inMemoryDatabase.createPC(newPC1)

        val loadedPCListSlotOne: PCBuild? = inMemoryDatabase.retrievePCList().value?.get(0)
        Assert.assertEquals(null, loadedPCListSlotOne)
    }

}