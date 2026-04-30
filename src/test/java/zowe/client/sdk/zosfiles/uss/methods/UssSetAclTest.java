/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zowe.client.sdk.zosfiles.uss.methods;

import kong.unirest.core.Cookie;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.core.ZosConnectionFactory;
import zowe.client.sdk.rest.PutJsonZosmfRequest;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.ZosmfRequest;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.uss.types.DeleteAclType;
import zowe.client.sdk.zosfiles.uss.types.LinkType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

/**
 * Class containing unit tests for UssSetAcl.
 *
 * @author Ashish Kumar Dash
 * @version 6.0
 */
public class UssSetAclTest {

    private final ZosConnection connection = ZosConnectionFactory
            .createBasicConnection("1", 443, "1", "1");
    private final ZosConnection tokenConnection = ZosConnectionFactory
            .createTokenConnection("1", 443, new Cookie("hello=hello"));
    private PutJsonZosmfRequest mockJsonPutRequest;
    private PutJsonZosmfRequest mockJsonPutRequestToken;
    private UssSetAcl ussSetAcl;

    @BeforeEach
    public void init() throws ZosmfRequestException {
        mockJsonPutRequest = Mockito.mock(PutJsonZosmfRequest.class);
        Mockito.when(mockJsonPutRequest.executeRequest()).thenReturn(
                new Response(new JSONObject(), 200, "success"));
        doCallRealMethod().when(mockJsonPutRequest).setUrl(any());
        doCallRealMethod().when(mockJsonPutRequest).getUrl();

        mockJsonPutRequestToken = Mockito.mock(PutJsonZosmfRequest.class,
                withSettings().useConstructor(tokenConnection));
        Mockito.when(mockJsonPutRequestToken.executeRequest()).thenReturn(
                new Response(new JSONObject(), 200, "success"));
        doCallRealMethod().when(mockJsonPutRequestToken).setHeaders(anyMap());
        doCallRealMethod().when(mockJsonPutRequestToken).setStandardHeaders();
        doCallRealMethod().when(mockJsonPutRequestToken).setUrl(any());
        doCallRealMethod().when(mockJsonPutRequestToken).getHeaders();
        doCallRealMethod().when(mockJsonPutRequestToken).getUrl();

        ussSetAcl = new UssSetAcl(connection);
    }

    @Test
    public void tstUssSetAclSetSuccess() throws ZosmfRequestException {
        final UssSetAcl ussSetAcl = new UssSetAcl(connection, mockJsonPutRequest);
        final Response response = ussSetAcl.set("/xxx/xx", "u::rwx");
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Fxxx%2Fxx", mockJsonPutRequest.getUrl());

        final Map<String, Object> setAclMap = new HashMap<>();
        setAclMap.put("request", "setfacl");
        setAclMap.put("links", LinkType.FOLLOW.getValue());
        setAclMap.put("set", "u::rwx");
        verify(mockJsonPutRequest).setBody(new JSONObject(setAclMap).toString());
    }

    @Test
    public void tstUssSetAclToggleTokenSuccess() throws ZosmfRequestException {
        final UssSetAcl ussSetAcl = new UssSetAcl(tokenConnection, mockJsonPutRequestToken);
        final Response response = ussSetAcl.set("/xxx/xx", "u::rwx");
        assertEquals("{X-CSRF-ZOSMF-HEADER=true, Content-Type=application/json}",
                mockJsonPutRequestToken.getHeaders().toString());
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Fxxx%2Fxx", mockJsonPutRequestToken.getUrl());
    }

    @Test
    public void tstUssSetAclModifySuccess() throws ZosmfRequestException {
        final UssSetAcl ussSetAcl = new UssSetAcl(connection, mockJsonPutRequest);
        final Response response = ussSetAcl.modify("/xxx/xx", "g::r-x");
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Fxxx%2Fxx", mockJsonPutRequest.getUrl());

        final Map<String, Object> setAclMap = new HashMap<>();
        setAclMap.put("request", "setfacl");
        setAclMap.put("links", LinkType.FOLLOW.getValue());
        setAclMap.put("modify", "g::r-x");
        verify(mockJsonPutRequest).setBody(new JSONObject(setAclMap).toString());
    }

    @Test
    public void tstUssSetAclDeleteSuccess() throws ZosmfRequestException {
        final UssSetAcl ussSetAcl = new UssSetAcl(connection, mockJsonPutRequest);
        final Response response = ussSetAcl.delete("/xxx/xx", "u:ibmuser:rwx");
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Fxxx%2Fxx", mockJsonPutRequest.getUrl());

        final Map<String, Object> setAclMap = new HashMap<>();
        setAclMap.put("request", "setfacl");
        setAclMap.put("links", LinkType.FOLLOW.getValue());
        setAclMap.put("delete", "u:ibmuser:rwx");
        verify(mockJsonPutRequest).setBody(new JSONObject(setAclMap).toString());
    }

    @Test
    public void tstUssSetAclDeleteTypeSuccess() throws ZosmfRequestException {
        final UssSetAcl ussSetAcl = new UssSetAcl(connection, mockJsonPutRequest);
        final Response response = ussSetAcl.deleteByType("/xxx/xx", DeleteAclType.ACCESS);
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Fxxx%2Fxx", mockJsonPutRequest.getUrl());

        final Map<String, Object> setAclMap = new HashMap<>();
        setAclMap.put("request", "setfacl");
        setAclMap.put("links", LinkType.FOLLOW.getValue());
        setAclMap.put("delete-type", DeleteAclType.ACCESS.getValue());
        verify(mockJsonPutRequest).setBody(new JSONObject(setAclMap).toString());
    }

    @Test
    public void tstUssSetAclSecondaryConstructorWithValidRequestType() {
        ZosConnection connection = Mockito.mock(ZosConnection.class);
        ZosmfRequest request = Mockito.mock(PutJsonZosmfRequest.class);
        UssSetAcl ussSetAcl = new UssSetAcl(connection, request);
        assertNotNull(ussSetAcl);
    }

}
