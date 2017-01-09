package huberd.laurent.radialbuttonmenu.Animation;

import android.support.design.widget.FloatingActionButton;

import huberd.laurent.radialbuttonmenu.Translation.Translation;

public interface AnimationManager {
    Animation getShowMainButtonAnimation(FloatingActionButton floatingActionButton, Translation translation);
    Animation getHideMainButtonAnimation(FloatingActionButton floatingActionButton, Translation translation);
    Animation getShowSecondaryButtonAnimation(int index, FloatingActionButton floatingActionButton, Translation translation);
    Animation getHideSecondaryButtonAnimation(int index, FloatingActionButton floatingActionButton, Translation translation);
}
