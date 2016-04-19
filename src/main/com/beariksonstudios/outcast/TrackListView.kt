package com.beariksonstudios.outcast

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent

class TrackListView(var onTrackOpen: (Track) -> Unit): ListView<Track>() {
    var podcast: Podcast? = null;
    set(value) {
        if (value != null) {
            selectionModel.clearSelection();
            items.setAll(value.tracks);
        }
        else {
            selectionModel.clearSelection();
            items.removeAll(items);
        }

        field = value;
    }

    init {
        setCellFactory {
            val cell = object : ListCell<Track>() {
                override fun updateItem(track: Track?, empty: Boolean) {
                    super.updateItem(track, empty);
                    if (track != null) text = track.title;
                    else text = "";
                }
            }
            return@setCellFactory cell;
        }

        onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onTrackOpen(selectionModel.selectedItem);
            }
        }

        items = FXCollections.observableArrayList();
    }
}