import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import java.awt.Desktop
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.json.Json
import javax.json.JsonObject

data class Feed(val title: String, val rssUrl: URL, val date: String);

interface SelectResultEvent {
    fun handle(result: Feed);
}

class SearchPane: VBox() {
    val serverPath: URL;
    val dbFeedPath: URL;
    val feedConnection: HttpURLConnection;
    val resultsPane: ListView<Feed>;
    var feeds: List<Feed>;
    var onResultOpen: SelectResultEvent = object: SelectResultEvent { override fun handle(result: Feed) {} };

    init {
        val serverStr = "http://localhost";
        serverPath = URL(serverStr);
        dbFeedPath = URL("$serverStr/feeds");
        feedConnection = dbFeedPath.openConnection() as HttpURLConnection;
        feeds = updateFeeds();

        val searchField = TextField("Search...");
        resultsPane = ListView<Feed>();
        resultsPane.setCellFactory {
            val cell = object: ListCell<Feed>() {
                override fun updateItem(feed: Feed?, empty: Boolean) {
                    super.updateItem(feed, empty);
                    if (feed != null) text = feed.title;
                    else text = "";
                }
            }
            return@setCellFactory cell;
        }
        resultsPane.onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onResultOpen.handle(resultsPane.selectionModel.selectedItem);
            }
        }
        resultsPane.items = FXCollections.observableArrayList(feeds);

        val bottomNotice = Button("Not what you're looking for?");

        searchField.setOnAction {
            onSearch(searchField.text.trim());
        }

        bottomNotice.setOnAction {
            openWebsite(serverPath.toURI());
        }

        children.addAll(searchField, resultsPane, bottomNotice);
    }

    private fun onSearch(query: String) {
        println("Searching for $query");
        val results = getFilteredItems(query);
        updateResults(results);
    }

    private fun openWebsite(uri: URI) {
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

    private fun updateResults(list: List<Feed>) {
        resultsPane.selectionModel.clearSelection();
        resultsPane.items.setAll(list);
        resultsPane.itemsProperty().set(resultsPane.items);
    }

    private fun getFilteredItems(regexp: String): List<Feed> {
        return feeds.filter {
            return@filter it.title.contains(regexp);
        }
    }

    private fun updateFeeds(): List<Feed> {
        println("=================");
        println("Updating Feeds...");
        feedConnection.requestMethod = "GET";

        val newFeeds = mutableListOf<Feed>();
        val jsonArray = Json.createReader(InputStreamReader(feedConnection.inputStream)).readArray();
        jsonArray.forEach {
            if (it as JsonObject != null) {
                val title: String? = it.getString("title");
                val url: String? = it.getString("url");
                val date: String? = it.getString("date");

                if (title != null && url != null && date != null) {
                    try {
                        val validUrl = URL(url);
                        newFeeds.add(Feed(title, validUrl, date));
                    }
                    catch (e: Exception) {
                        println(e.message);
                    }
                }
            }
        }
        return newFeeds.toList();
    }
}