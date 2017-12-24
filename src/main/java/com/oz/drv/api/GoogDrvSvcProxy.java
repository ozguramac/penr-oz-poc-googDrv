package com.oz.drv.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public interface GoogDrvSvcProxy {

	String insert(String user, java.io.File file, String mimeType) throws IOException, GeneralSecurityException;

	InputStream get(String user, String fileId, String mimeType) throws IOException, GeneralSecurityException;

	void delete(String user, String fileId) throws IOException, GeneralSecurityException;
}