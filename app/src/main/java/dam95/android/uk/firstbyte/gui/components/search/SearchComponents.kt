package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.gui.components.hardware.HardwareList
import dam95.android.uk.firstbyte.gui.mainactivity.HomeActivity

/**
 *
 */
class SearchComponents : Fragment(), SearchCategoryRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        //
        setupSearchSelection()
        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setupSearchSelection() {
        //
        val categories = listOf("all", "gpu", "cpu", "ram", "psu", "motherboard")
        val displayCategories = recyclerListBinding.recyclerList
        //
        displayCategories.layoutManager = LinearLayoutManager(this.context)
        displayCategories.adapter = SearchCategoryRecyclerList(context, this, categories)

    }

    /**
     *
     */
    override fun onButtonClick(chosenCategory: String) {
        //
        when (chosenCategory) {
            "all" -> (activity as HomeActivity).changeFragment(HardwareList(),false)
            "gpu" -> (activity as HomeActivity).changeFragment(HardwareList(),false)
        }
    }
}