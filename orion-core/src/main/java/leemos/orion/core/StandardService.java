package leemos.orion.core;

import cn.hutool.core.util.StrUtil;
import leemos.orion.Connector;
import leemos.orion.Engine;
import leemos.orion.EventBus;
import leemos.orion.Server;
import leemos.orion.Service;
import leemos.orion.config.ConnectorConfig;
import leemos.orion.config.ServiceConfig;
import leemos.orion.core.bus.EventBusFactory;
import leemos.orion.core.engine.StandardEngine;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleSupport;

/**
 * Service的标准实现
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public class StandardService extends LifecycleSupport implements Service {

    /**
     * 所属的{@link Server}
     */
    private Server server;

    /**
     * 消息总线
     */
    private EventBus eventBus;

    /**
     * Engine的集合
     */
    private Engine engine;

    /**
     * Connector的集合
     */
    private Connector[] connectors = new Connector[0];

    /**
     * Service的配置项
     */
    private ServiceConfig serviceConfig;

    public StandardService(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    /*
     * @see leemos.orion.Service#getServer()
     */
    @Override
    public Server getServer() {
        return server;
    }

    /*
     * @see leemos.orion.Service#setServer(leemos.orion.Server)
     */
    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    /*
     * @see leemos.orion.Service#getEventBus()
     */
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /*
     * @see leemos.orion.Service#setEventBus(leemos.orion.EventBus)
     */
    @Override
    public synchronized void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        synchronizeStatus(eventBus);
    }

    /*
     * @see leemos.orion.Service#getEngine()
     */
    @Override
    public Engine getEngine() {
        return engine;
    }

    /*
     * @see leemos.orion.Service#setEngine(leemos.orion.Engine)
     */
    @Override
    public synchronized void setEngine(Engine engine) {
        this.engine = engine;
        synchronizeStatus(engine);
    }

    /*
     * @see leemos.orion.Service#addConnector(leemos.orion.Connector)
     */
    @Override
    public void addConnector(Connector connector) {
        connector.setService(this);

        synchronized (connectors) {
            Connector[] results = new Connector[connectors.length + 1];
            System.arraycopy(connectors, 0, results, 0, connectors.length);
            results[connectors.length] = connector;
            connectors = results;

            synchronizeStatus(connector);
        }
    }

    /*
     * @see leemos.orion.Service#getConnectors()
     */
    @Override
    public Connector[] getConnectors() {
        return connectors;
    }

    /*
     * @see leemos.orion.Service#removeConnector(leemos.orion.Connector)
     */
    @Override
    public void removeConnector(Connector connector) {
        synchronized (connectors) {
            int n = -1;
            for (int i = 0; i < connectors.length; i++) {
                if (connector == connectors[i]) {
                    n = i;
                    break;
                }
            }

            if (n < 0) {
                // Not found
                return;
            }

            try {
                connector.stop();
            } catch (LifecycleException e) {
                e.printStackTrace(System.err);
            }

            Connector[] results = new Connector[connectors.length - 1];
            for (int i = 0, j = 0; i < connectors.length; i++) {
                if (i != n) {
                    results[j++] = connectors[i];
                }
            }
            connectors = results;

        }
    }

    /*
     * @see leemos.orion.LifecycleSupport#initializeInternal()
     */
    @Override
    protected void initializeInternal() throws LifecycleException {
        setName(StrUtil.blankToDefault(serviceConfig.getName(), "default-service"));

        setEventBus(EventBusFactory.createEventBus(serviceConfig.getEventBus()));

        StandardEngine engine = new StandardEngine(serviceConfig.getEngine());
        engine.setEventBus(eventBus);
        setEngine(engine);

        for (ConnectorConfig connectorConfig: serviceConfig.getConnectors()) {
            addConnector(ConnectorFactory.createConnector(connectorConfig));
        }
        for (int i = 0; i < connectors.length; i++) {
            connectors[i].initialize();
        }
    }

    /*
     * @see leemos.orion.LifecycleSupport#startInternal()
     */
    @Override
    protected void startInternal() throws LifecycleException {
        eventBus.start();
        engine.start();

        for (int i = 0; i < connectors.length; i++) {
            connectors[i].start();
        }
    }

    /*
     * @see leemos.orion.LifecycleSupport#stopInternal()
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        for (int i = 0; i < connectors.length; i++) {
            connectors[i].stop();
        }

        engine.stop();
        eventBus.stop();
    }

}
