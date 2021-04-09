package leemos.orion.commons;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringManagerTestCase {

    private static StringManager sm = StringManager.getManager("leemos.orion.commons");

    @Test
    public void testGetString() {
        assertEquals("args[]", sm.getString("testCase.getString.noArgs"));
        assertEquals("args[0]", sm.getString("testCase.getString.oneArgs", "0"));
        assertEquals("args[0,1]", sm.getString("testCase.getString.twoArgs", "0", "1"));
        assertEquals("args[0,1,2]", sm.getString("testCase.getString.threeArgs",
                new String[] { "0", "1", "2" }));
    }
}
