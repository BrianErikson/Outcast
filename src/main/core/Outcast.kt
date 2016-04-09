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


class Outcast(val feedUrl: URL) : IHeadless {

    override var previousTrack: String = "None";
        get() {
            if (trackIndex - 1 >= 0) {
                println("GTe 0")
                return tracks[trackIndex - 1].title;
            }
            return currentTrack.title;
        }

    override var nextTrack: String = "None";
        get() {
            if (trackIndex + 1 < tracks.size) {
                return tracks[trackIndex + 1].title;
            }
            return currentTrack.title;
        }

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

    private var player: Player? = null;

    override fun launch(): Boolean {
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
            return true;
        }
        catch (e: Exception) {
            e.printStackTrace();
            println("ERROR: " + e.message);
            return false;
        }
    }

    override fun play() {
        if (currentTrack != tracks[trackIndex]) {
        currentTrack = tracks[trackIndex];
            player = createPlayer(getTrackUrl(currentTrack));
            player!!.start();
        }
        else {
            player?.start() ?: fun() {
                player = createPlayer(getTrackUrl(currentTrack));
                player!!.start();
            }
        }
    }

    override fun stop() {
        player?.stop();
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

    override fun getTrackName(): String {
        return tracks[trackIndex].title;
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