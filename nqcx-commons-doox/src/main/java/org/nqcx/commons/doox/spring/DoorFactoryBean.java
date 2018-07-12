/*
 * Copyright 2017 nqcx.org All right reserved. This software is the confidential and proprietary information
 * of nqcx.org ("Confidential Information"). You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered into with nqcx.org.
 */

package org.nqcx.commons.doox.spring;

import org.nqcx.commons.doox.core.Door;
import org.nqcx.commons.doox.core.DoorProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;

/**
 * @author naqichuan 17/8/15 14:28
 */
public class DoorFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private String etherName;
    private Method etherOpen;

    private ApplicationContext applicationContext;

    public void setEtherName(String etherName) {
        this.etherName = etherName;
    }

    public void setEtherOpen(Method etherOpen) {
        this.etherOpen = etherOpen;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() throws Exception {
        Object door = this.applicationContext.getBean(etherName);
        Method doorMethod = door.getClass().getMethod(etherOpen.getName(), etherOpen.getParameterTypes());
        return (T) DoorProxy.newInstance(this.getObjectType(), door, doorMethod);
    }

    @Override
    public Class<?> getObjectType() {
        return Door.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
