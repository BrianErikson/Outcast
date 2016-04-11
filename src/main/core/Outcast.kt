import com.sun.syndication.feed.synd.SyndFeedImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.stage.Screen
import javafx.stage.Stage
import java.io.File

class Outcast: Application() {
    private var mediaController: MediaController;
    private val trackController: TrackController;
    private var rssFeed: SyndFeedImpl;
    private var stage: Stage? = null;

    init {
        val input = SyndFeedInput();
        rssFeed = input.build(XmlReader(File(javaClass.getResource("atpRss.txt").file))) as SyndFeedImpl;
        trackController = TrackController(rssFeed);
        mediaController = MediaController(Media(trackController.currentTrack.url));
    }

    override fun start(primaryStage: Stage) {
        VBox.setVgrow(mediaController, Priority.ALWAYS);
        stage = primaryStage;
        val root = VBox();
        val rssDisplay = RssFeedDisplay(rssFeed, mediaController);
        root.children.add(rssDisplay);
        root.children.add(mediaController);

        val bounds = Screen.getPrimary().visualBounds;
        root.prefHeight = bounds.height;

        primaryStage.title = "Outcast";
        primaryStage.scene = Scene(root, bounds.width / 2, bounds.height);
        primaryStage.show();
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Outcast::class.java);
        }
    }
}