package com.oz.drv;

import java.io.InputStream;

public interface DocumentService {

	public String upload(java.io.File file, String mimeType);

	public InputStream download(String fileId, String mimeType);

	public void delete(String fileId);
}