/*
 * digitalpetri OPC-UA SDK
 *
 * Copyright (C) 2015 Kevin Herron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.digitalpetri.opcua.sdk.client.api.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.stack.core.types.structured.CallMethodRequest;
import com.digitalpetri.opcua.stack.core.types.structured.CallResponse;

public interface MethodServices {

    /**
     * This service is used to call (invoke) a list of methods. Each method call is invoked within the context of an
     * existing session. If the session is terminated, the results of the method’s execution cannot be returned to the
     * client and are discarded. This is independent of the task actually performed at the server.
     *
     * @param methodsToCall a list of methods to call.
     * @return a {@link CompletableFuture} containing the {@link CallResponse}.
     */
    CompletableFuture<CallResponse> call(List<CallMethodRequest> methodsToCall);

}
