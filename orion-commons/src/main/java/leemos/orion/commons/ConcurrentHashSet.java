package leemos.orion.commons;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过{@link ConcurrentHashMap}包装的Set实现
 *
 * @author lihao
 * @date 2020年8月11日
 * @version 1.0
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> {

    private ConcurrentHashMap<E, Boolean> map;

    public ConcurrentHashSet() {
        super();
        map = new ConcurrentHashMap<E, Boolean>();
    }

    /*
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return map.size();
    }

    /*
     * @see java.util.AbstractCollection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    /*
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /*
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    @Override
    public boolean add(E o) {
        return map.putIfAbsent(o, Boolean.TRUE) == null;
    }

    /*
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    /*
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        map.clear();
    }

}
