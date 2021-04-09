package leemos.orion.core.engine;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import leemos.orion.Constants;
import leemos.orion.commons.StringManager;

/**
 * 场景扫描器，用于扫描出所有被{@link Scene}和{@link Api}标记后的方法，并生成{@link SceneDefinition}的集合
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class SceneScanner {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private Map<String, Set<String>> annotationIndex;

    public SceneScanner() {
        initialize();
    }

    /**
     * 加载所有场景
     *
     * @return
     */
    public SceneDefinition[] loadScenes() {
        Set<String> sceneClassNameSet = annotationIndex.get(Scene.class.getName());
        if (sceneClassNameSet == null || sceneClassNameSet.size() == 0) {
            return new SceneDefinition[0];
        }

        ArrayList<SceneDefinition> scenes = new ArrayList<SceneDefinition>();
        Iterator<String> iter = sceneClassNameSet.iterator();
        while (iter.hasNext()) {
            scenes.add(parseSceneDefinition(iter.next()));
        }
        return scenes.toArray(new SceneDefinition[0]);
    }

    /**
     * 解析场景定义
     *
     * @param sceneClassName
     * @return
     */
    private SceneDefinition parseSceneDefinition(String sceneClassName) {
        SceneDefinition sceneDefinition = new SceneDefinition();

        Class<?> sceneClass = getAssociatedClass(sceneClassName);
        Scene sceneAnno = sceneClass.getAnnotation(Scene.class);
        sceneDefinition.setName(sceneAnno.name());
        sceneDefinition.setSingleton(sceneAnno.singleton());
        sceneDefinition.setSceneClass(sceneClass);
        sceneDefinition.setApis(parseApiDefinition(sceneClass));

        return sceneDefinition;
    }

    /**
     * string -> class
     *
     * @param targetClassName
     * @return
     */
    private Class<?> getAssociatedClass(String targetClassName) {
        Class<?> targetClass = null;
        try {
            targetClass = Class.forName(targetClassName);
        } catch (ClassNotFoundException e) {
            throw new ScannotationException(sm.getString(
                    "sceneScanner.getAssociatedClass.error", targetClassName), e);
        }
        return targetClass;
    }

    /**
     * 解析api定义
     *
     * @param sceneClass
     * @return
     */
    private ApiDefinition[] parseApiDefinition(Class<?> sceneClass) {
        Method[] allMethods = sceneClass.getMethods();
        if (allMethods.length == 0) {
            return new ApiDefinition[0];
        }

        ArrayList<ApiDefinition> apis = new ArrayList<ApiDefinition>();
        for (int i = 0; i < allMethods.length; i++) {
            Method targetMethod = allMethods[i];
            Api apiAnno = targetMethod.getAnnotation(Api.class);
            if (apiAnno != null) {
                ApiDefinition apiDefinition = new ApiDefinition();
                apiDefinition.setName(apiAnno.name());
                apiDefinition.setAsync(apiAnno.async());
                apiDefinition.setApiMethod(targetMethod);

                apis.add(apiDefinition);
            } else {
                continue;
            }
        }
        return apis.toArray(new ApiDefinition[0]);
    }

    /**
     * 初始化
     */
    private void initialize() {
        URL[] urls = ClasspathUrlFinder.findClassPaths();
        AnnotationDB db = new AnnotationDB();
        try {
            db.scanArchives(urls);
            annotationIndex = db.getAnnotationIndex();
        } catch (IOException e) {
            throw new ScannotationException(e);
        }
    }
}
