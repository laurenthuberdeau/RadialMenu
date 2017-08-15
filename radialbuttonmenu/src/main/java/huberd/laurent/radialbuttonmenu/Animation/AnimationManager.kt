package huberd.laurent.radialbuttonmenu.Animation

import android.support.design.widget.FloatingActionButton

import huberd.laurent.radialbuttonmenu.Translation.Translation

interface AnimationManager {
    fun getShowMainButtonAnimation(floatingActionButton: FloatingActionButton, translation: Translation): Animation
    fun getHideMainButtonAnimation(floatingActionButton: FloatingActionButton, translation: Translation): Animation
    fun getShowSecondaryButtonAnimation(index: Int, floatingActionButton: FloatingActionButton, translation: Translation): Animation
    fun getHideSecondaryButtonAnimation(index: Int, floatingActionButton: FloatingActionButton, translation: Translation): Animation
}
