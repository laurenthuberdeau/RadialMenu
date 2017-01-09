package laurent.huberd.radialmenubutton;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import huberd.laurent.radialbuttonmenu.Animation.BasicAnimationManager;
import huberd.laurent.radialbuttonmenu.RadialMenu;
import huberd.laurent.radialbuttonmenu.Translation.AngleInterval;
import huberd.laurent.radialbuttonmenu.Translation.CircleLayoutManager;

public class RadialMenuDemo extends AppCompatActivity implements RadialMenu.OnRadialMenuStateChangedListener, RadialMenu.OnRadialMenuInteractionListener{

    private View overlayView;
    private RadialMenu radialMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radial_menu_demo);

        overlayView = findViewById(R.id.overlayView);

        FloatingActionButton floatingMoreActionButton = (FloatingActionButton) findViewById(R.id.floatingMoreActionButton);
        FloatingActionButton floatingCameraActionButton = (FloatingActionButton) findViewById(R.id.floatingCameraActionButton);
        FloatingActionButton floatingGalleryActionButton = (FloatingActionButton) findViewById(R.id.floatingGalleryActionButton);
        FloatingActionButton floatingTextActionButton = (FloatingActionButton) findViewById(R.id.floatingTextActionButton);

        radialMenu = new RadialMenu(floatingMoreActionButton, new CircleLayoutManager(new AngleInterval(30, 150)), new BasicAnimationManager());
        radialMenu.setOnRadialMenuInteractionListener(this);
        radialMenu.setOnRadialMenuStateChangeListener(this);

        radialMenu.addSecondaryButton(floatingTextActionButton);
        radialMenu.addSecondaryButton(floatingCameraActionButton);
        radialMenu.addSecondaryButton(floatingGalleryActionButton);
    }

    @Override
    public void onOptionSelected(int i) {
        System.out.println(i);
    }

    @Override
    public void onOptionHover(int i) {
        System.out.println(i);
    }

    @Override
    public void onStateChanged(RadialMenu.RadialMenuState oldState, RadialMenu.RadialMenuState newState) {
        switch (newState) {
            case EXTENDED:
                break;
            case MINIMIZED:
                break;
            case ANIMATING:
                toggleOverlay(oldState);
                break;

        }
    }

    private void toggleOverlay(RadialMenu.RadialMenuState oldState) {
        switch (oldState) {
            case EXTENDED:
                overlayView.animate().alpha(0).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        overlayView.setOnClickListener(null);
                        overlayView.setVisibility(View.GONE);
                    }
                });
                break;
            case MINIMIZED:
                overlayView.clearAnimation();
                overlayView.setVisibility(View.VISIBLE);
                overlayView.setAlpha(0);
                overlayView.animate().alpha(1).setDuration(150).start();

                overlayView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (radialMenu.getRadialMenuState() == RadialMenu.RadialMenuState.EXTENDED) {
                            radialMenu.hide();
                        }
                    }
                });
                break;
        }
    }
}
