package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.gui.mainactivity.HomeActivity

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"
private const val HARDWARELIST_KEY = "HARDWARELIST"
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
        val categories: ArrayList<Pair<String, String>> = ArrayList()
        val categoryHolder = ArrayList(listOf(*resources.getStringArray(R.array.categoriesDisplayText)))
        val categoryConnector = ArrayList(listOf(*resources.getStringArray(R.array.categoryConnector)))

        for (i in categoryHolder.indices) {
            categories.add(Pair(categoryHolder[i], categoryConnector[i]))
            Log.i("CATEGORY", categories[i].toString())
        }
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
        Log.i("CHOSEN_CATEGORY", chosenCategory)
            val categoryBundle = Bundle()
            categoryBundle.putString(CATEGORY_KEY, chosenCategory)
            (activity as HomeActivity).changeFragmentWithArgs(HARDWARELIST_KEY, categoryBundle)
    }
}