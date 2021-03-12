package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam95.android.uk.firstbyte.databinding.FragmentHomeBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.model.components.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Home : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val coroutine = CoroutineScope(Dispatchers.Main)
        coroutine.launch {
            val componentsComponentDB: ComponentDBAccess =
                context?.let { ComponentDBAccess.dbInstance(it) }!!

            val component: Component? =
                componentsComponentDB.getHardware("EVGA NVIDIA GeForce GTX 1080", "gpu")
            homeBinding.txt.text = (component?.toString() + "\n\n THIS IS A TEST FOR LOADING HARDWARE COMPONENT OBJECT!")
        }
        return homeBinding.root
    }
}