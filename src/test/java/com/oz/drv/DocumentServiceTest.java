package com.oz.drv;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.oz.drv.api.GoogDrvSvcProxy;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class DocumentServiceTest
{
	private static final String FILE_PATH = "testing.test";

	@Mock(answer=Answers.RETURNS_MOCKS)
	private GoogDrvSvcProxy mockGdsp;
	@InjectMocks
	private DocumentService ds = new DocumentServiceImpl();

	@Before
	public void setUp() throws Exception {
		Assert.assertNotNull(mockGdsp);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testUpload() throws Exception {
		final String mimeType = "text/plain";
		final File f = new File(ClassLoader.getSystemResource(FILE_PATH).toURI());

		final String expectedFileId = "someExceptedFileId";
		when(mockGdsp.insert(anyString(), any(File.class), anyString())).thenReturn(expectedFileId);

		final String fileId = ds.upload(f, mimeType);

		verify(mockGdsp).insert(anyString(), eq(f), eq(mimeType));
		Assert.assertEquals("File Id", expectedFileId, fileId);
	}

	@Test
	public final void testDownload() throws Exception {
		final String mimeType = "application/pdf";
		final InputStream mockIs = mock(InputStream.class);
		when(mockGdsp.get(anyString(), anyString(), anyString())).thenReturn(mockIs);

		final InputStream is = ds.download(FILE_PATH, mimeType);

		verify(mockGdsp).get(anyString(), eq(FILE_PATH), eq(mimeType));
		Assert.assertEquals("Input stream", mockIs, is);
	}

	@Test
	public final void testDelete() throws Exception {
		ds.delete(FILE_PATH);

		verify(mockGdsp).delete(anyString(), eq(FILE_PATH));
	}
}
