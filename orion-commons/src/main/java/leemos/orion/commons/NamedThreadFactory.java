package leemos.orion.commons;

import static leemos.orion.commons.utils.StringUtils.DASH;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名的线程工厂类，由此类创建的线程都将具有指定的命名前缀。
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolId = new AtomicInteger(1);

    private final AtomicInteger threadId = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final boolean daemon;

    public NamedThreadFactory(String prefix, boolean isDaemon) {
        SecurityManager sm = System.getSecurityManager();
        group = (sm != null) ? sm.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + DASH + poolId.getAndIncrement() + DASH + "thread" + DASH;
        daemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadId.getAndIncrement(), 0);
        t.setDaemon(daemon);
        return t;
    }

}
