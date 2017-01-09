package huberd.laurent.radialbuttonmenu.Animation;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.OvershootInterpolator;

import huberd.laurent.radialbuttonmenu.Translation.AngleInterval;
import huberd.laurent.radialbuttonmenu.Translation.Translation;

import static android.R.attr.radius;

public class BasicAnimationManager implements AnimationManager {

    final protected int mainButtonRotationAngle = 225;
    protected int duration = 300;

    public BasicAnimationManager(int duration) {
        this.duration = duration;
    }

    public BasicAnimationManager() {

    }

    @Override
    public Animation getShowMainButtonAnimation(FloatingActionButton floatingActionButton, Translation translation) {
        Animation animation = new Animation(mainButtonRotationAngle, duration);
        animation.setRotationInterpolator(new OvershootInterpolator(2.0f));
        return animation;
    }

    @Override
    public Animation getHideMainButtonAnimation(FloatingActionButton floatingActionButton, Translation translation) {
        return getShowMainButtonAnimation(floatingActionButton, translation).reverse();
    }

    @Override
    public Animation getShowSecondaryButtonAnimation(int index, FloatingActionButton floatingActionButton, Translation translation) {
        Animation animation = new Animation(0, 300);
        animation.setTranslationInterpolator(new OvershootInterpolator(1.0f));
        return animation;
    }

    @Override
    public Animation getHideSecondaryButtonAnimation(int index, FloatingActionButton floatingActionButton, Translation translation) {
        Animation animation = new Animation(0, 300);
        animation.setTranslationInterpolator(new OvershootInterpolator(1.5f));
        return animation;
    }
}
