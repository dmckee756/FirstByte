package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.builds.SELECTED_PC
import dam95.android.uk.firstbyte.gui.configuration.RECOMMENDED_BUILDS
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.Dispatchers
const val READ_ONLY_PC = "READ_ONLY_PC"
//The last PC ID will always be n+3. n must never be 0 or less.
private const val TO_ENTHUSIAST_PC_ID = 3
class Home : Fragment(), RecommendedBuildRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var recommendedListAdapter: RecommendedBuildRecyclerList
    private lateinit var fbHardwareDB: FirstByteDBAccess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)

        fbHardwareDB = FirstByteDBAccess(requireContext(), Dispatchers.Main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val entryLevelPCID: Int = sharedPreferences.getInt(RECOMMENDED_BUILDS, 1)
        val recommendedBuilds = mutableListOf<PCBuild>()

        for (pc_ID in entryLevelPCID..(entryLevelPCID + TO_ENTHUSIAST_PC_ID)){
            fbHardwareDB.retrievePC(pc_ID).value?.let { PC -> recommendedBuilds.add(PC) }
        }

        setUpRecommendedBuildList(recommendedBuilds.toList())

        return recyclerListBinding.root
    }

    private fun setUpRecommendedBuildList(recommendedBuilds: List<PCBuild>) {
        //
        val displayDetails = recyclerListBinding.recyclerList
        //
        displayDetails.layoutManager = LinearLayoutManager(this.context)
        recommendedListAdapter = RecommendedBuildRecyclerList(context, this, fbHardwareDB)

        recommendedListAdapter.setDataList(recommendedBuilds)
        displayDetails.adapter = recommendedListAdapter
    }

    override fun onBuildButtonClick(recommendedPC: PCBuild) {
        val nameBundle = bundleOf(READ_ONLY_PC to true)
        nameBundle.putParcelable(SELECTED_PC, recommendedPC)
        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_home_fragmentID_to_personalBuild_fragmentID,
            nameBundle
        )
    }
}