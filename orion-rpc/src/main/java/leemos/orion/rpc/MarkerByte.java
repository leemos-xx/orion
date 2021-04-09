package leemos.orion.rpc;

/**
 * Rpc协议中预设的单个字节的标识位
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class MarkerByte {

    private byte marker;

    public MarkerByte(byte markerByte) {
        this.marker = markerByte;
    }

    /**
     * 请求或响应标识位
     *
     * @return
     */
    public boolean isReqOrRsp() {
        return (marker & 0x80) >> 7 == 1;
    }

    /**
     * one-way或two-way标识位
     *
     * @return
     */
    public boolean isOneway() {
        return (marker & 0x40) >> 6 == 1;
    }

    /**
     * 心跳标识位
     *
     * @return
     */
    public boolean isHeartbeat() {
        return (marker & 0x20) >> 5 == 1;
    }

    /**
     * 是否需要CRC校验标识位
     *
     * @return
     */
    public boolean isCrcRequired() {
        return (marker & 0x10) >> 4 == 1;
    }

    /**
     * 序列化类型标识位
     *
     * @return
     */
    public byte getSerializationType() {
        return (byte) (marker & 0x0f);
    }
    
    /**
     * byte
     *
     * @return
     */
    public byte getActualByte() {
        return marker;
    }

    /**
     * 根据传入标识位，生成byte
     *
     * @param isReqOrRsp
     * @param oneway
     * @param isHeartbeat
     * @param isCrcRequired
     * @param serializationType
     * @return
     */
    public static MarkerByte from(boolean isReqOrRsp, boolean isOneway, boolean isHeartbeat,
            boolean isCrcRequired, byte serializationType) {
        return new MarkerByte((byte) 
                ((isReqOrRsp ? 0x01 << 7 : 0x00) | 
                (isOneway ? 0x01 << 6 : 0x00) | 
                (isHeartbeat ? 0x01 << 5 : 0x00) | 
                (isCrcRequired ? 0x01 << 4 : 0x00) | 
                (serializationType & 0x0f)));
    }

}
