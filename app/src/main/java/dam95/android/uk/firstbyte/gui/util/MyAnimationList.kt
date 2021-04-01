package dam95.android.uk.firstbyte.gui.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object MyAnimationList {

    fun startCrossFade(view: View, alphaStart: Float, alphaTarget: Float, duration: Long, visibility: Int) {

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