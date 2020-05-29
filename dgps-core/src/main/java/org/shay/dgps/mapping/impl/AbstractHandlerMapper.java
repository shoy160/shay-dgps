package org.shay.dgps.mapping.impl;

import org.shay.dgps.annotation.Mapping;
import org.shay.dgps.mapping.Handler;
import org.shay.dgps.mapping.HandlerMapper;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shay
 * @date 2020/5/29
 */
public abstract class AbstractHandlerMapper implements HandlerMapper {

    private Map<Integer, Handler> handlerMap = new HashMap(55);

    protected void findHandlers(Class<?> handlerClass) {
        Method[] methods = handlerClass.getDeclaredMethods();
        if (methods.length == 0) {
            return;
        }
        for (Method method : methods) {
            if (method.isAnnotationPresent(Mapping.class)) {
                Mapping annotation = method.getAnnotation(Mapping.class);
                String desc = annotation.desc();
                int[] types = annotation.types();
                Handler value = new Handler(getBean(handlerClass), method, desc);
                for (int type : types) {
                    handlerMap.put(type, value);
                }
            }
        }
    }

    /**
     * 获取实例
     *
     * @param cls
     * @return
     */
    protected abstract Object getBean(Class<?> cls);

    @Override
    public Handler getHandler(Integer key) {
        return handlerMap.get(key);
    }
}
