package dam95.android.uk.firstbyte.datasource

import dam95.android.uk.firstbyte.model.builds.PCbuildDAO

/**
 *
 */
interface FirstByteRoomDB {
    fun pcBuildDao(): PCbuildDAO
    fun closeDatabase()
}