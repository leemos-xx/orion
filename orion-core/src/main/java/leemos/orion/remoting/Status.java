package leemos.orion.remoting;

/**
 * 协议中的状态字段
 *
 * @author lihao
 * @date 2020年8月14日
 * @version 1.0
 */
public enum Status {

    SUCCESS, // 业务方法执行成功，非实际业务成功，仅代表业务方法正常执行
    ERROR, // 失败，此处的失败特指业务方法执行时异常（未捕获处理）
    SERVER_EXCEPTION, // 服务端异常
    SERVER_BUSY, // 服务端繁忙
    CLIENT_EXCEPTION, // 客户端发送异常
    CONNECTION_CLOSED, // 连接关闭
    TIMEOUT, // 等待响应超时
    UNKNOWN, // 未知异常
    PADDING; // 不包含状态时的填充字段

    public byte getValue() {
        switch (this) {
        case SUCCESS:
            return 1;
        case ERROR:
            return 2;
        case SERVER_EXCEPTION:
            return 3;
        case SERVER_BUSY:
            return 4;
        case CLIENT_EXCEPTION:
            return 5;
        case CONNECTION_CLOSED:
            return 6;
        case TIMEOUT:
            return 7;
        case UNKNOWN:
            return Byte.MAX_VALUE;
        case PADDING:
            return -1;
        default:
            return UNKNOWN.getValue();
        }
    }

    public static Status parse(byte value) {
        switch (value) {
        case 1:
            return SUCCESS;
        case 2:
            return ERROR;
        case 3:
            return SERVER_EXCEPTION;
        case 4:
            return SERVER_BUSY;
        case 5:
            return CLIENT_EXCEPTION;
        case 6:
            return CONNECTION_CLOSED;
        case 7:
            return TIMEOUT;
        case Byte.MAX_VALUE:
            return UNKNOWN;
        case -1:
            return PADDING;
        }
        throw new IllegalArgumentException();
    }
}
