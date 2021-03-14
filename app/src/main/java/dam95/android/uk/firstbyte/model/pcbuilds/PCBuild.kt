package dam95.android.uk.firstbyte.model.pcbuilds

import kotlin.properties.Delegates

/**
 *
 */
class PCBuild {
    var pcName: String = "New-PC"
    var pcPrice: Double? = null
    var isPcCompleted: Boolean = false
    var gpuName: String? = null
    var cpuName: String? = null
    var ramList: List<String?>? = null
    var psuName: String? = null
    var motherboardName: String? = null
    var storageList: List<String?>? = null
    var heatsinkName: String? = null
    var caseName: String? = null
    var fanList: List<String?>? = null
    var deletable: Boolean = true

    fun getPrimitiveDetails(): List<*> {
        return listOf(
            pcName, pcPrice,
            isPcCompleted, gpuName, cpuName,
            psuName, motherboardName,
            heatsinkName, caseName, deletable
        )
    }

    fun setPrimitiveDetails(builtPCDetails: List<*>){
        pcName = builtPCDetails[0] as String
        pcPrice = builtPCDetails[1] as Double?
        isPcCompleted = builtPCDetails[2] != 0
        gpuName = builtPCDetails[3] as String?
        cpuName = builtPCDetails[4] as String?
        psuName = builtPCDetails[5] as String?
        motherboardName = builtPCDetails[6] as String?
        heatsinkName = builtPCDetails[7] as String?
        caseName = builtPCDetails[8] as String?
        deletable = builtPCDetails[builtPCDetails.lastIndex] != 0
    }
}