package com.beariksonstudios.outcast

import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text

class Showcase: HBox() {
    var track: Track? = null;
    set(value) {
        if (value != null) {
            podcastTitle.text = value.podcast.title;
            trackTitle.text = value.title;
            if (value.podcast.imageUrl != null) {
                imageView.image = Image(value.podcast.imageUrl.openStream(), 100.0, 100.0, true, true);
            }
            else {
                imageView.isVisible = false;
            }
        }
        else {
            podcastTitle.isVisible = false;
            trackTitle.isVisible = false;
            imageView.isVisible = false;
        }

        field = value;
    }

    private val podcastTitle: Text = Text();
    private val trackTitle: Text = Text();
    private val imageView: ImageView = ImageView();

    init {
        setMargin(imageView, Insets(0.0, 4.0, 0.0, 0.0));
        padding = Insets(2.0);

        podcastTitle.font = Font("Arial Bold", 24.0);
        trackTitle.font = Font("Arial Bold", 18.0);

        val vbox = VBox(podcastTitle, trackTitle);
        children.addAll(imageView, vbox);
    }
}