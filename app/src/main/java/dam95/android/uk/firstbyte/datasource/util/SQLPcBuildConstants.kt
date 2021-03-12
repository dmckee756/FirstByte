package dam95.android.uk.firstbyte.datasource.util

abstract class SQLPcBuildConstants {

    companion object Creation {


        val TABLE_CREATION_COMMANDS: List<String> = listOf()
    }

    interface PcBuild{
        companion object{
            const val PCBUILD_TABLE: String = "pcbuild"
            const val PC_ID: String = "pc_id"
            const val PC_NAME :String = "pc_name"
            const val PC_GPU_NAME :String = "gpu_name"
            const val PC_CPU_NAME :String = "cpu_name"
            const val PC_RAM_NAME :String = "ram_name"
            const val PC_PSU_NAME :String = "psu_name"
            const val PC_BOARD_NAME : String = "board_name"
            const val PC_HEATSINK_NAME :String = "heatsink_name"
            const val PC_CASE_NAME :String = "case_name"
        }
    }

    interface Storage{
        companion object{
            const val PC_STORAGE_TABLE: String = "pcstorage"
            const val PC_ID: String = "pc_id"
            const val PC_STORAGE_NAME: String = "storage_name"
        }
    }

    interface Fans{
        companion object{
            const val PC_FAN_TABLE: String = "pcfans"
            const val PC_ID: String = "pc_id"
            const val PC_FAN_NAME: String = "fan_name"
        }
    }
}