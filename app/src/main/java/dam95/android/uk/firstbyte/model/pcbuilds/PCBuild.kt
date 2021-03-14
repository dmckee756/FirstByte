package dam95.android.uk.firstbyte.model.pcbuilds

/**
 *
 */
data class PCBuild(
    var pc_id: Int,
    var pc_name: String? = null
) {
    var pc_price: Double? = null
    var is_pc_completed: Boolean = false
    var gpu_name: String? = null
    var cpu_name: String? = null
    var ram_name: List<String>? = null
    var psu_name: String? = null
    var motherboard_name: String? = null
    var storage_list: List<String>? = null
    var heatsink_name: String? = null
    var case_name: String? = null
    var fan_list: List<String>? = null
    var deleteable: Boolean = true
}