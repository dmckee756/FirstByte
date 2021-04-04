package dam95.android.uk.firstbyte.gui.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * @author David Mckee
 * @Version 1.0
 * A list of personally created animation methods used in the app.
 */
object MyAnimationList {

    /**
     * A fade in or out animation for views.
     * @param view Can be any layout view, image view etc.
     * @param alphaStart If the view is coming into view, the view will start with an alpha value of 0F. To disappear the start is 1F
     * @param alphaTarget If the view is coming into view, this will be an alpha value of 1F. To disappear the target is 0F
     * @param duration How long the animation takes to start til finish.
     * @param visibility Determines if the animation is making the view gradually visible, invisible or gone.
     */
    fun startCrossFade(
        view: View,
        alphaStart: Float,
        alphaTarget: Float,
        duration: Long,
        visibility: Int
    ) {
        view.apply {
            //AlphaStart is used, it sets the views initial Alpha/Transparency value for a cross fade animation
            alphaStart
            animate().alpha(alphaTarget).setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        view.visibility = visibility
                    }
                })
        }
    }
}