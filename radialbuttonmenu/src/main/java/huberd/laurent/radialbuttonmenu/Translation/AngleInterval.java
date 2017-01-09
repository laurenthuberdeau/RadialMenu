package huberd.laurent.radialbuttonmenu.Translation;

public class AngleInterval {

    private float start;
    private float end;

    public AngleInterval(float start, float end) {
        this.end = end;
        this.start = start;
    }

    public float angleBetween() {
        return end - start;
    }

    public float getStart() {
        return start;
    }

    public float getEnd() {
        return end;
    }
}
