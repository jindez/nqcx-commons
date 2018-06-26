/*
 * Copyright 2017 nqcx.org All right reserved. This software is the confidential and proprietary information
 * of nqcx.org ("Confidential Information"). You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered into with nqcx.org.
 */

package org.nqcx.commons.doox.core;

import org.nqcx.commons.lang.o.DTO;

/**
 * 门接口，用于构造门和实现开门方法，请要用于动态代理
 *
 * @author naqichuan 17/8/14 18:13
 */
public interface Door {

    /**
     * 开门
     *
     * @param dto DTO
     * @return DTO
     */
    DTO open(DTO dto);
}
