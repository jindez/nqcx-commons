/*
 * Copyright 2017 nqcx.org All right reserved. This software is the confidential and proprietary information
 * of nqcx.org ("Confidential Information"). You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered into with nqcx.org.
 */

package org.nqcx.commons.doox.core;


import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author naqichuan 17/8/15 15:11
 */
public class DoorProxy implements InvocationHandler, Serializable {

    private Object etherObject;
    private Method etherOpen;

    public DoorProxy(Object etherObject, Method etherOpen) {
        this.etherObject = etherObject;
        this.etherOpen = etherOpen;
    }

    /**
     * @param proxy  proxy
     * @param method method
     * @param args   args
     * @return boject
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        return etherOpen.invoke(etherObject, args);
    }

    /**
     * @param doorType doorType
     * @param <T>      t
     * @return t
     */
    public static <T> T newInstance(Class<T> doorType, Object door, Method doorMethod) {
        ClassLoader classLoader = doorType.getClassLoader();
        Class[] interfaces = new Class[]{doorType};
        DoorProxy proxy = new DoorProxy(door, doorMethod);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }
}