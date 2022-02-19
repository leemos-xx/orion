package leemos.orion.hrpc.test;

import java.util.concurrent.CountDownLatch;

import leemos.orion.Server;
import leemos.orion.core.StandardServer;
import leemos.orion.lifecycle.LifecycleException;

public class HrpcServerTest {

    private Server server;
    private CountDownLatch latch = new CountDownLatch(1);

    public HrpcServerTest() {
        setup();
    }
    
    public static void main(String[] args) throws LifecycleException {
        new HrpcServerTest().start();
    }

    public void start() throws LifecycleException {
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

    /**
     * 引导对server的配置
     */
    private void setup() {
        server = createDefaultServer();
    }

    private Server createDefaultServer() {
        Server server = new StandardServer();
        return server;
    }
}
