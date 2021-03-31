package dam95.android.uk.firstbyte.gui.mainactivity

import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import kotlinx.coroutines.Dispatchers

/**
 *
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
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val nightModeOn: Boolean = sharedPreferences.getBoolean(NIGHT_MODE, false)

        // If user put app into night mode, load it, otherwise load light mode
        if (nightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //Create recommended builds and build databases
        fbHardwareDB = FirstByteDBAccess(applicationContext, Dispatchers.Main)

        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)

        //
        val topAppBar = homeActivityBinding.HomeActTopBar
        setSupportActionBar(topAppBar)

        drawerLayout = homeActivityBinding.actMainDrawer
        navController = findNavController(R.id.nav_fragment)
        setUpNavigation()
    }

    /**
     *
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
     *
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.toolbar_menu_items, menu)
        return true
    }

    /**
     *
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

    @SuppressLint("ResourceType")
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