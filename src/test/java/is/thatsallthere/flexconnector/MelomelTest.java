package is.thatsallthere.flexconnector;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MelomelTest {

    @Test
    public void run_the_server_and_listen_to_melomel() throws Exception {
        Melomel melomel = Melomel.connect();
        melomel.play("<get-class name=\"melomel.core.UI\" throwable=\"false\"/>");
        assertTrue(true);
    }
}
