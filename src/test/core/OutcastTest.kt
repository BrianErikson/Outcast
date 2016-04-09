import org.junit.After
import org.junit.Test
import org.junit.Assert
import org.junit.Before
import java.net.URL

class OutcastTest {
    var outcastApp : IHeadless? = null;

    @Before fun Lifecycle_init_Successful() {
        outcastApp = Outcast(URL("http://atp.fm/episodes?format=rss"));
        Assert.assertNotNull(outcastApp);
        Assert.assertTrue(outcastApp!!.launch());
    }

    @After fun Lifecycle_destruction_Successful() {
        Assert.assertTrue(outcastApp!!.quit());
    }

    @Test fun Track_next_Changed() {
        val prevTrack = outcastApp!!.previousTrack;
        val curTrack = outcastApp!!.getTrackName();
        val nextTrack = outcastApp!!.nextTrack;
        outcastApp!!.loadNextTrack();


        val newLoadedTrack = outcastApp!!.getTrackName();

        Assert.assertFalse("Previous: $prevTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(prevTrack));
        Assert.assertFalse("Current: $curTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(curTrack));
        Assert.assertTrue("Next: $nextTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(nextTrack));
    }

    @Test fun Track_previous_Changed() {
        val prevTrack = outcastApp!!.previousTrack;
        val curTrack = outcastApp!!.getTrackName();
        val nextTrack = outcastApp!!.nextTrack;
        outcastApp!!.loadPreviousTrack();

        val newLoadedTrack = outcastApp!!.getTrackName();

        Assert.assertTrue("Previous: $prevTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(prevTrack));
        Assert.assertFalse("Current: $curTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(curTrack));
        Assert.assertFalse("Next: $nextTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(nextTrack));
    }

    @Test fun Track_seek_Operational() {
        Assert.fail("Unimplemented");
    }

    @Test fun Track_play_Operational() {
        Assert.fail("Unimplemented");
    }

    @Test fun Track_pause_Operational() {
        Assert.fail("Unimplemented");
    }

    @Test fun Track_stop_Operational() {
        Assert.fail("Unimplemented");
    }
}