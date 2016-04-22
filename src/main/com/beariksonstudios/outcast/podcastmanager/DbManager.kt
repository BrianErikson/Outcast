package podcastmanager

import com.beariksonstudios.outcast.Podcast
import com.sun.syndication.feed.synd.SyndFeedImpl
import org.h2.jdbc.JdbcSQLException
import org.h2.tools.Server
import java.io.IOException
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp

internal class DbManager {
    val connection: Connection
    get() {
        if (server.isRunning(false)) return field else throw RuntimeException("ERROR: Database is not running.");
    }

    val lastUpdated: Timestamp
    get() {
        val result = connection.createStatement().executeQuery("SELECT TOP 1 $FEED_LAST_UPDATED FROM $TABLE_FEED " +
                                                               "ORDER BY $FEED_LAST_UPDATED ASC");
        result.next();
        try {
            return result.getTimestamp(FEED_LAST_UPDATED);
        }
        catch (e: JdbcSQLException) {
            return Timestamp(0);
        }
    }

    private val TABLE_FEED = "Feeds";
    private  val FEED_TITLE = "title";
    private val FEED_URL = "url";
    private val FEED_RSS = "rss";
    private val FEED_LAST_UPDATED = "lastUpdated";
    private val server: Server = Server.createTcpServer("-tcpAllowOthers");

    init {
        print("Database Starting...")
        server.setOut(System.out);
        server.start();

        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:~/test");
        try {
            connection.createStatement().execute("CREATE TABLE $TABLE_FEED ($FEED_TITLE text NOT NULL, $FEED_URL text NOT NULL, " +
                                                "$FEED_RSS text NOT NULL, $FEED_LAST_UPDATED timestamp NOT NULL DEFAULT(now()))");
        }
        catch (e: JdbcSQLException) {
            print(e.message?.substringAfter("ERROR: ")?.split(";")?.get(0) ?: e.originalMessage);
        }
        if (server.isRunning(false)) print("Database Started.") else server.isRunning(true);
    }

    fun stop() {
        print("Database stopping...")
        connection.close();
        server.stop();
        print("Database stopped.");
    };

    fun addFeed(feed: Feed, rss: SyndFeedImpl) {
        print("Adding or updating feed ${feed.title}");

        try {
            connection.createStatement().executeUpdate("INSERT INTO $TABLE_FEED VALUES('${feed.title}', '${feed.rssUrl}', '$rss')" +
                    "ON DUPLICATE KEY UPDATE $FEED_RSS='${rss.toString()}', $FEED_URL='${feed.rssUrl}', $FEED_LAST_UPDATED=now()");
        }
        catch (e: JdbcSQLException) {
            print(e.message?.substring(0, 100) ?: e.originalMessage);
        }
    }

    fun getFeeds(): List<Feed> {
        print("Getting feeds");
        val feeds = mutableListOf<Feed>();

        val result = connection.createStatement().executeQuery("SELECT $FEED_TITLE, $FEED_URL, $FEED_LAST_UPDATED FROM $TABLE_FEED");
        while (result.next()) {
            val title: String = result.getString(FEED_TITLE);
            val url: String = result.getString(FEED_URL);
            val date: Timestamp = result.getTimestamp(FEED_LAST_UPDATED);
            feeds.add(Feed(title, URL(url), date));
        }

        return feeds;
    }

    fun getPodcast(feed: Feed): Podcast? {
        print("Getting podcast ${feed.title}");
        val result = connection.createStatement().executeQuery("SELECT $FEED_TITLE, $FEED_URL, $FEED_LAST_UPDATED FROM $TABLE_FEED WHERE $FEED_TITLE=${feed.title}");
        result.next();
        val title: String? = result.getString(FEED_TITLE);
        val rss: String? = result.getString(FEED_RSS);

        if (title != null && rss != null) {
            val parsedRss = RssManager.parseRssFeed(rss);
            val imageStr = RssManager.getImageURL(parsedRss);
            var imageUrl: URL? = null;
            if (imageStr != null && imageStr.isNotEmpty()) {
                try {
                    imageUrl = URL(imageStr);
                }
                catch (e: IOException) {
                    e.printStackTrace();
                }
            }
            return Podcast(title, parsedRss, imageUrl, parsedRss.description);
        }

        print("${feed.title} not found");
        return null;
    }

    private fun print(str:String) {
        println("DbManager: $str");
    }
}