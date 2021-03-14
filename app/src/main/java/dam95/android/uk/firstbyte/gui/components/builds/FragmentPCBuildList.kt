package dam95.android.uk.firstbyte.gui.components.builds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.model.pcbuilds.PCBuild

class FragmentPCBuildList : Fragment(), PcBuildRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var pcBuildListAdapter: PcBuildRecyclerList
    private lateinit var fb_Hardware_DB: ComponentDBAccess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)

        fb_Hardware_DB = context?.let { ComponentDBAccess.dbInstance(it) }!!
        setupPCList()
        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setupPCList() {
        //
        val displayPCbuilds = recyclerListBinding.recyclerList
        //
        displayPCbuilds.layoutManager = LinearLayoutManager(this.context)
        pcBuildListAdapter = PcBuildRecyclerList(context, fb_Hardware_DB, this)

        val pcList: List<PCBuild?> = fb_Hardware_DB.getPersonalPCList()

        pcList[0]?.case_name = "Cooler Master MasterBox MB511"
        pcBuildListAdapter.setDataList(pcList)
        displayPCbuilds.adapter = pcBuildListAdapter
    }

    override fun onButtonClick() {
        val nameBundle = bundleOf(
        )
        fb_Hardware_DB.closeDatabase()
        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_buildPC_fragmentID_to_personalBuild_fragmentID,
            nameBundle
        )
    }
}