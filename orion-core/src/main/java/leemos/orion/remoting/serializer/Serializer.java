package leemos.orion.remoting.serializer;

import leemos.orion.codec.CodecException;

/**
 * 序列化接口，声明序列化与反序列化方法
 *
 *
 * @author lihao
 * @date 2020年8月15日 上午8:39:58
 * @version 1.0
 *
 */
public interface Serializer {

    /**
     * 将对象序列化为字节数组
     *
     * @param obj
     * @return
     * @throws CodecException
     */
    byte[] serialize(final Object obj) throws CodecException;

    /**
     * 将字节数组反序列化为对象
     *
     * @param <T>
     * @param data
     * @return
     * @throws CodecException
     */
    <T> T deserialize(final byte[] data) throws CodecException;
}
