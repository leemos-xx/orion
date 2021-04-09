package leemos.orion.remoting.message;

import java.io.Serializable;

/**
 * Rpc请求体，该请求体包含服务端需要反射调用的服务名及参数。
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public class RequestBody implements Serializable {

    private static final long serialVersionUID = 4615738770400548530L;

    /**
     * 服务名，格式为sceneName.apiName
     */
    private String serviceName;

    /**
     * 业务参数
     */
    private Object[] parameters;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
