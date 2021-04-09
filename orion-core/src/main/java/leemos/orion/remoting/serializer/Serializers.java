package leemos.orion.remoting.serializer;

/**
 * 序列化机制管理器
 *
 *
 * @author lihao
 * @date 2020年8月15日 下午10:18:01
 * @version 1.0
 *
 */
public class Serializers {

    public static final byte HESSIAN = 0;
    public static final byte PROTOBUF = 1;
    private static Serializer[] serializers = new Serializer[2];

    static {
        addSerializer(HESSIAN, new HessianSerializer());
    }

    /**
     * 增加序列化处理器
     *
     * @param idx
     * @param serializer
     */
    public static void addSerializer(int idx, Serializer serializer) {
        if (serializers.length <= idx) {
            Serializer[] results = new Serializer[idx + 1];
            System.arraycopy(serializers, 0, results, 0, serializers.length);
            serializers = results;
        }
        serializers[idx] = serializer;
    }

    /**
     * 返回序列化处理器
     *
     * @param idx
     * @return
     */
    public static Serializer getSerializer(int idx) {
        return serializers[idx];
    }
}
