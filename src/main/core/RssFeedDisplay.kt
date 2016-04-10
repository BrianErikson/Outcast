import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.feed.synd.SyndFeedImpl
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox

class RssFeedDisplay(feed: SyndFeedImpl) : ScrollPane() {
    val rssFeed: SyndFeedImpl = feed;

    init {
        hbarPolicy = ScrollBarPolicy.NEVER;
        vbarPolicy = ScrollBarPolicy.AS_NEEDED;

        val vbox = VBox();
        rssFeed.entries.forEach(fun(entry: Any?) {
            if (entry is SyndEntryImpl) {
                vbox.children.add(RssEntryDisplay(entry));
            }
        });

        content = vbox;
    }
}