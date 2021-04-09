package leemos.orion.remoting;

import java.util.List;
import java.util.Random;

/**
 * 随机选择策略
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public class RandomSelectStrategy<T> implements SelectStrategy<T> {

    private final Random random = new Random();

    /**
     * 实现随机选择策略
     */
    @Override
    public T select(List<T> instances) {
        if (instances == null || instances.size() == 0) {
            return null;
        }
        instances = filter(instances);

        return randomSelect(instances);
    }

    /**
     * 如果需要提前对<code>instances</code>进行筛选，就覆盖该方法
     *
     * @param <T>
     * @param instances
     * @return
     */
    protected List<T> filter(List<T> instances) {
        // do nothing
        return instances;
    }

    /**
     * 随机选择
     *
     * @param instances
     * @return
     */
    private T randomSelect(List<T> instances) {
        return instances.get(random.nextInt(instances.size()));
    }
}
