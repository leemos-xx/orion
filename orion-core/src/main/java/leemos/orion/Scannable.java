package leemos.orion;

/**
 * 指明该接口的实现类是可遍历扫描的。如会话池、线程池等可以实现该接口以检查池内对象的状态。
 *
 *
 * @author lihao
 * @date 2020年8月11日
 * @version 1.0
 */
public interface Scannable {

    void scan();
}
