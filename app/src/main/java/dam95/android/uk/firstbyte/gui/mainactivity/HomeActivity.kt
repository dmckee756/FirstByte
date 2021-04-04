package dam95.android.uk.firstbyte.gui.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.ActivityHomeBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.configuration.NIGHT_MODE
import dam95.android.uk.firstbyte.model.SetupReadOnlyData
import kotlinx.coroutines.*

private const val FIRST_TIME_SETUP = "FIRST_TIME_SETUP"

/**
 * @author David Mckee
 * @Version 0.9
 * The main class used to setup the app for the first time.
 * Utilises Android's JetPack Navigation, allowing a one activity app.
 * The app can only be views in vertical mode and is not designed for tablets, only smart phones.
 * This class creates the first instance of the app's database, and on it's destruction closes the database.
 * Overrides some methods that is utilised/called back to throughout the app's fragments.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var fbHardwareDB: FirstByteDBAccess

    private lateinit var homeActivityBinding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationBar: BottomNavigationView
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var navListener: NavController.OnDestinationChangedListener

    /**
     * This on create method Initiates first time setup, loading the read only components and
     * pc builds into the database by calling the SetUpReadOnlyData class.
     * Initialises the app bar, toggle drawer and assigns the navigation controller.
     * Apply's the app's current theme.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //Create recommended builds and build databases
        fbHardwareDB = FirstByteDBAccess(applicationContext, Dispatchers.Main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        if (sharedPreferences.getBoolean(FIRST_TIME_SETUP, true)) {
            SetupReadOnlyData(application, applicationContext).loadReadOnlyValues()
            sharedPreferences.edit().putBoolean(FIRST_TIME_SETUP, false).apply()
        }

        super.onCreate(savedInstanceState)
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)

        navController = findNavController(R.id.nav_fragment)

        val nightModeOn: Boolean = sharedPreferences.getBoolean(NIGHT_MODE, false)

        // If user put app into night mode, load it, otherwise load light mode
        if (nightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //
        val topAppBar = homeActivityBinding.HomeActTopBar
        setSupportActionBar(topAppBar)

        drawerLayout = homeActivityBinding.actMainDrawer

        setUpNavigation()
    }

    /**
     * Sets up the JetPack Navigation for both the bottom navigation bar and the Toggle Drawer.
     * Allows a very easy method of navigation utilising the navigation menu in the XML resources.
     */
    private fun setUpNavigation() {

        //Setup navigation for toggle Drawer and bottom navigation bar
        bottomNavigationBar = homeActivityBinding.bottomNav
        bottomNavigationBar.setupWithNavController(navController)
        homeActivityBinding.navDrawer.setupWithNavController(navController)

        //Let Jet Pack handle the navigation of the application and allow it to set up the toggle drawer
        //Allowing back navigation on other fragments.
        appBarConfig = AppBarConfiguration(
            navController.graph, drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfig)
        navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home_fragmentID -> bottomNavigationBar.visibility = View.VISIBLE
                R.id.buildPC_fragmentID -> bottomNavigationBar.visibility = View.VISIBLE
                R.id.compare_fragmentID -> bottomNavigationBar.visibility = View.VISIBLE
                else -> bottomNavigationBar.visibility = View.GONE
            }
        }

        navController.addOnDestinationChangedListener(navListener)
    }

    /**
     * Creates the generic app toolbar menu with shared items. Used on the main 3 front screens.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.toolbar_menu_items, menu)
        return true
    }

    /**
     * Creates and handles on click events with the app's main 3 front screens app bar.
     * Navigates to online search category list when the user clicks on the magnifying glass.
     * Displays a random tip when the user clicks on the Speech icon in the 3 dots icon menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Otherwise execute toolbar button command.
        when (item.itemId) {
            // Navigate to search components fragment
            R.id.searchCategory_fragmentID -> item.onNavDestinationSelected(navController)
            // Display a tip to the user
            R.id.tipsID -> Toast.makeText(this, "Tip Displayed", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * If the toggleDrawer is clicked on when the icon is the back arrow,
     * navigate one place up the fragment stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
                || super.onSupportNavigateUp()
    }

    /**
     * If the Toggle Drawer menu is currently open, the back button will close it first before popping the navigation stack.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    /**
     * Close database on app's exit
     */
    override fun onDestroy() {
        fbHardwareDB.closeDatabase()
        super.onDestroy()
    }
}