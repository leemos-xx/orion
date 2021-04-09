package leemos.orion.commons.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public class StringUtils {

    public static final String EMPTY = "";
    public static final String DASH = "-";
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * 是否为null或空字符串
     *
     * @param cs
     * @return
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 是否不为null或空字符串
     *
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    /**
     * 是否为空白字符串
     *
     * @param cs
     * @return
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否不为空白字符串
     *
     * @param cs
     * @return
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    /**
     * 字符串拆分
     *
     * @param str
     * @param separatorChar
     * @return
     */
    public static String[] split(final String str, final char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    /**
     * 重复字符串
     *
     * @param target
     * @param times
     * @return
     */
    public static String repeats(String target, int times) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < times; i++) {
            sb.append(target);
        }
        return sb.toString();
    }

    private static String[] splitWorker(final String str, final char separatorChar,
            final boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<String>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 是否数值类型
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        } else {
            int sz = str.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

}
