package leemos.orion.test.scenes;

import leemos.orion.core.engine.Api;
import leemos.orion.core.engine.Scene;

@Scene(name = "counter")
public class CounterScene {

    private static int counter = 1;

    @Api(name = "increase")
    public int increase() {
        return counter++;
    }

    @Api(name = "reset")
    public int reset() {
        counter = 0;
        return counter;
    }
}
