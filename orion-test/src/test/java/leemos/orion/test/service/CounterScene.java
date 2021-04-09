package leemos.orion.test.service;

import java.util.concurrent.atomic.AtomicInteger;

import leemos.orion.core.engine.Api;
import leemos.orion.core.engine.Scene;

@Scene(name = "counter")
public class CounterScene {

    private static AtomicInteger counter = new AtomicInteger(1);

    @Api(name = "increase")
    public int increase() {
        return counter.getAndIncrement();
    }

    @Api(name = "reset")
    public int reset() {
        counter.set(1);
        return 0;
    }
}
