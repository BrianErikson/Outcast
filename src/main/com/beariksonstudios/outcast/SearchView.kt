package com.beariksonstudios.outcast

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import podcastmanager.Feed
import java.net.URI

class SearchView(private var onFeedOpen: (Feed) -> Unit): VBox() {


    val listView = PodcastListView(onFeedOpen);
    val searchField = TextField();
    val button = Button("Something Else?");

    init {
        VBox.setVgrow(listView, Priority.ALWAYS);
        VBox.setVgrow(searchField, Priority.NEVER);
        VBox.setVgrow(button, Priority.NEVER);
        button.tooltip = Tooltip("Can't find your favorite podcast? Add it!");

        button.setOnAction {
            Outcast.openWebsite(URI("http://localhost"));
        }

        onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onFeedOpen(listView.selectionModel.selectedItem);
            }
        }

        children.addAll(searchField, listView, button);
    }

    fun setFeeds(feeds: List<Feed>) {
        listView.feeds = feeds;
    }

    fun setOnPodcastOpen(onOpen: (Feed) -> Unit) {
        onFeedOpen = onOpen;
        listView.onFeedOpen = onOpen;
    }
}