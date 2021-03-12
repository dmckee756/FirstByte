package dam95.android.uk.firstbyte.model.pcbuilds

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.model.components.Fan
import dam95.android.uk.firstbyte.model.components.Storage

/**
 *
 */
data class PCBuild(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var pc_id: Int,
    var pc_name: String,
    var gpu_name: String?,
    var cpu_name: String?,
    var ram_name: String?,
    var psu_name: String?,
    var motherboard_name: String?,
    var storage_list: List<String?>,
    var heatsink_name: String?,
    var case_name: String?,
    var fan_list: List<String?>?
) {}