package leemos.orion.core.engine;

/**
 * 场景定义
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class SceneDefinition {

    private String name;
    private boolean isSingleton;
    private Class<?> sceneClass;
    private ApiDefinition[] apis;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }

    public Class<?> getSceneClass() {
        return sceneClass;
    }

    public void setSceneClass(Class<?> sceneClass) {
        this.sceneClass = sceneClass;
    }

    public ApiDefinition[] getApis() {
        return apis;
    }

    public void setApis(ApiDefinition[] apis) {
        this.apis = apis;
    }
}
