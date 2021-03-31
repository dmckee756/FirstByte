package dam95.android.uk.firstbyte.gui.configuration

import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceFragmentCompat
import dam95.android.uk.firstbyte.R


private const val REPORT = "REPORT"
private const val CONTACT = "CONTACT"
private const val VERSION = "VERSION"
private const val LICENSES = "LICENSES"
class Help : PreferenceFragmentCompat() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.help_preferences, rootKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}