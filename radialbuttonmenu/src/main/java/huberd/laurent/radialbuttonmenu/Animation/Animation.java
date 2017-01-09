package huberd.laurent.radialbuttonmenu.Animation;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class Animation {

    private int rotation = 0;
    private float scale = 1;
    private int duration = 300;
    private Interpolator translationInterpolator = new LinearInterpolator();
    private Interpolator rotationInterpolator = new LinearInterpolator();
    private Interpolator scaleInterpolator = new LinearInterpolator();

    public Animation(int rotation, int duration) {
        this.rotation = rotation;
        this.duration = duration;
    }

    public Animation(int rotation, float scale, int duration) {
        this.rotation = rotation;
        this.scale = scale;
        this.duration = duration;
    }

    public Animation(int rotation, float scale, int duration, Interpolator translationInterpolator, Interpolator rotationInterpolator, Interpolator scaleInterpolator) {
        this.rotation = rotation;
        this.scale = scale;
        this.duration = duration;
        this.translationInterpolator = translationInterpolator;
        this.rotationInterpolator = rotationInterpolator;
        this.scaleInterpolator = scaleInterpolator;
    }

    public int getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public int getDuration() {
        return duration;
    }

    public Interpolator getTranslationInterpolator() {
        return translationInterpolator;
    }

    public Interpolator getRotationInterpolator() {
        return rotationInterpolator;
    }

    public Interpolator getScaleInterpolator() {
        return scaleInterpolator;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setTranslationInterpolator(Interpolator translationInterpolator) {
        this.translationInterpolator = translationInterpolator;
    }

    public void setRotationInterpolator(Interpolator rotationInterpolator) {
        this.rotationInterpolator = rotationInterpolator;
    }

    public void setScaleInterpolator(Interpolator scaleInterpolator) {
        this.scaleInterpolator = scaleInterpolator;
    }

    public Animation reverse() {
        return new Animation(-rotation, 1/scale, duration, translationInterpolator, rotationInterpolator, scaleInterpolator);
    }
}