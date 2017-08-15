package huberd.laurent.radialbuttonmenu.Animation

import android.support.design.widget.FloatingActionButton
import android.view.animation.OvershootInterpolator
import huberd.laurent.radialbuttonmenu.Translation.Translation

data class BasicAnimationManager(
        val mainButtonRotationAngle: Int = 225,
        var duration: Int = 300
) : AnimationManager {

    override fun getShowMainButtonAnimation(floatingActionButton: FloatingActionButton, translation: Translation): Animation
        = Animation(rotation = mainButtonRotationAngle, duration = duration, rotationInterpolator = OvershootInterpolator(2.0f))

    override fun getHideMainButtonAnimation(floatingActionButton: FloatingActionButton, translation: Translation): Animation
        = getShowMainButtonAnimation(floatingActionButton, translation).reverse()

    override fun getShowSecondaryButtonAnimation(index: Int, floatingActionButton: FloatingActionButton, translation: Translation): Animation
        = Animation(rotation = 0, duration = 300, translationInterpolator = OvershootInterpolator(1.0f))

    override fun getHideSecondaryButtonAnimation(index: Int, floatingActionButton: FloatingActionButton, translation: Translation): Animation
        = Animation(rotation = 0, duration = 300, translationInterpolator = OvershootInterpolator(1.5f))
}
