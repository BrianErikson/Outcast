import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.media.Media
import javafx.stage.Stage

class Outcast: Application() {
    private var mediaController: MediaController;
    private val trackController: TrackController;

    init {
        trackController = TrackController();
        mediaController = MediaController(Media(trackController.currentTrack.url));
    }

    override fun start(primaryStage: Stage) {
        val root = mediaController;
        primaryStage.title = "Outcast";
        primaryStage.scene = Scene(root);
        primaryStage.show();
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Outcast::class.java);
        }
    }
}