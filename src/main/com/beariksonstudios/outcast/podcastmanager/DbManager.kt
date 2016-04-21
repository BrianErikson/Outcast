package podcastmanager

import org.h2.tools.Server
import java.sql.Connection
import java.sql.DriverManager

internal class DbManager {
    val TABLE_FEED = "Feeds";
    val FEED_TITLE = "title";
    val FEED_URL = "url";
    val FEED_RSS = "rss";
    val FEED_LAST_UPDATED = "lastUpdated";

    val connection: Connection
    get() {
        if (server.isRunning(false)) return field else throw RuntimeException("ERROR: Database is not running.");
    }

    private val server: Server = Server.createTcpServer("-tcpAllowOthers");

    init {
        println("Database Starting...")
        server.setOut(System.out);
        server.start();

        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:~/test");
        try {
            connection.createStatement().execute("CREATE TABLE ${TABLE_FEED} (${FEED_TITLE} text, ${FEED_URL} text, " +
                                                "${FEED_RSS} text, ${FEED_LAST_UPDATED} timestamp DEFAULT(now()))");
        }
        catch (e: Exception) {
            println(e.message?.substringAfter("ERROR: "));
        }
        if (server.isRunning(false)) println("Database Started.") else server.isRunning(true);
    }

    fun stop() {
        println("Database stopping...")
        connection.close();
        server.stop();
        println("Database stopped.");
    };
}