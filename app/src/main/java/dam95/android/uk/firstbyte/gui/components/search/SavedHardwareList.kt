package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"
private const val NAME_KEY = "NAME"
private const val OFFLINE_LOAD_KEY = "OFFLINE"

class SavedHardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var recyclerListBinding: RecyclerListBinding

    private lateinit var hardwareListAdapter: HardwareListRecyclerList

    private var searchCategory: String? = null

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchCategory = arguments?.getString(CATEGORY_KEY)
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        if (searchCategory != null) {
            Log.i("SEARCH_CATEGORY", searchCategory!!)


            setUpSearch()

        }
        // Inflate the layout for this fragment
        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setUpSearch() {
        //Display the search view for the hardware list
        val searchView = recyclerListBinding.hardwareListSearchViewID
        searchView.visibility = View.VISIBLE
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
    }

    /**
     *
     */
    private fun setUpHardwareList() {

        val displayHardwareList = recyclerListBinding.recyclerList
        //
        displayHardwareList.layoutManager = LinearLayoutManager(this.context)
        hardwareListAdapter = HardwareListRecyclerList(context, this)

        assignListToRecycler()

        displayHardwareList.adapter = hardwareListAdapter
    }

    /**
     *
     */
    private fun assignListToRecycler() {

    }

    /**
     *
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.hardwarelist_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * TO FINISH and to get working
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Bring up a sorting menu
            R.id.sortID -> Log.i("SORT", "Sort")
            // Bring up a filter menu
            R.id.filterID -> Log.i("FILTER", "Filter")
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     *
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }


    /**
     *
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            if (newText != "") {

            } else {

            }
        }
        return true
    }

    /**
     *
     */
    override fun onHardwareClick(componentName: String, componentType: String) {

        val nameBundle = bundleOf(NAME_KEY to componentName, CATEGORY_KEY to componentType, OFFLINE_LOAD_KEY to OFFLINE_LOAD_KEY)

        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(R.id.action_savedHardwareList_fragmentID_to_hardwareDetails_fragmentID, nameBundle)
    }
}