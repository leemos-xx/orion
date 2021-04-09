package leemos.orion.remoting;

import java.util.List;

/**
 * 选择策略接口
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public interface SelectStrategy<T> {

    /**
     * 实现选择策略，可以为随机、权重等
     *
     * @param <T>
     * @param instances
     * @return
     */
    T select(List<T> instances);
}
