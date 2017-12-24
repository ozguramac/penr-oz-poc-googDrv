package com.oz.drv.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.oz.drv.api.GoogDrvConnConfig;

public class DefaultGoogDrvConnConfig implements GoogDrvConnConfig {

	/*
	 * Directory path to store user credentials.
	 */
	private static final String DATA_STORE_DIR_PATH = ".credentials/drive-api";

	/*
	 * Scopes for authorization flow 
	 */
	private final Collection<String> SCOPES = Arrays.asList(
		DriveScopes.DRIVE_FILE //Per-file access to files created or opened by the app
	);

	private HttpTransport transport = null;
	private DataStoreFactory dataStoreFactory = null;

	public DefaultGoogDrvConnConfig() {
		
	}

	@Override
	public HttpTransport getTransport() throws GeneralSecurityException, IOException {
		if (null == transport) {
			transport = GoogleNetHttpTransport.newTrustedTransport();
		}
		return transport;
	}

	@Override
	public DataStoreFactory getDataStoreFactory() throws IOException {
		if (null == dataStoreFactory) {
			dataStoreFactory = new FileDataStoreFactory(
				new java.io.File(System.getProperty("user.home"), DATA_STORE_DIR_PATH)
			);
		}
		return dataStoreFactory;
	}

	@Override
	public Collection<String> getScopes() {
		return SCOPES;
	}

	@Override
	public JsonFactory getJsonFactory() {
		return JacksonFactory.getDefaultInstance();
	}
}
