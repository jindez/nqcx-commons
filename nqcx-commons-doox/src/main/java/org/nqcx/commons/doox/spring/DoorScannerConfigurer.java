/*
 * Copyright 2017 nqcx.org All right reserved. This software is the confidential and proprietary information
 * of nqcx.org ("Confidential Information"). You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered into with nqcx.org.
 */

package org.nqcx.commons.doox.spring;

import org.nqcx.commons.doox.annotation.Ether;
import org.nqcx.commons.doox.annotation.EtherOpen;
import org.nqcx.commons.doox.exception.DoorException;
import org.nqcx.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * @author naqichuan 17/8/15 15:26
 */
public class DoorScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(DoorScannerConfigurer.class);

    private String basePackage;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Scanner scanner = new Scanner(registry);
        scanner.setBeanNameGenerator(new DoorAnnotationBeanNameGenerator());
        scanner.scan(basePackage);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * 扫描需要初始化为任意门的类，实例化并注册到 Spring，同时通过动态代码生成 door 调用的 bean
     */
    private final class Scanner extends ClassPathBeanDefinitionScanner {

        public Scanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        protected void registerDefaultFilters() {
            this.addIncludeFilter(new AnnotationTypeFilter(Ether.class));
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return registerDoox(super.doScan(basePackages));
        }

        /**
         * 注册任意门到 Sping Registry
         *
         * @param beanDefinitions definitions
         */
        protected Set<BeanDefinitionHolder> registerDoox(Set<BeanDefinitionHolder> beanDefinitions) {
            if (beanDefinitions != null && !beanDefinitions.isEmpty()) {
                for (BeanDefinitionHolder holder : beanDefinitions)
                    registerDoors(holder);
            }

            return beanDefinitions;
        }

        /**
         * 注册多个 Door 到 Sping Registry
         *
         * @param holder holder
         */
        protected void registerDoors(BeanDefinitionHolder holder) {
            String etherName;
            String etherClassName;
            if (holder == null || holder.getBeanDefinition() == null
                    || StringUtils.isBlank(etherName = holder.getBeanName())
                    || StringUtils.isBlank(etherClassName = holder.getBeanDefinition().getBeanClassName()))
                return;

            Method[] ms;
            try {
                Class<?> clzz;
                if ((clzz = Class.forName(etherClassName)) == null
                        || (ms = clzz.getDeclaredMethods()) == null || ms.length == 0)
                    return;
            } catch (ClassNotFoundException e) {
                throw new DoorException("DoorScannerConfigurer.Scanner.doScan " + e.getMessage(), e);
            }

            for (Method m : ms)
                registerDoor(etherName, m);
        }

        /**
         * 注册一个 door
         *
         * @param etherName etherName
         * @param method    method
         */
        private void registerDoor(String etherName, Method method) {
            EtherOpen ehterOpen;
            if (StringUtils.isBlank(etherName) || method == null
                    || (ehterOpen = (EtherOpen) getAnnotation(method.getDeclaredAnnotations(), EtherOpen.class)) == null)
                return;

            String ehterOpenName = ehterOpen.value();
            if (StringUtils.isBlank(ehterOpenName))
                ehterOpenName = method.getName();

            LOGGER.info("etherName: {}, ehterOpenName: {}", etherName, ehterOpenName);

            // 为方法生成代理类
            BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(DoorFactoryBean.class);
            bdb.addPropertyValue("etherName", etherName);
            bdb.addPropertyValue("etherOpen", method);

            getRegistry().registerBeanDefinition(etherName + "." + ehterOpenName, bdb.getBeanDefinition());
        }

        /**
         * 包含指定注解
         *
         * @param annotations annotations
         * @return Annotation
         */
        protected Annotation getAnnotation(Annotation[] annotations, Class<? extends Annotation> target) {
            if (annotations == null || target == null)
                return null;

            for (Annotation a : annotations) {
                if (a != null && a.annotationType().equals(target))
                    return a;
            }

            return null;
        }
    }

    /**
     * bean name 生成器
     */
    public final static class DoorAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

        @Override
        protected boolean isStereotypeWithNameValue(String annotationType, Set<String> metaAnnotationTypes, Map<String, Object> attributes) {
            boolean isStereotype = annotationType.equals(Ether.class.getName());
            return (isStereotype && attributes != null &&
                    attributes.containsKey("value") &&
                    attributes.get("value") instanceof String);
        }
    }
}
