package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam95.android.uk.firstbyte.databinding.FragmentHomeBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess

class Home : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var createDB: ComponentDBAccess
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        createDB = ComponentDBAccess(requireContext())


        return homeBinding.root
    }

    override fun onDestroy() {
        createDB.closeDatabase()
        super.onDestroy()
    }
}