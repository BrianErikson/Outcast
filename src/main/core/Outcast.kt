import com.sun.syndication.feed.synd.SyndFeedImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.stage.Screen
import javafx.stage.Stage
import java.awt.TextField
import java.io.File

class Outcast: Application() {
    private val mediaController: MediaController;
    private val trackController: TrackController? = null;
    private var stage: Stage? = null;

    init {
        if (trackController != null) { mediaController = MediaController(Media(trackController.currentTrack.url)); }
        else { mediaController = MediaController(); }
    }

    override fun start(primaryStage: Stage) {
        VBox.setVgrow(mediaController, Priority.NEVER);
        stage = primaryStage;
        val root = HBox();

        val searchPane = SearchPane();

        val rightPane = VBox();

        val rssDisplay = RssFeedDisplay(mediaController);
        VBox.setVgrow(rssDisplay, Priority.ALWAYS);

        searchPane.onResultOpen = object: SelectResultEvent { override fun handle(result: SearchResult) {
            rssDisplay.rssFeed = SyndFeedInput().build(XmlReader(result.rssUrl)) as SyndFeedImpl;
        } };

        rightPane.children.addAll(rssDisplay, mediaController);

        root.children.addAll(searchPane, rightPane);

        primaryStage.title = "Outcast";

        val bounds = Screen.getPrimary().visualBounds;
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