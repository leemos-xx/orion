package leemos.orion.rpc.serializer;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import leemos.orion.remoting.serializer.HessianSerializer;
import leemos.orion.remoting.serializer.Serializer;
import leemos.orion.remoting.serializer.Serializers;

public class SerializerManagerTestCase {

    @Test
    public void testSerializerManager() {
        Serializer hessianSerializer = Serializers
                .getSerializer(Serializers.HESSIAN);
        assertTrue(hessianSerializer instanceof HessianSerializer);

        HessianSerializer newHessianSeriazlier = new HessianSerializer();
        Serializers.addSerializer(2, newHessianSeriazlier);
        Assert.assertEquals(newHessianSeriazlier, Serializers.getSerializer(2));
    }
}
