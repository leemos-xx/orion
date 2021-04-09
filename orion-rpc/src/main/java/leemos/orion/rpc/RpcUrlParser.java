package leemos.orion.rpc;

import java.util.Properties;

import io.netty.util.internal.StringUtil;
import leemos.orion.Configs;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.StringUtils;
import leemos.orion.remoting.Protocol;
import leemos.orion.remoting.Url;
import leemos.orion.remoting.UrlParser;

/**
 * 解析rpc地址
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public class RpcUrlParser implements UrlParser {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private Protocol protocl;

    public RpcUrlParser(Protocol protocol) {
        this.protocl = protocol;
    }

    /*
     * @see leemos.orion.connector.AddressParser#parse(java.lang.String)
     */
    @Override
    public Url parse(String url) {
        if (StringUtil.isNullOrEmpty(url)) {
            throw new IllegalArgumentException(
                    sm.getString("rpcUrlParser.parse.blank", url));
        }
        Url parsedUrl = Url.tryGet(url);
        if (parsedUrl != null) {
            return parsedUrl;
        }
        String ip = null;
        String port = null;
        Properties properties = null;

        int size = url.length();
        int pos = 0;
        for (int i = 0; i < size; i++) {
            if (COLON == url.charAt(i)) {
                ip = url.substring(pos, i);
                pos = i;
                if (i == size - 1) {
                    throw new IllegalArgumentException(
                            sm.getString("rpcUrlParser.parse.endWithColon", url));
                }
                break;
            }
            if (i == size - 1) {
                throw new IllegalArgumentException(
                        sm.getString("rpcUrlParser.parse.notColonFound", url));
            }
        }

        for (int i = pos; i < size; i++) {
            if (QUES == url.charAt(i)) {
                port = url.substring(pos + 1, i);
                pos = i;
                if (i == size - 1) {
                    throw new IllegalArgumentException(
                            sm.getString("rpcUrlParser.parse.endWithQues", url));
                }
                break;
            }
            if (i == size - 1) {
                port = url.substring(pos + 1, i + 1);
                pos = size;
            }
        }

        if (pos < size - 1) {
            properties = new Properties();
            while (pos < (size - 1)) {
                String key = null;
                String value = null;
                for (int i = pos; i < size; ++i) {
                    if (EQUAL == url.charAt(i)) {
                        key = url.substring(pos + 1, i);
                        pos = i;
                        if (i == size - 1) {
                            throw new IllegalArgumentException(sm.getString(
                                    "rpcUrlParser.parse.endWithEqual", url));
                        }
                        break;
                    }
                    if (i == size - 1) {
                        throw new IllegalArgumentException(
                                sm.getString("rpcUrlParser.parse.noEqualFound", url));
                    }
                }
                for (int i = pos; i < size; ++i) {
                    if (AND == url.charAt(i)) {
                        value = url.substring(pos + 1, i);
                        pos = i;
                        if (i == size - 1) {
                            throw new IllegalArgumentException(sm
                                    .getString("rpcUrlParser.parse.endWithAnd", url));
                        }
                        break;
                    }
                    if (i == size - 1) {
                        value = url.substring(pos + 1, i + 1);
                        pos = size;
                    }
                }
                properties.put(key, value);
            }
        }
        parsedUrl = new Url(url, ip, Integer.parseInt(port), properties);
        initUrlArgs(parsedUrl);

        Url.cached(parsedUrl);
        return parsedUrl;
    }

    /*
     * @see
     * leemos.orion.connector.AddressParser#parseUniqueKey(java.lang.String)
     */
    @Override
    public String parseUniqueKey(String url) {
        boolean illegal = false;
        if (StringUtils.isEmpty(url)) {
            illegal = true;
        }

        String uniqueKey = StringUtils.EMPTY;
        String addr = url.trim();
        String[] sectors = StringUtils.split(addr, QUES);
        if (!illegal && sectors.length == 2 && StringUtils.isNotBlank(sectors[0])) {
            uniqueKey = sectors[0].trim();
        } else {
            illegal = true;
        }

        if (illegal) {
            throw new IllegalArgumentException(
                    sm.getString("rpcUrlParser.parseUniqueKey.illegal", url));
        }
        return uniqueKey;
    }

    private void initUrlArgs(Url url) {
        String connectionTimeoutStr = url.getProperty(Constants.URL_CONNECTION_TIMEOUT);
        int connectionTimeout = Configs.connectTimeout();
        if (StringUtils.isNotBlank(connectionTimeoutStr)) {
            if (StringUtils.isNumeric(connectionTimeoutStr)) {
                connectionTimeout = Integer.parseInt(connectionTimeoutStr);
            } else {
                throw new IllegalArgumentException(
                        sm.getString("rpcUrlParser.initUrlArgs.notNumeric",
                                Constants.URL_CONNECTION_TIMEOUT, url.getOriginalUrl()));
            }
        }
        url.setConnectTimeout(connectionTimeout);

        String protocolStr = url.getProperty(Constants.URL_PROTOCOL_CODE);
        byte protocolCode = protocl.getProtocolCode();
        if (StringUtils.isNotBlank(protocolStr)) {
            if (StringUtils.isNumeric(protocolStr)) {
                protocolCode = Byte.parseByte(protocolStr);
            } else {
                throw new IllegalArgumentException(
                        sm.getString("rpcUrlParser.initUrlArgs.notNumeric",
                                Constants.URL_PROTOCOL_CODE, url.getOriginalUrl()));
            }
        }
        url.setProtocol(protocolCode);

        String versionStr = url.getProperty(Constants.URL_PROTOCOL_VERSION);
        byte version = protocl.getProtocolVersion();
        if (StringUtils.isNotBlank(versionStr)) {
            if (StringUtils.isNumeric(versionStr)) {
                version = Byte.parseByte(versionStr);
            } else {
                throw new IllegalArgumentException(
                        sm.getString("rpcUrlParser.initUrlArgs.notNumeric",
                                Constants.URL_PROTOCOL_VERSION, url.getOriginalUrl()));
            }
        }
        url.setVersion(version);

        String connectionNumStr = url.getProperty(Constants.URL_CONNECTION_MAX_NUM);
        int connectionNum = Configs.connectNumPerUrl();
        if (StringUtils.isNotBlank(connectionNumStr)) {
            if (StringUtils.isNumeric(connectionNumStr)) {
                connectionNum = Integer.parseInt(connectionNumStr);
            } else {
                throw new IllegalArgumentException(
                        sm.getString("rpcUrlParser.initUrlArgs.notNumeric",
                                Constants.URL_CONNECTION_MAX_NUM, url.getOriginalUrl()));
            }
        }
        url.setConnectionNum(connectionNum);

        String connectionWarmupStr = url.getProperty(Constants.URL_CONNECTION_WARMUP);
        boolean connectionWarmup = false;
        if (StringUtils.isNotBlank(connectionWarmupStr)) {
            connectionWarmup = Boolean.parseBoolean(connectionWarmupStr);
        }
        url.setConnectionWarmup(connectionWarmup);
    }
}
