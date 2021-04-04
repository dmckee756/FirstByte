package dam95.android.uk.firstbyte.gui.components.search.util

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.compare.FINISH_ID
import java.util.*

/**
 * @author David Mckee
 * @Version 1.0
 * This objects handles external requests that are used for adding a component to the PCBuild or compare list.
 */
object HandleComponent {

    /**
     * When the HardwareList fragment was navigated to from PCBuild and a component was clicked/pressed,
     * then add that item into the PCBuild on the database and navigate back to the PC.
     * @param activity HardwareLists Activity
     * @param fbHardwareDb Instance of the DBAccess class.
     * @param componentName Name of saved component/PC Part.
     * @param componentType Saved component type, used to determine which slot the component is saved to in the computer and if it's a single of multi slot.
     * @param pcID Current ID of PC that is having hardware saved to.
     */
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

    /**
     * When the HardwareList fragment was navigated to from the comparison and a component was clicked/pressed,
     * then add that item into the compared list on the database.
     * @param activity HardwareLists Activity
     * @param fbHardwareDb Instance of the DBAccess class.
     * @param componentName Name of now compared component.
     * @param componentType Saved component type, used to determine which compared list the component is being added to.
     */
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
            //If the component is already being compared, inform the user and do nothing.
            Toast.makeText(activity.applicationContext, "$componentName is already being compared.", Toast.LENGTH_SHORT).show()
        }
    }

}