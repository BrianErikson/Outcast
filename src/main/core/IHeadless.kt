interface IHeadless {
    var previousTrack: String;
    var currentTrack: String;
    var nextTrack: String;

    fun launch(): Boolean;
    fun quit(): Boolean;

    fun loadNextTrack();
    fun loadPreviousTrack();
    fun seek(percent: Float);
    fun getSeekState(): Float;
}