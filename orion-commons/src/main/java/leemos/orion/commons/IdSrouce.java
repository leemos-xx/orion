package leemos.orion.commons;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 单个jvm内作为{@link Message}的唯一标识
 *
 *
 * @author lihao
 * @date 2020年8月24日
 * @version 1.0
 */
public class IdSrouce {

    private static final AtomicLong id = new AtomicLong(1);

    /**
     * 获取下一个id
     *
     * @return
     */
    public static long nextId() {
        return id.getAndIncrement();
    }
}
