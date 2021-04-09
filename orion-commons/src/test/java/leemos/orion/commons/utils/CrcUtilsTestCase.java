package leemos.orion.commons.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CrcUtilsTestCase {

    @Test
    public void testCrc32() {
        byte[] rawBytes = "A string used only for testing".getBytes();
        int res = CrcUtils.crc32(rawBytes);
        assertEquals(res, CrcUtils.crc32(rawBytes, 0, rawBytes.length));
    }
}
