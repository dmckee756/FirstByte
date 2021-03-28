package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.Dispatchers

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
        setUpRecommendedBuildList()

        return recyclerListBinding.root
    }

    private fun setUpRecommendedBuildList(){
        //
        val displayDetails = recyclerListBinding.recyclerList
        //
        displayDetails.layoutManager = LinearLayoutManager(this.context)
        recommendedListAdapter = RecommendedBuildRecyclerList(context, fbHardwareDB, this)
        val pairList: MutableList<Pair<PCBuild, String>> = mutableListOf()
        val pcTiers = listOf("Entry-Level PC", "Budget PC", "High-End PC", "Enthusiast PC")
        for (i in 0..3){
            val pcBuild = PCBuild()
            pcBuild.pcName = pcTiers[i]
            pcBuild.pcPrice = 0.00

            pairList.add(Pair(pcBuild, pcTiers[i]))
        }

        recommendedListAdapter.setDataList(pairList)
        displayDetails.adapter = recommendedListAdapter
    }

    override fun onBuildButtonClick(recommendedPC: PCBuild) {

    }
}