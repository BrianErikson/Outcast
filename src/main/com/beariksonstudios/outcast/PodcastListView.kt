package com.beariksonstudios.outcast

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent
import podcastmanager.Feed

class PodcastListView(var onFeedOpen: (Feed) -> Unit): ListView<Feed>() {
    var feeds: List<Feed> = listOf();
    set(value) {
        selectionModel.clearSelection();
        items.setAll(value);
        field = value;
    }

    init {
        setCellFactory {
            val cell = object : ListCell<Feed>() {
                override fun updateItem(feed: Feed?, empty: Boolean) {
                    super.updateItem(feed, empty);
                    if (feed != null) text = feed.title;
                    else text = "";
                }
            }
            return@setCellFactory cell;
        }

        onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onFeedOpen(selectionModel.selectedItem);
            }
        }

        items = FXCollections.observableArrayList();
    }

    fun filterDisplay(regex: Regex) {
        val filtered = feeds.filter { return@filter it.title.contains(regex); }
        selectionModel.clearSelection();
        items.setAll(filtered);
    }
}