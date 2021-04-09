package leemos.orion.core.engine;

import java.util.concurrent.ConcurrentHashMap;

import leemos.orion.Constants;
import leemos.orion.commons.StringManager;

/**
 * Bean工厂
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class BeanFactory {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);
    private static final ConcurrentHashMap<Class<?>, Object> cached = new ConcurrentHashMap<Class<?>, Object>();

    /**
     * 获取对象实例
     *
     * @param sceneClass
     * @param singleton
     * @return
     * @throws InvocationException
     */
    public static Object getBean(Class<?> sceneClass, boolean singleton)
            throws InvocationException {
        Object instance = null;
        if (singleton) {
            if (cached.containsKey(sceneClass)) {
                instance = cached.get(sceneClass);
            } else {
                synchronized (cached) {
                    instance = newInstance(sceneClass);
                    cached.put(sceneClass, instance);
                }
            }
        } else {
            instance = newInstance(sceneClass);
        }
        return instance;
    }

    private static Object newInstance(Class<?> sceneClass)
            throws InvocationException {
        try {
            return sceneClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvocationException(
                    sm.getString("beanFactory.newInstance.error", sceneClass.getName()),
                    e);
        }
    }

}
