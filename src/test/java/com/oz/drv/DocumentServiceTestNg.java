package com.oz.drv;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-beans.xml")
public class DocumentServiceTestNg extends AbstractTestNGSpringContextTests {
	private static final Logger logger = Logger.getLogger(DocumentServiceTestNg.class);
	private static final String FILE_PATH = "testing.test";

	@Autowired
	private DocumentService docSvc;

	private String docSvcFileId = null;
	
	@Test(enabled=false)
	public void uploadTest() throws Exception {
		final String mimeType = "text/plain"; //upload plain text
		
		final java.io.File f = new java.io.File(ClassLoader.getSystemResource(FILE_PATH).toURI());
		
		docSvcFileId = docSvc.upload(f, mimeType);
		
		Assert.assertNotNull("No File Id received!", docSvcFileId);
		logger.debug("Received file id = " + docSvcFileId);
	}

	@Test(enabled=false, dependsOnMethods = { "uploadTest" })
	public void downloadTest() throws Exception {
		Assert.assertNotNull("No File Id found to use for download!", docSvcFileId);
		
		final String mimeType = "application/pdf"; //converting to pdf
		
		final InputStream is = docSvc.download(docSvcFileId, mimeType);
		Assert.assertNotNull("Input stream not opened!", is);
		final ReadableByteChannel rbc = Channels.newChannel(is);
		
		final FileOutputStream fos = new FileOutputStream(ClassLoader.getSystemResource(FILE_PATH).getPath() + ".pdf");
		try {
			final FileChannel fosc = fos.getChannel();
			final long bt = fosc.transferFrom(rbc, 0, Long.MAX_VALUE);
			Assert.assertTrue(bt > 0);
		}
		finally {
			fos.close();
			is.close();
		}
	}

	@Test(enabled=false, dependsOnMethods = { "uploadTest", "downloadTest" }, alwaysRun = true)
	public void deleteTest() throws Exception {
		Assert.assertNotNull("No File Id found to use for deletion!", docSvcFileId);
		
		docSvc.delete(docSvcFileId);
	}
	
	@Test(enabled=true)
	public void resumableUploadIso() throws Exception
	{
		final String mimeType = "application/octet-stream"; //upload iso file
		
		final java.io.File f = new java.io.File("/media/ourbookworld/VM/small-vm.iso");
		
		docSvcFileId = docSvc.upload(f, mimeType);
		
		logger.debug("Received file id = " + docSvcFileId);
	}
}
