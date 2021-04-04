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
 * @author David Mckee
 * @Version 1.0
 * Sets up the Category search recycler list for retrieving all or the same category of component's from
 * the App's Database.
 */
class SearchSavedComponents : Fragment(), SearchCategoryRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding

    /**
     * Re-uses the recycler list layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        setupSearchSelection()
        return recyclerListBinding.root
    }

    /**
     * Loads 2 string resource arrays for displaying each category of components the user can select from,
     * then creates the recycler list adapter with these values.
     */
    private fun setupSearchSelection() {

        //Load in 2 lists of category search options for the user.
        val categories: ArrayList<Pair<String, String>> = ArrayList()
        val categoryHolder = ArrayList(listOf(*resources.getStringArray(R.array.categoriesDisplayText)))
        //For this offline variant, change the search all text to "Search All Saved Components" to show it's an offline search.
        categoryHolder[CATEGORY_ALL] = resources.getString(R.string.offlineSearchAll)
        val categoryConnector = ArrayList(listOf(*resources.getStringArray(R.array.categoryConnector)))

        //Pair both loaded lists into their corresponding component types...
        //Search All is paired with "all", Graphics card is paired with "gpu"
        for (i in categoryHolder.indices) {
            categories.add(Pair(categoryHolder[i], categoryConnector[i]))
            Log.i("CATEGORY", categories[i].toString())
        }

        //Setup the the category recycler list
        val displayCategories = recyclerListBinding.recyclerList

        displayCategories.layoutManager = LinearLayoutManager(this.context)
        val categoryListAdapter = SearchCategoryRecyclerList(context, this, online = false)

        //Assign the saved category list to the recycler list adapter
        categoryListAdapter.setDataList(categories)
        displayCategories.adapter = categoryListAdapter
    }

    /**
     * When a button is clicked, load into HardwareList and load the desired category of components.
     * App's database version.
     */
    override fun onButtonClick(chosenCategory: String) {
        Log.i("CHOSEN_CATEGORY", chosenCategory)
        //Finds the action that allows navigation from this fragment to the HardwareList,
        //with a bundle of the chosen category and a boolean to inform the fragment to load from the App's database.
        val categoryBundle =
            bundleOf(CATEGORY_KEY to chosenCategory, LOCAL_OR_NETWORK_KEY to false, PC_ID to -1)
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_searchSavedComponents_fragmentID_to_hardwareList_fragmentID,
            categoryBundle
        )
    }
}