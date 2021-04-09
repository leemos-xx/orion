package leemos.orion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import leemos.orion.core.engine.BeanFactory;
import leemos.orion.core.engine.InvocationException;

public class BeanFactoryTestCase {

    @Test
    public void testGetBean() {
        try {
            BeanFactory factory = (BeanFactory) BeanFactory.getBean(BeanFactory.class,
                    true);
            assertTrue(factory == BeanFactory.getBean(BeanFactory.class, true));
            assertFalse(factory == BeanFactory.getBean(BeanFactory.class, false));
        } catch (InvocationException e) {
            fail();
        }
    }
}
