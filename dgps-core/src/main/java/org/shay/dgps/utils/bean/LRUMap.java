package org.shay.dgps.utils.bean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于LinkedHashMap实现，最近访问优先的LRU缓存
 *
 * @author Alan.ye
 * @date 2017/6/6
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

    private int maxSize;

    public LRUMap(int maxSize) {
        super(maxSize, 0.75F, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return (size() > this.maxSize);
    }

}