package org.shay.dgps.enums;

import org.shay.dgps.message.SyncFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shay
 */

public enum MessageManager {
    /**
     * 实例
     */
    INSTANCE;

    private Map<String, SyncFuture> map = new ConcurrentHashMap<>();


    public SyncFuture receive(String key) {
        SyncFuture future = new SyncFuture();
        map.put(key, future);
        return future;
    }

    public void remove(String key) {
        map.remove(key);
    }

    public void put(String key, Object value) {
        SyncFuture syncFuture = map.get(key);
        if (syncFuture == null) {
            return;
        }
        syncFuture.setResponse(value);
    }
}
