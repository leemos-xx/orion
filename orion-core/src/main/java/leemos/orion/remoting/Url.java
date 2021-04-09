package leemos.orion.remoting;

import java.lang.ref.SoftReference;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Url的定义，包含ip/port/protocol等。
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public class Url {

    /**
     * 作为已解析的{@link Url}的缓存
     */
    private static ConcurrentHashMap<String, SoftReference<Url>> parsedUrls = new ConcurrentHashMap<String, SoftReference<Url>>();

    /**
     * 将已解析的url放入缓存
     *
     * @param url
     */
    public static void cached(Url url) {
        parsedUrls.put(url.getOriginalUrl(), new SoftReference<Url>(url));
    }

    /**
     * 尝试从缓存中获取并返回url
     *
     * @param url
     * @return
     */
    public static Url tryGet(String url) {
        SoftReference<Url> reference = parsedUrls.get(url);
        return reference == null ? null : reference.get();
    }

    /**
     * 原始url字符串
     */
    private String originalUrl;

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * unique key
     */
    private String uniqueKey;

    /**
     * 超时时间
     */
    private int connectTimeout;

    /**
     * 协议标识
     */
    private byte protocol;

    /**
     * 协议版本标识
     */
    private byte version;

    /**
     * 连接数
     */
    private int connectionNum;

    /**
     * 连接是否需要预热
     */
    private boolean connectionWarmup;

    /**
     * 参数
     */
    private Properties properties;

    /**
     * 根据参数构造{@link Url}
     * 
     * @param originalUrl
     */
    public Url(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    /**
     * 根据参数构造{@link Url}
     * 
     * @param ip
     * @param port
     */
    public Url(String ip, int port) {
        this(ip + UrlParser.COLON + port);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = this.originalUrl;
    }

    /**
     * 根据参数构造{@link Url}
     * 
     * @param originalUrl
     * @param ip
     * @param port
     */
    public Url(String originalUrl, String ip, int port) {
        this(originalUrl);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = ip + UrlParser.COLON + port;
    }

    /**
     * 根据参数构造{@link Url}
     * 
     * @param originUrl
     * @param ip
     * @param port
     * @param properties
     */
    public Url(String originUrl, String ip, int port, Properties properties) {
        this(originUrl, ip, port);
        this.properties = properties;
    }

    /**
     * 根据参数构造{@link Url}
     * 
     * @param originUrl
     * @param ip
     * @param port
     * @param uniqueKey
     * @param properties
     */
    public Url(String originUrl, String ip, int port, String uniqueKey,
            Properties properties) {
        this(originUrl, ip, port);
        this.uniqueKey = uniqueKey;
        this.properties = properties;
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        if (properties == null) {
            return null;
        }
        return properties.getProperty(key);
    }

    /**
     * 获取原始的url字符串
     *
     * @return
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * 获取ip
     *
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * 获取端口
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取unique key
     *
     * @return
     */
    public String getUniqueKey() {
        return uniqueKey;
    }

    /**
     * 获取超时时间
     *
     * @return
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置超时时间
     *
     * @param connectTimeout
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 获取连接数限制
     *
     * @return
     */
    public int getConnectionNum() {
        return connectionNum;
    }

    /**
     * 设置连接数限制
     *
     * @param connectionNum
     */
    public void setConnectionNum(int connectionNum) {
        this.connectionNum = connectionNum;
    }

    /**
     * 获取协议类型
     *
     * @return
     */
    public byte getProtocol() {
        return protocol;
    }

    /**
     * 设置协议类型
     *
     * @param protocol
     */
    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    /**
     * 获取协议版本
     *
     * @return
     */
    public byte getVersion() {
        return version;
    }

    /**
     * 设置协议版本
     *
     * @param version
     */
    public void setVersion(byte version) {
        this.version = version;
    }

    /**
     * 连接是否需要预热
     *
     * @return
     */
    public boolean isConnectionWarmup() {
        return connectionWarmup;
    }

    /**
     * 设置连接是否需要预热
     *
     * @param connectionWarmup
     */
    public void setConnectionWarmup(boolean connectionWarmup) {
        this.connectionWarmup = connectionWarmup;
    }

    /**
     * 获取参数的集合
     *
     * @return
     */
    public Properties getProperties() {
        return properties;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Url url = (Url) obj;
        if (this.getOriginalUrl().equals(url.getOriginalUrl())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getOriginalUrl() == null) ? 0
                : this.getOriginalUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Original url [").append(this.getOriginalUrl())
                .append("], Unique key [").append(this.uniqueKey).append("].");
        return sb.toString();
    }
}
