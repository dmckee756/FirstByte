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
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.Dispatchers

class FragmentPCBuildList : Fragment(), PcBuildRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var pcBuildListAdapter: PcBuildRecyclerList
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var pcListLiveData: LiveData<List<PCBuild?>>

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)

        fbHardwareDb = context?.let { FirstByteDBAccess.dbInstance(it, Dispatchers.Main) }!!
        pcListLiveData = fbHardwareDb.retrievePCList()

        pcListLiveData.observe(viewLifecycleOwner){
            setupPCList(it)
        }
        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setupPCList(pcList: List<PCBuild?>) {
        //
        val displayPCbuilds = recyclerListBinding.recyclerList
        //
        displayPCbuilds.layoutManager = LinearLayoutManager(this.context)
        pcBuildListAdapter = PcBuildRecyclerList(context, fbHardwareDb, this)


        pcBuildListAdapter.setDataList(pcList)
        displayPCbuilds.adapter = pcBuildListAdapter
    }

    /**
     *
     */
    override fun onButtonClick(pcBuild: PCBuild) {
        val nameBundle = bundleOf()
        nameBundle.putParcelable(SELECTED_PC, pcBuild)
        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_buildPC_fragmentID_to_personalBuild_fragmentID,
            nameBundle
        )
    }
}