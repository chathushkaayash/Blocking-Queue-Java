package io.ayash.blockingqueue;/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com)
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BError;

/**
 * {@code CommonUtils} contains the common utility functions for the Ballerina AWS MPE connector.
 */
public final class CommonUtils {

    private CommonUtils() {
    }


    public static BError createError(String message, Throwable exception) {
        return ErrorCreator.createError(ModuleUtils.getModule(), Constants.MPE_ERROR, StringUtils.fromString(message), null, null);
    }
}