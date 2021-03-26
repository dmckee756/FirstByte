package dam95.android.uk.firstbyte.gui.components.hardware

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.FragmentWebViewConnectionBinding

/**
 * This fragment is dedicated to loading and displaying the components web links to...
 * Amazon.co.uk or Scan.co.uk.
 */
const val URL_LINK = "URL_LINK"
class WebViewConnection : Fragment() {

    private lateinit var webViewConnectionBinding: FragmentWebViewConnectionBinding

    /**
     * When this fragment is navigated to, load the passed in url link and set up the WebViewClient.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val urlLink = arguments?.getString("URL_LINK")
        if (urlLink != null) {
            webViewConnectionBinding =
                FragmentWebViewConnectionBinding.inflate(inflater, container, false)

            setUpWebView(urlLink)
        }
        return webViewConnectionBinding.root
    }

    /**
     * Finds this fragments WebView layout, get's the webViewClient and loads the selected...
     * ...components Scan.co.uk or Amazon link and displays it to the user.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(url: String){
        val webViewer = webViewConnectionBinding.webClient
        webViewer.webViewClient = WebViewClient()

        webViewer.apply {
            loadUrl(url)
            //Allow the use of Javascript in the webViewer
            settings.javaScriptEnabled = true
            //Enforce safe browsing on FirstByte app
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
        }
    }
}