package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
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

        val pcTiers = listOf("Entry-Level PC", "Budget PC", "High-End PC", "Enthusiast PC")
        recommendedListAdapter.setDataList(pcTiers)
        displayDetails.adapter = recommendedListAdapter
    }

    override fun onBuildButtonClick() {
        TODO("Not yet implemented")
    }

    override fun onLeftImageClick() {
        TODO("Not yet implemented")
    }

    override fun onRightImageClick() {
        TODO("Not yet implemented")
    }
}