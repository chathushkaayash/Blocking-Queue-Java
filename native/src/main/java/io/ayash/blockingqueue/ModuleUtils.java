package io.ayash.blockingqueue;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.Module;

/**
 * Module utils for the Ballerina AWS Marketplace Entitlement connector to obtain the module info in the init.
 */
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
