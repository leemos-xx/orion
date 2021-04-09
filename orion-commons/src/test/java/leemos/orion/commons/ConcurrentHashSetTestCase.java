package leemos.orion.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.Test;

public class ConcurrentHashSetTestCase {

    @Test
    public void testSet() {
        ConcurrentHashSet<Boolean> set = new ConcurrentHashSet<Boolean>();
        set.add(Boolean.TRUE);
        set.add(Boolean.FALSE);
        assertEquals(2, set.size());

        assertFalse(set.add(Boolean.TRUE));

        set.remove(Boolean.TRUE);
        assertEquals(1, set.size());
        set.remove(Boolean.FALSE);
        assertEquals(0, set.size());

        set.addAll(Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        assertEquals(2, set.size());
        set.removeAll(Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        assertEquals(0, set.size());
    }
}
