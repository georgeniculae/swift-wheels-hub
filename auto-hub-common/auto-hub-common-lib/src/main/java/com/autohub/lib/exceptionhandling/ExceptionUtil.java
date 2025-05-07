package com.autohub.lib.exceptionhandling;

import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

    public static RuntimeException handleException(Throwable e) {
        if (e instanceof AutoHubNotFoundException autoHubNotFoundException) {
            return autoHubNotFoundException;
        }

        if (e instanceof AutoHubResponseStatusException autoHubResponseStatusException) {
            return autoHubResponseStatusException;
        }

        if (e instanceof AutoHubException autoHubException) {
            return autoHubException;
        }

        return new AutoHubException(e.getMessage());
    }

}
