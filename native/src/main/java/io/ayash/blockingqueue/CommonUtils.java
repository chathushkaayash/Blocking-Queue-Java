package io.ayash.blockingqueue;

import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BError;

public final class CommonUtils {

    private CommonUtils() {
    }

    public static BError createError(String message, Throwable exception) {
        return ErrorCreator.createError(ModuleUtils.getModule(), "Error",
                StringUtils.fromString(message), null, null);
    }
}
