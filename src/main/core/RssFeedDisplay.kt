import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.feed.synd.SyndFeedImpl
import javafx.event.EventHandler
import javafx.scene.control.Accordion
import javafx.scene.control.ScrollPane
import javafx.scene.media.Media

class RssFeedDisplay(feed: SyndFeedImpl, mediaController: MediaController) : ScrollPane() {
    val rssFeed: SyndFeedImpl = feed;

    init {
        hbarPolicy = ScrollBarPolicy.NEVER;
        vbarPolicy = ScrollBarPolicy.AS_NEEDED;

        val accordion = Accordion();
        rssFeed.entries.forEach(fun(entry: Any?) {
            if (entry is SyndEntryImpl) {
                val entryDisplay = RssEntryDisplay(entry);
                entryDisplay.playButton.onAction = EventHandler<javafx.event.ActionEvent> {
                    mediaController.track = Media(TrackController.getTrackUrl(entryDisplay.rssEntry));
                }

                accordion.panes.add(entryDisplay);
            }
        });

        content = accordion;
    }
}