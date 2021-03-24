package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dam95.android.uk.firstbyte.model.tables.FK_ON
import dam95.android.uk.firstbyte.model.tables.SQLComponentConstants
import java.lang.Exception

private const val DATABASE_NAME = "FB_TEST_DATABASE"
private const val NEW_DATABASE = 0

/**
 * This is an in-memory database for the FirstByte app.
 */
class FirstByteDBHandler(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    1
) {

    /**
     *
     */
     override fun onCreate(database: SQLiteDatabase?) {
            Log.i("DATABASE_START", "Creating $DATABASE_NAME...")
            updateFBHardware(
                database,
                NEW_DATABASE,
                1
            )
    }

    /**
     *
     */
    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            updateFBHardware(database, oldVersion, newVersion)

    }

    /**
     *
     */
    private fun updateFBHardware(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //If it's the first version of the database, then initialize the database by creating relational tables
        //Allow foreign keys in SQLite
        database?.execSQL(FK_ON)
        if (oldVersion < 1) {
            buildNewFBDatabase(database)
            //If it is not the first version of the database and a new version is requested, then update/rebuild the database
        } else if (oldVersion < newVersion){
            rebuildFBDatabase(database)
        }
    }

    /**
     *
     */
    private fun buildNewFBDatabase(database: SQLiteDatabase?) {
        val creationCommands: List<String> = SQLComponentConstants.TABLE_CREATION_COMMANDS
        try {
            //Create each table in Components Database
            for (createTable in (creationCommands)) database?.execSQL(createTable)
            Log.i("DB_CREATED", "Components Database successfully created")
            //If there was an error, drop the database and exit out.
        } catch (exception: Exception) {
            database?.close()
            Log.e("DB_CREATE_ERROR", "Creation Error: ", exception)
            return
        }

    }

    /**
     *
     */
    private fun rebuildFBDatabase(database: SQLiteDatabase?) {
        //We keep this empty currently as there is there will not be updates to this database
    }
}