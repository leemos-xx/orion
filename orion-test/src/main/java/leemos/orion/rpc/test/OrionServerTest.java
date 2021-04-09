package leemos.orion.rpc.test;

import java.util.concurrent.CountDownLatch;

import leemos.orion.Server;
import leemos.orion.core.StandardServer;
import leemos.orion.lifecycle.LifecycleException;

public class OrionServerTest {

    private Server server;
    private CountDownLatch latch = new CountDownLatch(1);

    public OrionServerTest() {
    }

    public static void main(String[] args) throws LifecycleException {
        new OrionServerTest().start();
    }

    public void start() throws LifecycleException {
        server = new StandardServer();
        try {
            server.start();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    public void stop() throws LifecycleException {
        try {
            server.stop();
        } finally {
            latch.countDown();
        }
    }

}
