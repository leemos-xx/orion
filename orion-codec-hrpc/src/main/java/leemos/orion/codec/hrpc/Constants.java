package leemos.orion.codec.hrpc;

/**
 * 静态常量
 *
 *
 * @author lihao
 * @date 2020年9月8日
 * @version 1.0
 */
public class Constants {

    public static final String PACKAGE = "leemos.orion.codec.hrpc";

    /**
     * http协议中承载rpc相关信息的请求头字段
     */
    public static final String ONEWAY_HEADER_NAME = "Hrpc_Oneway";
    public static final String HEARTBEAT_HEADER_NAME = "Hrpc_Heartbeat";
    public static final String SERIALIZATION_TYPE_HEADER_NAME = "Hrpc_Serialization_Type";
    public static final String STATUS_HEADER_NAME = "Hrpc_Status";
    public static final String MESSAGE_ID_HEADER_NAME = "Hrpc_Id";
    public static final String BODY_LENGTH_HEADER_NAME = "Hrpc_Body_Length";
    public static final String SERVICE_HEADER_NAME = "Hrpc_Service";

}
