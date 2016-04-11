import com.sun.syndication.feed.synd.SyndEntryImpl
import javafx.scene.control.Button
import javafx.scene.control.TitledPane
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.web.WebView

class RssEntryDisplay(entry: SyndEntryImpl): TitledPane() {
    val rssEntry: SyndEntryImpl = entry;

    val playButton: Button;

    init {
        val vbox = VBox();
        val contentHBox = HBox();
        text = rssEntry.title;

        if (rssEntry.source?.image?.url != null) {
            val image = ImageView(rssEntry.source.image.url);
            contentHBox.children.add(image);
        }

        val description = WebView();
        description.engine.loadContent("<html><body bgcolor='#f4f4f4' font-size='xx-small'>${rssEntry.description.value}</body></html>")
        contentHBox.children.add(description);

        playButton = Button("Play");
        vbox.children.add(playButton);
        vbox.children.add(contentHBox);

        content = vbox;

    }


}