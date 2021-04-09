package leemos.orion.codec.rpc;

import leemos.orion.remoting.Protocol;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.heartbeat.HeartbeatTrigger;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.codec.MessageDecoder;
import leemos.orion.remoting.message.codec.MessageEncoder;
import leemos.orion.rpc.RpcHeartbeatTrigger;
import leemos.orion.rpc.RpcMessageFactory;

/**
 * 自定义的rpc协议。
 * 
 * <ul>
 *   <li>通过magic或version字段可以对协议进行升级扩展</li>
 *   <li>index为3的byte位为标志位</li>
 *   <ul>
 *     <li>其中高四位的bit为boolean类型，分别代表是否为请求消息、是否为单向消息、是否为心跳消息以及是否开启crc校验</li>
 *     <li>低四位为序列化类型，指代content bytes部分采用的序列化方式</li>
 *   </ul>
 *   <li>status特指响应状态，当服务端出现非业务异常时，通过status传递。请求消息中无需携带status，目前以{@link Status}.PADDING填充</li>
 *   <li>messageId为必须的字段，请求和响应消息通过该字段进行匹配</li>
 *   <li>dataLength指代content bytes的长度，当content为<code>null</code>时，dataLength为0</li>
 *   <li>尾部的CRC32为可选字段，由marker中的crc标识位决定</li>
 * </ul>
 *
 * 0      1      2      3      4      5      6      7      8      9     10     11     12     13     14     15     16 byte
 * +------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+
 * | magic|  ver |marker|status|                       messageId                       |        dataLength         |
 * +------+------+------+------+-------------------------------------------------------+---------------------------+
 * |                                                 content bytes                                                 |
 * +                                                                                                               |
 * |                                                     ......                        |           CRC32           |
 * +---------------------------------------------------------------------------------------------------------------+

 * about marker byte：
 * 0      1      2      3      4      5      6      7      8 bit
 * +------+------+------+------+------+------+------+------+
 * | rq/rp|oneway|  hb  |  crc |       serialization       |
 * +------+------+------+------+---------------------------+
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class OrionProtocol implements Protocol {

    /**
     * 自定义rpc的协议码
     */
    public static final byte PROTOCOL_CODE = 0x22;

    /**
     * 自定义rpc协议的协议版本
     */
    public static final byte PROTOCOL_VERSION = 0x01;

    /**
     * 自定义rpc协议的最小报文头长度(byte length)，用于校验，当报文低于该长度，则直接丢弃
     */
    public static final int FIXED_HEADER_LENGTH = 16;

    /*
     * @see leemos.orion.remoting.Protocol#getProtocolCode()
     */
    @Override
    public byte getProtocolCode() {
        return PROTOCOL_CODE;
    }

    /*
     * @see leemos.orion.remoting.Protocol#getProtocolVersion()
     */
    @Override
    public byte getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    /*
     * @see leemos.orion.remoting.Protocol#getEncoder()
     */
    @Override
    public MessageEncoder getEncoder() {
        return new OrionMessageEncoder();
    }

    /*
     * @see leemos.orion.remoting.Protocol#getDecoder()
     */
    @Override
    public MessageDecoder getDecoder() {
        return new OrionMessageDecoder();
    }

    /*
     * @see leemos.orion.remoting.Protocol#getMessageFactory()
     */
    @Override
    public MessageFactory getMessageFactory() {
        return new RpcMessageFactory(this);
    }

    /*
     * @see leemos.orion.remoting.Protocol#getHeartbeatTrigger()
     */
    @Override
    public HeartbeatTrigger getHeartbeatTrigger() {
        return new RpcHeartbeatTrigger(this);
    }

}
