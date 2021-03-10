package dam95.android.uk.firstbyte.datasource.util

private const val NN = "NOT NULL"
private const val PK = "PRIMARY KEY"
private const val FK = "FOREIGN KEY"

/**
 * This class is dedicated to creating a new component database and retrieving columns of tables
 */
class SQLConstantCreation {

    /**
     * companion object to hold constant value SQL commands that creates the...
     * ...relational tables within the Components Database
     */
    companion object Creation {

        private const val CREATE_COMPONENTS_TABLE = "CREATE TABLE ${Components.COMPONENT_TABLE}(\n" +
                "${Components.COMPONENT_NAME} VARCHAR(50) $NN $PK,\n" +
                "${Components.COMPONENT_TYPE} ENUM('gpu','cpu','ram','psu','storage','motherboard','heatsink','cases','fan') $NN,\n" +
                "${Components.COMPONENT_IMAGE} TEXT $NN" +
                "${Components.RRP_PRICE} DOUBLE(7,2) $NN,\n" +
                "${Components.AMAZON_PRICE} DOUBLE(7,2),\n" +
                "${Components.AMAZON_LINK} TEXT,\n" +
                "${Components.SCAN_PRICE} DOUBLE(7,2),\n" +
                "${Components.SCAN_LINK} TEXT);"

        private const val CREATE_GPU_TABLE = "CREATE TABLE ${GraphicsCards.GPU_TABLE}(\n" +
                "${GraphicsCards.GPU_NAME} VARCHAR(50),\n" +
                "${GraphicsCards.GPU_CORE_SPEED} INT(10) $NN,\n" +
                "${GraphicsCards.GPU_MEMORY_SIZE} INT(4) $NN,\n" +
                "${GraphicsCards.GPU_MEMORY_SPEED} INT(10) $NN,\n" +
                "${GraphicsCards.GPU_WATTAGE} INT(5) $NN,\n" +
                "${GraphicsCards.GPU_DIMENSIONS} VARCHAR(20) $NN," +
                "CONSTRAINT PK_${GraphicsCards.GPU_NAME} $PK(${GraphicsCards.GPU_NAME}),\n" +
                "INDEX index_${GraphicsCards.GPU_NAME}(${GraphicsCards.GPU_NAME}),\n" +
                "CONSTRAINT FK_${GraphicsCards.GPU_NAME} $FK(${GraphicsCards.GPU_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_CPU_TABLE = "CREATE TABLE ${Processors.CPU_TABLE}(\n" +
                "${Processors.CPU_NAME} VARCHAR(50),\n" +
                "${Processors.CPU_CORE_SPEED} DOUBLE(3, 2) $NN,\n" +
                "${Processors.CPU_CORE_COUNT} INT(2) $NN,\n" +
                "${Processors.IS_MULTITHREADED} TINYINT(1) $NN,\n" +
                "${Processors.CPU_SOCKET} VARCHAR(10) $NN,\n" +
                "${Processors.CPU_WATTAGE} INT(5) $NN,\n" +
                "${Processors.HAS_HEATSINK} TINYINT(1) $NN," +
                "CONSTRAINT PK_${Processors.CPU_NAME} $PK(${Processors.CPU_NAME}),\n" +
                "INDEX index_${Processors.CPU_NAME}(${Processors.CPU_NAME}),\n" +
                "CONSTRAINT FK_${Processors.CPU_NAME} $FK(${Processors.CPU_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_RAM_TABLE = "CREATE TABLE ${RamSticks.RAM_TABLE}(\n" +
                "${RamSticks.RAM_NAME} VARCHAR(50),\n" +
                "${RamSticks.RAM_SPEED} INT(8) $NN,\n" +
                "${RamSticks.RAM_SIZE} INT(4) $NN,\n" +
                "${RamSticks.RAM_DDR} VARCHAR(5) $NN,\n" +
                "${RamSticks.NUM_OF_STICKS} INT(1) $NN;," +
                "CONSTRAINT PK_${RamSticks.RAM_NAME} $PK(${RamSticks.RAM_NAME}),\n" +
                "INDEX index_${RamSticks.RAM_NAME}(${RamSticks.RAM_NAME}),\n" +
                "CONSTRAINT FK_${RamSticks.RAM_NAME} $FK(${RamSticks.RAM_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_PSU_TABLE = "CREATE TABLE ${PowerSupplys.PSU_TABLE}(\n" +
                "${PowerSupplys.PSU_NAME} VARCHAR(50),\n" +
                "${PowerSupplys.PSU_WATTAGE} INT(5) $NN,\n" +
                "${PowerSupplys.PSU_RATING} ENUM('80+','Bronze','Silver','Gold','Platinum','Titanium') $NN,\n" +
                "${PowerSupplys.PSU_IS_MODULAR} TINYINT(1) $NN," +
                "CONSTRAINT PK_${PowerSupplys.PSU_NAME} $PK(${PowerSupplys.PSU_NAME}),\n" +
                "INDEX index_${PowerSupplys.PSU_NAME}(${PowerSupplys.PSU_NAME}),\n" +
                "CONSTRAINT FK_${PowerSupplys.PSU_NAME} $FK(${PowerSupplys.PSU_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_STORAGE_TABLE = "CREATE TABLE ${StorageList.STORAGE_TABLE}(\n" +
                "${StorageList.STORAGE_NAME} VARCHAR(50),\n" +
                "${StorageList.STORAGE_TYPE} ENUM('HDD','SSHD','SSD','M.2 NVMe') $NN,\n" +
                "${StorageList.EXTERNAL_STORAGE} TINYINT(1) $NN,\n" +
                "${StorageList.STORAGE_CAPACITY} INT(10) $NN,\n" +
                "${StorageList.STORAGE_SPEED} INT(10) $NN," +
                "CONSTRAINT PK_${StorageList.STORAGE_NAME} $PK(${StorageList.STORAGE_NAME}),\n" +
                "INDEX index_${StorageList.STORAGE_NAME}(${StorageList.STORAGE_NAME}),\n" +
                "CONSTRAINT FK_${GraphicsCards.GPU_NAME} $FK(${StorageList.STORAGE_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_MOTHERBOARD_TABLE = "CREATE TABLE ${Motherboards.MOTHERBOARD_TABLE}(\n" +
                "${Motherboards.BOARD_NAME} VARCHAR(50),\n" +
                "${Motherboards.BOARD_TYPE} ENUM('ATX','Micro-ATX','µATX','Mini-ATX','Nano-ITX','Pico-ITX') $NN,\n" +
                "${Motherboards.BOARD_DIMENSION} VARCHAR(20) $NN,\n" +
                "${Motherboards.BOARD_CPU_SOCKET} VARCHAR(10) $NN,\n" +
                "${Motherboards.BOARD_DDR} VARCHAR(5) $NN,\n" +
                "${Motherboards.BOARD_USB3} TINYINT(1) $NN,\n" +
                "${Motherboards.BOARD_WIFI} TINYINT(1) $NN,\n" +
                "${Motherboards.BOARD_PCIE} DOUBLE(4, 2) $NN,\n" +
                "${Motherboards.BOARD_NVME} TINYINT(1) $NN," +
                "CONSTRAINT PK_${Motherboards.BOARD_NAME} $PK(${Motherboards.BOARD_NAME}),\n" +
                "INDEX index_${Motherboards.BOARD_NAME}(${Motherboards.BOARD_NAME}),\n" +
                "CONSTRAINT FK_${Motherboards.BOARD_NAME} $FK(${Motherboards.BOARD_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_CASES_TABLE = "CREATE TABLE ${Cases.CASE_TABLE}(\n" +
                "${Cases.CASE_NAME} VARCHAR(50), \n" +
                "${Cases.CASE_FAN_SLOTS} INT(1) $NN,\n" +
                "${Cases.CASE_FAN_SIZES} INT(4) $NN,\n" +
                "${Cases.CASE_BOARD} ENUM('ATX','Micro-ATX','µATX','Mini-ATX','Nano-ITX','Pico-ITX') $NN,\n" +
                "${Cases.CASE_DIMENSIONS} VARCHAR(20) $NN," +
                "CONSTRAINT PK_${Cases.CASE_NAME} $PK(${Cases.CASE_NAME}),\n" +
                "INDEX index_${Cases.CASE_NAME}(${Cases.CASE_NAME}),\n" +
                "CONSTRAINT FK_${Cases.CASE_NAME} $FK(${Cases.CASE_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_HEATSINK_TABLE = "CREATE TABLE ${Heatsinks.HEATSINK_TABLE}(\n" +
                "${Heatsinks.HEATSINK_NAME} VARCHAR(50),\n" +
                "${Heatsinks.AMD_SOCKET_MIN} VARCHAR(10),\n" +
                "${Heatsinks.AMD_SOCKET_MAX} VARCHAR(10),\n" +
                "${Heatsinks.INTEL_SOCKET_MIN} VARCHAR(10),\n" +
                "${Heatsinks.INTEL_SOCKET_MAX} VARCHAR(10),\n" +
                "${Heatsinks.HEATSINK_FAN_SLOTS} INT(1) $NN,\n" +
                "${Heatsinks.HEATSINK_DIMENSIONS} VARCHAR(20) $NN," +
                "CONSTRAINT PK_${Heatsinks.HEATSINK_NAME} $PK(${Heatsinks.HEATSINK_NAME}),\n" +
                "INDEX index_${Heatsinks.HEATSINK_NAME}(${Heatsinks.HEATSINK_NAME}),\n" +
                "CONSTRAINT FK_${Heatsinks.HEATSINK_NAME} $FK(${Heatsinks.HEATSINK_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

        private const val CREATE_FAN_TABLE = "CREATE TABLE ${Fans.FAN_TABLE}(\n" +
                "${Fans.FAN_NAME} VARCHAR(50), \n" +
                "${Fans.FAN_SIZE} INT(4) $NN,\n" +
                "${Fans.FAN_SPEED} INT(6) $NN," +
                "CONSTRAINT PK_${Fans.FAN_NAME} $PK(${Fans.FAN_NAME}),\n" +
                "INDEX index_${Fans.FAN_NAME}(${Fans.FAN_NAME}),\n" +
                "CONSTRAINT FK_${Fans.FAN_NAME} $FK(${Fans.FAN_NAME}) REFERENCES ${Components.COMPONENT_TABLE}(${Components.COMPONENT_NAME}) ON UPDATE CASCADE ON DELETE CASCADE;"

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
            CREATE_FAN_TABLE
        )
    }

    /**
     *
     */
    private interface Components {
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
        }
    }

    /**
     *
     */
    private interface GraphicsCards {
        companion object {
            const val GPU_TABLE: String = "gpu"

            //Columns
            const val GPU_NAME: String = "gpu_name"
            const val GPU_CORE_SPEED: String = "core_speed_mhz"
            const val GPU_MEMORY_SIZE: String = "memory_size_gb"
            const val GPU_MEMORY_SPEED: String = "memory_speed_mhz"
            const val GPU_WATTAGE: String = "wattage"
            const val GPU_DIMENSIONS: String = "dimensions"
        }
    }

    /**
     *
     */
    private interface Processors {
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
        }
    }

    /**
     *
     */
    private interface RamSticks {
        companion object {
            const val RAM_TABLE: String = "ram"

            //Columns
            const val RAM_NAME: String = "ram_name"
            const val RAM_SPEED: String = "ram_memory_speed_mhz"
            const val RAM_SIZE: String = "ram_memory_size_gb"
            const val RAM_DDR: String = "ram_ddr"
            const val NUM_OF_STICKS: String = "num_of_sticks"
        }
    }

    /**
     *
     */
    private interface PowerSupplys {
        companion object {
            const val PSU_TABLE: String = "psu"

            //Columns
            const val PSU_NAME: String = "psu_name"
            const val PSU_WATTAGE: String = "psu_wattage"
            const val PSU_RATING: String = "rating"
            const val PSU_IS_MODULAR: String = "modular"
        }
    }

    /**
     *
     */
    private interface StorageList {
        companion object {
            const val STORAGE_TABLE: String = "storage"

            //Columns
            const val STORAGE_NAME: String = "storage_name"
            const val STORAGE_TYPE: String = "storage_type"
            const val EXTERNAL_STORAGE: String = "external_storage"
            const val STORAGE_CAPACITY: String = "storage_capacity_gb"
            const val STORAGE_SPEED: String = "storage_speed_mbps"
        }
    }

    /**
     *
     */
    private interface Motherboards {
        companion object {
            const val MOTHERBOARD_TABLE: String = "motherboard"

            //Columns
            const val BOARD_NAME: String = "motherboard_name"
            const val BOARD_TYPE: String = "board_type"
            const val BOARD_DIMENSION: String = "board_dimensions"
            const val BOARD_CPU_SOCKET: String = "processor_socket"
            const val BOARD_DDR: String = "ddr_sdram"
            const val BOARD_USB3: String = "usb3+"
            const val BOARD_WIFI: String = "wifi"
            const val BOARD_PCIE: String = "pci-e"
            const val BOARD_NVME: String = "nvme_support"
        }
    }

    /**
     *
     */
    private interface Cases {
        companion object {
            const val CASE_TABLE: String = "cases"

            //Columns
            const val CASE_NAME: String = "cases_name"
            const val CASE_FAN_SLOTS: String = "case_fan_slots"
            const val CASE_FAN_SIZES: String = "case_fan_sizes_mm"
            const val CASE_BOARD: String = "case_motherboard"
            const val CASE_DIMENSIONS: String = "case_dimensions"
        }
    }

    /**
     *
     */
    private interface Heatsinks {
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
        }
    }

    /**
     *
     */
    private interface Fans {
        companion object {
            const val FAN_TABLE: String = "fan"

            //Columns
            const val FAN_NAME: String = "fan_name"
            const val FAN_SIZE: String = "fan_size_mm"
            const val FAN_SPEED: String = "fan_rpm"
        }
    }
}