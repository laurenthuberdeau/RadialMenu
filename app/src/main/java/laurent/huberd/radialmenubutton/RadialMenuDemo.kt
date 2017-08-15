package laurent.huberd.radialmenubutton

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View

import huberd.laurent.radialbuttonmenu.Animation.BasicAnimationManager
import huberd.laurent.radialbuttonmenu.RadialMenu
import huberd.laurent.radialbuttonmenu.Translation.AngleInterval
import huberd.laurent.radialbuttonmenu.Translation.CircleLayoutManager

class RadialMenuDemo : AppCompatActivity(), RadialMenu.OnRadialMenuStateChangedListener, RadialMenu.OnRadialMenuInteractionListener {

    private var overlayView: View? = null
    private lateinit var radialMenu: RadialMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radial_menu_demo)

        overlayView = findViewById(R.id.overlayView)

        val floatingMoreActionButton = findViewById(R.id.floatingMoreActionButton) as FloatingActionButton
        val floatingCameraActionButton = findViewById(R.id.floatingCameraActionButton) as FloatingActionButton
        val floatingGalleryActionButton = findViewById(R.id.floatingGalleryActionButton) as FloatingActionButton
        val floatingTextActionButton = findViewById(R.id.floatingTextActionButton) as FloatingActionButton

        radialMenu = RadialMenu(mainButton = floatingMoreActionButton,
                layoutManager = CircleLayoutManager(AngleInterval(30f, 150f)),
                animationManager = BasicAnimationManager())
        radialMenu.onRadialMenuInteractionListener = this
        radialMenu.onRadialMenuStateChangeListener = this

        radialMenu.addSecondaryButton(floatingTextActionButton)
        radialMenu.addSecondaryButton(floatingCameraActionButton)
        radialMenu.addSecondaryButton(floatingGalleryActionButton)
    }

    override fun onOptionSelected(i: Int) {
        println(i)
    }

    override fun onOptionHover(i: Int) {
        println(i)
    }

    override fun onStateChanged(oldState: RadialMenu.RadialMenuState, newState: RadialMenu.RadialMenuState) {
        when (newState) {
            RadialMenu.RadialMenuState.EXTENDED -> {
            }
            RadialMenu.RadialMenuState.MINIMIZED -> {
            }
            RadialMenu.RadialMenuState.ANIMATING -> toggleOverlay(oldState)
        }
    }

    private fun toggleOverlay(oldState: RadialMenu.RadialMenuState) {
        when (oldState) {
            RadialMenu.RadialMenuState.EXTENDED -> overlayView!!.animate().alpha(0f).setDuration(150).withEndAction {
                overlayView!!.setOnClickListener(null)
                overlayView!!.visibility = View.GONE
            }
            RadialMenu.RadialMenuState.MINIMIZED -> {
                overlayView!!.clearAnimation()
                overlayView!!.visibility = View.VISIBLE
                overlayView!!.alpha = 0f
                overlayView!!.animate().alpha(1f).setDuration(150).start()

                overlayView!!.setOnClickListener {
                    if (radialMenu.radialMenuState == RadialMenu.RadialMenuState.EXTENDED) {
                        radialMenu.hide()
                    }
                }
            }
            RadialMenu.RadialMenuState.ANIMATING -> {
                
            }
        }
    }
}
