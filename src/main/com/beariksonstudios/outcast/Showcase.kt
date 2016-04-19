package com.beariksonstudios.outcast

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javax.imageio.ImageIO

class Showcase: HBox() {
    var track: Track? = null;
    set(value) {
        if (value != null) {
            podcastTitle.text = value.podcast.title;
            trackTitle.text = value.title;
            if (value.podcast.imageUrl != null) {
                val buffer = ImageIO.read(value.podcast.imageUrl.openStream());
                imageView.image = SwingFXUtils.toFXImage(buffer, WritableImage(buffer.width, buffer.height));
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
        val vbox = VBox(podcastTitle, trackTitle);
        children.addAll(imageView, vbox);
    }
}