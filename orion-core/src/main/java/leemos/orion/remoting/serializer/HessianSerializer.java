package leemos.orion.remoting.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import leemos.orion.codec.CodecException;

/**
 * 基于Hessian的序列化机制
 *
 *
 * @author lihao
 * @date 2020年8月15日 上午8:44:51
 * @version 1.0
 *
 */
public class HessianSerializer implements Serializer {

    private SerializerFactory serializerFactory = new SerializerFactory();

    /*
     * @see
     * leemos.orion.rpc.serialization.Serializer#serialize(java.lang.Object)
     */
    @Override
    public byte[] serialize(Object obj) throws CodecException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(obj);
            output.close();
        } catch (IOException e) {
            throw new CodecException(
                    "IOException occurred when Hessian serializer encode!", e);
        }

        return byteArray.toByteArray();
    }

    /*
     * @see leemos.orion.rpc.serialization.Serializer#deserialize(byte[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(byte[] data) throws CodecException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        input.setSerializerFactory(serializerFactory);
        Object resultObject = null;
        try {
            resultObject = input.readObject();
            input.close();
        } catch (IOException e) {
            throw new CodecException("Exception occurred when Hessian serializer decode!",
                    e);
        }
        return resultObject;
    }

}
