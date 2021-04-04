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

/**
 * @author David Mckee
 * @Version 1.0
 * Sets up the Category search recycler list for retrieving all or the same category of component's from
 * FirstByte API.
 */
class SearchComponents : Fragment(), SearchCategoryRecyclerList.OnItemClickListener {

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
        val categoryHolder =
            ArrayList(listOf(*resources.getStringArray(R.array.categoriesDisplayText)))
        val categoryConnector =
            ArrayList(listOf(*resources.getStringArray(R.array.categoryConnector)))

        //Pair both loaded lists into their corresponding component types...
        //Search All is paired with "all", Graphics card is paired with "gpu"
        for (i in categoryHolder.indices) {
            categories.add(Pair(categoryHolder[i], categoryConnector[i]))
            Log.i("CATEGORY", categories[i].toString())
        }

        //Setup the the category recycler list
        val displayCategories = recyclerListBinding.recyclerList

        displayCategories.layoutManager = LinearLayoutManager(this.context)
        val categoryListAdapter = SearchCategoryRecyclerList(context, this, online = true)

        //Assign the online category list to the recycler list adapter
        categoryListAdapter.setDataList(categories)
        displayCategories.adapter = categoryListAdapter

    }

    /**
     * When a button is clicked, load into HardwareList and load the desired category of components.
     * API loading version.
     */
    override fun onButtonClick(chosenCategory: String) {
        Log.i("CHOSEN_CATEGORY", chosenCategory)
        //Finds the action that allows navigation from this fragment to the HardwareList,
        //with a bundle of the chosen category and a boolean to inform the fragment to load from the API.
        val categoryBundle = bundleOf(CATEGORY_KEY to chosenCategory, LOCAL_OR_NETWORK_KEY to true)
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_searchCategory_fragmentID_to_hardwareList_fragmentID,
            categoryBundle
        )
    }

}