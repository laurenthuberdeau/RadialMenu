package huberd.laurent.radialbuttonmenu

import android.graphics.Rect
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.view.MotionEvent
import android.view.View

import huberd.laurent.radialbuttonmenu.Animation.Animation
import huberd.laurent.radialbuttonmenu.Animation.AnimationManager
import huberd.laurent.radialbuttonmenu.Translation.LayoutManager
import huberd.laurent.radialbuttonmenu.Translation.Translation
import kotlin.collections.ArrayList

class RadialMenu(
        val mainButton: FloatingActionButton,
        private val secondaryButtons: ArrayList<FloatingActionButton> = ArrayList<FloatingActionButton>(),
        var layoutManager: LayoutManager,
        var animationManager: AnimationManager,
        var onRadialMenuStateChangeListener: OnRadialMenuStateChangedListener? = null,
        var onRadialMenuInteractionListener: OnRadialMenuInteractionListener? = null
) {

    var radialMenuState = RadialMenuState.MINIMIZED
        private set

    var triggerMode = TriggerMode.Touch

    // Used to enlarge the Radial Menu's bounds in isTouchInBounds method
    private var topPadding: Float = 0f
    private var rightPadding: Float = 0f
    private var bottomPadding: Float = 0f
    private var leftPadding: Float = 0f

    init {
        mainButton.setOnTouchListener { _, motionEvent -> handleMainButtonTouch(motionEvent) }
    }

    // region Public methods

    fun addSecondaryButton(floatingActionButton: FloatingActionButton) {
        configureNewButton(floatingActionButton)
        secondaryButtons.add(floatingActionButton)
    }

    fun addSecondaryButton(index: Int, floatingActionButton: FloatingActionButton) {
        configureNewButton(floatingActionButton)
        secondaryButtons.add(index, floatingActionButton)
    }

    fun removeSecondaryButton(floatingActionButton: FloatingActionButton) {
        secondaryButtons.remove(floatingActionButton)
        returnToDefaultPosition(floatingActionButton)
    }

    fun removeSecondaryButton(index: Int, floatingActionButton: FloatingActionButton) {
        secondaryButtons.removeAt(index)
        returnToDefaultPosition(floatingActionButton)
    }

    fun secondaryButtonsSize(): Int {
        return secondaryButtons.size
    }

    fun hide() {
        if (radialMenuState != RadialMenuState.EXTENDED) return

        changeRadialMenuState(RadialMenuState.ANIMATING)

        var translation = layoutManager.getMainButtonTranslation(this, mainButton)
        var animation = animationManager.getHideMainButtonAnimation(mainButton, translation)

        applyTransformationAndAnimation(mainButton, translation.reverse(), animation, Runnable {
            returnToDefaultPosition(mainButton)
            changeRadialMenuState(RadialMenuState.MINIMIZED)
        })

        for (i in secondaryButtons.indices) {
            val fab = secondaryButtons[i]
            translation = layoutManager.getSecondaryButtonTranslation(this, i, fab)
            animation = animationManager.getHideSecondaryButtonAnimation(i, fab, translation)

            applyTransformationAndAnimation(fab, translation.reverse(), animation, Runnable { returnToDefaultPosition(fab) })
        }

        resetRadialButtonBounds()
    }

    fun show() {
        if (radialMenuState != RadialMenuState.MINIMIZED) return

        changeRadialMenuState(RadialMenuState.ANIMATING)

        var translation = layoutManager.getMainButtonTranslation(this, mainButton)
        var animation = animationManager.getShowMainButtonAnimation(mainButton, translation)

        applyTransformationAndAnimation(mainButton, translation, animation, null)

        // Used to activate secondary button's interaction before animation finishes.
        // The result is a much easier to use Radial Menu
        Handler().postDelayed({
            changeRadialMenuState(RadialMenuState.EXTENDED)
        }, (animation.duration / 2).toLong())

        for (i in secondaryButtons.indices) {
            val fab = secondaryButtons[i]
            translation = layoutManager.getSecondaryButtonTranslation(this, i, fab)
            animation = animationManager.getShowSecondaryButtonAnimation(i, fab, translation)

            applyTransformationAndAnimation(fab, translation, animation, null)
            fab.visibility = View.VISIBLE
            enlargeRadialButtonBounds(translation)
        }
    }

    // endregion

    // region MotionEvent handling

    private fun handleMainButtonTouch(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handleEventTouchDown(motionEvent)
                handleEventTouchUp(motionEvent)
                handleEventTouchMove(motionEvent)
                return true
            }
            MotionEvent.ACTION_UP -> {
                handleEventTouchUp(motionEvent)
                handleEventTouchMove(motionEvent)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                handleEventTouchMove(motionEvent)
                return true
            }
            else -> return true
        }
    }

    private fun handleEventTouchDown(motionEvent: MotionEvent): Boolean {
        when (radialMenuState) {
            RadialMenu.RadialMenuState.ANIMATING -> return false
            RadialMenu.RadialMenuState.MINIMIZED -> show()
            RadialMenu.RadialMenuState.EXTENDED -> hide()
        }
        return true
    }

    private fun handleEventTouchUp(motionEvent: MotionEvent): Boolean {
        if (triggerMode == TriggerMode.Touch && radialMenuState == RadialMenuState.EXTENDED) {
            if (isTouchInBounds(motionEvent)) {
                val index = isTouchInSecondaryButtonBounds(motionEvent)
                if (index != -1) {
                    onSecondaryButtonSelected(index)
                    return true
                }
            } else {
                hide()
            }
        }
        return false
    }

    private fun handleEventTouchMove(motionEvent: MotionEvent): Boolean {
        if (triggerMode == TriggerMode.Touch && radialMenuState == RadialMenuState.EXTENDED) {
            val index = isTouchInSecondaryButtonBounds(motionEvent)
            if (index != -1) {
                onSecondaryButtonHover(index)
                return true
            }
        }
        return false
    }

    private fun onSecondaryButtonSelected(i: Int) {
        onRadialMenuInteractionListener?.onOptionSelected(i)
        hide()
    }

    private fun onSecondaryButtonHover(i: Int) {
        onRadialMenuInteractionListener?.onOptionHover(i)
    }

    //endregion

    // region Helper methods

    private fun changeRadialMenuState(state: RadialMenuState) {
        val oldState = radialMenuState
        radialMenuState = state

        onRadialMenuStateChangeListener?.onStateChanged(oldState, RadialMenuState.ANIMATING)
    }

    private fun configureNewButton(floatingActionButton: FloatingActionButton) {
        removeSecondaryButton(floatingActionButton)
        returnToDefaultPosition(floatingActionButton)
        floatingActionButton.setOnClickListener {
            val index = secondaryButtons.indexOf(floatingActionButton)
            if (index != -1) {
                onSecondaryButtonSelected(index)
            }
        }
    }

    private fun returnToDefaultPosition(floatingActionButton: FloatingActionButton) {
        floatingActionButton.clearAnimation()
        floatingActionButton.y = mainButton.y
        floatingActionButton.x = mainButton.x
        floatingActionButton.scaleX = 1f
        floatingActionButton.scaleY = 1f
        floatingActionButton.rotation = 0f

        mainButton.bringToFront()
    }

    private fun applyTransformationAndAnimation(floatingActionButton: FloatingActionButton, translation: Translation, animation: Animation, endAction: Runnable?) {
        floatingActionButton.clearAnimation()

        floatingActionButton.animate().translationXBy(translation.x).translationYBy(translation.y).setInterpolator(animation.translationInterpolator).setDuration(animation.duration.toLong()).withEndAction(endAction).start()
        floatingActionButton.animate().rotationBy(animation.rotation.toFloat()).setInterpolator(animation.rotationInterpolator).setDuration(animation.duration.toLong()).start()
        floatingActionButton.animate().scaleX(animation.scale).scaleY(animation.scale).setInterpolator(animation.scaleInterpolator).setDuration(animation.duration.toLong()).start()
    }

    private fun enlargeRadialButtonBounds(translation: Translation) {
        if (translation.x > 0) {
            rightPadding = Math.max(rightPadding, translation.x)
        } else {
            leftPadding = Math.max(leftPadding, Math.abs(translation.x))
        }
        if (translation.y > 0) {
            bottomPadding = Math.max(bottomPadding, translation.y)
        } else {
            topPadding = Math.max(topPadding, Math.abs(translation.y))
        }
    }

    private fun resetRadialButtonBounds() {
        topPadding = 0f
        rightPadding = 0f
        bottomPadding = 0f
        leftPadding = 0f
    }

    private fun isTouchInSecondaryButtonBounds(motionEvent: MotionEvent): Int {
        for (i in secondaryButtons.indices) {
            val buttonRect = Rect()
            secondaryButtons[i].getGlobalVisibleRect(buttonRect)
            if (buttonRect.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())) {
                return i
            }
        }
        return -1
    }

    private fun isTouchInBounds(motionEvent: MotionEvent): Boolean {
        val r = Rect()
        mainButton.getGlobalVisibleRect(r)
        r.top -= (topPadding + 100).toInt()
        r.right += (rightPadding + 100).toInt()
        r.bottom += (bottomPadding + 100).toInt()
        r.left -= (leftPadding + 100).toInt()

        return r.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
    }

    // endregion

    // region RadialMenuState Enum

    enum class RadialMenuState {
        EXTENDED, MINIMIZED, ANIMATING
    }

    // endregion

    // region TriggerMode Enum

    enum class TriggerMode {
        Click, Touch
    }

    // endregion

    // region Interfaces

    interface OnRadialMenuInteractionListener {
        fun onOptionSelected(i: Int)
        fun onOptionHover(i: Int)
    }

    interface OnRadialMenuStateChangedListener {
        fun onStateChanged(oldState: RadialMenuState, newState: RadialMenuState)
    }

    // endregion
}
