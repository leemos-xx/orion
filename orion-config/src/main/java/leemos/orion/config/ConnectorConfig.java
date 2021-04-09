package leemos.orion.config;

/**
 * Connector可配置項
 * 
 * @author lihao
 * @date 2020-12-05
 * @version 1.0
 */
public class ConnectorConfig {

    private String name;
    private String protocol;
    private String ip;
    private int port;
    private int ioThreads;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIoThreads() {
        return ioThreads;
    }

    public void setIoThreads(int ioThreads) {
        this.ioThreads = ioThreads;
    }

}
