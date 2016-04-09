interface IHeadless {
    var previousTrack: String;
    var nextTrack: String;

    fun launch(): Boolean;
    fun quit(): Boolean;

    fun play();
    fun stop();

    fun loadNextTrack();
    fun loadPreviousTrack();
    fun seek(percent: Float);
    fun getSeekTime(): Float;
    fun getPlayerState(): PlayerState;
    fun getTrackName(): String;
}