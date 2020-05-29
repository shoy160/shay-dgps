package org.shay.dgps.mapping;

import org.shay.dgps.annotation.Endpoint;
import org.shay.dgps.annotation.Mapping;
import org.shay.dgps.mapping.impl.AbstractHandlerMapper;
import org.shay.dgps.utils.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shay
 */
public class SpringHandlerMapper extends AbstractHandlerMapper implements ApplicationContextAware {

    private ApplicationContext context;
    private String[] packageNames;
    private Class<?>[] handlerClasses;

    public SpringHandlerMapper(String... packageNames) {
        this.packageNames = packageNames;
    }

    public SpringHandlerMapper(Class<?>... handlerClasses) {
        this.handlerClasses = handlerClasses;
    }

    @Override
    protected Object getBean(Class<?> cls) {
        return this.context.getBean(cls);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        if (this.handlerClasses != null) {
            for (Class<?> handlerClass : this.handlerClasses) {
                findHandlers(handlerClass);
            }
        }
        if (packageNames != null && packageNames.length > 0) {
            for (String packageName : packageNames) {
                List<Class<?>> handlerClassList = ClassUtils.getClassList(packageName, Endpoint.class);
                if (handlerClassList.size() == 0) {
                    continue;
                }
                for (Class<?> handlerClass : handlerClassList) {
                    findHandlers(handlerClass);
                }
            }
        }
    }
}