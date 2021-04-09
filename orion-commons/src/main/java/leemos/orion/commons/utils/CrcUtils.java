package leemos.orion.commons.utils;

import java.util.zip.CRC32;

/**
 * Crc utils
 *
 * @author lihao
 * @date 2020年8月14日
 * @version 1.0
 */
public class CrcUtils {

    private static final ThreadLocal<CRC32> CRC_32_THREAD_LOCAL = new ThreadLocal<CRC32>() {
        @Override
        protected CRC32 initialValue() {
            return new CRC32();
        }
    };

    public static final int crc32(byte[] array) {
        if (array != null) {
            return crc32(array, 0, array.length);
        }

        return 0;
    }

    public static final int crc32(byte[] array, int offset, int length) {
        CRC32 crc32 = CRC_32_THREAD_LOCAL.get();
        crc32.update(array, offset, length);
        int ret = (int) crc32.getValue();
        crc32.reset();
        return ret;
    }
}
