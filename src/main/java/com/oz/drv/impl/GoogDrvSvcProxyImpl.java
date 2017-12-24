package com.oz.drv.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploader.UploadState;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Insert;
import com.oz.drv.api.GoogDrvAuthProxy;
import com.oz.drv.api.GoogDrvSvcProxy;

public class GoogDrvSvcProxyImpl implements GoogDrvSvcProxy
{
	private static final Logger logger = Logger.getLogger(GoogDrvSvcProxyImpl.class);
	
	private final String appName;
	private final GoogDrvAuthProxy drvAuthProxy;

	public GoogDrvSvcProxyImpl(final String appName, final GoogDrvAuthProxy drvAuthProxy)
	{
		this.appName = appName;
		this.drvAuthProxy = drvAuthProxy;
	}

	private Drive build(final HttpTransport transport, final Credential credential) {
		return new Drive.Builder(transport, drvAuthProxy.getJsonFactory(), credential)
			.setApplicationName(appName)
			.build();
	}

	@Override
	public String insert(final String user, final File file, final String mimeType) throws IOException, GeneralSecurityException {
		final HttpTransport transport = drvAuthProxy.getTransport();
		final Credential cred = drvAuthProxy.authorize(user);

		final InputStreamContent mediaContent = new InputStreamContent(mimeType, new BufferedInputStream(new FileInputStream(file)));
		mediaContent.setLength(file.length());

		final com.google.api.services.drive.model.File content = new com.google.api.services.drive.model.File()
			.setTitle(file.getName())
			.setMimeType(mimeType)
			.setFileSize(file.length());
		;

		final Insert insert = build(transport, cred)
			.files()
			.insert(content, mediaContent)
			//.setConvert(true)
			.set("uploadType", "resumable")
			;
		
		insert.getMediaHttpUploader()
			.setDirectUploadEnabled(false)
			.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE)
			.setProgressListener(new MediaHttpUploaderProgressListener() {
				private int lastProgressPerc = 0;
				@Override
				public void progressChanged(MediaHttpUploader uploader) throws IOException {
					final UploadState uploadState = uploader.getUploadState();
					switch(uploadState) {
					case MEDIA_IN_PROGRESS:
					case MEDIA_COMPLETE:
						final int progressPerc = (int) (uploader.getProgress()*100);
						if (lastProgressPerc < progressPerc) {
							logger.debug('['+uploadState.name()+']'+" uploaded "+uploader.getNumBytesUploaded()+" bytes so far..."
									+"Current progress: "+ progressPerc +'%');
							lastProgressPerc = progressPerc;
						}
						break;
					default:
						logger.debug('['+uploadState.name()+']');
						break;
					}
				}
			})
			;
		
		return insert
			.execute()
			.getId()
		;
	}

	@Override
	public InputStream get(final String user, final String fileId, final String mimeType) throws IOException, GeneralSecurityException {
		final HttpTransport transport = drvAuthProxy.getTransport();
		final Credential cred = drvAuthProxy.authorize(user);
		final String downloadUrl = build(transport, cred)
			.files()
			.get(fileId)
			.execute()
			.getExportLinks()
			.get(mimeType)
		;
		if (null == downloadUrl) {
			return null;
		}
		return build(transport, cred)
			.getRequestFactory()
			.buildGetRequest(new GenericUrl(downloadUrl))
			.execute()
			.getContent()
		;
	}

	@Override
	public void delete(final String user, final String fileId) throws IOException, GeneralSecurityException {
		final HttpTransport transport = drvAuthProxy.getTransport();
		final Credential cred = drvAuthProxy.authorize(user);
		build(transport, cred)
		.files()
		.delete(fileId)
		.execute()
		;
	}
}
