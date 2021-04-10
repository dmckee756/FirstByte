package dam95.android.uk.firstbyte.gui.launchapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.ActivityFirstTimeSetupBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.configuration.*
import dam95.android.uk.firstbyte.gui.mainactivity.HomeActivity
import dam95.android.uk.firstbyte.model.SetupReadOnlyData
import kotlinx.coroutines.*
import java.util.*

/**
 * @author David Mckee
 * @Version 0.8
 * When the user launches the app for the first time, this class will first instruct the user to
 * select what type of PC they are wanting to build. The selection is between:
 * Home Casual PC, Gaming PC or a Workstation PC.
 * Once they have selected this, the user will load into the home screen.
 */
private const val FIRST_TIME_SETUP = "FIRST_TIME_SETUP"
private const val LOGO = "fb_logo.png"
private const val HOME_CASUAL = "HOME CASUAL"
private const val GAMING = "GAMING"
private const val WORKSTATION = "WORKSTATION"

class FirstTimeSetup : AppCompatActivity() {

    private lateinit var firstTimeSetupBinding: ActivityFirstTimeSetupBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fbHardwareDB: FirstByteDBAccess

    /**
     * Load the app's logo, the spinner used for selecting what type of PC the user is building
     * and the functionality to enter the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create recommended builds and build databases
        val coroutineScope = CoroutineScope(Dispatchers.Default)
        coroutineScope.launch {
            fbHardwareDB = FirstByteDBAccess(applicationContext, Dispatchers.Main)
        }

        //Display this activity
        firstTimeSetupBinding = ActivityFirstTimeSetupBinding.inflate(layoutInflater)
        setContentView(firstTimeSetupBinding.root)
        setSupportActionBar(null)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            baseContext
        )

        //Load the FirstByte logo
        try {
            val inputStream =
                baseContext.assets.open(LOGO)
            //Convert loaded image into drawable...
            val image = Drawable.createFromStream(inputStream, null)
            //Assign the drawable to image view if it exists
            image?.let { firstTimeSetupBinding.firstTimeSetupFBLogo.setImageDrawable(it) }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        //Set up the spinner
        val recommendedTypes = resources.getStringArray(R.array.recommendedBuildOptions).toList()
        initializeSpinner(recommendedTypes, firstTimeSetupBinding.selectRecommendedBuildSpinner)

        //Set up the button that enters into the app.
        firstTimeSetupBinding.enterAppBtn.setOnClickListener {
            //Enter the app's main activity.
            //Never launch this activity again.
            sharedPreferences.edit().putBoolean(FIRST_TIME_SETUP, false).apply()
            val launchBackIntoApp = Intent(this, HomeActivity::class.java)
            startActivity(launchBackIntoApp)
            finish()
        }

        runBlocking {
            coroutineScope.launch {
                //Sets up the database with the read only Components and recommended builds.
                SetupReadOnlyData(application, baseContext).loadReadOnlyValues()
                sharedPreferences.edit().putBoolean(FIRST_TIME_SETUP, false).apply()

                joinAll()
                //Once the database is created and all read only values saved, then allow the user to enter the app
                coroutineScope.launch(Dispatchers.Main) {
                    firstTimeSetupBinding.enterAppBtn.visibility = View.VISIBLE
                    firstTimeSetupBinding.enterAppText.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Setup spinner that allows users to choose recommended build on the home screen.
     */
    private fun initializeSpinner(recommendedTypes: List<String>, recommendedSpinner: Spinner) {
        recommendedTypes.setUpRecommendBuildSpinner(recommendedSpinner)
    }

    /**
     * Allows the user to select what type of recommended PC they see in the home screen.
     */
    private fun List<String>.setUpRecommendBuildSpinner(
        valueSpinner: Spinner
    ) {
        //
        val valueSelection =
            ArrayAdapter<String>(baseContext, android.R.layout.simple_spinner_item)
        valueSelection.addAll(this)

        valueSelection.setDropDownViewResource(android.R.layout.simple_list_item_1)
        valueSpinner.adapter = valueSelection

        valueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Ignore
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ignore
            }

            /**
             * Handles the spinners on click when the user changes value that is being compared in HardwareCompare.
             * It retrieves the function that HardwareCompare must call to retrieve all of the currently compared components values.
             */
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                //Assign the new starting PC ID for each type of recommended build set.
                when (valueSelection.getItem(position).toString().toUpperCase(Locale.ROOT)) {
                    HOME_CASUAL -> sharedPreferences.edit()
                        .putInt(RECOMMENDED_BUILDS, HOME_CASUAL_PCS_START).apply()
                    GAMING -> sharedPreferences.edit()
                        .putInt(RECOMMENDED_BUILDS, GAMING_PCS_START).apply()
                    WORKSTATION -> sharedPreferences.edit()
                        .putInt(RECOMMENDED_BUILDS, WORKSTATION_PCS_START).apply()
                }
            }
        }
    }
}