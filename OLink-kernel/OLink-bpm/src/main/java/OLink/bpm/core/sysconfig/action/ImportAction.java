package OLink.bpm.core.sysconfig.action;

import java.io.File;

import OLink.bpm.core.sysconfig.util.ImportUtil;

import com.opensymphony.xwork.ActionSupport;

public class ImportAction extends ActionSupport {

	private static final long serialVersionUID = -8566223843186650982L;

	private File xmlConfig;
	private String xmlConfigContentType;
	private String xmlConfigFileName;

	public File getXmlConfig() {
		return xmlConfig;
	}

	public void setXmlConfig(File xmlConfig) {
		this.xmlConfig = xmlConfig;
	}

	public String getXmlConfigContentType() {
		return xmlConfigContentType;
	}

	public void setXmlConfigContentType(String xmlConfigContentType) {
		this.xmlConfigContentType = xmlConfigContentType;
	}

	public String getXmlConfigFileName() {
		return xmlConfigFileName;
	}

	public void setXmlConfigFileName(String xmlConfigFileName) {
		this.xmlConfigFileName = xmlConfigFileName;
	}

	public String doImport() {
		if (!validateImport())
			return INPUT;
		try {
			ImportUtil.load(this.xmlConfig);
			this.addActionMessage("{*[expimp.import.successful]*}");
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("import.error", e.getMessage());
			return INPUT;
		}
	}

	private boolean validateImport() {
		if (this.xmlConfig == null) {
			this.addFieldError("empty.file", "{*{[empty.file.to.import]}*}");
			return false;
		}
		return true;
	}

}
