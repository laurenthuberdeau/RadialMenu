package huberd.laurent.radialbuttonmenu;

import android.graphics.Rect;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import huberd.laurent.radialbuttonmenu.Animation.Animation;
import huberd.laurent.radialbuttonmenu.Animation.AnimationManager;
import huberd.laurent.radialbuttonmenu.Translation.LayoutManager;
import huberd.laurent.radialbuttonmenu.Translation.Translation;

public class RadialMenu {

    // region Member variables and Accessors

    private FloatingActionButton mainButton;
    protected ArrayList<FloatingActionButton> secondaryButtons = new ArrayList<>();

    protected RadialMenuState radialMenuState = RadialMenuState.MINIMIZED;

    private TriggerMode triggerMode = TriggerMode.Touch;
    protected LayoutManager layoutManager;
    protected AnimationManager animationManager;

    // Used to enlarge the Radial Menu's bounds in isTouchInBounds method
    protected float topPadding;
    protected float rightPadding;
    protected float bottomPadding;
    protected float leftPadding;

    protected OnRadialMenuStateChangedListener onRadialMenuStateChangeListener;
    protected OnRadialMenuInteractionListener onRadialMenuInteractionListener;

    // endregion

    // region Constructor

    public RadialMenu(FloatingActionButton mainFloatingActionButton, LayoutManager layoutManager, AnimationManager animationManager) {
        this.mainButton = mainFloatingActionButton;
        this.layoutManager = layoutManager;
        this.animationManager = animationManager;

        this.mainButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return handleMainButtonTouch(motionEvent);
            }
        });
    }

    // endregion

    // region Public methods

    public void addSecondaryButton(FloatingActionButton floatingActionButton) {
        configureNewButton(floatingActionButton);
        secondaryButtons.add(floatingActionButton);
    }

    public void addSecondaryButton(int index, FloatingActionButton floatingActionButton) {
        configureNewButton(floatingActionButton);
        secondaryButtons.add(index, floatingActionButton);
    }

    public void removeSecondaryButton(FloatingActionButton floatingActionButton) {
        secondaryButtons.remove(floatingActionButton);
        returnToDefaultPosition(floatingActionButton);
    }

    public int secondaryButtonsSize() {
        return secondaryButtons.size();
    }

    public void setTriggerMode(TriggerMode triggerMode) {
        this.triggerMode = triggerMode;
    }

    public RadialMenuState getRadialMenuState() {
        return radialMenuState;
    }

    public void setOnRadialMenuStateChangeListener(OnRadialMenuStateChangedListener listener) {
        this.onRadialMenuStateChangeListener = listener;
    }

    public void setOnRadialMenuInteractionListener(OnRadialMenuInteractionListener listener) {
        this.onRadialMenuInteractionListener = listener;
    }

    public void hide() {
        if (radialMenuState != RadialMenuState.EXTENDED) return;

        RadialMenuState oldState = radialMenuState;
        radialMenuState = RadialMenuState.ANIMATING;

        if (onRadialMenuStateChangeListener != null) {
            onRadialMenuStateChangeListener.onStateChanged(oldState, RadialMenuState.ANIMATING);
        }

        Translation translation = layoutManager.getMainButtonTranslation(this, mainButton);
        Animation animation = animationManager.getHideMainButtonAnimation(mainButton, translation);

        applyTransformationAndAnimation(mainButton, translation.reverse(), animation, new Runnable() {
            @Override
            public void run() {
                returnToDefaultPosition(mainButton);

                RadialMenuState oldState = radialMenuState;
                radialMenuState = RadialMenuState.MINIMIZED;

                if (onRadialMenuStateChangeListener != null) {
                    onRadialMenuStateChangeListener.onStateChanged(oldState, RadialMenuState.MINIMIZED);
                }

            }
        });

        for (int i = 0; i < secondaryButtons.size(); i++) {
            final FloatingActionButton fab = secondaryButtons.get(i);
            translation = layoutManager.getSecondaryButtonTranslation(this, i, fab);
            animation = animationManager.getHideSecondaryButtonAnimation(i, fab, translation);

            applyTransformationAndAnimation(fab, translation.reverse(), animation, new Runnable() {
                @Override
                public void run() {
                    returnToDefaultPosition(fab);
                }
            });
        }

        resetRadialButtonBounds();
    }

    public void show() {
        if (radialMenuState != RadialMenuState.MINIMIZED) return;

        RadialMenuState oldState = radialMenuState;
        radialMenuState = RadialMenuState.ANIMATING;

        if (onRadialMenuStateChangeListener != null) {
            onRadialMenuStateChangeListener.onStateChanged(oldState, RadialMenuState.ANIMATING);
        }

        Translation translation = layoutManager.getMainButtonTranslation(this, mainButton);
        Animation animation = animationManager.getShowMainButtonAnimation(mainButton, translation);

        applyTransformationAndAnimation(mainButton, translation, animation, null);

        // Used to activate secondary button's interaction before animation finishes.
        // The result is a much easier to use Radial Menu
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RadialMenuState oldState = radialMenuState;
                radialMenuState = RadialMenuState.EXTENDED;

                if (onRadialMenuStateChangeListener != null) {
                    onRadialMenuStateChangeListener.onStateChanged(oldState, RadialMenuState.EXTENDED);
                }
            }
        }, animation.getDuration() / 2);

        for (int i = 0; i < secondaryButtons.size(); i++) {
            FloatingActionButton fab = secondaryButtons.get(i);
            translation = layoutManager.getSecondaryButtonTranslation(this, i, fab);
            animation = animationManager.getShowSecondaryButtonAnimation(i, fab, translation);

            applyTransformationAndAnimation(fab, translation, animation, null);
            fab.setVisibility(View.VISIBLE);
            enlargeRadialButtonBounds(translation);
        }
    }

    // endregion

    // region MotionEvent handling

    private boolean handleMainButtonTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleEventTouchDown(motionEvent);
            case MotionEvent.ACTION_UP:
                handleEventTouchUp(motionEvent);
            case MotionEvent.ACTION_MOVE:
                handleEventTouchMove(motionEvent);
            default:
                return true;
        }
    }

    private boolean handleEventTouchDown(MotionEvent motionEvent) {
        switch (radialMenuState) {
            case ANIMATING:
                return false;
            case MINIMIZED:
                show();
                break;
            case EXTENDED:
                hide();
                break;
        }
        return true;
    }

    private boolean handleEventTouchUp(MotionEvent motionEvent) {
        if (triggerMode == TriggerMode.Touch && radialMenuState == RadialMenuState.EXTENDED) {
            if (isTouchInBounds(motionEvent)) {
                int index = isTouchInSecondaryButtonBounds(motionEvent);
                if (index != -1) {
                    onSecondaryButtonSelected(index);
                    return true;
                }
            } else {
                hide();
            }
        }
        return false;
    }

    private boolean handleEventTouchMove(MotionEvent motionEvent) {
        if (triggerMode == TriggerMode.Touch && radialMenuState == RadialMenuState.EXTENDED) {
            int index = isTouchInSecondaryButtonBounds(motionEvent);
            if (index != -1) {
                onSecondaryButtonHover(index);
                return true;
            }
        }
        return false;
    }

    private void onSecondaryButtonSelected(int i) {
        if (onRadialMenuInteractionListener != null) {
            onRadialMenuInteractionListener.onOptionSelected(i);
        }
        hide();
    }

    private void onSecondaryButtonHover(int i) {
        if (onRadialMenuInteractionListener != null) {
            onRadialMenuInteractionListener.onOptionHover(i);
        }
    }

    //endregion

    // region Helper methods

    private void configureNewButton(final FloatingActionButton floatingActionButton) {
        removeSecondaryButton(floatingActionButton);
        returnToDefaultPosition(floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = secondaryButtons.indexOf(floatingActionButton);
                if (index != -1) {
                    onSecondaryButtonSelected(index);
                }
            }
        });
    }

    private void returnToDefaultPosition(FloatingActionButton floatingActionButton) {
        floatingActionButton.clearAnimation();
        floatingActionButton.setY(mainButton.getY());
        floatingActionButton.setX(mainButton.getX());
        floatingActionButton.setScaleX(1);
        floatingActionButton.setScaleY(1);
        floatingActionButton.setRotation(0);

        mainButton.bringToFront();
    }

    private void applyTransformationAndAnimation(FloatingActionButton floatingActionButton, Translation translation, Animation animation, Runnable endAction) {
        floatingActionButton.clearAnimation();

        floatingActionButton.animate().translationXBy(translation.getX()).translationYBy(translation.getY()).setInterpolator(animation.getTranslationInterpolator()).setDuration(animation.getDuration()).withEndAction(endAction).start();
        floatingActionButton.animate().rotationBy(animation.getRotation()).setInterpolator(animation.getRotationInterpolator()).setDuration(animation.getDuration()).start();
        floatingActionButton.animate().scaleX(animation.getScale()).scaleY(animation.getScale()).setInterpolator(animation.getScaleInterpolator()).setDuration(animation.getDuration()).start();
    }

    private void enlargeRadialButtonBounds(Translation translation) {
        if (translation.getX() > 0) {
            rightPadding = Math.max(rightPadding, translation.getX());
        } else {
            leftPadding = Math.max(leftPadding, Math.abs(translation.getX()));
        }
        if (translation.getY() > 0) {
            bottomPadding = Math.max(bottomPadding, translation.getY());
        } else {
            topPadding = Math.max(topPadding, Math.abs(translation.getY()));
        }
    }

    private void resetRadialButtonBounds() {
        topPadding = 0;
        rightPadding = 0;
        bottomPadding = 0;
        leftPadding = 0;
    }

    private int isTouchInSecondaryButtonBounds(MotionEvent motionEvent) {
        for (int i = 0; i < secondaryButtons.size(); i++) {
            Rect buttonRect = new Rect();
            secondaryButtons.get(i).getGlobalVisibleRect(buttonRect);
            if (buttonRect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
                return i;
            }
        }
        return -1;
    }

    private boolean isTouchInBounds(MotionEvent motionEvent) {
        Rect r = new Rect();
        mainButton.getGlobalVisibleRect(r);
        r.top -= (topPadding + 100);
        r.right += (rightPadding + 100);
        r.bottom += (bottomPadding + 100);
        r.left -= (leftPadding + 100);

        return r.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
    }

    // endregion

    // region RadialMenuState Enum

    public enum RadialMenuState {
        EXTENDED, MINIMIZED, ANIMATING
    }

    // endregion

    // region TriggerMode Enum

    public enum TriggerMode {
        Click, Touch
    }

    // endregion

    // region Interfaces

    public interface OnRadialMenuInteractionListener {
        void onOptionSelected(int i);
        void onOptionHover(int i);
    }

    public interface OnRadialMenuStateChangedListener {
        void onStateChanged(RadialMenuState oldState, RadialMenuState newState);
    }

    // endregion
}
