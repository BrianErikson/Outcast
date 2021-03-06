package com.beariksonstudios.outcast

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.web.WebView
import org.apache.logging.log4j.LogManager
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLAnchorElement
import java.net.URI
import java.net.URISyntaxException

class PlayingView: VBox() {
    private val logger = LogManager.getLogger(PlayingView::class.java);
    private val showcase: Showcase = Showcase();
    private val description: WebView = WebView();
    private val mediaPlayer: MediaController = MediaController();

    init {
        description.engine.documentProperty().addListener({ observableValue, oldValue, newValue -> run {
            if (newValue != null) hookHyperlinkEvents(newValue);
        }});

        VBox.setVgrow(mediaPlayer, Priority.NEVER);

        val splitPane = SplitPane(showcase, description);
        splitPane.orientation = Orientation.VERTICAL;
        splitPane.setDividerPositions(0.15);

        children.addAll(splitPane, mediaPlayer);
    }

    fun setTrack(track: Track?) {
        if (track != null) {
            mediaPlayer.track = Media(track.url.toExternalForm());
            showcase.track = track;
            description.engine.loadContent("<html><body bgcolor='#f4f4f4' font-size='xx-small'>${track.description}</body></html>");
        }
        else {
            mediaPlayer.track = null;
            showcase.track = null;
            description.engine.loadContent("<html><body bgcolor='#f4f4f4' font-size='xx-small'></body></html>");
        }
    }

    private fun hookHyperlinkEvents(document: Document) {
        val list: NodeList = document.getElementsByTagName("a");
        for (i in 0..list.length) {
            val node = list.item(i);
            if (node is EventTarget) {
                node.addEventListener("click", {
                    val target = it.currentTarget;
                    if (target is HTMLAnchorElement) {
                        try {
                            val uri = URI(target.href);
                            Outcast.openWebsite(uri);
                        }
                        catch(e: URISyntaxException) {
                            // Ignore
                            logger.error(e.message);
                        }
                    }
                    it.preventDefault();
                }, false);
            }
        }
    }
}