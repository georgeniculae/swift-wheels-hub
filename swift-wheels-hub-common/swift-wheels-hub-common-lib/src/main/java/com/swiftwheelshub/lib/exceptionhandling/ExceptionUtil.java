package com.swiftwheelshub.lib.exceptionhandling;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

    public static RuntimeException handleException(Throwable e) {
        if (e instanceof SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException) {
            return swiftWheelsHubNotFoundException;
        }

        if (e instanceof SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException) {
            return swiftWheelsHubResponseStatusException;
        }

        if (e instanceof SwiftWheelsHubException swiftWheelsHubException) {
            return swiftWheelsHubException;
        }

        return new SwiftWheelsHubException(e.getMessage());
    }

}
