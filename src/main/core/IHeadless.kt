interface IHeadless {
    fun quit(): Boolean;

    fun play();
    fun stop();

    fun loadNextTrack();
    fun loadPreviousTrack();
    fun seek(percent: Float);
    fun getSeekTime(): Float;
    fun getPlayerState(): PlayerState;
    fun getPreviousTrackName(): String;
    fun getTrackName(): String;
    fun getNextTrackName(): String;
}