package leemos.orion.core;

import java.net.InetSocketAddress;

import leemos.orion.Connector;
import leemos.orion.Constants;
import leemos.orion.EventBus;
import leemos.orion.Service;
import leemos.orion.commons.StringManager;
import leemos.orion.lifecycle.LifecycleSupport;
import leemos.orion.remoting.Protocol;

/**
 * Connector的骨架实现
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public abstract class ConnectorBase extends LifecycleSupport implements Connector {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    /**
     * 监听的ip
     */
    private String ip;

    /**
     * 监听的端口
     */
    private int port;

    /**
     * 关联的{@link Service}
     */
    private Service service;

    /**
     * 关联的{@link EventBus}
     */
    private EventBus eventBus;

    /**
     * 指定Connector的协议
     */
    private Protocol protocol;

    public ConnectorBase(int port) {
        this(new InetSocketAddress(port).getAddress().getHostAddress(), port);
    }

    public ConnectorBase(String ip, int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException(sm.getString(
                    "connectorBase.constructor.illegalPort", String.valueOf(port)));
        }
        this.ip = ip;
        this.port = port;
    }

    /*
     * @see leemos.orion.Connector#getIp()
     */
    @Override
    public String getIp() {
        return ip;
    }

    /*
     * @see leemos.orion.Connector#getPort()
     */
    @Override
    public int getPort() {
        return port;
    }

    /*
     * @see leemos.orion.Connector#getService()
     */
    @Override
    public Service getService() {
        return service;
    }

    /*
     * @see leemos.orion.Connector#setService(leemos.orion.Service)
     */
    @Override
    public void setService(Service service) {
        this.service = service;
    }

    /*
     * @see leemos.orion.Connector#getEventBus()
     */
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /*
     * @see leemos.orion.Connector#setEventBus(leemos.orion.EventBus)
     */
    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /*
     * @see leemos.orion.Connector#getProtocol()
     */
    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    /*
     * @see leemos.orion.Connector#setProtocol(leemos.orion.Protocol)
     */
    @Override
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

}
