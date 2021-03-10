package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.datasource.util.SQLConstantCreation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

const val DATABASE_NAME = "AndroidFB_Hardware"
const val NEW_DATABASE = 0

class ComponentDBHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    context.resources.getInteger(R.integer.AndroidFB_HardwareVersion)
) {
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     *
     */
    override fun onCreate(database: SQLiteDatabase?) {
        coroutineScope.launch {
            updateFBHardware(
                database,
                NEW_DATABASE,
                context.resources.getInteger(R.integer.AndroidFB_HardwareVersion)
            )
        }
    }

    /**
     *
     */
    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        coroutineScope.launch {
            updateFBHardware(database, oldVersion, newVersion)
        }
    }

    /**
     *
     */
    private fun updateFBHardware(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 1) {
            buildNewFBDatabase(database)
        } else {
            rebuildFBDatabase(database)
        }
    }

    /**
     *
     */
    private fun buildNewFBDatabase(database: SQLiteDatabase?) {
        val creationCommands: List<String> = SQLConstantCreation.TABLE_CREATION_COMMANDS
        try {
            for (createTable in (creationCommands)) executeQuery(database, createTable)
        } catch (exception: Exception) {
            Log.e("DB_CREATE_ERROR", "Creation Error: ", exception)
        }

    }

    /**
     *
     */
    private fun rebuildFBDatabase(database: SQLiteDatabase?) {
        //We keep this empty currently as there is there will not be updates to this database
    }

    /**
     * This is a generic query execution method for CRUD commands into the database,
     * whilst more structured methods would be better, currently this generic method will suffice to minimise coding time.
     */
    private fun executeQuery(database: SQLiteDatabase?, sqlQuery: String) {
        try {
            //Insert generic imported SQL query into the database...
            database?.execSQL(sqlQuery)
            //...If query is unsuccessful, catch an exception and notify the developer.
        } catch (exception: Exception) {
            Log.e("DB_QUERY_ERROR", "Error: ", exception)
        }
    }
}