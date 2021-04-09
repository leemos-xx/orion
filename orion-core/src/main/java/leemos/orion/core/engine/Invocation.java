package leemos.orion.core.engine;

import java.lang.reflect.Method;

/**
 * 服务执行所需的上下文
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class Invocation {

    private Class<?> sceneClass;
    private Method apiMethod;
    private boolean isSingleton;
    private boolean isAsync;

    /**
     * invoke
     *
     * @param parameters
     * @return
     * @throws InvocationException
     */
    public Object invoke(Object[] parameters) throws InvocationException {
        Object result = null;
        try {
            Object target = BeanFactory.getBean(sceneClass, isSingleton);
            result = apiMethod.invoke(target, parameters);
        } catch (InvocationException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationException(e);
        }
        return result;
    }

    public Class<?> getSceneClass() {
        return sceneClass;
    }

    public void setSceneClass(Class<?> sceneClass) {
        this.sceneClass = sceneClass;
    }

    public Method getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(Method apiMethod) {
        this.apiMethod = apiMethod;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }
}
