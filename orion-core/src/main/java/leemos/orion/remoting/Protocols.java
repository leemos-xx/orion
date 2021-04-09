package leemos.orion.remoting;

import java.util.concurrent.ConcurrentHashMap;

import leemos.orion.Constants;
import leemos.orion.commons.StringManager;

/**
 * 作为协议的管理类，所有协议都应该在此处注册，暂不支持统一协议的不同版本注册。
 *
 *
 * @author lihao
 * @date 2020年8月23日 下午1:49:22
 * @version 1.0
 *
 */
public class Protocols {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);
    private static final ConcurrentHashMap<Byte, Protocol> protocols = new ConcurrentHashMap<Byte, Protocol>();

    /**
     * 根据协议码返回协议对象
     *
     * @param protocolCode
     * @return
     */
    public static Protocol getProtocol(byte protocolCode) {
        return protocols.get(protocolCode);
    }

    /**
     * 协议注册
     *
     * @param protocolCode
     * @param protocol
     */
    public static void registerProtocol(byte protocolCode, Protocol protocol) {
        if (protocols.putIfAbsent(protocolCode, protocol) != null) {
            throw new RuntimeException(
                    sm.getString("protocols.registerProtocol.duplicate",
                            String.valueOf(protocolCode)));
        }
    }

    /**
     * 协议注销
     *
     * @param protocolCode
     */
    public static void unregisterProtocol(byte protocolCode) {
        protocols.remove(protocolCode);
    }
}
