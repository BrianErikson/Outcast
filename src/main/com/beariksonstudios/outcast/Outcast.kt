package com.beariksonstudios.outcast

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import javafx.stage.Screen
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.controlsfx.control.BreadCrumbBar
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

    private val logger = LogManager.getLogger(Outcast::class.java);
    private val borderPane: BorderPane;
    private val podcastList: SearchView;
    private val trackList: TrackListView;
    private val playView = PlayingView();
    private val PODCAST_CRUMB = "Podcasts";
    private val TRACK_CRUMB = "Tracks";
    private val plItem = TreeItem<String>(PODCAST_CRUMB);
    private val tlItem = TreeItem<String>(TRACK_CRUMB);
    private val breadcrumb = BreadCrumbBar<String>(plItem);
    private var stage: Stage? = null;


    init {
        System.setProperty("log4j.configurationFile", Outcast::class.java.classLoader.getResource("log4j2.xml").path);
        PodcastManager.start();

        trackList = TrackListView({
            playView.setTrack(it);
        }, {
            tlItem.valueProperty().value = it.title;
            breadcrumb.selectedCrumbProperty().set(tlItem);
            breadcrumb.onCrumbAction.handle(BreadCrumbBar.BreadCrumbActionEvent(tlItem));
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
        podcastList.setFeeds(PodcastManager.getFeeds());

        borderPane = BorderPane(playView, breadcrumb, null, null, podcastList);

        plItem.children.add(tlItem);
        breadcrumb.setOnCrumbAction {
            when (it.selectedCrumb) {
                plItem -> borderPane.left = podcastList;
                tlItem -> borderPane.left = trackList;
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
    }
}