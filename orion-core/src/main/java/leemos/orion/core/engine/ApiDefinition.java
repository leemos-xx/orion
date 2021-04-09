package leemos.orion.core.engine;

import java.lang.reflect.Method;

/**
 * Api定义
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class ApiDefinition {

    private String name;
    private boolean isAsync;
    private Method apiMethod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }

    public Method getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(Method apiMethod) {
        this.apiMethod = apiMethod;
    }
}
