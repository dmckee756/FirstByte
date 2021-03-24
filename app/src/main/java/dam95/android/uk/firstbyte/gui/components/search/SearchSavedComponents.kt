package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding


private const val CATEGORY_ALL = 0

/**
 *
 */
class SearchSavedComponents : Fragment(), SearchCategoryRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        setupSearchSelection()

        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setupSearchSelection() {
        //
        val categories: ArrayList<Pair<String, String>> = ArrayList()
        val categoryHolder =
            ArrayList(listOf(*resources.getStringArray(R.array.categoriesDisplayText)))
        categoryHolder[CATEGORY_ALL] = resources.getString(R.string.offlineSearchAll)
        val categoryConnector =
            ArrayList(listOf(*resources.getStringArray(R.array.categoryConnector)))

        //
        for (i in categoryHolder.indices) {
            categories.add(Pair(categoryHolder[i], categoryConnector[i]))
            Log.i("CATEGORY", categories[i].toString())
        }
        val displayCategories = recyclerListBinding.recyclerList
        //
        displayCategories.layoutManager = LinearLayoutManager(this.context)
        displayCategories.adapter =
            SearchCategoryRecyclerList(context, this, categories, online = false)

    }

    /**
     *
     */
    override fun onButtonClick(chosenCategory: String) {
        Log.i("CHOSEN_CATEGORY", chosenCategory)
        //
        val categoryBundle =
            bundleOf(CATEGORY_KEY to chosenCategory, LOCAL_OR_NETWORK_KEY to false, PC_ID to -1)

        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_searchSavedComponents_fragmentID_to_hardwareList_fragmentID,
            categoryBundle
        )
    }
}