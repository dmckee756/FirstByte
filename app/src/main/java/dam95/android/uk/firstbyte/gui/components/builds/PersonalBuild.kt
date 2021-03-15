package dam95.android.uk.firstbyte.gui.components.builds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.FragmentPersonalBuildBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.model.components.Component


class PersonalBuild : Fragment(), PersonalBuildRecyclerList.OnItemClickListener {

    private lateinit var personalBuildBinding: FragmentPersonalBuildBinding
    private lateinit var personalBuildListAdapter: PersonalBuildRecyclerList
    private lateinit var fb_Hardware_DB: ComponentDBAccess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        personalBuildBinding = FragmentPersonalBuildBinding.inflate(inflater, container, false)
        fb_Hardware_DB = ComponentDBAccess(requireContext())

        setUpPCDisplay()
        return personalBuildBinding.root
    }

    private fun setUpPCDisplay(){
        //
        val displayDetails = personalBuildBinding.pcDetailsRecyclerList
        //
        displayDetails.layoutManager = LinearLayoutManager(this.context)
        personalBuildListAdapter = PersonalBuildRecyclerList(context, fb_Hardware_DB, this)

        personalBuildListAdapter.setDataList(listOf(null))
        displayDetails.adapter = personalBuildListAdapter
    }

    override fun onButtonClick() {
        TODO("Not yet implemented")
    }
}