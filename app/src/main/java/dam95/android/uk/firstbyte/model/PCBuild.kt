package dam95.android.uk.firstbyte.model

import android.os.Parcel
import android.os.Parcelable
import dam95.android.uk.firstbyte.model.components.Component


/**
 *
 */
class PCBuild() : Parcelable {
    var pcID: Int? = null
    var pcName: String = "New-PC"
    var pcPrice: Double = 0.00
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

    constructor(parcel: Parcel) : this() {
        pcID = parcel.readInt()
        pcName = parcel.readString().toString()
        pcPrice = parcel.readDouble()
        isPcCompleted = parcel.readBoolean()
        gpuName = parcel.readString()
        cpuName = parcel.readString()
        ramList = parcel.createStringArrayList()
        psuName = parcel.readString()
        motherboardName = parcel.readString()
        storageList = parcel.createStringArrayList()
        heatsinkName = parcel.readString()
        caseName = parcel.readString()
        fanList = parcel.createStringArrayList()
        deletable = parcel.readBoolean()
    }

    /**
     *
     */
    fun getPrimitiveDetails(): List<*> {
        return listOf(
            pcID, pcName, pcPrice,
            isPcCompleted, gpuName, cpuName,
            psuName, motherboardName,
            heatsinkName, caseName, deletable
        )
    }

    /**
     *
     */
    fun setPrimitiveDetails(readInData: List<*>) {
        pcID = readInData[0] as Int
        pcName = readInData[1] as String
        pcPrice = readInData[2] as Double
        isPcCompleted = (readInData[3] as Int) !=0
        gpuName = readInData[4] as String?
        cpuName = readInData[5] as String?
        psuName = readInData[6] as String?
        motherboardName = readInData[7] as String?
        heatsinkName = readInData[8] as String?
        caseName = readInData[9] as String?
        deletable = (readInData[readInData.lastIndex] as Int) !=0
    }

    /**
     *
     */
    fun pcPartsSearchConfig(): List<Pair<String?, String>> {
        return listOf(
            Pair(gpuName, "gpu"),
            Pair(cpuName, "cpu"),
            Pair(psuName, "psu"),
            Pair(motherboardName, "motherboard"),
            Pair(heatsinkName, "heatsink"),
            Pair(caseName, "cases")
        )
    }

    override fun describeContents(): Int {
        return 0
    }


    /**
     *
     */
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeInt(pcID!!)
            it.writeString(pcName)
            pcPrice.let { price -> it.writeDouble(price) }
            it.writeBoolean(isPcCompleted)
            it.writeString(gpuName)
            it.writeString(cpuName)
            it.writeList(ramList)
            it.writeString(psuName)
            it.writeString(motherboardName)
            it.writeList(storageList)
            it.writeString(heatsinkName)
            it.writeString(caseName)
            it.writeList(fanList)
            it.writeBoolean(deletable)
        }
    }

    companion object CREATOR : Parcelable.Creator<PCBuild> {
        override fun createFromParcel(parcel: Parcel): PCBuild {
            return PCBuild(parcel)
        }

        override fun newArray(size: Int): Array<PCBuild?> {
            return arrayOfNulls(size)
        }
    }
}