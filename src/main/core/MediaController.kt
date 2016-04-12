import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.*
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.MediaView
import javafx.util.Duration

class MediaController(startMedia: Media? = null) : BorderPane() {

    var track: Media? = startMedia;
    set(value) {
        field = value;
        rebuildMediaPlayer();
    }

    private var mediaPlayer: MediaPlayer? = null;
    private val mediaView: MediaView;
    private val repeat = false;
    private var stopRequested = false;
    private var atEndOfMedia = false;
    private var duration: Duration;
    private val timeSlider: Slider;
    private val playTime: Label;
    private val volumeSlider: Slider;
    private val mediaBar: HBox;
    private val playButton: Button;

    init {
        playButton = Button(">");
        duration = Duration.ZERO;
        style = "-fx-background-color: #bfc2c7;"
        mediaView = MediaView(mediaPlayer)
        val mvPane = Pane();
        mvPane.children.add(mediaView);
        center = mvPane
        mediaBar = HBox()
        mediaBar.alignment = Pos.CENTER
        mediaBar.padding = Insets(5.0, 10.0, 5.0, 10.0)
        BorderPane.setAlignment(mediaBar, Pos.CENTER)


        playButton.onAction = EventHandler<javafx.event.ActionEvent> {
            val status = mediaPlayer?.status ?: Status.UNKNOWN;

            if (status == Status.UNKNOWN || status == Status.HALTED) {
                // don't do anything in these states
                return@EventHandler
            }

            if (status == Status.PAUSED || status == Status.READY
                    || status == Status.STOPPED) {
                // rewind the movie if we're sitting at the end
                if (atEndOfMedia) {
                    mediaPlayer?.seek(mediaPlayer?.startTime)
                    atEndOfMedia = false
                }
                mediaPlayer?.play()
            } else {
                mediaPlayer?.pause()
            }
        }

        mediaBar.children.add(playButton)
        // Add spacer
        val spacer = Label("   ")
        mediaBar.children.add(spacer)

        // Add Time label
        val timeLabel = Label("Time: ")
        mediaBar.children.add(timeLabel)

        // Add time slider
        timeSlider = Slider()
        HBox.setHgrow(timeSlider, Priority.ALWAYS)
        timeSlider.minWidth = 50.0
        timeSlider.maxWidth = java.lang.Double.MAX_VALUE

        timeSlider.valueProperty().addListener(InvalidationListener {
            if (timeSlider.isValueChanging) {
                // multiply duration by percentage calculated by slider position
                mediaPlayer?.seek(duration.multiply(timeSlider.value / 100.0))
            }
        })

        mediaBar.children.add(timeSlider)

        // Add Play label
        playTime = Label()
        playTime.prefWidth = 130.0
        playTime.minWidth = 50.0
        mediaBar.children.add(playTime)

        // Add the volume label
        val volumeLabel = Label("Vol: ")
        mediaBar.children.add(volumeLabel)

        // Add Volume slider
        volumeSlider = Slider()
        volumeSlider.prefWidth = 70.0
        volumeSlider.maxWidth = Region.USE_PREF_SIZE
        volumeSlider.minWidth = 30.0
        volumeSlider.valueProperty().addListener(InvalidationListener {
            if (volumeSlider.isValueChanging) {
                mediaPlayer?.volume = volumeSlider.value / 100.0
            }
        })
        mediaBar.children.add(volumeSlider)
        bottom = mediaBar
    }

    protected fun updateValues() {
        Platform.runLater {
            val currentTime = mediaPlayer?.currentTime ?: Duration.ZERO;
            playTime.text = formatTime(currentTime, duration);
            timeSlider.isDisable = duration.isUnknown;
            if (!timeSlider.isDisabled && duration.greaterThan(Duration.ZERO)
                    && !timeSlider.isValueChanging) {
                timeSlider.value = currentTime.divide(duration).toMillis() * 100.0;
            }
            if (!volumeSlider.isValueChanging) {
                volumeSlider.value = Math.round((mediaPlayer?.volume ?: 1.toDouble()) * 100).toDouble();
            }
        }
    }

    private fun formatTime(elapsed: Duration, duration: Duration): String {
        var intElapsed = Math.floor(elapsed.toSeconds()).toInt()
        val elapsedHours = intElapsed / (60 * 60)
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60
        }
        val elapsedMinutes = intElapsed / 60
        val elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60

        if (duration.greaterThan(Duration.ZERO)) {
            var intDuration = Math.floor(duration.toSeconds()).toInt()
            val durationHours = intDuration / (60 * 60)
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60
            }
            val durationMinutes = intDuration / 60
            val durationSeconds = intDuration - durationHours * 60 * 60
            -durationMinutes * 60
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds, durationHours, durationMinutes,
                        durationSeconds)
            } else {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes,
                        elapsedSeconds, durationMinutes, durationSeconds)
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes,
                        elapsedSeconds)
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)
            }
        }
    }

    private fun rebuildMediaPlayer() {
        mediaPlayer?.dispose();
        playButton.text = ">";

        mediaPlayer = MediaPlayer(track);
        mediaPlayer!!.isAutoPlay = true;
        updateValues();
        mediaPlayer!!.currentTimeProperty().addListener(InvalidationListener { updateValues() })

        mediaPlayer!!.onPlaying = Runnable {
            if (stopRequested) {
                mediaPlayer!!.pause()
                stopRequested = false
            } else {
                playButton.text = "||"
            }
        }

        mediaPlayer!!.onPaused = Runnable {
            playButton.text = ">"
        }

        mediaPlayer!!.onReady = Runnable {
            duration = mediaPlayer!!.media.duration
            updateValues()
        }

        mediaPlayer!!.cycleCount = if (repeat) MediaPlayer.INDEFINITE else 1
        mediaPlayer!!.onEndOfMedia = Runnable {
            if (!repeat) {
                playButton.text = ">"
                stopRequested = true
                atEndOfMedia = true
            }
        }
    }
}