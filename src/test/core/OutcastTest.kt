import org.junit.Test
import org.junit.Assert

class OutcastTest {
    val outcastApp : IHeadless = Outcast();

    // UnitOfWork_StateUnderTest_ExpectedBehavior
    @Test fun Lifecycle_startUp_Successful() {
        Assert.assertTrue(outcastApp.launch());
    }
    @Test fun Lifecycle_destruction_Successful() {
        Assert.assertTrue(outcastApp.quit());
    }

    @Test fun Track_next_Changed() {
        val prevTrack = outcastApp.previousTrack;
        val curTrack = outcastApp.currentTrack;
        val nextTrack = outcastApp.nextTrack;
        outcastApp.loadNextTrack();

        val newLoadedTrack = outcastApp.currentTrack;

        Assert.assertFalse(newLoadedTrack.equals(prevTrack));
        Assert.assertFalse(newLoadedTrack.equals(curTrack));
        Assert.assertTrue(newLoadedTrack.equals(nextTrack));
    }

    @Test fun Track_previous_Changed() {
        val prevTrack = outcastApp.previousTrack;
        val curTrack = outcastApp.currentTrack;
        val nextTrack = outcastApp.nextTrack;
        outcastApp.loadPreviousTrack();

        val newLoadedTrack = outcastApp.currentTrack;

        Assert.assertTrue(newLoadedTrack.equals(prevTrack));
        Assert.assertFalse(newLoadedTrack.equals(curTrack));
        Assert.assertFalse(newLoadedTrack.equals(nextTrack));
    }

    @Test fun Track_seek_Operational() {
        Assert.fail("Unimplemented");
    }
}