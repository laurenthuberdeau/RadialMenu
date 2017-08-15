package huberd.laurent.radialbuttonmenu.Translation

import android.support.design.widget.FloatingActionButton

import huberd.laurent.radialbuttonmenu.RadialMenu

class CircleLayoutManager(val angleInterval: AngleInterval, val radiusMultiplier: Float = 1.5f) : LayoutManager {

    init {
        if (angleInterval.angleBetween() > 360) {
            throw IllegalArgumentException("angleInterval.angleBetween() cannot be bigger that 360.")
        }
    }

    override fun getMainButtonTranslation(radialMenu: RadialMenu, floatingActionButton: FloatingActionButton): Translation
        = Translation(0f, 0f)

    override fun getSecondaryButtonTranslation(radialMenu: RadialMenu, index: Int, floatingActionButton: FloatingActionButton): Translation {

        val _index = if (angleInterval.start < angleInterval.end)
            index
        else
            radialMenu.secondaryButtonsSize() - index - 1

        val angle = angleInterval.start
            + _index * angleInterval.angleBetween() / (radialMenu.secondaryButtonsSize() - 1)

        val radius = floatingActionButton.height * radiusMultiplier

        val x = (radius * Math.cos(Math.toRadians(angle.toDouble()))).toFloat()
        val y = (-radius * Math.sin(Math.toRadians(angle.toDouble()))).toFloat()

        return Translation(x, y)
    }
}
