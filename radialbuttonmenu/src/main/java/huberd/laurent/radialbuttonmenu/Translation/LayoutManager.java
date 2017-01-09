package huberd.laurent.radialbuttonmenu.Translation;

import android.support.design.widget.FloatingActionButton;

import huberd.laurent.radialbuttonmenu.RadialMenu;

public interface LayoutManager {
    Translation getMainButtonTranslation(RadialMenu radialMenu, FloatingActionButton floatingActionButton);
    Translation getSecondaryButtonTranslation(RadialMenu radialMenu, int index, FloatingActionButton floatingActionButton);
}
