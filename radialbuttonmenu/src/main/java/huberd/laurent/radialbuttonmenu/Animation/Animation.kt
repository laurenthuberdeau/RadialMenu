package huberd.laurent.radialbuttonmenu.Animation

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

data class Animation(
    var rotation: Int = 0,
    var scale: Float = 1f,
    var duration: Int = 300,
    var translationInterpolator: Interpolator = LinearInterpolator(),
    var rotationInterpolator: Interpolator = LinearInterpolator(),
    var scaleInterpolator: Interpolator = LinearInterpolator()
) {

    fun reverse(): Animation
        = Animation(-rotation, 1 / scale, duration, translationInterpolator, rotationInterpolator, scaleInterpolator)
}