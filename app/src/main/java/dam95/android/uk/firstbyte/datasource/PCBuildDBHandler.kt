package dam95.android.uk.firstbyte.datasource

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.datasource.util.FK_ON
import dam95.android.uk.firstbyte.datasource.util.SQLPcBuildConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

private const val DATABASE_NAME = "PCBuildList"
private const val NEW_DATABASE = 0
class PCBuildDBHandler(val context: Context) : SQLiteOpenHelper(
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
            Log.i("DATABASE_START", "Creating $DATABASE_NAME...")
            updatePCBuildList(
                database,
                NEW_DATABASE,
                context.resources.getInteger(R.integer.PC_Build_DBVersion)
            )
        }
    }

    /**
     *
     */
    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        coroutineScope.launch {
            updatePCBuildList(database, oldVersion, newVersion)
        }
    }

    /**
     *
     */
    private fun updatePCBuildList(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //If it's the first version of the database, then initialize the database by creating relational tables
        database?.execSQL(FK_ON)
        if (oldVersion < 1) {
            buildNewPCBuildDatabase(database)
            //If it is not the first version of the database and a new version is requested, then update/rebuild the database
        } else if (oldVersion < newVersion){
            rebuildPCBuildDatabase(database)
        }
    }

    /**
     *
     */
    private fun buildNewPCBuildDatabase(database: SQLiteDatabase?) {
        val creationCommands: List<String> = SQLPcBuildConstants.TABLE_CREATION_COMMANDS
        //Allow foreign keys in SQLite
        try {
            //Create each table in PCBuild Database
            for (createTable in (creationCommands)) database?.execSQL(createTable)
            Log.i("DB_CREATED", "PCBuilds Database successfully created")
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
    private fun rebuildPCBuildDatabase(database: SQLiteDatabase?) {
        //We keep this empty for now
    }
}