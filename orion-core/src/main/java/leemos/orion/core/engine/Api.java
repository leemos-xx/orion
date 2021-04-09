package leemos.orion.core.engine;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * api标识
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Api {

    /**
     * 名称
     *
     * @return
     */
    String name();

    /**
     * 是否异步调用
     *
     * @return
     */
    boolean async() default false;
}
