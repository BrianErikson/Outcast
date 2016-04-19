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

    fun setTrack(track: Track) {
        mediaPlayer.track = Media(track.url.toURI().path);
        showcase.track = track;
        description.engine.loadContent("<html><body bgcolor='#f4f4f4' font-size='xx-small'>${track.description}</body></html>");
    }
}