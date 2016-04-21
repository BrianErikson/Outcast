package podcastmanager

import com.beariksonstudios.outcast.Podcast
import com.beariksonstudios.outcast.Track

// TODO: Delegate between the database and retrieval of new feeds based on how stale the feed table is
object PodcastManager {
    private val dbManager = DbManager();
    private val rssManager = RssManager();

    fun start() {}

    fun stop() {
        dbManager.stop();
    }

    fun getFeeds(): List<Feed> {
        return rssManager.getFeeds();
    }

    fun getPodcast(feed: Feed): Podcast? {
        return rssManager.getPodcast(feed);
    }

    fun getTracks(podcast: Podcast): List<Track> {
        return rssManager.getTracks(podcast);
    }
}