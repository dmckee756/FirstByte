package dam95.android.uk.firstbyte.junit

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Cpu
import dam95.android.uk.firstbyte.model.components.Gpu
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TestDatabaseHardwareComponents {

    private lateinit var instrumentContext: Context
    private lateinit var inMemoryDatabase: FirstByteDBAccess
    private lateinit var gpu_1660_Ti_specifications: Component
    private lateinit var amd_ryzen_5_3600: Component

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
    }

    @After
    fun close() {
        inMemoryDatabase.closeDatabase()
    }


    @Test
    fun testThat_Hardware_Gets_Saved() = runBlocking {
        val successfullyAddedToDatabase = 1
        inMemoryDatabase.insertHardware(gpu_1660_Ti_specifications)
        val gpuResult = inMemoryDatabase.hardwareExists("ASUS NVIDIA GeForce GTX 1660 Ti")
        Assert.assertEquals(successfullyAddedToDatabase, gpuResult)
    }

    @Test
    fun testThat_Hardware_Gets_Removed() = runBlocking {
        val successfullyAddedToDatabase = 1
        //Insert gpu into database
        inMemoryDatabase.insertHardware(gpu_1660_Ti_specifications)
        //Check to see if it was successfully added...
        var gpuResult = inMemoryDatabase.hardwareExists("ASUS NVIDIA GeForce GTX 1660 Ti")
        //If successfully added, remove it and then check if it is in the database...
        if (gpuResult == successfullyAddedToDatabase) {
            inMemoryDatabase.removeHardware("ASUS NVIDIA GeForce GTX 1660 Ti")

            gpuResult = inMemoryDatabase.hardwareExists("ASUS NVIDIA GeForce GTX 1660 Ti")
            //if hardwareExists returns 0, then the gpu doesn't exist in the database.
            Assert.assertEquals(0, gpuResult)
        } else {
            //Otherwise fail
            assert(false)
        }
    }

    @Test
    fun testThat_Saved_Category_List_Updates_After_Hardware_Removed() = runBlocking {

        inMemoryDatabase.insertHardware(gpu_1660_Ti_specifications)
        inMemoryDatabase.insertHardware(amd_ryzen_5_3600)

        var componentsDisplayList: LiveData<List<SearchedHardwareItem>>? =
            inMemoryDatabase.retrieveCategory("all")

        if (componentsDisplayList != null && componentsDisplayList.value?.size == 2) {
            inMemoryDatabase.removeHardware("AMD Ryzen 5 3600")
            delay(200)

            componentsDisplayList =
                inMemoryDatabase.retrieveCategory("all")

            if (componentsDisplayList != null) {
                Assert.assertEquals(1, componentsDisplayList.value!!.size)
            } else {
                assert(false)
            }
        } else {
            assert(false)
        }
    }


    @Test
    fun testThat_Saved_Category_List_Responds_To_Searching() =
        runBlocking { //TODO Rework with new system
            inMemoryDatabase.insertHardware(gpu_1660_Ti_specifications)
            inMemoryDatabase.insertHardware(amd_ryzen_5_3600)

            var componentsDisplayList: LiveData<List<SearchedHardwareItem>>? =
                inMemoryDatabase.retrieveCategory("all")

            Assert.assertEquals(1, componentsDisplayList!!.value!!.size)
            Assert.assertEquals("AMD Ryzen 5 3600", componentsDisplayList.value!![0].name)


            assert(false)

        }
}