import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.feed.synd.SyndFeedImpl
import javafx.event.EventHandler
import javafx.scene.control.Accordion
import javafx.scene.control.ScrollPane
import javafx.scene.media.Media

class RssFeedDisplay(val mediaController: MediaController, feed: SyndFeedImpl? = null) : ScrollPane() {
    val accordion: Accordion;
    var rssFeed: SyndFeedImpl? = feed;
    set(value) {
        field = value;
        buildRssFeed();
    }

    init {
        hbarPolicy = ScrollBarPolicy.NEVER;
        vbarPolicy = ScrollBarPolicy.AS_NEEDED;

        accordion = Accordion();
        buildRssFeed();
        content = accordion;
    }

    fun buildRssFeed() {
        accordion.panes.clear();

        rssFeed?.entries?.forEach(fun(entry: Any?) {
            if (entry is SyndEntryImpl) {
                val entryDisplay = RssEntryDisplay(entry);
                entryDisplay.playButton.onAction = EventHandler<javafx.event.ActionEvent> {
                    mediaController.track = Media(TrackController.getTrackUrl(entryDisplay.rssEntry));
                }

                accordion.panes.add(entryDisplay);
            }
        });

        println("Rebuilt rss feed for ${rssFeed?.title ?: "an empty list"}")
    }
}