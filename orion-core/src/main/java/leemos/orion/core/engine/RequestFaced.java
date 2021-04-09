package leemos.orion.core.engine;

import java.util.HashMap;
import java.util.Map;

import leemos.orion.Valve;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcRequest;

/**
 * 业务引擎中传递的Request对象
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class RequestFaced {

    private RpcRequest rpcRequest;
    private RequestBody requestBody;
    private Map<String, Object> attributes = new HashMap<String, Object>();

    public RequestFaced(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
        this.requestBody = rpcRequest.getRequestBody();
    }

    /**
     * 请求的唯一标识
     *
     * @return
     */
    public long getId() {
        return rpcRequest.getId();
    }

    /**
     * 获取需要执行的服务名
     *
     * @return
     */
    public String getServiceName() {
        return requestBody.getServiceName();
    }

    /**
     * 获取业务参数
     *
     * @return
     */
    public Object[] getParameters() {
        return requestBody.getParameters();
    }

    /**
     * 获取属性值，用于在{@link Valve}中进行参数传递
     *
     * @param name
     * @return
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * 设置属性值，用于在{@link Valve}中进行参数传递
     *
     * @param name
     * @param value
     */
    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * 获取所有属性
     *
     * @return
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 协议码
     *
     * @return
     */
    public byte getProtocolCode() {
        return rpcRequest.getProtocolCode();
    }

    /**
     * 协议版本
     *
     * @return
     */
    public byte getProtocolVersion() {
        return rpcRequest.getProtocolVersion();
    }

    /**
     * 是否为请求
     *
     * @return
     */
    public boolean isReqOrRsp() {
        return rpcRequest.isReqOrRsp();
    }

    /**
     * 是否为单向请求
     *
     * @return
     */
    public boolean isOneway() {
        return rpcRequest.isOneway();
    }

    /**
     * 是否需要进行crc校验
     *
     * @return
     */
    public boolean isCrcRequired() {
        return rpcRequest.isCrcRequired();
    }

    /**
     * 序列化类型
     *
     * @return
     */
    public byte getSerializationType() {
        return rpcRequest.getSerializationType();
    }
}
