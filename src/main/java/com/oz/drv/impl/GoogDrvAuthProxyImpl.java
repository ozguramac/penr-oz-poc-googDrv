package com.oz.drv.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.oz.drv.api.GoogDrvAuthProxy;
import com.oz.drv.api.GoogDrvConnConfig;

public class GoogDrvAuthProxyImpl implements GoogDrvAuthProxy
{
	/**
	 * Follow instructions at: 
	 * <a href="https://developers.google.com/drive/web/auth/web-server">Implementing Server-Side Authorization</a>
	 * Choose "Installed application" option.
	 */
	private static final String PATH_CLIENT_SECRET_JSON = "./client_secret.json";

	private final GoogDrvConnConfig drvConnConfig;

	public GoogDrvAuthProxyImpl(final GoogDrvConnConfig drvConnConfig)
	{
		this.drvConnConfig = drvConnConfig;
	}

	@Override
	public Credential authorize(final String user) throws IOException, GeneralSecurityException {
		final JsonFactory jsonFactory = drvConnConfig.getJsonFactory();
		final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory
				, new InputStreamReader(ClassLoader.getSystemResourceAsStream(PATH_CLIENT_SECRET_JSON)));

		final AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				 drvConnConfig.getTransport()
				,jsonFactory
				,clientSecrets
				,drvConnConfig.getScopes()
			)
			.setDataStoreFactory(drvConnConfig.getDataStoreFactory())
			.setAccessType("offline")
			.build();

		final VerificationCodeReceiver rcvr = new LocalServerReceiver();

		return new AuthorizationCodeInstalledApp(flow, rcvr).authorize(user);
	}

	@Override
	public HttpTransport getTransport() throws GeneralSecurityException, IOException {
		return drvConnConfig.getTransport();
	}

	@Override
	public JsonFactory getJsonFactory() {
		return drvConnConfig.getJsonFactory();
	}
}
