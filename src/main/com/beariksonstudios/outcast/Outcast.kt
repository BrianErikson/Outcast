package com.beariksonstudios.outcast

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.SplitPane
import javafx.stage.Screen
import javafx.stage.Stage
import podcastmanager.Feed
import podcastmanager.PodcastManager

class Outcast: Application() {
    var feeds: List<Feed>? = listOf();
    set(value) {
        if (value != null) {
            podcastList.setFeeds(value);
        }
        trackList.podcast = null;
        playView.setTrack(null);

        field = value;
    }

    private val podcastList: SearchView;
    private val trackList: TrackListView;
    private val playView = PlayingView();
    private var stage: Stage? = null;


    init {
        PodcastManager.start();

        trackList = TrackListView({
            playView.setTrack(it);
        }, {
            return@TrackListView PodcastManager.getTracks(it);
        });

        podcastList = SearchView {
            val podcast: Podcast? = PodcastManager.getPodcast(it);
            if (podcast != null) {
                trackList.podcast = podcast;
            }
            else {
                println("ERROR in SearchView: Could not obtain podcast data for ${it.title}.");
            }
        }
        podcastList.setFeeds(PodcastManager.getFeeds());
    }

    override fun start(primaryStage: Stage) {
        stage = primaryStage;
        primaryStage.setOnCloseRequest {
            PodcastManager.stop();
        }

        primaryStage.title = "Outcast";
        val root = SplitPane(podcastList, trackList, playView);
        root.setDividerPositions(0.2, 0.4);

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