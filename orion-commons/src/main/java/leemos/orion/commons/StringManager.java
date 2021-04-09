package leemos.orion.commons;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 国际化实现类，可以减少对{@link ResourceBundle}的直接操作，也可以处理常见的字符串格式化需求。
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public class StringManager {

    private static ConcurrentMap<String, StringManager> managers = new ConcurrentHashMap<String, StringManager>();

    /**
     * 根据package返回对应的{@link StringManager}，该对象会被复用，如果已经创建过，
     * 会缓存到<code>managers</code>
     *
     * @param packageName
     * @return
     */
    public synchronized static StringManager getManager(String packageName) {
        StringManager mgr = managers.get(packageName);
        if (mgr == null) {
            mgr = new StringManager(packageName);
            managers.put(packageName, mgr);
        }
        return mgr;
    }

    /**
     * {@link ResourceBundle}
     */
    private ResourceBundle bundle;

    /**
     * 根据package创建{@link StringManager}对象，对相同的包只会创建一个对象
     * 
     * @param packageName
     */
    private StringManager(String packageName) {
        String bundleName = packageName + ".LocalStrings";
        bundle = ResourceBundle.getBundle(bundleName);
    }

    /**
     * 根据key获取对应字符串
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }

        String str = null;

        try {
            str = bundle.getString(key);
        } catch (MissingResourceException e) {
            str = "Cannot find message associated with key '" + key + "'";
        }

        return str;
    }

    /**
     * 获取并格式化字符串
     *
     * @param key
     * @param arg
     */
    public String getString(String key, String arg) {
        Object[] args = new Object[] { arg };
        return getString(key, args);
    }

    /**
     * 获取并格式化字符串
     *
     * @param key
     * @param arg1
     * @param arg2
     */
    public String getString(String key, String arg1, String arg2) {
        Object[] args = new Object[] { arg1, arg2 };
        return getString(key, args);
    }

    /**
     * 获取并格式化字符串
     *
     * @param key
     * @param args
     */
    public String getString(String key, Object[] args) {
        String iString = null;
        String value = getString(key);

        try {
            Object nonNullArgs[] = args;
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    if (nonNullArgs == args)
                        nonNullArgs = (Object[]) args.clone();
                    nonNullArgs[i] = "null";
                }
            }

            iString = MessageFormat.format(value, nonNullArgs);
        } catch (IllegalArgumentException iae) {
            StringBuffer buf = new StringBuffer();
            buf.append(value);
            for (int i = 0; i < args.length; i++) {
                buf.append(" arg[" + i + "]=" + args[i]);
            }
            iString = buf.toString();
        }
        return iString;
    }
}
