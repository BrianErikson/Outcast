package podcastmanager

import com.beariksonstudios.outcast.Podcast
import com.beariksonstudios.outcast.Track
import com.sun.syndication.feed.synd.SyndEnclosureImpl
import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.feed.synd.SyndFeedImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import org.jdom.Element
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.json.Json
import javax.json.JsonObject

data class Feed(val title: String, val rssUrl: URL, val date: String);
data class AtomFeed(val author: String, val subtitle: String, val summary: String, val explicit: String,
                    val keywords: String, val owner: String, val category: String, val image: String);

internal class RssManager {
    private val dbFeedPath = URL("http://localhost/feeds");
    private val connection = dbFeedPath.openConnection() as HttpURLConnection;

    fun getFeeds(): List<Feed> {
        println("Updating Feeds...");
        connection.requestMethod = "GET";
        val feeds = mutableListOf<Feed>();
        val jsonArray = Json.createReader(InputStreamReader(connection.inputStream)).readArray();
        jsonArray.forEach {
            if (it as JsonObject != null) {
                val title: String? = it.getString("title");
                val url: String? = it.getString("url");
                val date: String? = it.getString("date");

                if (title != null && url != null && date != null) {
                    try {
                        val validUrl = URL(url);
                        feeds.add(Feed(title, validUrl, date));
                    }
                    catch (e: Exception) {
                        println(e.message);
                    }
                }
            }
        }

        return feeds;
    }

    fun getPodcast(feed: Feed): Podcast? {
        try {
            val xmlReader = XmlReader(feed.rssUrl);
            val rss = SyndFeedInput().build(xmlReader) as SyndFeedImpl;
            var imageUrl: URL? = null;
            if (rss.image != null) {
                imageUrl = URL(rss.image.url);
            }
            else {
                val atomFeed = parseAtomFeed(rss);
                if (atomFeed.image.isNotEmpty()) {
                    imageUrl = URL(atomFeed.image);
                }
            }
            return Podcast(feed.title, rss, imageUrl, rss.description);
        }
        catch (e: IOException) {
            e.printStackTrace();
            return null;
        }
    }

    fun parseAtomFeed(rss: SyndFeedImpl): AtomFeed {
        val elements: List<*> = rss.foreignMarkup as List<*>;
        var author = "";
        var subtitle = "";
        var summary = "";
        var explicit = "";
        var keywords = "";
        var owner = "";
        var category = "";
        var image = "";
        elements.forEach {
            if (it is Element) {
                when (it.name) {
                    "author" -> author = it.value;
                    "subtitle" -> subtitle = it.value;
                    "summary" -> summary = it.value;
                    "explicit" -> explicit = it.value;
                    "keywords" -> keywords = it.value;
                    "owner" -> owner = it.value;
                    "category" -> category = it.value;
                    "image" -> image = it.getAttributeValue("href");
                }
            }
        }

        return AtomFeed(author, subtitle, summary, explicit, keywords, owner, category, image);
    }

    fun getTracks(podcast: Podcast): List<Track> {
        val tracks = mutableListOf<Track>();

        podcast.feed.entries.forEach {
            if (it is SyndEntryImpl) {
                try {
                    val url = URL(getTrackUrl(it));
                    tracks.add(Track(podcast, it.title, it.description.value, url));
                }
                catch(e: Exception) {
                    println(e.message);
                }
            }
        }

        return tracks;
    }

    private fun getTrackUrl(track: SyndEntryImpl): String? {
        var url: String = "";
        track.enclosures.forEach(fun(enclosure: Any?) {
            if (enclosure is SyndEnclosureImpl && enclosure.url.isNotEmpty()) {
                url = enclosure.url;
                return;
            }
        });

        if (url.isEmpty()) {
            println("ERROR: Could not get track URL for ${track.title}");
            return null;
        }

        return url;
    }
}