package OLink.bpm.core.email.attachment.action;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.core.email.attachment.ejb.AttachmentProcess;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.util.AttachmentUtil;
import OLink.bpm.core.email.util.EmailConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

/**
 * 邮件附件上传
 * @author Tom
 *
 */
public class AttachmentServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(AttachmentServlet.class);
	
	/**所有文件的总大小*/
	public static final long ATTACHMENT_MAX_SIZE = EmailConfig.getInteger("attachment.max.size", 50 * 1000 * 1024); // 50M
	/**每次最多上传文件个数*/
	public static final long ATTACHMENT_MAX_COUNT = EmailConfig.getInteger("attachment.max.count", 3); // 5
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		try {
			if (isMultipart) {
				processFileUpload(request, response);
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * 处理文件上传
	 * @SuppressWarnings ServletFileUpload.parseRequest方法不支持泛型
	 * @see ServletFileUpload#parseRequest
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void processFileUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Attachment attachment = new Attachment();
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存缓冲区，超过后写入临时文件
		factory.setSizeThreshold(1024 * 1000 * 10);
		// 设置临时文件存储位置
		File tempFile = AttachmentUtil.createAttachmentTempFile();
		factory.setRepository(tempFile);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置单个文件的最大上传值
		upload.setFileSizeMax(ATTACHMENT_MAX_SIZE);
		// 设置整个request的最大值
		upload.setSizeMax(ATTACHMENT_MAX_SIZE);
		try {
			StringBuffer json = new StringBuffer();
			List<FileItem> items = upload.parseRequest(request);
			// 处理文件上传
			for (int i = 0; i < items.size(); i++) {
				json.reverse();
				FileItem item = items.get(i);
				if (item.isFormField() || StringUtil.isBlank(item.getName())) {
					continue;
				}
				// 保存文件
				String fileName = takeOutFileName(item.getName());
				attachment.setRealFileName(fileName);
				File file = AttachmentUtil.createAttachmentFile(fileName);
				attachment.setFileName(file.getName());
				attachment.setSize(item.getSize());
				attachment.setPath(file.getPath());
				item.write(file);
				this.saveAttachment(attachment);
				json.append(attachment.getId()).append(":");
				json.append(attachment.getRealFileName());
				response.getWriter().print(json.toString());
			}
			return;
		} catch (FileUploadException e) {
			LOG.warn(e);
		} catch (Exception e) {
			LOG.warn(e);
		}
		
		response.getWriter().print("error");
	}
	
	private void saveAttachment(Attachment attachment) throws Exception {
		AttachmentProcess process = (AttachmentProcess) ProcessFactory.createProcess(AttachmentProcess.class);
		process.doCreate(attachment);
		
	}
	
	/**
	 * 从文件路径中取出文件名
	 */
	private String takeOutFileName(String filePath) {
//		int pos = filePath.lastIndexOf(File.separator);
		int pos = filePath.lastIndexOf("/");
		if (pos > 0) {
			return filePath.substring(pos + 1);
		} else {
			return filePath;
		}
	}
	
}
