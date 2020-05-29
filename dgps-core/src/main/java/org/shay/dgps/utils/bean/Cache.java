package org.shay.dgps.utils.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 基于HashMap与读写锁的本地缓存
 *
 * @author Alan.ye
 * @date 2017/6/5
 */
public class Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(Cache.class.getSimpleName());

    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock read = rwl.readLock();
    private Lock write = rwl.writeLock();

    private Map<K, V> cache = null;

    public Cache() {
        this.cache = new LRUMap(64);
    }

    public Cache(int initialCapacity) {
        this.cache = new LRUMap(initialCapacity);
    }

    public Cache(Map<K, V> cache) {
        this.cache = cache;
    }


    public V get(K key, Callable<? extends V> loader) {
        V value;
        read.lock();
        try {
            value = cache.get(key);
        } finally {
            read.unlock();
        }

        if (null == value) {
            write.lock();
            try {
                value = cache.get(key);
                if (null == value) {
                    value = loader.call();
                    cache.put(key, value);
                }
            } catch (Exception e) {
                log.error("write cache error", e);
            } finally {
                write.unlock();
            }
        }
        return value;
    }
}