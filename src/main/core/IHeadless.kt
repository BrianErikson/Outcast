interface IHeadless {
    var previousTrack: String;
    var currentTrack: String;
    var nextTrack: String;

    fun launch(): Boolean;
    fun quit(): Boolean;

    fun play();
    fun pause();
    fun stop();

    fun loadNextTrack();
    fun loadPreviousTrack();
    fun seek(percent: Float);
    fun getSeekState(): Float;
}