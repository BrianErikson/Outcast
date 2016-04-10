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

    // TODO: Test for nextTrack == curTrack because at end of track list
    @Test fun Track_next_Changed() {
        testTracks();
        val prevTrack = outcastApp!!.getPreviousTrackName();
        val nextTrack = outcastApp!!.getNextTrackName();
        outcastApp!!.loadNextTrack();


        val newLoadedTrack = outcastApp!!.getTrackName();

        Assert.assertFalse("Previous: $prevTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(prevTrack));
        Assert.assertTrue("Next: $nextTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(nextTrack));
    }

    // TODO: Test for prevTrack == curTrack because at beginning of track list
    @Test fun Track_previous_Changed() {
        testTracks();
        val prevTrack = outcastApp!!.getPreviousTrackName();
        val nextTrack = outcastApp!!.getNextTrackName();
        outcastApp!!.loadPreviousTrack();

        val newLoadedTrack = outcastApp!!.getTrackName();

        Assert.assertTrue("Previous: $prevTrack Loaded: $newLoadedTrack", newLoadedTrack.equals(prevTrack));
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

    private fun testTracks() {
        val prevTrack = outcastApp!!.getPreviousTrackName();
        val curTrack = outcastApp!!.getTrackName();
        val nextTrack = outcastApp!!.getNextTrackName();

        Assert.assertFalse(prevTrack.equals("None"));
        Assert.assertFalse(curTrack.equals("None"));
        Assert.assertFalse(nextTrack.equals("None"));
    }
}