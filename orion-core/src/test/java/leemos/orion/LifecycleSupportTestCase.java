package leemos.orion;

import static org.junit.Assert.fail;

import org.junit.Test;

import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleSupport;

public class LifecycleSupportTestCase {

    @Test
    public void testInitialization() {
        Component component = new Component("initialization-cmpt");
        try {
            component.initialize();
        } catch (LifecycleException e) {
            fail("not reachable");
        }
        try {
            component.initialize();
            fail("not reachable");
        } catch (LifecycleException e) {
            ;
        }
    }

    @Test
    public void testStart() {
        Component component = new Component("start-cmpt");
        try {
            component.start();
        } catch (LifecycleException e) {
            fail("not reachable");
        }
        try {
            component.start();
            fail("not reachable");
        } catch (LifecycleException e) {
            ;
        }
    }

    @Test
    public void testStop() {
        Component component = new Component("stop-cmpt");
        try {
            component.stop();
            fail("not reachable");
        } catch (LifecycleException e) {
            ;
        }
        try {
            component.start();
        } catch (LifecycleException e) {
            fail("not reachable");
        }
        try {
            component.stop();
        } catch (LifecycleException e) {
            fail("not reachable");
        }
        try {
            component.stop();
            fail("not reachable");
        } catch (LifecycleException e) {
            ;
        }
    }
}


class Component extends LifecycleSupport {

    private String name;

    public Component(String name) {
        setName(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void initializeInternal() throws LifecycleException {

    }

    @Override
    protected void startInternal() throws LifecycleException {

    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    public int getLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

}