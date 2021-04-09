package leemos.orion.remoting.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 可以记录是否已执行的状态
 *
 *
 * @author lihao
 * @date 2020年8月20日
 * @version 1.0
 */
public class ExecutedTask<V> extends FutureTask<V> {

    private AtomicBoolean executed = new AtomicBoolean();

    public ExecutedTask(Callable<V> callable) {
        super(callable);
    }

    @Override
    public void run() {
        executed.set(true);
        super.run();
    }

    /**
     * 获取结果，如果此时任务未执行或执行未完成，则抛出异常
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws NotExecutedException
     * @throws NotCompletedException
     */
    public V getAfterRun() throws InterruptedException, ExecutionException,
            NotExecutedException, NotCompletedException {
        if (!executed.get()) {
            throw new NotExecutedException();
        }
        if (!isDone()) {
            throw new NotCompletedException();
        }

        return super.get();
    }
}
