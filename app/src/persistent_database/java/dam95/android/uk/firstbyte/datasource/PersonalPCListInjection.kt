package dan95.android.uk.firstbyte.datasource

import android.content.Context
import dam95.android.uk.firstbyte.datasource.FirstByteRoomDB

/**
 *
 */
object PersonalPCListInjection {
    fun getPCDatabase(context: Context): FirstByteRoomDB = PersonalPCPersistentRoomDatabase.getPCDatabase(
        context
    )!!
}