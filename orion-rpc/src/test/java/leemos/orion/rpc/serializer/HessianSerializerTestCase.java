package leemos.orion.rpc.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import leemos.orion.codec.CodecException;
import leemos.orion.remoting.serializer.HessianSerializer;

public class HessianSerializerTestCase {

    private HessianSerializer serializer;

    @Before
    public void setup() {
        serializer = new HessianSerializer();
    }

    @Test
    public void testHessianSerializer() {
        Cat tom = new Cat("Tom");

        byte[] data;
        try {
            data = serializer.serialize(tom);
            Object newTom = serializer.deserialize(data);

            assertTrue(newTom instanceof Cat);
            assertEquals(tom.name, ((Cat) newTom).name);
        } catch (CodecException e) {
            fail();
        }
    }

}


class Cat implements Serializable {

    private static final long serialVersionUID = 1L;

    String name;

    public Cat(String name) {
        this.name = name;
    }
}