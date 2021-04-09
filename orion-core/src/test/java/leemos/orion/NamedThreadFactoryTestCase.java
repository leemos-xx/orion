package leemos.orion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import leemos.orion.commons.NamedThreadFactory;

public class NamedThreadFactoryTestCase {

    @Test
    public void testNamedThreadFactory() {
        NamedThreadFactory factory = new NamedThreadFactory("orion", false);
        Thread t1 = factory.newThread(null);
        Thread t2 = factory.newThread(null);
        assertEquals("orion-1-thread-1", t1.getName());
        assertEquals("orion-1-thread-2", t2.getName());
        assertFalse(t1.isDaemon() || t2.isDaemon());

        NamedThreadFactory nextFactory = new NamedThreadFactory("orion", false);
        t1 = nextFactory.newThread(null);
        t2 = nextFactory.newThread(null);
        assertEquals("orion-2-thread-1", t1.getName());
        assertEquals("orion-2-thread-2", t2.getName());
        assertFalse(t1.isDaemon() || t2.isDaemon());
    }
}
