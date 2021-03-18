package dam95.android.uk.firstbyte.model.util

import dam95.android.uk.firstbyte.model.components.*
import java.util.*

/**
 *
 */
object DataClassTemplate {

    fun createTemplateObject(type: String): Component {
        return when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> Gpu(
                "",
                "",
                "",
                0,
                0,
                0,
                0,
                "",
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.CPU.toString() -> Cpu(
                "",
                "",
                "",
                0.0,
                0,
                0,
                "",
                0,
                0,
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.RAM.toString() -> Ram(
                "",
                "",
                "",
                0,
                0,
                "",
                0,
                0.0,
                0.0,
                null,
                null,
                null
            )
            ComponentsEnum.PSU.toString() -> Psu("", "", "", 0, "", 0, 0.0, null, null, null, null)
            ComponentsEnum.STORAGE.toString() -> Storage(
                "",
                "",
                "",
                "",
                0,
                0,
                0,
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.MOTHERBOARD.toString() -> Motherboard(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                0.0,
                0,
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.CASES.toString() -> Case(
                "",
                "",
                "",
                0,
                0,
                "",
                "",
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.HEATSINK.toString() -> Heatsink(
                "",
                "",
                "",
                0,
                null,
                null,
                null,
                null,
                "",
                0.0,
                null,
                null,
                null,
                null
            )
            ComponentsEnum.FAN.toString() -> Fan("", "", "", 0, 0, 0.0, null, null, null, null)
            else -> null
        } ?: throw java.lang.NullPointerException()
    }
}