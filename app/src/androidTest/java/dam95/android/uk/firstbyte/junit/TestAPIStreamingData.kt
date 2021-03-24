package dam95.android.uk.firstbyte.junit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dam95.android.uk.firstbyte.api.ApiRepository
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Gpu
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TestAPIStreamingData {
    private lateinit var instrumentContext: Context
    private lateinit var apiRepository: ApiRepository

    @JvmField
    @Rule
    val synchronousTaskExecution = InstantTaskExecutorRule()


    @Before
    fun initialise() = runBlocking{
        instrumentContext = InstrumentationRegistry.getInstrumentation().context
        apiRepository = ApiRepository(instrumentContext)
    }

    @Test
    fun testThat_All_Hardware_Is_Displayed_In_Category_All_Search() = runBlocking {
        val sizeOfTestResponse = 37

        val allList: List<SearchedHardwareItem>? = apiRepository.repoGetCategory("all").body()
        Log.i("EXPECTED_RESULT", "$sizeOfTestResponse")
        if (allList != null) {

            Log.i("ACTUAL_RESULT", "$allList.size")
            Assert.assertEquals(sizeOfTestResponse, allList.size)
        } else {
            Log.i("ACTUAL_RESULT", "Response not found : NULL")
            assert(false)
        }
    }

    @Test
    fun testThe_search_Category_functionality_by_searching_for_a_AMD_components() = runBlocking {
        val desiredListResult = listOf<SearchedHardwareItem>(
            SearchedHardwareItem(
                "AMD Ryzen 3 3300X",
                "cpu",
                "https://images-na.ssl-images-amazon.com/images/I/71qJkCH4EnL._AC_SL1384_.jpg",
                110.0
            ),
            SearchedHardwareItem(
                "AMD Ryzen 5 3600",
                "cpu",
                "https://images-na.ssl-images-amazon.com/images/I/71WPGXQLcLL._AC_SL1384_.jpg",
                199.99
            ),
            SearchedHardwareItem(
                "AMD Ryzen 9 3950X",
                "cpu",
                "https://m.media-amazon.com/images/I/61LQ1nNL-eL._AC_SL1198_.jpg",
                749.0
            ),
            SearchedHardwareItem(
                "ASUS PRIME B550-PLUS AMD B550",
                "motherboard",
                "https://images-na.ssl-images-amazon.com/images/I/91pZCvtUsOL._AC_SL1500_.jpg",
                156.48
            ),
        )

        val amdList: List<SearchedHardwareItem>? =
            apiRepository.repoSearchCategory("all", "AMD").body()

        if (amdList != null) {
            for (i in desiredListResult.indices) {
                Log.i("EXPECTED_RESULT", desiredListResult[i].toString())
                Log.i("ACTUAL_RESULT", amdList[i].toString())
                Assert.assertEquals(desiredListResult[i], amdList[i])
            }
        } else {
            Log.i("ACTUAL_RESULT", "Response not found : NULL")
            assert(false)
        }
    }

    @Test
    fun testThat_Specific_Storage_Category_Is_Displayed() = runBlocking {
        val desiredStorageList = listOf<SearchedHardwareItem>(
            SearchedHardwareItem(
                "Samsung 870 QVO 1 TB SATA 2.5",
                "storage",
                "https://images-na.ssl-images-amazon.com/images/I/91S1PIX%2ByWL._AC_SL1500_.jpg",
                91.99
            ),
            SearchedHardwareItem(
                "Samsung 870 QVO 2 TB SATA 2.5",
                "storage",
                "https://images-na.ssl-images-amazon.com/images/I/91S1PIX%2ByWL._AC_SL1500_.jpg",
                182.99
            ),
            SearchedHardwareItem(
                "Seagate BarraCuda 1TB 3.5\" SATA III",
                "storage",
                "https://www.scan.co.uk/images/products/super/2798059-l-a.jpg",
                35.51
            ),
            SearchedHardwareItem(
                "Seagate BarraCuda 2 TB Hard Drive 3.5\" SATA III",
                "storage",
                "https://images-na.ssl-images-amazon.com/images/I/71Czt9ypIbL._AC_SL1500_.jpg",
                48.0
            ),
            SearchedHardwareItem(
                "Seagate Expansion STEA1000400",
                "storage",
                "https://www.scan.co.uk/images/products/super/2951920-l-a.jpg",
                50.0
            ),
        )

        val storageList: List<SearchedHardwareItem>? =
            apiRepository.repoGetCategory("storage").body()

        if (storageList != null) {
            for (i in desiredStorageList.indices) {
                Log.i("EXPECTED_RESULT", desiredStorageList[i].toString())
                Log.i("ACTUAL_RESULT", storageList[i].toString())
                Assert.assertEquals(desiredStorageList[i], storageList[i])
            }
        } else {
            Log.i("ACTUAL_RESULT", "Response not found : NULL")
            assert(false)
        }
    }

    @Test
    fun testThat_Hardware_Detail_Of_GPU_NVIDIA_1660_Ti_Is_Displayed() = runBlocking {
        val gpu_1660_Ti_specifications: Component = Gpu(
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

        val gpuResult: List<Component>? =
            apiRepository.repoGetGpu("ASUS NVIDIA GeForce GTX 1660 Ti").body()

        if (gpuResult != null) {
            //Sadly I have to manually assign all incoming components as deletable = true.
            //This is due to the GSON converter not fully understanding Kotlin...
            //... and it doesn't understand kotlin initializer's. In the future, I would migrate to a different parsing method.
            gpuResult[0].deletable = true
            val gpuResultSpecifications = gpuResult[0].getDetails()

            for (i in (gpu_1660_Ti_specifications.getDetails().indices - 1)) {
                Log.i("EXPECTED_RESULT", gpu_1660_Ti_specifications.getDetails()[i].toString())
                Log.i("ACTUAL_RESULT", gpuResultSpecifications[i].toString())

                Assert.assertEquals(
                    gpu_1660_Ti_specifications.getDetails()[i].toString(),
                    gpuResultSpecifications[i].toString()
                )
            }
        } else {
            Log.i("ACTUAL_RESULT", "Response not found : NULL")
            assert(false)
        }

    }
}