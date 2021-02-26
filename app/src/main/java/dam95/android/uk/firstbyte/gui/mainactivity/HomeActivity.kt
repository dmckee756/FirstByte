package dam95.android.uk.firstbyte.gui.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.ActivityHomeBinding
import dam95.android.uk.firstbyte.gui.components.builds.PCBuilds
import dam95.android.uk.firstbyte.gui.components.compare.SelectCompare
import dam95.android.uk.firstbyte.gui.components.search.SearchComponents

/**
 *
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var homeActivityBinding: ActivityHomeBinding

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)

        //
        val topAppBar = homeActivityBinding.HomeActTopBar
        setSupportActionBar(topAppBar)

        val drawer: DrawerLayout = homeActivityBinding.actMainDrawer
        val drawerToggle = ActionBarDrawerToggle(
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

        setUpScrollerNavigation()
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
            R.id.searchID -> changeFragment(SearchComponents(), initialStart = false)
            // Display a tip to the user
            R.id.tipsID -> Toast.makeText(this, "Tip Displayed", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     *
     */
    private fun setUpScrollerNavigation() {
        changeFragment(Home(), initialStart = true)
        val homeBtn = homeActivityBinding.homeBtn
        homeBtn.setOnClickListener {
            changeFragment(Home(), initialStart = false)
        }
        val pcBuildBtn = homeActivityBinding.pcBuildBtn
        pcBuildBtn.setOnClickListener {
            changeFragment(PCBuilds(), initialStart = false)
        }
        val selectCompareBtn = homeActivityBinding.selectCompareBtn
        selectCompareBtn.setOnClickListener {
            changeFragment(SelectCompare(), initialStart = false)
        }
    }

    /**
     *
     */
    fun changeFragment(fragmentID: Fragment, initialStart: Boolean) { //, package: SOMETHING) if package, then...
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_fragment, fragmentID)
            if (!initialStart) addToBackStack(null)
            commit()
        }
    }

}