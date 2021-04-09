package leemos.orion.core;

import java.io.FileNotFoundException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import leemos.orion.Server;
import leemos.orion.Service;
import leemos.orion.config.ServerConfig;
import leemos.orion.config.ServiceConfig;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleStatus;
import leemos.orion.lifecycle.LifecycleSupport;

/**
 * {@link Server}的默认实现，其中adminPort用于提供对Server的管理和监控接口，暂未实现。
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public class StandardServer extends LifecycleSupport implements Server {

    /**
     * 配置文件，默认为classes目录下的orion.yml
     */
    private static final String ORION_CONFIG_PATH = "orion.yml";

    /**
     * 监听的端口，用于接收shutdown命令
     */
    private int port;

    /**
     * 关联的{@link Service}集合
     */
    private Service[] services = new Service[0];

    /*
     * @see leemos.orion.Server#getPort()
     */
    @Override
    public int getAdminPort() {
        return port;
    }

    /*
     * @see leemos.orion.Server#setPort(int)
     */
    @Override
    public void setAdminPort(int port) throws LifecycleException {
        if (status == LifecycleStatus.NEW || status == LifecycleStatus.INITIALIZING
                || status == LifecycleStatus.INITIALIZED) {
            this.port = port;
            return;
        }
        throw new LifecycleException();
    }

    /*
     * @see leemos.orion.Server#addService(leemos.orion.Service)
     */
    @Override
    public void addService(Service service) {
        // 建立与当前Server的关联
        service.setServer(this);

        synchronized (services) {
            Service[] results = new Service[services.length + 1];
            System.arraycopy(services, 0, results, 0, services.length);
            results[services.length] = service;
            services = results;

            synchronizeStatus(service);
        }

    }

    /*
     * @see leemos.orion.Server#getService(java.lang.String)
     */
    @Override
    public Service getService(String name) {
        if (name == null) {
            return null;
        }
        synchronized (services) {
            for (int i = 0; i < services.length; i++) {
                if (name.equals(services[i].getName())) {
                    return services[i];
                }
            }
        }
        return null;
    }

    /*
     * @see leemos.orion.Server#getServices()
     */
    @Override
    public Service[] getServices() {
        return services;
    }

    /*
     * @see leemos.orion.Server#removeService(leemos.orion.Service)
     */
    @Override
    public void removeService(Service service) {
        synchronized (services) {
            int n = -1;
            for (int i = 0; i < services.length; i++) {
                if (service == services[i]) {
                    n = i;
                    break;
                }
            }

            if (n < 0) {
                // Not found
                return;
            }

            try {
                service.stop();
            } catch (LifecycleException e) {
                e.printStackTrace(System.err);
            }

            Service[] results = new Service[services.length - 1];
            for (int i = 0, j = 0; i < services.length; i++) {
                if (i != n) {
                    results[j++] = services[i];
                }
            }
            services = results;
        }
    }

    /**
     * 同步初始化所有{@link Service}
     */
    @Override
    protected void initializeInternal() throws LifecycleException {
        try {
            ServerConfig serverConfig = readServerConfig();

            setName(StrUtil.blankToDefault(serverConfig.getServerName(),
                    "default-server"));
            setAdminPort(serverConfig.getAdminPort());

            for (ServiceConfig serviceConfig : serverConfig.getServices()) {
                addService(new StandardService(serviceConfig));
            }
        } catch (FileNotFoundException | YamlException e) {
            throw new LifecycleException(e);
        }
    }

    /*
     * 从orion.yml中读取并解析配置
     */
    private ServerConfig readServerConfig() throws FileNotFoundException, YamlException {
        YamlReader serverConfigReader = new YamlReader(
                new FileReader(ORION_CONFIG_PATH).readString());
        return serverConfigReader.read(ServerConfig.class);
    }

    /**
     * 同步启动所有{@link Service}
     */
    @Override
    protected void startInternal() throws LifecycleException {
        for (int i = 0; i < services.length; i++) {
            services[i].start();
        }
    }

    /**
     * 同步停止所有{@link Service}
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        for (int i = 0; i < services.length; i++) {
            services[i].stop();
        }
        services = new Service[0];
    }

}
