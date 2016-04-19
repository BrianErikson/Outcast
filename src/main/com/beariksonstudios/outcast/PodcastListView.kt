package com.beariksonstudios.outcast

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent

class PodcastListView(var onPodcastOpen: (Podcast) -> Unit): ListView<Podcast>() {
    var podcasts: List<Podcast> = listOf();
    set(value) {
        selectionModel.clearSelection();
        items.setAll(value);
        field = value;
    }

    init {
        onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onPodcastOpen(selectionModel.selectedItem);
            }
        }

        items = FXCollections.observableArrayList();
    }

    fun filterDisplay(regex: Regex) {
        val filtered = podcasts.filter { return@filter it.title.contains(regex); }
        selectionModel.clearSelection();
        items.setAll(filtered);
    }
}