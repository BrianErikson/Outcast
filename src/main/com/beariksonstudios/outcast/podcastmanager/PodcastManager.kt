package podcastmanager

import com.beariksonstudios.outcast.Podcast
import com.beariksonstudios.outcast.Track
import java.sql.Timestamp

// TODO: Delegate between the database and retrieval of new feeds based on how stale the feed table is
object PodcastManager {
    private val updateDelay = 30; // in minutes
    private val updateRate = (updateDelay * 60) * 1000; // in milliseconds
    private val dbManager = DbManager();
    private val rssManager = RssManager();

    fun start() {}

    fun stop() {
        dbManager.stop();
    }

    fun getFeeds(): List<Feed> {
        if (dbManager.lastUpdated.after(Timestamp(System.currentTimeMillis() + updateRate))) {
            return dbManager.getFeeds();
        }
        else { return rssManager.getFeeds(); }
    }

    fun getPodcast(feed: Feed): Podcast? {
        if (dbManager.lastUpdated.after(Timestamp(System.currentTimeMillis() + updateRate))) {
            val podcast: Podcast? = dbManager.getPodcast(feed);
            if (podcast != null) return podcast else return RssManager.getPodcast(feed);
        }
        else {
            val updatedPodcast = RssManager.getPodcast(feed);
            if (updatedPodcast != null) {
                dbManager.addFeed(feed, updatedPodcast.rss);
                return updatedPodcast;
            }
            return dbManager.getPodcast(feed);
        }
    }

    fun getTracks(podcast: Podcast): List<Track> {
        return RssManager.getTracks(podcast);
    }
}