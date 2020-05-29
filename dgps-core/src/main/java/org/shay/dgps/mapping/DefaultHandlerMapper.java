package org.shay.dgps.mapping;

import org.shay.dgps.annotation.Endpoint;
import org.shay.dgps.annotation.Mapping;
import org.shay.dgps.mapping.impl.AbstractHandlerMapper;
import org.shay.dgps.utils.ClassUtils;
import org.shay.dgps.utils.bean.BeanUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shay
 */
public class DefaultHandlerMapper extends AbstractHandlerMapper {

    public DefaultHandlerMapper(String... packageNames) {
        for (String packageName : packageNames) {
            addPackage(packageName);
        }
    }

    public DefaultHandlerMapper(Class<?>... handlerClasses) {
        for (Class<?> handlerClass : handlerClasses) {
            findHandlers(handlerClass);
        }
    }

    private void addPackage(String packageName) {
        List<Class<?>> handlerClassList = ClassUtils.getClassList(packageName, Endpoint.class);
        for (Class<?> handlerClass : handlerClassList) {
            this.findHandlers(handlerClass);
        }
    }

    @Override
    protected Object getBean(Class<?> cls) {
        return BeanUtils.newInstance(cls);
    }
}