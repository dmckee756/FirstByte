package dam95.android.uk.firstbyte.gui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.FragmentLicensesAndLibrariesBinding

/**
 * @author David Mckee
 * @Version 1.0
 * Displays all used libraries and licences in this app.
 * Excluding testing libraries.
 */
class Licenses : Fragment() {

    private lateinit var licenceBinding: FragmentLicensesAndLibrariesBinding

    /**
     * Get the licences and display them in a basic format.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        licenceBinding = FragmentLicensesAndLibrariesBinding.inflate(inflater, container, false)

        //Retrieve the array of libraries and licenses from the xml resource, make them all take a new line.
        var licenses = ""
        val licenceHolder = licenceBinding.licenceAndLibraryDisplay
        resources.getStringArray(R.array.usedLibrariesAndLicenses).forEach { text ->
            licenses += "$text\n"
        }
        //Display the text
        licenceHolder.text = licenses

        return licenceBinding.root
    }
}