import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import java.awt.Desktop
import java.net.URI
import java.net.URL

data class SearchResult(val title: String, val rssUrl: URL);

interface SelectResultEvent {
    fun handle(result: SearchResult);
}

class SearchPane: VBox() {
    val resultsPane: ListView<SearchResult>;
    var onResultOpen: SelectResultEvent = object: SelectResultEvent { override fun handle(result: SearchResult) {} };

    init {
        val searchField = TextField("Search...");
        resultsPane = ListView<SearchResult>();
        resultsPane.setCellFactory {
            val cell = object: ListCell<SearchResult>() {
                override fun updateItem(item: SearchResult?, empty: Boolean) {
                    super.updateItem(item, empty);
                    if (item != null) text = item.title;
                }
            }
            return@setCellFactory cell;
        }
        resultsPane.onMouseClicked = EventHandler<MouseEvent>() {
            if (it.clickCount == 2) {
                onResultOpen.handle(resultsPane.selectionModel.selectedItem);
            }
        }

        val bottomNotice = Button("Not what you're looking for?");

        searchField.setOnAction {
            onSearch(searchField.text.trim());
        }

        bottomNotice.setOnAction {
            openWebsite(URI("http://www.google.com"));
        }


        children.addAll(searchField, resultsPane, bottomNotice);
    }

    fun getSearchResults(query: String): List<SearchResult> {
        return listOf(SearchResult("Accidental Tech Podcast (Test result)", javaClass.getResource("atpRss.txt").toURI().toURL()));
    }

    private fun onSearch(query: String) {
        println("Searching for $query")
        val results = getSearchResults(query);
        resultsPane.items = FXCollections.observableArrayList(results);
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
}