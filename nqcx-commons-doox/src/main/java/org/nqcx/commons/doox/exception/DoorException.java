/*
 * Copyright 2017 nqcx.org All right reserved. This software is the confidential and proprietary information
 * of nqcx.org ("Confidential Information"). You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered into with nqcx.org.
 */

package org.nqcx.commons.doox.exception;

/**
 * @author naqichuan 17/8/17 18:44
 */
public class DoorException extends RuntimeException {

    public DoorException() {
        super();
    }

    public DoorException(String message) {
        super(message);
    }

    public DoorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DoorException(Throwable cause) {
        super(cause);
    }
}
