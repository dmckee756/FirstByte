package dam95.android.uk.firstbyte.gui.configuration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dam95.android.uk.firstbyte.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val REPORT = "REPORT"
private const val CONTACT = "CONTACT"
private const val LICENSES = "LICENSES"

/**
 * @author David Mckee
 * @Version 1.0
 * Help screen designed to utilise the persistence XML layout.
 * Allows the user to contact me at davidmckee756@outlook.com
 * Allows the user to report a problem to me at dam95@aber.ac.uk (Will change after University ofcourse)
 * Allows the user to browse the list of used libraries and licenses in this app.
 * Displays the app's current build version.
 */
class Help : PreferenceFragmentCompat() {

    /**
     * When the help fragment is created, assign listeners to the preferences and allow the resource preference layout to be displayed.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        reportProblemListener()
        contactUseListener()
        displayLicensesListener()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Creates the help preference XML resource layout, whilst this is not used for the correct reason,
     * it allows a very easy implementation of a simple screen design that I needed.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.help_preferences, rootKey)
    }

    /**
     * Makes the app bar clear of menu items.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    /**
     * Preference listener when the user wants to report an app related problem.
     * Currently uses my Aber Student Email. That will change after University.
     */
    private fun reportProblemListener() {

        findPreference<Preference>(REPORT)?.summaryProvider =
            Preference.SummaryProvider<Preference> { report ->
                report.setOnPreferenceClickListener {
                    emailCreator("dam95@aber.ac.uk", "Problem with FirstByte")
                    true
                }
                ""
            }
    }

    /**
     * Preference listener for getting in contact with the creator, me.
     * When the user just wants to... chat? Or something? Maybe have suggestions etc.
     * Uses my "Professional" Email.
     */
    private fun contactUseListener() {
        findPreference<Preference>(CONTACT)?.summaryProvider =
            Preference.SummaryProvider<Preference> { contact ->
                contact.setOnPreferenceClickListener {
                    emailCreator("davidmckee756@outlook.com", "Contact about the FirstByte App")
                    true
                }
                ""
            }
    }

    /**
     * Creates an intent to allow the user to send either a problem email or a contact email to myself.
     */
    private fun emailCreator(emailAddress: String, emailSubject: String) {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            //Launch an send email intent with the correct email address.
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emailAddress, null))

            //Assign the subject's name.
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)

            //Attempt to start this email intent
            try {
                startActivity(Intent.createChooser(emailIntent, "Choose Email App"))
            } catch (exception: Exception) {
                //Catch the exception and print the error to the user.
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Preference listener that displays the app's used licences and libraries.
     */
    private fun displayLicensesListener() {
        findPreference<Preference>(LICENSES)?.summaryProvider =
            Preference.SummaryProvider<Preference> { licensesDisplay ->
                licensesDisplay.setOnPreferenceClickListener {
                    //Navigate to the fragment that displays the libraries and licenses in a very basic format.
                    val navController =
                        activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
                    navController?.navigate(
                        R.id.action_help_fragmentID_to_licenses_fragmentID)
                    true
                }
                ""
            }
    }


}