package leemos.orion.core.engine;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 场景标识
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scene {

    /**
     * 场景名
     *
     * @return
     */
    String name();

    /**
     * 该注解标记的类是否以单例模式维护
     *
     * @return
     */
    boolean singleton() default true;
}
