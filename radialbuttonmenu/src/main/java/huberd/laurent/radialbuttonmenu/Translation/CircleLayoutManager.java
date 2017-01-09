package huberd.laurent.radialbuttonmenu.Translation;

import android.support.design.widget.FloatingActionButton;

import huberd.laurent.radialbuttonmenu.RadialMenu;

public class CircleLayoutManager implements LayoutManager {

    protected AngleInterval angleInterval;
    protected float radiusMultiplier;

    public CircleLayoutManager(AngleInterval angleInterval, float radiusMultiplier) {
        if (angleInterval.angleBetween() > 360) {
            throw new IllegalArgumentException("angleInterval.angleBetween() cannot be bigger that 360.");
        }
        this.angleInterval = angleInterval;
        this.radiusMultiplier = radiusMultiplier;
    }

    public CircleLayoutManager(AngleInterval angleInterval) {
        this(angleInterval, 1.5f);
    }

    @Override
    public Translation getMainButtonTranslation(RadialMenu radialMenu, FloatingActionButton floatingActionButton) {
        return new Translation(0, 0);
    }

    @Override
    public Translation getSecondaryButtonTranslation(RadialMenu radialMenu, int index, FloatingActionButton floatingActionButton) {
        int direction = angleInterval.getStart() < angleInterval.getEnd() ? 1 : -1;
        index = direction == 1 ? index : radialMenu.secondaryButtonsSize() - 1 - index;
        float angle = angleInterval.getStart() + (index * angleInterval.angleBetween() / (radialMenu.secondaryButtonsSize() - 1));

        float radius = floatingActionButton.getHeight() * 1.5f;

        float x = (float) (radius * Math.cos(Math.toRadians(angle)));
        float y = (float) (-radius * Math.sin(Math.toRadians(angle)));

        return new Translation(x,y);
    }
}
