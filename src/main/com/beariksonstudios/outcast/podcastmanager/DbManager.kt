package podcastmanager

import com.beariksonstudios.outcast.Podcast
import com.sun.syndication.feed.synd.SyndFeedImpl
import org.apache.logging.log4j.LogManager
import org.h2.jdbc.JdbcSQLException
import org.h2.tools.Server
import java.io.*
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

    private val logger = LogManager.getLogger(DbManager::class.java);
    private val TABLE_FEED = "Feeds";
    private val FEED_TITLE = "title";
    private val FEED_URL = "url";
    private val FEED_RSS = "rss";
    private val FEED_LAST_UPDATED = "lastUpdated";
    private val server: Server = Server.createTcpServer("-tcpAllowOthers");

    init {
        logger.info("Starting...")
        server.setOut(System.out);
        server.start();

        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test;mode=PostgreSQL");
        try {
            connection.createStatement().execute("CREATE TABLE $TABLE_FEED ($FEED_TITLE text NOT NULL, $FEED_URL text NOT NULL, " +
                                                "$FEED_RSS binary NOT NULL, $FEED_LAST_UPDATED timestamp DEFAULT(now()))");
        }
        catch (e: JdbcSQLException) {
            logger.info(e.message?.substringAfter("ERROR: ")?.split(";")?.get(0) ?: e.originalMessage);
        }
        if (server.isRunning(false)) logger.info("Started.") else server.isRunning(true);
    }

    fun stop() {
        logger.info("stopping...")
        connection.close();
        server.stop();
        logger.info("stopped.");
    };

    fun addFeed(feed: Feed, rss: SyndFeedImpl) {
        logger.info("Adding or updating feed ${feed.title}");
        val serializedRss = serializeRssFeed(rss);

        try {
            val statement = connection.prepareStatement("UPDATE $TABLE_FEED SET $FEED_RSS=?, $FEED_URL=?, $FEED_LAST_UPDATED=now() WHERE title=?");
            statement.setBytes(1, serializedRss);
            statement.setString(2, feed.rssUrl.path);
            statement.setString(3, feed.title);
            var result = statement.executeUpdate();
            statement.close();
            if (result <= 0) { // Doesn't exist
                val s2 = connection.prepareStatement("INSERT INTO $TABLE_FEED VALUES(?,?,?,now())");
                s2.setString(1, feed.title);
                s2.setString(2, feed.rssUrl.toURI().toASCIIString());
                s2.setBytes(3, serializedRss);
                result = s2.executeUpdate();
                s2.close();
            }
            if (result <= 0) throw RuntimeException("Could not add ${feed.title}. Reason unknown.");
        }
        catch (e: JdbcSQLException) {
            e.printStackTrace();
        }
    }

    fun getFeeds(): List<Feed> {
        logger.info("Getting feeds");
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
        logger.info("Getting podcast ${feed.title}");
        val statement = connection.prepareStatement("SELECT $FEED_RSS FROM $TABLE_FEED WHERE $FEED_TITLE=?");
        statement.setString(1, feed.title);
        val result = statement.executeQuery();
        result.next();
        var rss: ByteArray? = null;
        try {
            rss = result.getBytes(FEED_RSS);
        }
        catch(e: JdbcSQLException) {
            print(e.message);
        }

        if (rss != null) {
            val parsedRss = parseRssFeed(rss);
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
            return Podcast(feed.title, parsedRss, imageUrl, parsedRss.description);
        }

        logger.info("${feed.title} not found");
        return null;
    }

    private fun parseRssFeed(bean: ByteArray): SyndFeedImpl {
        val input = ObjectInputStream(ByteArrayInputStream(bean));
        return input.readObject() as SyndFeedImpl;
    }

    private fun serializeRssFeed(rss: SyndFeedImpl): ByteArray {
        val stream = ByteArrayOutputStream();
        val output = ObjectOutputStream(stream);
        output.writeObject(rss);
        output.flush();
        return stream.toByteArray();
    }
}