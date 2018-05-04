package OLink.bpm.util.ftp;

import junit.framework.TestCase;

public class FTPUploadTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFTP() throws Exception {
		FTPUpload ftp = new FTPUpload();
		ftp.login("192.168.0.104", "21", "test", "test");
		ftp.createFolder("testFolder");
	}
}
