package dam95.android.uk.firstbyte.model.components

import android.content.Context
import dam95.android.uk.firstbyte.R

/**
 *
 */
interface Component {
    var name: String
    var type: String
    var imageLink: String
    var rrpPrice: Double
    var amazonPrice: Double?
    var amazonLink: String?
    var scanPrice: Double?
    var scanLink: String?
    var deletable: Boolean

    /**
     * Bundles all variables of a Component into a list and returns it to the caller.
     * Name must be repeated twice, first at the start of the list and
     * again when the relational table in the AndroidFB_Hardware/Components database
     * has started being read/written to/from.
     */
    abstract fun getDetails(): List<*>

    /**
     *
     */
    fun getDetailsForDisplay(context: Context, childDetails: MutableList<String>?): List<String>? {
       val prettyPriceDetails = mutableListOf<String>()

        prettyPriceDetails.add(context.resources.getString(R.string.displayRrpPrice, "£", rrpPrice))
        //If amazon or scan price is not null, then display the price.
        amazonPrice?.let { prettyPriceDetails.add(context.resources.getString(R.string.displayAmazonPrice, "£", it.toString())) }
            ?: amazonLink?.let { prettyPriceDetails.add(context.resources.getString(R.string.displayAmazonPrice, "£", "N/A")) }

        scanPrice?.let  { prettyPriceDetails.add(context.resources.getString(R.string.displayScanPrice, "£", it.toString())) }
            ?: scanLink?.let { prettyPriceDetails.add(context.resources.getString(R.string.displayScanPrice, "£", "N/A")) }

        //Add child object details to this list and return it to the original caller.
        childDetails?.addAll(prettyPriceDetails)
        return childDetails?.toList()
    }

    /**
     * Important to keep this in the same order as the constructor
     */
    fun setAllDetails(allDetails: List<Any?>){
        name = allDetails[0] as String
        type = allDetails[1] as String
        imageLink = allDetails[2] as String
        rrpPrice = allDetails[3] as Double
        amazonPrice = allDetails[4] as Double?
        amazonLink = allDetails[5] as String?
        scanPrice = allDetails[6] as Double?
        scanLink = allDetails[7] as String?
        deletable = (allDetails[8] as Int) != 0
    }
}
