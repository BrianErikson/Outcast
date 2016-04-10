import com.sun.syndication.feed.synd.SyndEnclosureImpl
import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import java.io.File
import java.net.URL
import java.util.*


data class Track(val author: String, val url: String, val title: String, val description: String, val date: Date);

class TrackController {
    var nextTrack: Track
        get() {
            if (index + 1 >= tracks.size) return currentTrack else return tracks[index + 1];
        }
        private set(value) {}

    var previousTrack: Track
        get() {
            if (index - 1 < 0 || tracks.size <= 0) return currentTrack else return tracks[index - 1];
        }
        private set(value) {}

    var currentTrack: Track
        get() {
            if (tracks.size > 0) return tracks[index] else throw RuntimeException("Cannot get current track. Track list is empty.");
        }
        private set(value) {}

    private var tracks: List<Track> = listOf();
    private var index: Int = 0;

    init {
        val input = SyndFeedInput();
        val feed = input.build(XmlReader(File(javaClass.getResource("atpRss.txt").file)));
        val links = mutableListOf<Track>();

        feed.entries.forEach(fun(entry: Any?) {
            if (entry is SyndEntryImpl) {
                links.add(Track(entry.author, getTrackUrl(entry), entry.title, entry.description.value, entry.publishedDate));
            }
        });

        tracks = links.toList();

        if (tracks.isEmpty()) {
            throw RuntimeException("ERROR: Number of tracks: ${tracks.isEmpty()}");
        }
    }

    fun moveToNext(): Track {
        if (index + 1 < tracks.size) index++;
        return currentTrack;
    }

    fun moveToPrevious(): Track {
        if (index - 1 > 0) index--;
        return currentTrack;
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
}