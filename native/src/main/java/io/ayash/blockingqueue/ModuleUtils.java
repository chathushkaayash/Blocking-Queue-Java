package io.ayash.blockingqueue;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.Module;

public final class ModuleUtils {

    private static Module module;

    private ModuleUtils() {
    }

    public static Module getModule() {
        return module;
    }

    public static void setModule(Environment environment) {
        module = environment.getCurrentModule();
    }
}
