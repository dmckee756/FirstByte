package dam95.android.uk.firstbyte.gui.mainactivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.InterfaceAPI
import dam95.android.uk.firstbyte.api.RetrofitBuild
import dam95.android.uk.firstbyte.databinding.FragmentHomeBinding
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Home : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return homeBinding.root
    }
}