import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BError;

/**
 * {@code CommonUtils} contains the common utility functions for the Ballerina
 * AWS MPE connector.
 */
public final class CommonUtils {

    private CommonUtils() {
    }

    public static BError createError(String message, Throwable exception) {
        return ErrorCreator.createError(ModuleUtils.getModule(), Constants.MPE_ERROR, StringUtils.fromString(message),
                null, null);
    }
}
