package dam95.android.uk.firstbyte.gui.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.ActivityHomeBinding
import dam95.android.uk.firstbyte.gui.components.builds.FragmentPCBuildList
import dam95.android.uk.firstbyte.gui.components.compare.SelectCompare
import dam95.android.uk.firstbyte.gui.components.hardware.HardwareDetails
import dam95.android.uk.firstbyte.gui.components.search.HardwareList
import dam95.android.uk.firstbyte.gui.components.search.SearchComponents

/**
 *
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var homeActivityBinding: ActivityHomeBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var  navController: NavController

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)
        navController = findNavController(R.id.nav_fragment)

        //
        val topAppBar = homeActivityBinding.HomeActTopBar
        setSupportActionBar(topAppBar)

        val drawer: DrawerLayout = homeActivityBinding.actMainDrawer
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            topAppBar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        drawerToggle.toolbarNavigationClickListener = View.OnClickListener {
            onBackPressed()
        }

        setUpNavigationBottomNavigation()
    }

    /**
     *
     */
    private fun setUpNavigationBottomNavigation() {

        val bottomNavBar = homeActivityBinding.bottomNav
        //Set up bottom navigation bar for 4 fragments
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.pcBuildFragment,
                R.id.compareFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfig)
        bottomNavBar.setupWithNavController(navController)

    }

    /**
     *
     */
    private fun bringBackNavBar() {
        if (homeActivityBinding.bottomNav.visibility == View.GONE) homeActivityBinding.bottomNav.visibility =
            View.VISIBLE
    }

    /**
     *
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_items, menu)
        return true
    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Navigate to search components fragment
            R.id.searchCategory_fragmentID -> item.onNavDestinationSelected(navController)
                // Display a tip to the user
                R.id.tipsID -> Toast.makeText(this, "Tip Displayed", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     *
     */
    override fun onBackPressed() {
        bringBackNavBar()
        super.onBackPressed()
    }
}