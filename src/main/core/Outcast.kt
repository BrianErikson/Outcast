import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import java.io.File
import java.net.URL

class Outcast(val feedUrl: URL) : IHeadless {
    override var previousTrack: String
        get() {
            if (_currentTrack - 1 >= 0) {
                println("GTe 0")
                return tracks[_currentTrack - 1].title;
            }
            return tracks[_currentTrack].title;
        }
        set(value) {
        }
    override var nextTrack: String
        get() {
            if (_currentTrack + 1 < tracks.size) {
                return tracks[_currentTrack + 1].title;
            }
            return tracks[_currentTrack].title;
        }
        set(value) {
        }

    private var _currentTrack: Int = 0;
    override var currentTrack: String
        get() {
            return tracks[_currentTrack].title;
        }
        set(value) {
        }

    private var seekAmount: Float
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    private var tracks: List<SyndEntryImpl> = listOf();

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
        throw UnsupportedOperationException()
//        tracks[_currentTrack].enclosures.forEach(fun(enclosure: Any?) {
//            if (enclosure is SyndEnclosureImpl && enclosure.url.isNotEmpty()) {
//                try {
//                    val url = URL(enclosure.url);
//                    links.add(url);
//                }
//                catch (e: MalformedURLException) {
//                    println("ERROR: " + enclosure.url + " for " + entry.title + " is malformed.");
//                }
//            }
//            else {
//                println("ERROR: " + entry.title + " source is not available.");
//            }
//        });
    }

    override fun pause() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        throw UnsupportedOperationException()
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