package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.runBlocking

const val READ_ONLY_PC = "READ_ONLY_PC"

//The last PC ID will always be n+3. n must never be 0 or less.
private const val TO_ENTHUSIAST_PC_ID = 3

/**
 * @author David Mckee
 * @Version 1.0
 * FirstBytes home page, displays 4 Recommended builds according to the user's choice.
 * Displays each Recommended PC Build tier in a recycler list:
 * Entry-Level PC, Budget PC, High-End PC and Enthusiast PC.
 * Each build can be clicked on to be inspected and can be saved to the database for the user to edit (creates new instance of the build).
 */
class Home : Fragment(), RecommendedBuildRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var recommendedListAdapter: RecommendedBuildRecyclerList
    private lateinit var fbHardwareDB: FirstByteDBAccess

    /**
     * Retrieves the components of each Read Only Recommended build and then sends it over to the recycler list adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)

        fbHardwareDB = FirstByteDBAccess(requireContext(), Dispatchers.Default)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val entryLevelPCID: Int = sharedPreferences.getInt(RECOMMENDED_BUILDS, 1)
        //Group the Recommended Builds and their tier descriptions together for display.
        val recommendedBuilds = mutableListOf<Pair<PCBuild, String>>()
        val tierDescriptions = resources.getStringArray(R.array.tierDescriptions)

        runBlocking {
            //Use another index for inserting the tierDescriptions along with the PC's ID.
            for ((index, pc_ID) in (entryLevelPCID..(entryLevelPCID + TO_ENTHUSIAST_PC_ID)).withIndex()) {
                fbHardwareDB.retrievePC(pc_ID).value?.let { PC ->
                    recommendedBuilds.add(
                        Pair(PC, tierDescriptions[index])
                    )
                }
            }
        }
        setUpRecommendedBuildList(recommendedBuilds.toList())
        return recyclerListBinding.root
    }

    /**
     * Sets up the RecommendedBuildRecyclerList Adapter and assigns the 4 Recommended PC Builds into the recycler list,
     * Allowing it to be displayed in the home page, with future possibility of additional builds if a developer wanted.
     * @param recommendedBuilds List of the 4 Tiers of Read Only recommended PC Builds.
     */
    private fun setUpRecommendedBuildList(recommendedBuilds: List<Pair<PCBuild, String>>) {
        //Finds and initialises the correct recycler list for this fragment.
        val displayDetails = recyclerListBinding.recyclerList
        displayDetails.layoutManager = LinearLayoutManager(this.context)
        recommendedListAdapter = RecommendedBuildRecyclerList(context, this, fbHardwareDB)
        //Assigns the PC list into the recycler list adapter
        recommendedListAdapter.setDataList(recommendedBuilds)
        displayDetails.adapter = recommendedListAdapter
    }

    /**
     * When the user clicks on a PC Build's PC Part "Gallery", it brings them to this method and navigates to the PersonalBuild fragment...
     * ...with values that indicate to the PersonalBuild fragment to create a read only display.
     * @param recommendedPC The recommended build the user clicked on to inspect
     */
    override fun onBuildButtonClick(recommendedPC: PCBuild) {
        //Puts a read only boolean and the selected PC into the bundle.
        val nameBundle = bundleOf(READ_ONLY_PC to true)
        nameBundle.putParcelable(SELECTED_PC, recommendedPC)
        //Finds the action that allows navigation from the home page to the PersonalBuild Fragment,
        //with a bundle of this PC's Details and informing the class it's read only.
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_home_fragmentID_to_personalBuild_fragmentID,
            nameBundle
        )
    }
}