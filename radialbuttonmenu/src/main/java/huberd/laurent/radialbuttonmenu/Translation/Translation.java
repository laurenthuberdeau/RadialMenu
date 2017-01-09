package huberd.laurent.radialbuttonmenu.Translation;

public class Translation {

    private float x;
    private float y;

    public Translation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Translation reverse() {
        return new Translation(-x, -y);
    }

    public float length() {
        return (float)(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
    }
}
