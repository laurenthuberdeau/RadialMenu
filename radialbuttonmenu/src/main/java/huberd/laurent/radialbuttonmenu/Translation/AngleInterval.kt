package huberd.laurent.radialbuttonmenu.Translation

class AngleInterval(val start: Float, val end: Float) {

    fun angleBetween(): Float {
        return end - start
    }
}
