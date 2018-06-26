/*
 * Copyright 2017 nqcx.org All right reserved. This software is the confidential and proprietary information
 * of nqcx.org ("Confidential Information"). You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered into with nqcx.org.
 */

package org.nqcx.commons.doox.annotation;

import java.lang.annotation.*;

/**
 * 以太，该注解用于标记 bean 可以与门建立连接
 *
 * @author naqichuan 17/8/14 16:15
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ether {

    /**
     * Bean alias
     *
     * @return string
     */
    String value() default "";
}
