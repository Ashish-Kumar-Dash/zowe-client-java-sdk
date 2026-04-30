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
import zowe.client.sdk.zosfiles.uss.input.UssGetAclInputData;
import zowe.client.sdk.zosfiles.uss.types.GetAclType;

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
 * Class containing unit tests for UssGetAcl.
 *
 * @author Ashish Kumar Dash
 * @version 6.0
 */
public class UssGetAclTest {

    private final ZosConnection connection = ZosConnectionFactory
            .createBasicConnection("1", 443, "1", "1");
    private final ZosConnection tokenConnection = ZosConnectionFactory
            .createTokenConnection("1", 443, new Cookie("hello=hello"));
    private PutJsonZosmfRequest mockJsonPutRequest;
    private PutJsonZosmfRequest mockJsonPutRequestToken;
    private UssGetAcl ussGetAcl;

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

        ussGetAcl=new UssGetAcl(connection);
    }

    @Test
    public void tstUssGetAclCommonSuccess() throws ZosmfRequestException {
        final UssGetAcl ussGetAcl = new UssGetAcl(connection, mockJsonPutRequest);
        final Response response = ussGetAcl.getAclCommon("/test",
                new UssGetAclInputData.Builder().build());
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Ftest", mockJsonPutRequest.getUrl());

        final Map<String, Object> getAclMap = new HashMap<>();
        getAclMap.put("request", "getfacl");
        verify(mockJsonPutRequest).setBody(new JSONObject(getAclMap).toString());
    }

    @Test
    public void tstUssGetAclCommonWithParamsSuccess() throws ZosmfRequestException {
        final UssGetAcl ussGetAcl = new UssGetAcl(connection, mockJsonPutRequest);
        final UssGetAclInputData inputData = new UssGetAclInputData.Builder()
                .type(GetAclType.DIR)
                .user("IBMUSER")
                .usecommas(true)
                .suppressheader(true)
                .suppressbaseacl(true)
                .build();
        final Response response = ussGetAcl.getAclCommon("/test", inputData);
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Ftest", mockJsonPutRequest.getUrl());

        final Map<String, Object> getAclMap = new HashMap<>();
        getAclMap.put("request", "getfacl");
        getAclMap.put("type", GetAclType.DIR.getValue());
        getAclMap.put("user", "IBMUSER");
        getAclMap.put("use-commas", true);
        getAclMap.put("suppress-header", true);
        getAclMap.put("suppress-baseacl", true);
        verify(mockJsonPutRequest).setBody(new JSONObject(getAclMap).toString());
    }

    @Test
    public void tstUssGetAclToggleTokenSuccess() throws ZosmfRequestException {
        final UssGetAcl ussGetAcl = new UssGetAcl(tokenConnection, mockJsonPutRequestToken);
        final Response response = ussGetAcl.getAclCommon("/test",
                new UssGetAclInputData.Builder().build());
        assertEquals("{X-CSRF-ZOSMF-HEADER=true, Content-Type=application/json}",
                mockJsonPutRequestToken.getHeaders().toString());
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
        assertEquals("https://1:443/zosmf/restfiles/fs%2Ftest", mockJsonPutRequestToken.getUrl());
    }

    @Test
    public void tstUssGetAclSecondaryConstructorWithValidRequestType() {
        ZosConnection connection=Mockito.mock(ZosConnection.class);
        ZosmfRequest request=Mockito.mock(PutJsonZosmfRequest.class);
        UssGetAcl ussGetAcl=new UssGetAcl(connection, request);
        assertNotNull(ussGetAcl);
    }

}
