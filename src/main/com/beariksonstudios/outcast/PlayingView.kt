package com.beariksonstudios.outcast

import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.web.WebView

class PlayingView: VBox() {
    private val showcase: Showcase = Showcase();
    private val description: WebView = WebView();
    private val mediaPlayer: MediaController = MediaController();

    init {
        VBox.setVgrow(mediaPlayer, Priority.NEVER);
        children.addAll(showcase, description, mediaPlayer);
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
}