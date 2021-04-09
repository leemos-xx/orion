package leemos.orion.valves;

import leemos.orion.EventBus;
import leemos.orion.Valve;

public abstract class ValveBase implements Valve {

    private EventBus eventBus;

    public ValveBase(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected EventBus getEventBus() {
        return this.eventBus;
    }
}
