package leemos.orion.remoting.message;

import java.io.Serializable;

import leemos.orion.remoting.Status;

/**
 * rpc消息类，记录rpc消息的原始字段。无论采用何种协议，都应该将消息解析为该消息类的实例。
 *
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public abstract class RpcMessage implements Serializable {

    private static final long serialVersionUID = -5570809280690755242L;

    /**
     * 协议码
     */
    private byte protocolCode;

    /**
     * 协议版本
     */
    private byte protocolVersion;

    /**
     * 是请求消息或响应消息，<code>true</code>代表为请求消息
     */
    private boolean isReqOrRsp;

    /**
     * 服务端是否需要响应，<code>true</code>代表不需要响应
     */
    private boolean isOneway;

    /**
     * 是否为心跳消息，<code>true</code>代表是心跳消息
     */
    private boolean isHeartbeat;

    /**
     * 是否做crc校验，<code>true</code>代表需要校验
     */
    private boolean isCrcRequired;

    /**
     * 记录序列化的类型
     */
    private byte serializationType;

    /**
     * 消息状态
     */
    private Status status;

    /**
     * 消息的唯一标识
     */
    private long id;

    /**
     * 消息体长度
     */
    private int dataLength;

    /**
     * 消息体的byte数组
     */
    private byte[] data;

    /**
     * crc校验值
     */
    private int crc = Integer.MAX_VALUE;

    public RpcMessage(byte protocolCode, byte protocolVersion) {
        super();
        this.protocolCode = protocolCode;
        this.protocolVersion = protocolVersion;
    }

    public byte getProtocolCode() {
        return protocolCode;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setReqOrRsp(boolean isReqOrRsp) {
        this.isReqOrRsp = isReqOrRsp;
    }

    public boolean isReqOrRsp() {
        return isReqOrRsp;
    }

    public void setOneway(boolean isOneway) {
        this.isOneway = isOneway;
    }

    public boolean isOneway() {
        return isOneway;
    }

    public void setHeartbeat(boolean isHeartbeat) {
        this.isHeartbeat = isHeartbeat;
    }

    public boolean isHeartbeat() {
        return isHeartbeat;
    }

    public void setCrcRequired(boolean crcRequired) {
        this.isCrcRequired = crcRequired;
    }

    public boolean isCrcRequired() {
        return isCrcRequired;
    }

    public void setSerializationType(byte serializationType) {
        this.serializationType = serializationType;
    }

    public byte getSerializationType() {
        return serializationType;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public int getCrc() {
        return crc;
    }

}
