package com.beariksonstudios.outcast

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class SearchView(private var onPodcastOpen: (Podcast) -> Unit): VBox() {


    val listView = PodcastListView(onPodcastOpen);
    val searchField = TextField();
    val button = Button("Something Else?");

    init {
        VBox.setVgrow(listView, Priority.ALWAYS);
        VBox.setVgrow(searchField, Priority.NEVER);
        VBox.setVgrow(button, Priority.NEVER);
        button.tooltip = Tooltip("Can't find your favorite podcast? Add it!");

        onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onPodcastOpen(listView.selectionModel.selectedItem);
            }
        }

        children.addAll(searchField, listView, button);
    }

    fun setPodcasts(podcasts: List<Podcast>) {
        listView.podcasts = podcasts;
    }

    fun setOnPodcastOpen(onOpen: (Podcast) -> Unit) {
        onPodcastOpen = onOpen;
        listView.onPodcastOpen = onOpen;
    }
}