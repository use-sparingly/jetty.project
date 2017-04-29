//
//  ========================================================================
//  Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.jsr356.messages;

import java.io.IOException;
import java.util.function.Function;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;

import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.common.message.ReaderMessageSink;
import org.eclipse.jetty.websocket.jsr356.function.JsrEndpointFunctions;

public class DecodedReaderMessageSink extends ReaderMessageSink
{
    public DecodedReaderMessageSink(JsrEndpointFunctions endpointFunctions, Decoder.TextStream decoder, Function<Object, Object> onMessageFunction)
    {
        super(endpointFunctions.getExecutor(), (reader) ->
        {
            try
            {
                Object decoded = decoder.decode(reader);
                
                // notify event
                Object ret = onMessageFunction.apply(decoded);
                
                if (ret != null)
                {
                    // send response
                    endpointFunctions.getSession().getBasicRemote().sendObject(ret);
                }
                
                return null;
            }
            catch (DecodeException | EncodeException | IOException e)
            {
                throw new WebSocketException(e);
            }
        });
    }
}