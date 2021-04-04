package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dam95.android.uk.firstbyte.model.tables.FK_ON
import dam95.android.uk.firstbyte.model.tables.FirstByteSQLConstants
import java.lang.Exception

private const val DATABASE_NAME = "FB_TEST_DATABASE"
private const val NEW_DATABASE = 0

/**
 * Create the  in-memory database for the FirstByte app.
 * Can utilise upgrading at a later date after the end of the project.
 * @param context Used in SQLiteOpenHelper
*/
class FirstByteDBHandler(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    1
) {

    /**
     * When the database instance is initialised for the first time, create the database.
     * @param database
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
     * Currently not utilised, but typically used to upgrade the database
     */
    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            updateFBHardware(database, oldVersion, newVersion)

    }

    /**
     * If the database version is 0, then create the database for the first time. Otherwise upgrade the database.
     * @param database First Instance of this app's database
     * @param oldVersion Database old version
     * @param newVersion Database new version
     */
    private fun updateFBHardware(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //If it's the first version of the database, then initialize the database by creating relational tables
        //Allow foreign keys in SQLite
        database?.execSQL(FK_ON)
        if (oldVersion < 1) {
            buildNewFBDatabase(database)
            //If it is not the first version of the database and a new version is requested, then update/rebuild the database
        } else if (oldVersion < newVersion){
            rebuildFBDatabase(database, oldVersion, newVersion)
        }
    }

    /**
     * Create the database by iterating through the SQL creation commands within FirstByteSQLConstants.kt
     * @param database First Instance of this app's database
     */
    private fun buildNewFBDatabase(database: SQLiteDatabase?) {
        val creationCommands: List<String> = FirstByteSQLConstants.TABLE_CREATION_COMMANDS
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
     * Currently not implemented, no use for it in this project, but also struggling to see the use of an
     * update class that's called from a method that doesn't allow dynamic queries for updating different sections of the database.
     */
    private fun rebuildFBDatabase(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            //Create each table in Components Database
            Log.i("DB_CREATED", "Database successfully updated")
            //If there was an error, drop the database and exit out.
        } catch (exception: Exception) {
            database?.close()
            Log.e("DB_CREATE_ERROR", "Creation Error: ", exception)
            return
        }
    }
}