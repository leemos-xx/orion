package leemos.orion.remoting;

import java.util.concurrent.TimeUnit;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import leemos.orion.commons.NamedThreadFactory;

/**
 * Timer
 *
 * @author lihao
 * @date 2020年8月19日
 * @version 1.0
 */
public class TimerHolder {

    private final static long DEFAULT_TICK_DURATION = 10;

    private static class DefaultInstance {
        static final Timer INSTANCE = new HashedWheelTimer(
                new NamedThreadFactory("default-timer" + DEFAULT_TICK_DURATION, true),
                DEFAULT_TICK_DURATION, TimeUnit.MILLISECONDS);
    }

    private TimerHolder() {
    }

    /**
     * Get a singleton instance of {@link Timer}. <br>
     * The tick duration is {@link #defaultTickDuration}.
     * 
     * @return Timer
     */
    public static Timer getTimer() {
        return DefaultInstance.INSTANCE;
    }
}