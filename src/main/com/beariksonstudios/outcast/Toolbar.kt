package com.beariksonstudios.outcast

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.layout.VBox
import org.controlsfx.control.BreadCrumbBar

class Toolbar: VBox() {
    enum class CrumbType {
        PODCASTS,
        TRACKS
    }

    var onCrumbAction: (CrumbType) -> Unit = {};
    var onRefreshPodcasts: () -> Unit = {};
    var onRefreshTracks: () -> Unit = {};
    var onSetUpdateRate: () -> Unit = {};

    private val PODCAST_CRUMB = "Podcasts";
    private val TRACK_CRUMB = "Tracks";
    private val plItem = TreeItem<String>(PODCAST_CRUMB);
    private val tlItem = TreeItem<String>(TRACK_CRUMB);
    private val breadcrumb = BreadCrumbBar<String>(plItem);
    private val menubar = MenuBar();

    init {
        val editMenu = Menu("Edit");
        val refreshPodsItem = MenuItem("Update Podcasts");
        val refreshTracksItem = MenuItem("Update Current Podcast");
        refreshPodsItem.setOnAction { onRefreshPodcasts() };
        refreshTracksItem.setOnAction { onRefreshTracks() };
        editMenu.items.addAll(refreshPodsItem, refreshTracksItem);

        val optMenu = Menu("Options");
        val updRateItem = MenuItem("Set Update Rate");
        updRateItem.setOnAction { onSetUpdateRate() };
        optMenu.items.add(updRateItem);

        menubar.menus.addAll(editMenu, optMenu);

        plItem.children.add(tlItem);
        breadcrumb.setOnCrumbAction {
            when (it.selectedCrumb) {
                plItem -> onCrumbAction(CrumbType.PODCASTS);
                tlItem -> onCrumbAction(CrumbType.TRACKS);
            }
        }

        children.addAll(menubar, breadcrumb);
    }

    fun onPodcastChange(title: String) {
        tlItem.valueProperty().value = title;
        breadcrumb.selectedCrumbProperty().set(tlItem);
        breadcrumb.onCrumbAction.handle(BreadCrumbBar.BreadCrumbActionEvent(tlItem));
    }
}