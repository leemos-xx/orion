package leemos.orion.config;

import java.util.List;

/**
 * Server的可配置项
 * 
 * @author lihao
 * @date 2020-12-05
 * @version 1.0
 */
public class ServerConfig {
    private String projectName;
    private String projectVersion;
    private String serverName;
    private int adminPort;
    private List<ServiceConfig> services;

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public void setServices(List<ServiceConfig> services) {
        this.services = services;
    }

    public List<ServiceConfig> getServices() {
        return services;
    }
}
