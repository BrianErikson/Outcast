import com.sun.syndication.feed.synd.SyndEntryImpl
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment

class RssEntryDisplay(entry: SyndEntryImpl): HBox() {
    val rssEntry: SyndEntryImpl = entry;

    init {
        alignment = Pos.BASELINE_CENTER;

        val contentVBox = VBox();
        val title = Text(rssEntry.title);

        // TODO: Fix html rendering for description
        title.textAlignment = TextAlignment.LEFT;
        //val description = Text(rssEntry.description.value);

        title.style = "font-weight: bold";

        contentVBox.children.add(title);
        //contentVBox.children.add(description);


        if (rssEntry.source?.image?.url != null) {
            val image = ImageView(rssEntry.source.image.url);
            children.add(image);
        }
        children.add(contentVBox);
    }
}