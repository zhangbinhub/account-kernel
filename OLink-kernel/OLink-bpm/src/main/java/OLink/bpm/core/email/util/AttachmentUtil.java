package OLink.bpm.core.email.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @author Tom
 *
 */
public final class AttachmentUtil {
	
	private static final Logger LOG = Logger.getLogger(AttachmentUtil.class);
	
	public static Attachment saveAttachmentFile(String fileName, InputStream inputStream) throws Exception {
		BufferedOutputStream bufferedOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			File file = createAttachmentFile(fileName);
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
			bufferedInputStream = new BufferedInputStream(inputStream);
			int line = 0;
			while ((line = bufferedInputStream.read()) != -1) {
				bufferedOutputStream.write(line);
				bufferedOutputStream.flush();
			}
			Attachment attachment = new Attachment();
			attachment.setFileName(fileName);
//			attachment.setPath(getAttachmentDir() + File.separator + file.getName());
			attachment.setPath(getAttachmentDir() + "/" + file.getName());
			//System.out.println(attachment.getPath());
			return attachment;
		} catch (Exception exception) {
			LOG.error(exception.toString());
			throw exception;
		} finally {
			if (bufferedOutputStream != null) {
				bufferedOutputStream.close();
			}
			if (bufferedInputStream != null) {
				bufferedInputStream.close();
			}
		}
	}
	
	public static File createAttachmentFile(String fileName) throws Exception {
		try {
//			File file = new File(getAttachmentDir() + File.separator + fileName);
			File file = new File(getAttachmentDir() + "/" + fileName);
			for (int i = 0; file.exists(); i++) {
//				file = new File(getAttachmentDir() + File.separator + countFileName(fileName, i));
				file = new File(getAttachmentDir() + "/" + countFileName(fileName, i));
			}
			if (!file.createNewFile()) {
				throw new Exception("Can't create file！");
			}
			return file;
		} catch (Exception e) {
			LOG.error(e.toString());
			throw e;
		}
	}
	
	private static String countFileName(String fileName, int count) {
		int spx = fileName.lastIndexOf('.');
		String name = fileName.substring(0, spx);
		String type = fileName.substring(spx + 1);
		return name + String.valueOf(count) + "." + type;
	}
	
	public static String getAttachmentDir() throws Exception {
//		String attachmentDir = EmailConfig.getString("attachment.dir", File.separator + "email" + File.separator + "attachment");
		String attachmentDir = EmailConfig.getString("attachment.dir", "/" + "email" + "/" + "attachment");
		if (StringUtil.isBlank(attachmentDir)) {
			//LOG.error("系统找不到邮件存放路径！");
			throw new FileNotFoundException("E-mail storage system can not find the path!");
		}
		String osName = System.getProperty("os.name");
		if (attachmentDir.indexOf(':') >= 0
				&& osName.toLowerCase().indexOf("win") != -1) {
			File result = new File(attachmentDir);
			if (!result.exists()) {
				if (!result.mkdirs()) {
					LOG.error("系统无法创建邮件存放路径！");
					throw new Exception("The system can not create mail storage path!");
				}
			}
			return attachmentDir;
		} else {
			String webPath = Environment.getInstance().getRealPath(attachmentDir);
			File result = new File(attachmentDir);
			if (!result.exists()) {
				if (!result.mkdirs()) {
					throw new Exception("System can't create file！");
				}
			}
			return webPath;
		}
	}
	
	public static File createAttachmentTempFile() throws Exception {
		File tempFile = new File(AttachmentUtil.getAttachmentDir() + "/temp");
		if (!tempFile.exists()) {
			if (!tempFile.mkdirs()) {
				LOG.warn("Can not create folder！");
				throw new IOException("Can not create folder!");
			}
		}
		return tempFile;
	}
	
	public static void removeAttachmentFile(String fileName) {
		try {
//			File file = new File(getAttachmentDir() + File.separator + fileName);
			File file = new File(getAttachmentDir() + "/" + fileName);
			if (file.exists()) {
				if (file.delete()) {
					LOG.info("Delete file: " + file.getPath() + " success");
				}
			}
		} catch (Exception e) {
			LOG.warn(e);
		}
	}
	
}
