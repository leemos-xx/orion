package leemos.orion.remoting;

import leemos.orion.remoting.heartbeat.HeartbeatTrigger;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.codec.MessageDecoder;
import leemos.orion.remoting.message.codec.MessageEncoder;

/**
 * Protocol指代一种协议，如orion://,http://等。包含该协议处理所需的编解码器、消息工厂类等。
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public interface Protocol {

    /**
     * 获取协议码
     *
     * @return
     */
    public byte getProtocolCode();

    /**
     * 获取协议版本
     *
     * @return
     */
    public byte getProtocolVersion();

    /**
     * 获取协议对应的编码器
     *
     * @return
     */
    public MessageEncoder getEncoder();

    /**
     * 获取协议对应的解码器
     *
     * @return
     */
    public MessageDecoder getDecoder();

    /**
     * 返回消息工厂，用于生产消息
     *
     * @return
     */
    public MessageFactory getMessageFactory();

    /**
     * 心跳触发器
     *
     * @return
     */
    public HeartbeatTrigger getHeartbeatTrigger();
}