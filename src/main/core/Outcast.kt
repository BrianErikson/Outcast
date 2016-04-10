import com.sun.syndication.feed.synd.SyndEnclosureImpl
import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import javax.media.Controller
import javax.media.Manager
import javax.media.NoPlayerException
import javax.media.Player

fun main(args: Array<String>) {
    val outcast = Outcast(URL("http://atp.fm/episodes?format=rss"));
    outcast.play();
}


class Outcast(val feedUrl: URL) : IHeadless {

    private var trackIndex: Int = 0;
    set(value) {
        if (trackIndex < 0) {
            throw RuntimeException("ERROR: trackIndex cannot be less than zero");
        }
        else if (trackIndex - 1 > tracks.size) {
            throw RuntimeException("ERROR: trackIndex cannot be higher than the number of tracks")
        }
        else {
            field = value;
        }
    }


    private val state: PlayerState
        get() {
            when(player?.state) {
                Controller.Unrealized -> return PlayerState.Unrealized;
                Controller.Realizing -> return PlayerState.Realizing;
                Controller.Realized -> return PlayerState.Realized;
                Controller.Prefetching -> return PlayerState.Prefetching;
                Controller.Prefetched -> return PlayerState.Prefetched;
                Controller.Started -> return PlayerState.Started;
                else -> return PlayerState.Stopped;
            }
        }

    private var tracks: List<SyndEntryImpl> = listOf();
    private var currentTrack: SyndEntryImpl = SyndEntryImpl();
    set(value) {
        previousTrack = field;
        field = value;
    }

    private var previousTrack: SyndEntryImpl = SyndEntryImpl(); // Set when currentTrack is set
    private val nextTrack: SyndEntryImpl
    get() {
        if (trackIndex + 1 < tracks.size) {
            return tracks[trackIndex + 1];
        }
        else {
            return currentTrack;
        }
    }

    private var player: Player? = null;

    init {
        try {
            val input = SyndFeedInput();
            val feed = input.build(XmlReader(File(javaClass.getResource("atpRss.txt").file)));
            val links = mutableListOf<SyndEntryImpl>();

            feed.entries.forEach(fun(entry: Any?) {
                if (entry is SyndEntryImpl) {
                    links.add(entry);
                }
            });

            tracks = links.toList();
            currentTrack = tracks[trackIndex];
            previousTrack = currentTrack;

            if (tracks.isEmpty() || currentTrack.enclosures.isEmpty()) {
                throw RuntimeException("ERROR: Number of tracks: ${tracks.isEmpty()}, Current track enclosure size: ${currentTrack.enclosures.size}");
            }
        }
        catch (e: Exception) {
            e.printStackTrace();
            println("ERROR: " + e.message);
        }
    }

    override fun play() {
        println("Enter play method");
        if (currentTrack != tracks[trackIndex]) {
            println("Current track != indexed track");
            currentTrack = tracks[trackIndex];
            player?.close();
            player = createPlayer(getTrackUrl(currentTrack));
            player!!.start();
        }
        else {
            println("Current track is indexed properly");
            player?.start() ?: run {
                println("Player doesn't exist. Creating one.");
                player = createPlayer(getTrackUrl(currentTrack));
                player!!.start();
            }
        }
    }

    override fun stop() {
        player?.stop();
    }

    override fun loadNextTrack() {
        if (currentTrack != nextTrack) {
            loadTrack(nextTrack);
        }
    }

    override fun loadPreviousTrack() {
        if (currentTrack != previousTrack) {
            loadTrack(previousTrack);
        }
    }

    override fun seek(percent: Float) {
        throw UnsupportedOperationException()
    }

    override fun quit(): Boolean {
        player?.stop();
        player?.close();
        return true;
    }

    override fun getTrackName(): String {
        return currentTrack.title ?: "None";
    }

    override fun getSeekTime(): Float {
        val curTime: Float = player?.mediaTime?.seconds?.toFloat() ?: -1f;
        val trackDuration: Float = player?.duration?.seconds?.toFloat() ?: -1f;

        if (curTime > 0f && trackDuration > 0f) {
            return curTime / trackDuration;
        }
        else {
            return 0f;
        }
    }

    override fun getPlayerState(): PlayerState {
        return state;
    }

    override fun getNextTrackName(): String {
        return nextTrack.title ?: "None";
    }

    override fun getPreviousTrackName(): String {
        return previousTrack.title ?: "None";
    }

    fun loadTrack(track: SyndEntryImpl) {
        trackIndex = tracks.indexOf(track);
        play();
    }

    private fun getTrackUrl(track: SyndEntryImpl): String {
        var url: String = "";
        track.enclosures.forEach(fun(enclosure: Any?) {
            if (enclosure is SyndEnclosureImpl && enclosure.url.isNotEmpty()) {
                url = enclosure.url;
                return;
            }
        });

        if (url.isEmpty()) {
            throw RuntimeException("ERROR: Could not get track URL for ${track.title}");
        }

        return url;
    }

    private fun createPlayer(url: String): Player {
        try {
            return Manager.createPlayer(URL(url));
        }
        catch (e: MalformedURLException) { //IOException, NoPlayerException
            throw MalformedURLException("ERROR: Could not play ${tracks[trackIndex].title}, URL $url is malformed.");
        }
        catch (e: NoPlayerException) {
            val split = url.split(".");
            throw NoPlayerException("ERROR: Could not play ${tracks[trackIndex].title}, media type ${split[split.size - 1]} is not yet supported.");
        }
    }
}