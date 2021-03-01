package dam95.android.uk.firstbyte.gui.mainactivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dam95.android.uk.firstbyte.gui.components.builds.FragmentPCBuildList
import dam95.android.uk.firstbyte.gui.components.compare.CompareHardware

/**
 * For future use, invalid now
 */
class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Home()
            1 -> FragmentPCBuildList()
            else -> CompareHardware()
        }
    }
}