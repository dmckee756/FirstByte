package dam95.android.uk.firstbyte.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 */
@Entity(tableName = "pcBuilds")
data class PCBuild(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var pcID: Int,
    var pcName: String,
    var gpu_name: String?,
    var cpu_name: String?,
    var ram_name: String?,
    var psu_name: String?,
    var storage: String?, //CHANGE MULTIPLE 3 MAX
    var heatsink_name: String?,
    var theCase: String?,
    var fan: String? //CHANGE MULTIPLE AMOUNT OF SLOTS IN CASE
) {}