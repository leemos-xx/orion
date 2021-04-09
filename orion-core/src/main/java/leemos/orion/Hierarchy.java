package leemos.orion;

/**
 * 实现该接口的组件代表具有抽象层级关系，主要用于日志展示。
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public interface Hierarchy {

    // 用作打印层级日志
    public static final String SPACES = "    ";
    public static final String SYMBOL = "|___";

    /**
     * 返回组件所处的抽象层级
     *
     * @return
     */
    public int getLevel();

    /**
     * 获取该组件的名称
     */
    public String getName();

    /**
     * 设置该组件的名称
     * 
     * @param name
     */
    public void setName(String name);
}
