package dam95.android.uk.firstbyte.gui.components.compare

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.charts.BarChart
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.FragmentCompareHardwareBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess


private const val GPU_COMPARE = "GPU_COMPARE_LIST"
private const val CPU_COMPARE = "CPU_COMPARE_LIST"
private const val RAM_COMPARE = "RAM_COMPARE_LIST"

class CompareHardware : Fragment(), CompareHardwareRecyclerList.OnItemClickListener {

    private lateinit var compareHardwareBinding: FragmentCompareHardwareBinding
    private lateinit var fbHardwareDb: FirstByteDBAccess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        compareHardwareBinding = FragmentCompareHardwareBinding.inflate(inflater, container, false)

        val compareBarChart: MutableLiveData<BarChart> = MutableLiveData()
        compareBarChart.value = compareHardwareBinding.compareBarChart





        compareBarChart.observe(viewLifecycleOwner){ barChart ->

        }

        return compareHardwareBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.hardware_related_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}