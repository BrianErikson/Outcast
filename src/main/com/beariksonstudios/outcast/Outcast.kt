package com.beariksonstudios.outcast

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.stage.Screen
import javafx.stage.Stage

class Outcast: Application() {
    private val podcastList: SearchView;
    private val trackList: TrackListView;
    private val playView = PlayingView();
    private var stage: Stage? = null;

    init {
        trackList = TrackListView { playView.setTrack(it); }
        podcastList = SearchView { trackList.podcast = it; }
    }

    override fun start(primaryStage: Stage) {
        stage = primaryStage;
        primaryStage.title = "Outcast";
        val root = HBox(podcastList, trackList, playView);

        val bounds = Screen.getPrimary().visualBounds;
        primaryStage.scene = Scene(root, bounds.width / 2, bounds.height / 1.5f);
        primaryStage.show();
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Outcast::class.java);
        }
    }
}