package dam95.android.uk.firstbyte.model.builds

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 *
 */
@Dao
interface PCbuildDAO {

    @Insert
    fun insertBuild(pc: PCbuild)

    @Delete
    fun deleteBuild(pc: PCbuild)

    @Query("DELETE FROM personalPCs")
    fun deleteAllBuilds()

    @Query("SELECT * FROM personalPCs")
    fun retrieveAllPCs(): LiveData<List<PCbuild>>
}