package com.beariksonstudios.outcast

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Screen
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import podcastmanager.PodcastManager
import java.awt.Desktop
import java.net.URI

class Outcast: Application() {
    private val logger = LogManager.getLogger(Outcast::class.java);
    private val borderPane: BorderPane;
    private val podcastList: SearchView;
    private val trackList: TrackListView;
    private val playView = PlayingView();
    private val toolbar = Toolbar();
    private var stage: Stage? = null;


    init {
        System.setProperty("log4j.configurationFile", Outcast::class.java.classLoader.getResource("log4j2.xml").path);
        PodcastManager.start();

        trackList = TrackListView({
            playView.setTrack(it);
        }, {
            toolbar.onPodcastChange(it.feed.title);
            return@TrackListView PodcastManager.getTracks(it);
        });

        podcastList = SearchView {
            val podcast: Podcast? = PodcastManager.getPodcast(it);
            if (podcast != null) {
                trackList.podcast = podcast;
            }
            else {
                logger.info("ERROR in SearchView: Could not obtain podcast data for ${it.title}.");
            }
        }
        podcastList.setFeeds(PodcastManager.getFeeds(false));

        borderPane = BorderPane(playView, toolbar, null, null, podcastList);

        toolbar.onCrumbAction = {
            when (it) {
                Toolbar.CrumbType.PODCASTS -> borderPane.left = podcastList;
                Toolbar.CrumbType.TRACKS -> borderPane.left = trackList;
            }
        }
        toolbar.onRefreshPodcasts = {
            podcastList.setFeeds(PodcastManager.getFeeds(true));
            trackList.podcast = null; // wipe current podcast to avoid unknowns
            playView.setTrack(null);
        }
        toolbar.onRefreshTracks = {
            val podcast = trackList.podcast;
            if (podcast != null) {
                trackList.podcast = PodcastManager.getPodcast(podcast.feed, true);
                playView.setTrack(null);
            }
        }
    }

    override fun start(primaryStage: Stage) {
        stage = primaryStage;
        primaryStage.setOnCloseRequest {
            PodcastManager.stop();
        }

        primaryStage.title = "Outcast";

        val bounds = Screen.getPrimary().visualBounds;
        primaryStage.scene = Scene(borderPane, bounds.width / 2, bounds.height / 1.5f);
        primaryStage.show();
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Outcast::class.java);
        }

        fun openWebsite(uri: URI) {
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(uri);
                    }
                    catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}