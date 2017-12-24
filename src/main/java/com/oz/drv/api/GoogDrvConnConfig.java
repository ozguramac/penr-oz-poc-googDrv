package com.oz.drv.api;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStoreFactory;

public interface GoogDrvConnConfig {

	public HttpTransport getTransport() throws GeneralSecurityException, IOException;

	public DataStoreFactory getDataStoreFactory() throws IOException;

	public Collection<String> getScopes();

	public JsonFactory getJsonFactory();

}