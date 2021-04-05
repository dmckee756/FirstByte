package dam95.android.uk.firstbyte.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author David Mckee
 * @Version 1.0
 * PCBuild object designed to hold the ID, PC Name and pricing of a created PC.
 * Has a value informing the user if the PC is "completed", meaning there are no incompatibilities and all required parts are in the PC.
 * Each name and list holds represents a PC Part, and is a reference to the component's name,
 * which details are loaded within the PersonalBuildList, Home and PersonalBuild fragments.
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

    /**
     * Constructor used to assign all pc build values into a parcel/package for transport.
     */
    constructor(parcel: Parcel) : this() {
        pcID = parcel.readValue(Int::class.java.classLoader) as? Int
        pcName = parcel.readString().toString()
        pcPrice = parcel.readDouble()
        isPcCompleted = parcel.readByte() != 0.toByte()
        gpuName = parcel.readString()
        cpuName = parcel.readString()
        ramList = parcel.createStringArrayList()
        psuName = parcel.readString()
        motherboardName = parcel.readString()
        storageList = parcel.createStringArrayList()
        heatsinkName = parcel.readString()
        caseName = parcel.readString()
        fanList = parcel.createStringArrayList()
        deletable = parcel.readByte() != 0.toByte()
    }

    /**
     * Bundles all variables of a Gpu into a list and returns it to the caller.
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all Gpu variables and values.
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
     * Important to keep this in the same order as the constructor,
     * When the PCBuild is loaded from the database, assign primitive values of the PC's details their corresponding values.
     * @param readInData All loaded primitive PC details from the app's database.
     */
    fun setPrimitiveDetails(readInData: List<*>) {
        pcID = readInData[0] as Int
        pcName = readInData[1] as String
        pcPrice = readInData[2] as Double
        isPcCompleted = (readInData[3] as Int) != 0
        gpuName = readInData[4] as String?
        cpuName = readInData[5] as String?
        psuName = readInData[6] as String?
        motherboardName = readInData[7] as String?
        heatsinkName = readInData[8] as String?
        caseName = readInData[9] as String?
        deletable = (readInData[readInData.lastIndex] as Int) != 0
    }

    /**
     * Bundle up all pc parts that only allow one slot in the pc and send it to the
     * "getPCBuildContents" method, so that it can retrieve the pc parts details.
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

    /**
     * Recreates the PCBuild object when it reaches it's destination (Fragment class)
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(pcID)
        parcel.writeString(pcName)
        parcel.writeDouble(pcPrice)
        parcel.writeByte(if (isPcCompleted) 1 else 0)
        parcel.writeString(gpuName)
        parcel.writeString(cpuName)
        parcel.writeStringList(ramList)
        parcel.writeString(psuName)
        parcel.writeString(motherboardName)
        parcel.writeStringList(storageList)
        parcel.writeString(heatsinkName)
        parcel.writeString(caseName)
        parcel.writeStringList(fanList)
        parcel.writeByte(if (deletable) 1 else 0)
    }

    /**
     * A required method from the Parcelable interface. Ignore it.
     * @return 0, ignore.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Writes all of the data in the PCBuild into a parcelable package(?),
     * allowing it to be transported across a fragment.
     */
    companion object CREATOR : Parcelable.Creator<PCBuild> {
        override fun createFromParcel(parcel: Parcel): PCBuild {
            return PCBuild(parcel)
        }

        override fun newArray(size: Int): Array<PCBuild?> {
            return arrayOfNulls(size)
        }
    }

}