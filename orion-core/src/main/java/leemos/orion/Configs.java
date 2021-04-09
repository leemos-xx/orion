package leemos.orion;

import static java.lang.System.getProperty;

/**
 * 全局配置，从{@code System.getProperty}中获取用户配置值，或者使用默认值
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class Configs {

    /**
     * 是否开启epoll模式
     */
    public static final String EPOLL_ENABLED = "orion.epoll.enabled";
    public static final String EPOLL_ENABLED_DEFAULT = "false";

    /**
     * 是否开启应用层心跳
     */
    public static final String HEARTBEAT_ENABLED = "orion.heartbeat.enabled";
    public static final String HEARTBEAT_ENABLED_DEFAULT = "true";
    public static final String HEARTBEAT_MAX_FAILED_TIMES = "orion.heartbeat.max.failed.times";
    public static final String HEARTBEAT_MAX_FAILED_TIMES_DEFAULT = "6";

    /**
     * server端空闲时间，当超过该空闲时间时，认为客户端已经掉线，直接关闭连接
     */
    public static final String SERVER_IDLE_TIME = "orion.server.idle.time";
    public static final String SERVER_IDLE_TIME_DEFAULT = "90000";

    /**
     * client端空闲时间，当超过该空闲时间时，触发心跳
     */
    public static final String CLIENT_IDLE_TIME = "orion.client.idle.time";
    public static final String CLIENT_IDLE_TIME_DEFAULT = "15000";

    /**
     * 连接动作超时时间
     */
    public static final String CONNECT_TIMEOUT = "orion.connect.timeout";
    public static final String CONNECT_TIMEOUT_DEFAULT = "1000";

    /**
     * 单个url允许的最大连接数
     */
    public static final String CONNECT_NUM_PER_URL = "orion.connect.num.per.url";
    public static final String CONNECT_NUM_PER_URL_DEFAULT = "1";

    /**
     * 消息超时时间
     */
    private static final String REQUEST_MESSAGE_TIMEOUT = "orion.request.message.timeout";
    private static final String REQUEST_MESSAGE_TIMEOUT_DEFAULT = "5000";
    private static final String RESPONSE_MESSAGE_TIMEOUT = "orion.response.message.timeout";
    private static final String RESPONSE_MESSAGE_TIMEOUT_DEFAULT = "5000";

    /**
     * CRC校验状态
     */
    private static final String CRC_ENABLED = "orion.protocol.crc.enabled";
    private static final String CRC_ENABLED_DEFAULT = "true";

    /**
     * 默认序列化类型
     */
    private static final String SERIALIZATION_TYPE = "orion.protocol.serialization";
    private static final String SERIALIZATION_TYPE_DEFAULT = "0";

    /**
     * 是否开启优先级模式
     */
    private static final String EVENT_PRIORITY = "orion.event.priority";
    private static final String EVENT_PRIORITY_DEFAULT = "true";

    /**
     * 事件队列大小
     */
    private static final String EVENT_QUEUE_SIZE = "orion.event.queue.size";
    private static final String EVENT_QUEUE_SIZE_DEFAULT = "2000";

    /**
     * 服务引擎线程池大小
     */
    private static final String ENGINE_CORE_THREAD_POOL_SIZE = "orion.engine.core.thread.pool.size";
    private static final String ENGINE_CORE_THREAD_POOL_SIZE_DEFAULT = "-1";
    private static final String ENGINE_MAX_THREAD_POOL_SIZE = "orion.engine.max.thread.pool.size";
    private static final String ENGINE_MAX_THREAD_POOL_SIZE_DEFAULT = "16";

    /**
     * 客户端io线程数量
     */
    private static final String CLIENT_IO_THREAD_SIZE = "orion.client.io.size";
    private static final String CLIENT_IO_THREAD_SIZE_DEFAULT = "-1";

    /**
     * 是否可开启epoll模式
     *
     * @return
     */
    public static boolean epollEnabled() {
        return getBoolean(EPOLL_ENABLED, EPOLL_ENABLED_DEFAULT);
    }

    /**
     * 是否开启心跳检测
     *
     * @return
     */
    public static boolean heartbeatEnabled() {
        return getBoolean(HEARTBEAT_ENABLED, HEARTBEAT_ENABLED_DEFAULT);
    }

    /**
     * 最大心跳失败次数
     *
     * @return
     */
    public static int heartbeatMaxFailedTimes() {
        return getInt(HEARTBEAT_MAX_FAILED_TIMES, HEARTBEAT_MAX_FAILED_TIMES_DEFAULT);
    }

    /**
     * 服务端空闲时间
     *
     * @return
     */
    public static long serverIdleTime() {
        return getLong(SERVER_IDLE_TIME, SERVER_IDLE_TIME_DEFAULT);
    }

    /**
     * 客户端空闲时间
     *
     * @return
     */
    public static long clientIdleTime() {
        return getLong(CLIENT_IDLE_TIME, CLIENT_IDLE_TIME_DEFAULT);
    }

    /**
     * 连接超时时间
     *
     * @return
     */
    public static int connectTimeout() {
        return getInt(CONNECT_TIMEOUT, CONNECT_TIMEOUT_DEFAULT);
    }

    /**
     * 单个url的最大连接数
     *
     * @return
     */
    public static int connectNumPerUrl() {
        return getInt(CONNECT_NUM_PER_URL, CONNECT_NUM_PER_URL_DEFAULT);
    }

    /**
     * 请求消息超时时间
     *
     * @return
     */
    public static long requestMessageTimeout() {
        return getLong(REQUEST_MESSAGE_TIMEOUT, REQUEST_MESSAGE_TIMEOUT_DEFAULT);
    }

    /**
     * 响应消息超时时间
     *
     * @return
     */
    public static long responseMessageTimeout() {
        return getLong(RESPONSE_MESSAGE_TIMEOUT, RESPONSE_MESSAGE_TIMEOUT_DEFAULT);
    }

    /**
     * crc校验是否开启
     *
     * @return
     */
    public static boolean crcEnabled() {
        return getBoolean(CRC_ENABLED, CRC_ENABLED_DEFAULT);
    }

    /**
     * 序列化类型
     *
     * @return
     */
    public static byte serailizationType() {
        return getByte(SERIALIZATION_TYPE, SERIALIZATION_TYPE_DEFAULT);
    }

    /**
     * 事件优先级
     *
     * @return
     */
    public static boolean eventPriority() {
        return getBoolean(EVENT_PRIORITY, EVENT_PRIORITY_DEFAULT);
    }

    /**
     * 事件队列大小
     *
     * @return
     */
    public static int eventQueueSize() {
        return getInt(EVENT_QUEUE_SIZE, EVENT_QUEUE_SIZE_DEFAULT);
    }

    /**
     * 服务引擎核心线程池大小
     *
     * @return
     */
    public static int engineCoreThreadPoolSize() {
        return getInt(ENGINE_CORE_THREAD_POOL_SIZE, ENGINE_CORE_THREAD_POOL_SIZE_DEFAULT);
    }

    /**
     * 服务引擎最大线程池大小
     *
     * @return
     */
    public static int engineMaxThreadPoolSize() {
        return getInt(ENGINE_MAX_THREAD_POOL_SIZE, ENGINE_MAX_THREAD_POOL_SIZE_DEFAULT);
    }

    /**
     * 客户端io线程数
     *
     * @return
     */
    public static int clientIoThreads() {
        return getInt(CLIENT_IO_THREAD_SIZE, CLIENT_IO_THREAD_SIZE_DEFAULT);
    }

    /**
     * 获取int类型的数据，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private static int getInt(String key, String defaultValue) {
        return Integer.parseInt(getProperty(key, defaultValue));
    }

    /**
     * 获取long类型的数据，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private static long getLong(String key, String defaultValue) {
        return Long.parseLong(getProperty(key, defaultValue));
    }

    /**
     * 获取boolean类型的数据，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private static boolean getBoolean(String key, String defaultValue) {
        return Boolean.parseBoolean(getProperty(key, defaultValue));
    }

    /**
     * 获取byte类型的数据，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private static byte getByte(String key, String defaultValue) {
        return Byte.parseByte(getProperty(key, defaultValue));
    }

    public static boolean serverSSLEnabled() {
        // TODO Auto-generated method stub
        return false;
    }
}
