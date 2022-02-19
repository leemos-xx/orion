package leemos.orion;

import java.lang.reflect.Constructor;

import leemos.orion.config.ConnectorConfig;
import leemos.orion.lifecycle.LifecycleException;

/**
 * ConnectorFactory
 * 
 * @author lihao
 * @date 2022年2月19日
 * @version 1.0
 */
public class ConnectorFactory {

    public static Connector createConnector(ConnectorConfig connectorConfig)
            throws LifecycleException {
        try {
            Class<?> connectorCls = Class.forName(connectorConfig.getProtocol());
            Constructor<?> constructor = connectorCls
                    .getConstructor(ConnectorConfig.class);

            return (Connector) constructor.newInstance(connectorConfig);
        } catch (Exception e) {
            throw new LifecycleException(e);
        }
    }

}
