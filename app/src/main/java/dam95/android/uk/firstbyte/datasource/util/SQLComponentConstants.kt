package dam95.android.uk.firstbyte.datasource.util

private const val NN = "NOT NULL"
private const val PK = "PRIMARY KEY"
private const val FK = "FOREIGN KEY"
const val FK_ON = "PRAGMA foreign_keys=1"

/**
 * This class is dedicated to creating the FB_Hardware_Android database and retrieving columns of tables
 * It's a long class filled with SQL that will only be executed once when creating the database.
 * It is not meant to be pretty, but rather a storage compartment for constant value commands
 */
abstract class SQLComponentConstants {

    /**
     * companion object to hold constant value SQL commands that creates the...
     * ...relational tables within the Components Database
     */
    companion object Creation {

        private const val CREATE_COMPONENTS_TABLE =
            "CREATE TABLE ${Components.COMPONENT_TABLE}(\n" +
                    "${Components.COMPONENT_NAME} VARCHAR(50) $PK,\n" +
                    "${Components.COMPONENT_TYPE} VARCHAR(20) $NN,\n" +
                    "${Components.COMPONENT_IMAGE} TEXT $NN," +
                    "${Components.RRP_PRICE} DOUBLE(7,2) $NN,\n" +
                    "${Components.AMAZON_PRICE} DOUBLE(7,2),\n" +
                    "${Components.AMAZON_LINK} TEXT,\n" +
                    "${Components.SCAN_PRICE} DOUBLE(7,2),\n" +
                    "${Components.SCAN_LINK} TEXT," +
                    "${Components.IS_DELETABLE} TINYINT(1));"

        private const val CREATE_GPU_TABLE = "CREATE TABLE ${GraphicsCards.GPU_TABLE}(\n" +
                "${GraphicsCards.GPU_NAME} VARCHAR(50) $PK,\n" +
                "${GraphicsCards.GPU_CORE_SPEED} INT(10) $NN,\n" +
                "${GraphicsCards.GPU_MEMORY_SIZE} INT(4) $NN,\n" +
                "${GraphicsCards.GPU_MEMORY_SPEED} INT(10) $NN,\n" +
                "${GraphicsCards.GPU_WATTAGE} INT(5) $NN,\n" +
                "${GraphicsCards.GPU_DIMENSIONS} VARCHAR(20) $NN,\n" +
                "$FK (${GraphicsCards.GPU_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_CPU_TABLE = "CREATE TABLE ${Processors.CPU_TABLE}(\n" +
                "${Processors.CPU_NAME} VARCHAR(50) $PK,\n" +
                "${Processors.CPU_CORE_SPEED} DOUBLE(3, 2) $NN,\n" +
                "${Processors.CPU_CORE_COUNT} INT(2) $NN,\n" +
                "${Processors.IS_MULTITHREADED} TINYINT(1) $NN,\n" +
                "${Processors.CPU_SOCKET} VARCHAR(10) $NN,\n" +
                "${Processors.CPU_WATTAGE} INT(5) $NN,\n" +
                "${Processors.HAS_HEATSINK} TINYINT(1) $NN,\n" +
                "$FK (${Processors.CPU_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_RAM_TABLE = "CREATE TABLE ${RamSticks.RAM_TABLE}(\n" +
                "${RamSticks.RAM_NAME} VARCHAR(50) $PK,\n" +
                "${RamSticks.RAM_SPEED} INT(8) $NN,\n" +
                "${RamSticks.RAM_SIZE} INT(4) $NN,\n" +
                "${RamSticks.RAM_DDR} VARCHAR(5) $NN,\n" +
                "${RamSticks.NUM_OF_STICKS} INT(1) $NN,\n" +
                "$FK (${RamSticks.RAM_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_PSU_TABLE = "CREATE TABLE ${PowerSupplys.PSU_TABLE}(\n" +
                "${PowerSupplys.PSU_NAME} VARCHAR(50) $PK,\n" +
                "${PowerSupplys.PSU_WATTAGE} INT(5) $NN,\n" +
                "${PowerSupplys.PSU_RATING} VARCHAR(12) $NN,\n" +
                "${PowerSupplys.PSU_IS_MODULAR} TINYINT(1) $NN,\n" +
                "$FK (${PowerSupplys.PSU_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_STORAGE_TABLE = "CREATE TABLE ${StorageList.STORAGE_TABLE}(\n" +
                "${StorageList.STORAGE_NAME} VARCHAR(50) $PK,\n" +
                "${StorageList.STORAGE_TYPE} VARCHAR(10) $NN,\n" +
                "${StorageList.EXTERNAL_STORAGE} TINYINT(1) $NN,\n" +
                "${StorageList.STORAGE_CAPACITY} INT(10) $NN,\n" +
                "${StorageList.STORAGE_SPEED} INT(10) $NN,\n" +
                "$FK (${StorageList.STORAGE_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_MOTHERBOARD_TABLE =
            "CREATE TABLE ${Motherboards.MOTHERBOARD_TABLE}(\n" +
                    "${Motherboards.BOARD_NAME} VARCHAR(50) $PK,\n" +
                    "${Motherboards.BOARD_TYPE} VARCHAR(10) $NN,\n" +
                    "${Motherboards.BOARD_DIMENSION} VARCHAR(20) $NN,\n" +
                    "${Motherboards.BOARD_CPU_SOCKET} VARCHAR(10) $NN,\n" +
                    "${Motherboards.BOARD_DDR} VARCHAR(5) $NN,\n" +
                    "${Motherboards.HAS_USB3} TINYINT(1) $NN,\n" +
                    "${Motherboards.HAS_WIFI} TINYINT(1) $NN,\n" +
                    "${Motherboards.BOARD_PCIE} DOUBLE(4, 2) $NN,\n" +
                    "${Motherboards.HAS_NVME} TINYINT(1) $NN,\n" +
                    "$FK (${Motherboards.BOARD_NAME})\n" +
                    "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_CASES_TABLE = "CREATE TABLE ${Cases.CASE_TABLE}(\n" +
                "${Cases.CASE_NAME} VARCHAR(50) $PK,\n" +
                "${Cases.CASE_FAN_SLOTS} INT(1) $NN,\n" +
                "${Cases.CASE_FAN_SIZES} INT(4) $NN,\n" +
                "${Cases.CASE_BOARD} VARCHAR(10) $NN,\n" +
                "${Cases.CASE_DIMENSIONS} VARCHAR(20) $NN,\n" +
                "$FK (${Cases.CASE_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_HEATSINK_TABLE = "CREATE TABLE ${Heatsinks.HEATSINK_TABLE}(\n" +
                "${Heatsinks.HEATSINK_NAME} VARCHAR(50) $PK,\n" +
                "${Heatsinks.AMD_SOCKET_MIN} VARCHAR(10),\n" +
                "${Heatsinks.AMD_SOCKET_MAX} VARCHAR(10),\n" +
                "${Heatsinks.INTEL_SOCKET_MIN} VARCHAR(10),\n" +
                "${Heatsinks.INTEL_SOCKET_MAX} VARCHAR(10),\n" +
                "${Heatsinks.HEATSINK_FAN_SLOTS} INT(1) $NN,\n" +
                "${Heatsinks.HEATSINK_DIMENSIONS} VARCHAR(20) $NN,\n" +
                "$FK (${Heatsinks.HEATSINK_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_FAN_TABLE = "CREATE TABLE ${Fans.FAN_TABLE}(\n" +
                "${Fans.FAN_NAME} VARCHAR(50) $PK,\n" +
                "${Fans.FAN_SIZE} INT(4) $NN,\n" +
                "${Fans.FAN_SPEED} INT(6) $NN,\n" +
                "$FK (${Fans.FAN_NAME})\n" +
                "REFERENCES ${Components.COMPONENT_TABLE} (${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_PCBUILD_TABLE = "CREATE TABLE ${PcBuild.PC_BUILD_TABLE}(\n" +
                "${PcBuild.PC_ID} INT(3) $PK,\n" +
                "${PcBuild.PC_NAME} VARCHAR(20),\n" +
                "${PcBuild.PC_GPU_NAME} VARCHAR(50),\n" +
                "${PcBuild.PC_CPU_NAME} VARCHAR(50),\n" +
                "${PcBuild.PC_PSU_NAME} VARCHAR(50),\n" +
                "${PcBuild.PC_BOARD_NAME} VARCHAR(50),\n" +
                "${PcBuild.PC_HEATSINK_NAME} VARCHAR(50),\n" +
                "${PcBuild.PC_CASE_NAME} VARCHAR(50),\n" +
                "${PcBuild.PC_IS_DELETABLE} VARCHAR(50),\n" +
                "$FK(${PcBuild.PC_GPU_NAME})REFERENCES ${GraphicsCards.GPU_TABLE} (${GraphicsCards.GPU_NAME}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK(${PcBuild.PC_CPU_NAME})REFERENCES ${Processors.CPU_TABLE} (${Processors.CPU_NAME}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK(${PcBuild.PC_PSU_NAME})REFERENCES ${PowerSupplys.PSU_TABLE} (${PowerSupplys.PSU_NAME}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK(${PcBuild.PC_BOARD_NAME})REFERENCES ${Motherboards.MOTHERBOARD_TABLE} (${Motherboards.BOARD_NAME}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK(${PcBuild.PC_HEATSINK_NAME})REFERENCES ${Heatsinks.HEATSINK_TABLE} (${Heatsinks.HEATSINK_NAME}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK(${PcBuild.PC_CASE_NAME})REFERENCES ${Cases.CASE_TABLE} (${Cases.CASE_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        //Many to Many tables between a PC build and these created components below.
        private const val CREATE_FANS_IN_PC_TABLE = "CREATE TABLE ${FansInPc.PC_FAN_TABLE}(" +
                "${FansInPc.PC_ID} INT(3),\n" +
                "${FansInPc.PC_FAN_NAME} VARCHAR(50),\n" +
                "$FK (${FansInPc.PC_ID}) REFERENCES ${PcBuild.PC_BUILD_TABLE} (${PcBuild.PC_ID}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK (${FansInPc.PC_FAN_NAME}) REFERENCES ${Fans.FAN_TABLE} (${Fans.FAN_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_STORAGE_IN_PC_TABLE =
            "CREATE TABLE ${StorageInPc.PC_STORAGE_TABLE}(" +
                    "${StorageInPc.PC_ID} INT(3),\n" +
                    "${StorageInPc.PC_STORAGE_NAME} VARCHAR(50),\n" +
                    "$FK (${StorageInPc.PC_ID}) REFERENCES ${PcBuild.PC_BUILD_TABLE} (${PcBuild.PC_ID}) ON UPDATE CASCADE ON DELETE CASCADE," +
                    "$FK (${StorageInPc.PC_STORAGE_NAME}) REFERENCES ${StorageList.STORAGE_TABLE} (${StorageList.STORAGE_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"

        private const val CREATE_RAM_IN_PC_TABLE = "CREATE TABLE ${RamInPc.PC_RAM_TABLE}(" +
                "${RamInPc.PC_ID} INT(3),\n" +
                "${RamInPc.PC_RAM_NAME} VARCHAR(50),\n" +
                "$FK (${RamInPc.PC_ID}) REFERENCES ${PcBuild.PC_BUILD_TABLE} (${PcBuild.PC_ID}) ON UPDATE CASCADE ON DELETE CASCADE," +
                "$FK (${RamInPc.PC_RAM_NAME}) REFERENCES ${RamSticks.RAM_TABLE} (${RamSticks.RAM_NAME}) ON UPDATE CASCADE ON DELETE CASCADE);"


        val TABLE_CREATION_COMMANDS: List<String> = listOf(
            CREATE_COMPONENTS_TABLE,
            CREATE_GPU_TABLE,
            CREATE_CPU_TABLE,
            CREATE_RAM_TABLE,
            CREATE_PSU_TABLE,
            CREATE_STORAGE_TABLE,
            CREATE_MOTHERBOARD_TABLE,
            CREATE_CASES_TABLE,
            CREATE_HEATSINK_TABLE,
            CREATE_FAN_TABLE,
            CREATE_PCBUILD_TABLE,
            CREATE_FANS_IN_PC_TABLE,
            CREATE_STORAGE_IN_PC_TABLE,
            CREATE_RAM_IN_PC_TABLE
        )
    }

    /**
     *
     */
    interface Components {
        companion object {
            const val COMPONENT_TABLE: String = "component"

            //Columns
            const val COMPONENT_NAME: String = "component_name"
            const val COMPONENT_TYPE: String = "component_type"
            const val COMPONENT_IMAGE: String = "image_link"
            const val RRP_PRICE: String = "rrp_price"
            const val AMAZON_PRICE: String = "amazon_price"
            const val AMAZON_LINK: String = "amazon_link"
            const val SCAN_PRICE: String = "scan_price"
            const val SCAN_LINK: String = "scan_link"
            const val IS_DELETABLE: String = "deletable"

            //This is the Brain of the database, this is where all components link to.
            //This list is for easier data insertion and retrieval
            val COLUMN_LIST = listOf(
                COMPONENT_NAME,
                COMPONENT_TYPE,
                COMPONENT_IMAGE,
                RRP_PRICE,
                AMAZON_PRICE,
                AMAZON_LINK,
                SCAN_PRICE,
                SCAN_LINK,
                IS_DELETABLE,
            )
        }
    }

    /**
     *
     */
    interface GraphicsCards {
        companion object {
            const val GPU_TABLE: String = "gpu"

            //Columns
            const val GPU_NAME: String = "gpu_name"
            const val GPU_CORE_SPEED: String = "core_speed_mhz"
            const val GPU_MEMORY_SIZE: String = "memory_size_gb"
            const val GPU_MEMORY_SPEED: String = "memory_speed_mhz"
            const val GPU_WATTAGE: String = "wattage"
            const val GPU_DIMENSIONS: String = "dimensions"
            val COLUMN_LIST = listOf(
                GPU_NAME,
                GPU_CORE_SPEED,
                GPU_MEMORY_SIZE,
                GPU_MEMORY_SPEED,
                GPU_WATTAGE,
                GPU_DIMENSIONS
            )
        }
    }

    /**
     *
     */
    interface Processors {
        companion object {
            const val CPU_TABLE: String = "cpu"

            //Columns
            const val CPU_NAME: String = "cpu_name"
            const val CPU_CORE_SPEED: String = "core_speed_ghz"
            const val CPU_CORE_COUNT: String = "core_count"
            const val IS_MULTITHREADED: String = "multi_threading"
            const val CPU_SOCKET: String = "cpu_socket"
            const val CPU_WATTAGE: String = "cpu_wattage"
            const val HAS_HEATSINK: String = "default_heatsink"
            val COLUMN_LIST = listOf(
                CPU_NAME,
                CPU_CORE_SPEED,
                CPU_CORE_COUNT,
                IS_MULTITHREADED,
                CPU_SOCKET,
                CPU_WATTAGE,
                HAS_HEATSINK
            )
        }
    }

    /**
     *
     */
    interface RamSticks {
        companion object {
            const val RAM_TABLE: String = "ram"

            //Columns
            const val RAM_NAME: String = "ram_name"
            const val RAM_SPEED: String = "ram_memory_speed_mhz"
            const val RAM_SIZE: String = "ram_memory_size_gb"
            const val RAM_DDR: String = "ram_ddr"
            const val NUM_OF_STICKS: String = "num_of_sticks"
            val COLUMN_LIST = listOf(RAM_NAME, RAM_SPEED, RAM_SIZE, RAM_DDR, NUM_OF_STICKS)
        }
    }

    /**
     *
     */
    interface PowerSupplys {
        companion object {
            const val PSU_TABLE: String = "psu"

            //Columns
            const val PSU_NAME: String = "psu_name"
            const val PSU_WATTAGE: String = "psu_wattage"
            const val PSU_RATING: String = "rating"
            const val PSU_IS_MODULAR: String = "modular"
            val COLUMN_LIST = listOf(PSU_NAME, PSU_WATTAGE, PSU_RATING, PSU_IS_MODULAR)
        }
    }

    /**
     *
     */
    interface StorageList {
        companion object {
            const val STORAGE_TABLE: String = "storage"

            //Columns
            const val STORAGE_NAME: String = "storage_name"
            const val STORAGE_TYPE: String = "storage_type"
            const val EXTERNAL_STORAGE: String = "external_storage"
            const val STORAGE_CAPACITY: String = "storage_capacity_gb"
            const val STORAGE_SPEED: String = "storage_speed_mbps"
            val COLUMN_LIST = listOf(
                STORAGE_NAME,
                STORAGE_TYPE,
                EXTERNAL_STORAGE,
                STORAGE_CAPACITY,
                STORAGE_SPEED
            )
        }
    }

    /**
     *
     */
    interface Motherboards {
        companion object {
            const val MOTHERBOARD_TABLE: String = "motherboard"

            //Columns
            const val BOARD_NAME: String = "motherboard_name"
            const val BOARD_TYPE: String = "board_type"
            const val BOARD_DIMENSION: String = "board_dimensions"
            const val BOARD_CPU_SOCKET: String = "processor_socket"
            const val BOARD_DDR: String = "ddr_sdram"
            const val HAS_USB3: String = "usb3plus"
            const val HAS_WIFI: String = "wifi"
            const val BOARD_PCIE: String = "pci_e"
            const val HAS_NVME: String = "nvme_support"
            val COLUMN_LIST = listOf(
                BOARD_NAME,
                BOARD_TYPE,
                BOARD_DIMENSION,
                BOARD_CPU_SOCKET,
                BOARD_DDR,
                HAS_USB3,
                HAS_WIFI,
                BOARD_PCIE,
                HAS_NVME
            )
        }
    }

    /**
     *
     */
    interface Cases {
        companion object {
            const val CASE_TABLE: String = "cases"

            //Columns
            const val CASE_NAME: String = "cases_name"
            const val CASE_FAN_SLOTS: String = "case_fan_slots"
            const val CASE_FAN_SIZES: String = "case_fan_sizes_mm"
            const val CASE_BOARD: String = "case_motherboard"
            const val CASE_DIMENSIONS: String = "case_dimensions"
            val COLUMN_LIST =
                listOf(CASE_NAME, CASE_FAN_SLOTS, CASE_FAN_SIZES, CASE_BOARD, CASE_DIMENSIONS)
        }
    }

    /**
     *
     */
    interface Heatsinks {
        companion object {
            const val HEATSINK_TABLE: String = "heatsink"

            //Columns
            const val HEATSINK_NAME: String = "heatsink_name"
            const val HEATSINK_FAN_SLOTS: String = "fan_slots"
            const val AMD_SOCKET_MIN: String = "amd_socket_min"
            const val AMD_SOCKET_MAX: String = "amd_socket_max"
            const val INTEL_SOCKET_MIN: String = "intel_socket_min"
            const val INTEL_SOCKET_MAX: String = "intel_socket_max"
            const val HEATSINK_DIMENSIONS: String = "heatsink_dimensions"
            val COLUMN_LIST = listOf(
                HEATSINK_NAME,
                HEATSINK_FAN_SLOTS,
                AMD_SOCKET_MIN,
                AMD_SOCKET_MAX,
                INTEL_SOCKET_MIN,
                INTEL_SOCKET_MAX,
                HEATSINK_DIMENSIONS
            )
        }
    }

    /**
     *
     */
    interface Fans {
        companion object {
            const val FAN_TABLE: String = "fan"

            //Columns
            const val FAN_NAME: String = "fan_name"
            const val FAN_SIZE: String = "fan_size_mm"
            const val FAN_SPEED: String = "fan_rpm"
            val COLUMN_LIST = listOf(FAN_NAME, FAN_SIZE, FAN_SPEED)
        }
    }

    /**
     *
     */
    interface PcBuild {
        companion object {
            const val PC_BUILD_TABLE: String = "pcbuild"

            //Columns
            const val PC_ID: String = "pc_id"
            const val PC_NAME: String = "pc_name"
            const val PC_GPU_NAME: String = "gpu_name"
            const val PC_CPU_NAME: String = "cpu_name"
            const val PC_PSU_NAME: String = "psu_name"
            const val PC_BOARD_NAME: String = "board_name"
            const val PC_HEATSINK_NAME: String = "heatsink_name"
            const val PC_CASE_NAME: String = "case_name"
            const val PC_IS_DELETABLE: String = "deletable"
        }
    }

    /**
     *
     */
    interface RamInPc {
        companion object {
            const val PC_RAM_TABLE: String = "ram_in_pc"

            //Columns
            const val PC_ID: String = "pc_id"
            const val PC_RAM_NAME: String = "ram_name"
        }
    }

    /**
     *
     */
    interface StorageInPc {
        companion object {
            const val PC_STORAGE_TABLE: String = "storage_in_pc"

            //Columns
            const val PC_ID: String = "pc_id"
            const val PC_STORAGE_NAME: String = "storage_name"
        }
    }

    /**
     *
     */
    interface FansInPc {
        companion object {
            const val PC_FAN_TABLE: String = "fans_in_pc"

            //Columns
            const val PC_ID: String = "pc_id"
            const val PC_FAN_NAME: String = "fan_name"
        }
    }
}