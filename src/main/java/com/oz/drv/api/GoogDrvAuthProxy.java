package com.oz.drv.api;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public interface GoogDrvAuthProxy {

	public Credential authorize(String user) throws IOException, GeneralSecurityException;

	public HttpTransport getTransport() throws GeneralSecurityException, IOException;

	public JsonFactory getJsonFactory();

}