package huberd.laurent.radialbuttonmenu.Translation

class Translation(val x: Float, val y: Float) {

    fun reverse(): Translation = Translation(-x, -y)

    fun length(): Float = Math.hypot(x.toDouble(), y.toDouble()).toFloat()
}
