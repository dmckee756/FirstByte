package dam95.android.uk.firstbyte.model.builds

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dam95.android.uk.firstbyte.model.components.*
import java.io.Serializable

/**
 *
 */
@Entity(tableName = "personalPCs")
data class PCbuild(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var pcID: Int,
    @ColumnInfo(name = "name")
    var pcName: String = "New PC"
) : Serializable {
/*
    @ColumnInfo(name = "gpu")
    var gpu: Gpu? = null

    @ColumnInfo(name = "cpu")
    var cpu: Cpu? = null

    @ColumnInfo(name = "ram")
    var ram: Ram? = null

    @ColumnInfo(name = "psu")
    var psu: Psu? = null

    @ColumnInfo(name = "storage")
    private lateinit var storage: Array<Storage?>

    @ColumnInfo(name = "heatsink")
    var heatsink: Heatsink? = null

    @ColumnInfo(name = "case")
    var case: Case? = null

    @ColumnInfo(name = "fans")
    private lateinit var fan: Array<Fan?>
*/

}