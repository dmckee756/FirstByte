package dam95.android.uk.firstbyte.gui.components.search.util

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.compare.FINISH_ID
import java.util.*

object HandleComponent {

    fun calledFromPCBuild(
        activity: FragmentActivity,
        fbHardwareDb: FirstByteDBAccess,
        componentName: String,
        componentType: String,
        pcID: Int
    ) {
        //If this fragment was loaded from the personal build screen, then add the clicked component to the PC that called this fragment.
        fbHardwareDb.savePCPart(componentName, componentType, pcID)
        Toast.makeText(activity.applicationContext, "$componentName added to PC.", Toast.LENGTH_SHORT).show()
        activity.onBackPressed()
    }

    fun calledFromComparedList(
        activity: FragmentActivity,
        fbHardwareDb: FirstByteDBAccess,
        componentName: String,
        componentType: String
    ) {
        //If this fragment was loaded from the comparison screen, then add the clicked component to the compared list.
        if (fbHardwareDb.checkIfComponentIsInComparedTable(componentName) == 0) {
            val comparedID = "${componentType.toUpperCase(Locale.ROOT)}$FINISH_ID"
            //Save component to compared list
            fbHardwareDb.saveComparedComponent(comparedID, componentName)
            Toast.makeText(activity.applicationContext, "$componentName added to comparing list.", Toast.LENGTH_SHORT).show()
            //Go back to compared fragment
            activity.onBackPressed()
        } else{
            Toast.makeText(activity.applicationContext, "$componentName is already being compared.", Toast.LENGTH_SHORT).show()
        }
    }

}