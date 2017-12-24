package com.oz.drv;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.oz.drv.api.GoogDrvSvcProxy;

@Component("documentService")
public class DocumentServiceImpl implements DocumentService
{
	private static final Logger logger = Logger.getLogger(DocumentServiceImpl.class);

	@Autowired
	private GoogDrvSvcProxy drvSvcProxy;

	@Value("${com.oz.drv.user}")
	private String user;

	@PostConstruct
	public void init() {
		if (null == user) {
			logger.error("No user specified to access google drive!!");
		}
		if (null == drvSvcProxy) {
			logger.error("Document handler's google drive service builder not wired!!");
		}
	}

	@Override
	public String upload(final java.io.File file, final String mimeType) {
		try {
			return drvSvcProxy.insert(user, file, mimeType);
		}
		catch (IOException | GeneralSecurityException e) {
			logger.error("Uploading document failed!!", e);
			return null;
		}
	}

	@Override
	public InputStream download(final String fileId, final String mimeType) {
		try {
			return drvSvcProxy.get(user, fileId, mimeType);
		}
		catch (IOException | GeneralSecurityException e) {
			logger.error("Downloading document failed!!", e);
			return null;
		}
	}

	@Override
	public void delete(final String fileId) {
		try {
			drvSvcProxy.delete(user, fileId);
		}
		catch (IOException | GeneralSecurityException e) {
			logger.error("Deleting document failed!!", e);
		}
	}
}
