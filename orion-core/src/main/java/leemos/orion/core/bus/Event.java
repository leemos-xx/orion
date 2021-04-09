package leemos.orion.core.bus;

/**
 * 内部事件，用于发布到事件总线，并分发到相关的监听器，该类提供最基本的优先级功能
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public abstract class Event implements Comparable<Event> {

    public static final byte MIN_PRIORITY = 1;
    public static final byte NORM_PRIORITY = 5;
    public static final byte MAX_PRIORITY = 10;

    /**
     * 事件优先级
     */
    private byte priority = NORM_PRIORITY;

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        if (priority < MIN_PRIORITY) {
            this.priority = MIN_PRIORITY;
        } else if (priority > MAX_PRIORITY) {
            this.priority = MAX_PRIORITY;
        } else {
            this.priority = priority;
        }
    }

    /**
     * 获取事件的唯一标识，由子类自行决定实现方式
     *
     * @return
     */
    public abstract long getId();

    @Override
    public int compareTo(Event e) {
        if (this.priority < e.priority) {
            return 1;
        } else if (this.priority > e.priority) {
            return -1;
        } else {
            return 0;
        }
    }
}
