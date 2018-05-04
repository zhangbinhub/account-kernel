package OLink.bpm.core.upload.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.util.property.DefaultProperty;

/**
 * 上传文件的信息
 * 
 * @author Nicholas
 * 
 */
public class UploadInfo extends ValueObject {
	public final static String FILE_SAVE_MODE_SYSTEM = "00";

	public final static String FILE_SAVE_MODE_CUSTOM = "01";

	/**
	 * 
	 */
	private static final long serialVersionUID = -4580547834982352884L;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件保存模式
	 */
	private String fileSaveMode;

	/**
	 * 文件类型
	 */
	private String contentType;

	/**
	 * 允许的类型
	 */
	private String allowedTypes;

	/**
	 * 最大限制
	 */
	private int maximumSize = Integer.MAX_VALUE;

	/**
	 * 系统文件夹路径
	 */
	private String path;

	private Environment env = Environment.getInstance();

	private String fileSaveName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件保存目录
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFileDir() {
		try {
			String dir = DefaultProperty.getProperty(getPath());
			if (fileSaveMode.equals(FILE_SAVE_MODE_CUSTOM)) {
				dir = "/uploads/" + path + "/";
			}

			return dir;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取文件真实保存目录
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFileRealDir() {
		return env.getRealPath(getFileDir());
	}

	/**
	 * 获取文件真实保存路径
	 * 
	 * @return
	 */
	public String getFileRealSavePath() {
		return getFileRealDir() + getFileSaveName();
	}

	public String getFileSaveMode() {
		return fileSaveMode;
	}

	public void setFileSaveMode(String fileSaveMode) {
		this.fileSaveMode = fileSaveMode;
	}

	/**
	 * 获取文件保存名称
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFileSaveName() {
		try {
				//旧文件上传
				//int index = fileName.lastIndexOf(".");
				//String suffix = fileName.substring(index, fileName.length());// 获取后缀
				//this.fileSaveName = Tools.getSequence() + suffix;
				this.fileSaveName = fileName;

			return fileSaveName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAllowedTypes() {
		return allowedTypes;
	}

	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	/**
	 * 获取文件网络路径
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getWebPath() {
		try {
			String webPath = getFileDir() + getFileSaveName();
			return webPath;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
