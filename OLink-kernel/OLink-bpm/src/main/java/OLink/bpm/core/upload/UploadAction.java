package OLink.bpm.core.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.upload.ejb.UploadProcess;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.base.action.AbstractRunTimeAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.upload.ejb.UploadInfo;
import OLink.bpm.core.upload.ejb.UploadProcessBean;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import nl.justobjects.pushlet.util.Log;
import eWAP.core.Tools;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

import flex.messaging.util.URLDecoder;

//public class UploadAction extends ActionSupport implements Action {
public class UploadAction extends AbstractRunTimeAction<UploadVO> {

	private String applicationid;

	private static final long serialVersionUID = 6865609097678926341L;

	private File upload;

	private String uploadContentType;

	private String uploadFileName;

	private String path;

	private String id;

	private String viewid;

	private String uploadList_;

	private String fieldValue;

	private String newUploadFileName;

	private String webPath;

	private String allowedTypes;

	private String[] fileName;

	private double perc;

	private String layer;

	private String fileSaveMode = UploadInfo.FILE_SAVE_MODE_SYSTEM;

	private File[] file;

	private String fileFullName;

	public String getFileFullName() {
		return fileFullName;
	}

	public void setFileFullName(String fileFullName) {
		this.fileFullName = fileFullName;
	}

	public String getUploadList_() {
		return uploadList_;
	}

	public void setUploadList_(String uploadList_) {
		this.uploadList_ = uploadList_;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	private int maximumSize = Integer.MAX_VALUE;

	private static final String IMAGE_TYPES[] = { "image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/bmp",
			"application/pdf" };

	public double getPerc() {
		return perc;
	}

	public void setPerc(double perc) {
		this.perc = perc;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getViewid() {
		return viewid;
	}

	public void setViewid(String viewid) {
		this.viewid = viewid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	public String getAllowedTypes() {
		return allowedTypes;
	}

	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public String getPath() throws Exception {
		return this.path;
	}

	public void setPath(String path) throws Exception {
		this.path = path;
	}

	public String getNewUploadFileName() {
		return newUploadFileName;
	}

	public void setNewUploadFileName(String newUploadFileName) {
		this.newUploadFileName = newUploadFileName;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public File[] getFile() {
		return file;
	}

	public void setFile(File[] file) {
		this.file = file;
	}

	public void setFileName(String[] fileName) {
		this.fileName = fileName;
	}

	public String[] getFileName() {
		return fileName;
	}

	public UploadInfo getUploadInfo() {
		// 设置属性
		UploadInfo uploadInfo = new UploadInfo();
		uploadInfo.setAllowedTypes(getAllowedTypes());
		uploadInfo.setContentType(getUploadContentType());
		uploadInfo.setFileName(getUploadFileName());
		uploadInfo.setFileSaveMode(getFileSaveMode());
		uploadInfo.setMaximumSize(getMaximumSize());
		try {
			uploadInfo.setPath(getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return uploadInfo;
	}

	public String getFileSaveMode() {
		return fileSaveMode;
	}

	public void setFileSaveMode(String fileSaveMode) {
		this.fileSaveMode = fileSaveMode;
	}

	public IRunTimeProcess<UploadVO> getProcess() {
		return new UploadProcessBean(applicationid);
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 上传文件校验 1.校验上传文件是否存在 2.校验上传文件类型是否合法 3.校验上传文件大小是否合法
	 */
	public void validate() {
		Map<?, ?> map = ActionContext.getContext().getParameters();
		file = (File[]) map.get("EDITFILE");
		String[] wordForms = (String[]) map.get("EDITFILEFileName");
		// 通过是上传什么文件word不做验证
		if (file == null && wordForms == null) {
			uploadfileValidate();
		}
	}

	public void uploadfileValidate() {
		if (upload == null) {
			addFieldError("", "{*[upload.file.empty]*}");
		} else {
			if (!isAllowedType()) {
				addFieldError("", "{*[core.upload.notallow]*}GIF/PNG/JPG/BMP/PDF");
			}

			if (isTooLarge(upload)) {
				addFieldError("", "{*[core.upload.toolarge]*}:\"" + uploadFileName + "\",{*[MaximumSize]*}("
						+ (getMaximumSize() / 1024) + "KB)");
			}
		}
	}

	private boolean isAllowedType() {
		if (!StringUtil.isBlank(allowedTypes)) {
			if (allowedTypes.equalsIgnoreCase("image")) {
				for (int i = 0; i < IMAGE_TYPES.length; i++) {
					// String type = getUploadContentType();
					if (uploadContentType.equalsIgnoreCase(IMAGE_TYPES[i])) {
						return true;
					}
				}
			}
		} else {
			return true;
		}

		return false;
	}

	private boolean isTooLarge(File file) {
		return file.length() > getMaximumSize();
	}

	public String execute() throws Exception {

		return NONE;
	}

	public String getPercs() throws Exception {
		try {
			HttpSession session = ServletActionContext.getRequest().getSession();
			HttpServletResponse response = ServletActionContext.getResponse();
			PrintWriter writer = null;
			writer = response.getWriter();
			int perc = session.getAttribute("perc") != null ? ((Integer) session.getAttribute("perc"))
					.intValue() : 0;
			writer.println(perc);
			if (perc == 100) {
				session.setAttribute("perc", Integer.valueOf(0));
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NONE;
	}

	public String clearPerc() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("perc", Integer.valueOf(0));

		return SUCCESS;
	}

	/**
	 * 删除列表中所有文件
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doDelete() {
		try {
			if (!StringUtil.isBlank(fileFullName)) {
				fileFullName = URLDecoder.decode(fileFullName,"UTF-8");
				UploadProcess uploadProcess = (UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class, this.applicationid);
				String[] fileFullNameArry = fileFullName.split(";");
				for (int i = 0; i < fileFullNameArry.length; i++) {
					UploadVO uploadVO = uploadProcess.findByColumnName1("PATH", fileFullNameArry[i]);
					if (uploadVO != null) {
						String fileRealPath = getEnvironment().getRealPath(uploadVO.getPath());
						File file = new File(fileRealPath);
						if (file.exists()) {
							if (!file.delete()) {
								Log.warn("File(" + fileRealPath + ") delete failed");
								throw new Exception("File(" + fileRealPath + ") delete failed");
							}
						}
						uploadProcess.doRemove(uploadVO.getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return NONE;
		}
		return NONE;
	}

	/**
	 * 删除一个文件
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doDeleteOne() throws Exception {
		if (!StringUtil.isBlank(fileFullName)) {
			fileFullName = URLDecoder.decode(fileFullName,"UTF-8");
			UploadProcess uploadProcess = (UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class, this.applicationid);
			UploadVO uploadVO = uploadProcess.findByColumnName1("PATH", fileFullName);
			if (uploadVO != null) {
				String fileRealPath = getEnvironment().getRealPath(uploadVO.getPath());
				File file = new File(fileRealPath);
				if (file.exists()) {
					if (!file.delete()) {
						Log.warn("File(" + fileRealPath + ") delete failed");
						throw new Exception("File(" + fileRealPath + ") delete failed");
					}
				}
				uploadProcess.doRemove(uploadVO.getId());
			}
		}
		return NONE;
	}

	/**
	 *获得文件信息
	 * 
	 * @return
	 */
	public String doFileInfor() {
		if (!StringUtil.isBlank(fileFullName)) {
			try {
				fileFullName = URLDecoder.decode(fileFullName,"UTF-8");
				UploadProcess uploadProcess = (UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class, this.applicationid);
				SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
						.createProcess(SuperUserProcess.class);
				UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
				UploadVO uploadVO = uploadProcess.findByColumnName1("PATH", fileFullName);
				if (uploadVO == null) {
					uploadVO = uploadProcess.findByColumnName1("ID", fileFullName);
				}
				if (uploadVO != null) {
					ActionContext ctx = ActionContext.getContext();
					HttpServletResponse response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);
					StringBuffer sb = new StringBuffer();
					sb.append("<table><tr>");
					if (uploadVO.getSize() != 0) {
						double size = 0;
						if (uploadVO.getSize() < 1024) {
							size = uploadVO.getSize();
							sb.append("<td>{*[Size]*}:").append(size).append(" B </td>");
						} else if (uploadVO.getSize() < (1024 * 1024) && uploadVO.getSize() >= 1024) {
							size = (double)(uploadVO.getSize() / 1024);
							sb.append("<td>{*[Size]*}:").append(size).append(" KB </td>");
						} else {
							size = (double)(uploadVO.getSize() / (1024f * 1024));
							sb.append("<td>{*[Size]*}:").append(size).append(" M </td>");
						}
					}
					if (uploadVO.getType() != null) {
						sb.append("<td>{*[Type]*}:").append(uploadVO.getType()).append(" </td>");
					}
					if (uploadVO.getModifyDate() != null) {
						sb.append("<td>{*[Upload]*}{*[Date]*}:").append(uploadVO.getModifyDate()).append(" </td>");
					}
					if (uploadVO.getUserid() != null) {
						if (superUserProcess.doView(uploadVO.getUserid()) != null) {
							sb.append("<td>{*[Upload]*}{*[Personal]*}:").append(
									((SuperUserVO) superUserProcess.doView(uploadVO.getUserid())).getName()).append(
									" </td>");
						} else if (userProcess.doView(uploadVO.getUserid()) != null) {
							sb.append("<td>{*[Upload]*}{*[Personal]*}:").append(
									((UserVO) userProcess.doView(uploadVO.getUserid())).getName()).append(" </td>");
						}
					}
					sb.append("</tr></table>");
					response.setContentType("text/html;charset=UTF-8");
					response.getWriter().print(sb.toString());
					response.getWriter().close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return NONE;
			}
		}
		return NONE;
	}

	/**
	 * 删除数据库中的图片
	 * 
	 * @return
	 * @throws Exception
	 */
	public String deleteToDataBaseFile() throws Exception {
		if (!StringUtil.isBlank(fileFullName)) {
			fileFullName = URLDecoder.decode(fileFullName,"UTF-8");
			UploadProcess uploadProcess = (UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class, this.applicationid);
			if (fileFullName != null && !fileFullName.equals("")) {
				String[] fileFullNameArray = fileFullName.split(";");
				if (fileFullNameArray.length > 0) {
					for (int i = 0; i < fileFullNameArray.length; i++) {
						uploadProcess.doRemove(fileFullNameArray[i].split("_")[0]);
					}
				} else {
					uploadProcess.doRemove(fileFullName.split("_")[0]);
				}
			}
		}
		return NONE;
	}

	public Environment getEnvironment() {
		Environment evt = (Environment) ActionContext.getContext().getApplication().get(Environment.class.getName());

		return evt;
	}

	public String doUploadFile() {
		ParamsTable params = this.getParams();
		int type = Integer.parseInt(params.getParameterAsString("type"));
		String[] path = { "SECSIGN_PATH", "REDHEAD_DOCPATH", "TEMPLATE_DOCPATH" };
		String[] extensions = { "esp", "doc,docx", "doc.docx" };
		Boolean isse = params.getParameterAsBoolean("isse");

		try {
			String dir = DefaultProperty.getProperty(path[type]);
			String savePath = getEnvironment().getRealPath(dir);
			File uploadFile = null;

			if (isse) {
				uploadFile = new File(savePath + params.getParameterAsString("filename"));
			} else {
				String extension = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
				if (extensions[type].indexOf(extension.toLowerCase()) != -1) {
					uploadFile = new File(savePath + uploadFileName);
				} else {
					this.addActionMessage("{*[Invalid_File_Format]*}!");
					return INPUT;
				}
			}

			if (uploadFile.exists()) {
				if (!uploadFile.delete()) {
					throw new Exception("旧文件删除失败!");
				}
			}

			if (!upload.renameTo(uploadFile)) {
				throw new Exception("文件保存失败!");
			}

			if (isse) {
				this.addActionMessage("保存成功");
				return "saveSignSuccess";
			}
			this.addActionMessage("{*[Upload_File]*}{*[Success]*}!");
			return SUCCESS;

		} catch (Exception e) {
			this.addActionMessage("{*[Upload_File]*}{*[Fail]*}!");
			e.printStackTrace();
			return INPUT;
		}
	}

	public String dosaveWord() {
		FileInputStream input = null;
		FileOutputStream outputStream = null;
		try {
			String[] wordForms = (String[]) ActionContext.getContext().getParameters().get("EDITFILEFileName");// 默认文件名
			String filename = wordForms[0];
			File[] files = getFile();
			String dir = DefaultProperty.getProperty("WEB_DOCPATH");
			StringBuffer savePath = new StringBuffer(getEnvironment().getRealPath(dir));

			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (filename == null) {
						filename = Tools.getSequence();
					}

					String suffix = "";
					if (!(new File(savePath.toString()).isDirectory())) {
						if (!new File(savePath.toString()).mkdirs()) {
							Log.warn("Failed to create folder (" + savePath.toString() + ")");
							throw new Exception("Failed to create folder (" + savePath.toString() + ")");
						}
					}
					input = new FileInputStream(file);
					File docfile = new File(savePath + filename + suffix);
					if (docfile.exists()) {
						// File bak = new File(savePath + filename + "_bak"
						// + suffix);
						// if (bak.exists())
						// forceDelete(bak);
						forceDelete(docfile);
					}
					savePath.append(filename).append(suffix);
					outputStream = new FileOutputStream(savePath.toString(), true);

					byte[] buffer = new byte[1024];
					int len;
					while ((len = input.read(buffer)) > 0) {
						outputStream.write(buffer, 0, len);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
				if (outputStream != null)
					outputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public boolean forceDelete(File f) {
		boolean result = false;
		int tryCount = 0;
		while (!result && tryCount++ < 10) {
			// System.gc();
			result = f.delete();
		}
		return result;
	}

	public boolean forceRename(File f, File newFile) {
		boolean result = false;
		int tryCount = 0;
		while (!result && tryCount++ < 10) {
			// System.gc();
			result = f.renameTo(newFile);
		}
		return result;
	}
}
