package OLink.bpm.core.expimp.exp.ejb;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpSelect implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2434845164761482767L;
	public final static int EXPROT_TYPE_APPLICATION = 0x00001;
	public final static int EXPROT_TYPE_MODULE = 0x00010;
	public final static int EXPROT_TYPE_MODULE_ELEMENTS = 0x00100;

	private String[] views = new String[0];
	private String[] forms = new String[0];
	private String[] workflows = new String[0];
	private String[] crossReports = new String[0];
	private String[] printers = new String[0]; // 打印配置

	private String moduleid;
	private String applicationid;
	private int exportType;


	public String[] getPrinters() {
		return printers;
	}

	public void setPrinters(String[] printers) {
		this.printers = printers;
	}

	public String[] getViews() {
		return views;
	}

	public void setViews(String[] views) {
		this.views = views;
	}

	public String[] getForms() {
		return forms;
	}

	public void setForms(String[] forms) {
		this.forms = forms;
	}

	public String[] getWorkflows() {
		return workflows;
	}

	public void setWorkflows(String[] workflows) {
		this.workflows = workflows;
	}

	public int length() {
		return views.length + forms.length + workflows.length //+ tasks.length
				+ crossReports.length;
	}

	public String getModuleid() {
		return moduleid;
	}

	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	public int getExportType() {
		return exportType;
	}

	public void setExportType(int exportType) {
		this.exportType = exportType;
	}

	public boolean isEmpty() {
		return length() == 0;
	}

	/**
	 * 获取导出(类型-名称)映射
	 * 
	 * @return
	 */
	public static Map<Integer, String> getExportTypeNameMap() {
		Map<Integer, String> typeNameMap = new LinkedHashMap<Integer, String>();
		typeNameMap.put(0, "{*[Select]*}");
		typeNameMap.put(Integer.valueOf(EXPROT_TYPE_APPLICATION),
				"{*[Application]*}");
		typeNameMap.put(Integer.valueOf(EXPROT_TYPE_MODULE), "{*[Module]*}");
		typeNameMap.put(Integer.valueOf(EXPROT_TYPE_MODULE_ELEMENTS),
				"{*[Module]*}{*[Elements]*}");

		return typeNameMap;
	}

	public String[] getCrossReports() {
		return crossReports;
	}

	public void setCrossReports(String[] crossReports) {
		this.crossReports = crossReports;
	}

}
