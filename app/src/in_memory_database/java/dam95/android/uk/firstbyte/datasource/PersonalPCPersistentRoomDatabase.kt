package dan95.android.uk.firstbyte.datasource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dam95.android.uk.firstbyte.datasource.FirstByteRoomDB
import dam95.android.uk.firstbyte.model.builds.PCbuild
import dam95.android.uk.firstbyte.model.builds.PCbuildDAO

@Database(entities = [PCbuild::class], version = 1)
abstract class PersonalPCPersistentRoomDatabase : RoomDatabase(), FirstByteRoomDB {

    abstract override fun pcBuildDao(): PCbuildDAO

    override fun closeDatabase() {
        instanceOfDictionary?.close()
        instanceOfDictionary = null
    }

    companion object DictionaryList {
        private var instanceOfDictionary: PersonalPCPersistentRoomDatabase? = null

        /**
         * Get's the full dictionary database,
         * or the databases official name "Languages_dictionary_database"
         */
        fun getPCDatabase(context: Context): PersonalPCPersistentRoomDatabase? {
            //Limit only one thread to access the initializing of the dictionary database
            synchronized(this) {
                if (instanceOfDictionary == null) {
                    instanceOfDictionary = Room.databaseBuilder(
                        context.applicationContext,
                        PersonalPCPersistentRoomDatabase::class.java,
                        "personalPCs"
                    ).addCallback(languageDictionaryDatabaseCallback()).build()
                }
                return instanceOfDictionary!!
            }
        }

        private fun languageDictionaryDatabaseCallback(): Callback {
            return object : Callback() {
                override fun onCreate(languagesDictionaryDatabase: SupportSQLiteDatabase) {
                    Log.i("PC_DATABASE_BUILT", "personalPCs")
                    super.onCreate(languagesDictionaryDatabase)
                }
            }
        }
    }

}