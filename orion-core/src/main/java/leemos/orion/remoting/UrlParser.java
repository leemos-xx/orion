package leemos.orion.remoting;

/**
 * 地址解析器
 *
 * @author lihao
 * @date 2020年8月10日
 * @version 1.0
 */
public interface UrlParser {

    char COLON = ':';

    char EQUAL = '=';

    char AND = '&';

    char QUES = '?';

    /**
     * 将原始的url字符串，转换为易于操作的{@link Url}实例
     *
     * @param url
     * @return
     */
    Url parse(String url);

    /**
     * 尝试从原始url字符串中获取uniqueKey
     *
     * @param url
     * @return
     */
    String parseUniqueKey(String url);

}
