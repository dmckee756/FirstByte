package dam95.android.uk.firstbyte.gui.components.builds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.mainactivity.READ_ONLY_PC
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.Dispatchers

/**
 * @author David Mckee
 * @Version 1.0
 * This fragment displays all writable PC builds currently saved to the app's database.
 * It allows the user to enter into their already created PC Builds for editing, or creating a new PC build if
 * there is a slot available. There can only be 10 Writable PC Builds on the App at once.
 */
class PCBuildList : Fragment(), PcBuildRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var pcBuildListAdapter: PcBuildRecyclerList
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var pcListLiveData: LiveData<List<PCBuild?>>

    /**
     * Retrieve all currently create writable PC Builds from the database and fill the rest of the slots with null.
     * Then pass the information into the recycler list.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)

        //Load all pc builds
        fbHardwareDb = context?.let { FirstByteDBAccess.dbInstance(it, Dispatchers.Default) }!!
        pcListLiveData = fbHardwareDb.retrievePCList()

        //Observe these builds and pass it into the recycler list adapter for display.
        pcListLiveData.observe(viewLifecycleOwner){
            setupPCList(it)
        }
        return recyclerListBinding.root
    }

    /**
     * Sets up the recycler list for displaying all PC Builds on the app, and allowing the user
     * to create new PC's if there is an available slot.
     * @param pcList List of all the writeable PC Builds on the app's database.
     */
    private fun setupPCList(pcList: List<PCBuild?>) {
        //Finds and initialises the correct recycler list for this fragment.
        val displayPCbuilds = recyclerListBinding.recyclerList
        displayPCbuilds.layoutManager = LinearLayoutManager(this.context)
        pcBuildListAdapter = PcBuildRecyclerList(context, fbHardwareDb, this)
        //Assigns the all writable PC Builds into the recycler list adapter
        pcBuildListAdapter.setDataList(pcList)
        displayPCbuilds.adapter = pcBuildListAdapter
    }

    /**
     * When a user clicks on a occupied PC Build slot, navigate to the PersonalBuild fragment to edit the PCBuild.
     * When the user clicks on an empty PC Build slot, create the PC and to the PersonalBuild fragment to edit the PCBuild.
     * @param pcBuild the selected PCBuild, can be an already existing PC build, or a newly created PC build
     */
    override fun onButtonClick(pcBuild: PCBuild) {
        val nameBundle = bundleOf(READ_ONLY_PC to false)
        nameBundle.putParcelable(SELECTED_PC, pcBuild)
        //Finds the action that allows navigation from this PCBuildList fragment to the PersonalBuild Fragment,
        //with a bundle of the selected PC Build that the user is editing/creating and informing the class it's writable.
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_buildPC_fragmentID_to_personalBuild_fragmentID,
            nameBundle
        )
    }
}