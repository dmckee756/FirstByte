package dam95.android.uk.firstbyte.gui.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.ActivityHomeBinding

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

        setUpScrollerNavigation()
    }

    private fun setUpScrollerNavigation() {

        val homeBtn = homeActivityBinding.homeBtn
        homeBtn.setOnClickListener {
            changeFragment(R.id.homeFragment)
        }
        val pcBuildBtn = homeActivityBinding.pcBuildBtn
        pcBuildBtn.setOnClickListener {
            changeFragment(R.id.pcBuildFragment)
        }
        val compareBtn = homeActivityBinding.compareBtn
        compareBtn.setOnClickListener {
            changeFragment(R.id.compareFragment)
        }
    }

    private fun changeFragment(fragmentID: Int) {
        supportFragmentManager.beginTransaction().apply {

        }
    }

}