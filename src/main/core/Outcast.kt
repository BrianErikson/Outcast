class Outcast : IHeadless {
    override var previousTrack: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var nextTrack: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var currentTrack: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    private var seekAmount: Float
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    override fun launch(): Boolean {
        return true;
    }

    override fun loadNextTrack() {
        throw UnsupportedOperationException()
    }

    override fun loadPreviousTrack() {
        throw UnsupportedOperationException()
    }

    override fun seek(percent: Float) {
        throw UnsupportedOperationException()
    }

    override fun quit(): Boolean {
        return true;
    }
    override fun getSeekState(): Float {
        throw UnsupportedOperationException()
    }
}