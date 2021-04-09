package leemos.orion.config;

import java.util.List;

/**
 * Service的可配置项
 * 
 * @author lihao
 * @date 2020-12-05
 * @version 1.0
 */
public class ServiceConfig {
    private String name;
    private EventBusConfig eventBus;
    private EngineConfig engine;
    private List<ConnectorConfig> connectors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventBusConfig getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBusConfig eventBus) {
        this.eventBus = eventBus;
    }

    public EngineConfig getEngine() {
        return engine;
    }

    public void setEngine(EngineConfig engine) {
        this.engine = engine;
    }

    public List<ConnectorConfig> getConnectors() {
        return connectors;
    }

    public void setConnectors(List<ConnectorConfig> connectors) {
        this.connectors = connectors;
    }

}
