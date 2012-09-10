package is.thatsallthere.flexconnector;

import is.thatsallthere.flexconnector.melomel.Bridge;

import java.io.IOException;

public class Melomel {

    private Bridge bridge;

    public static Melomel connect() throws IOException {
        final Melomel melomel = new Melomel();
        melomel.bridge = new Bridge();
        melomel.bridge.connect();
        return melomel;
    }

    public void play(String message) throws IOException {
        bridge.send(message);
    }
}
