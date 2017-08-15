package huberd.laurent.radialbuttonmenu.Translation

import android.support.design.widget.FloatingActionButton

import huberd.laurent.radialbuttonmenu.RadialMenu

interface LayoutManager {
    fun getMainButtonTranslation(radialMenu: RadialMenu, floatingActionButton: FloatingActionButton): Translation
    fun getSecondaryButtonTranslation(radialMenu: RadialMenu, index: Int, floatingActionButton: FloatingActionButton): Translation
}
