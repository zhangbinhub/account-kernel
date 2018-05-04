package OLink.bpm.core.expimp.imp.ejb;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImpSelect implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2022983949004770335L;
	public final static int IMPORT_TYPE_APPLICATION = 0x00001;
	public final static int IMPORT_TYPE_MODULE = 0x00010;
	public final static int IMPORT_TYPE_MODULE_ELEMENTS = 0x00100;

	private String moduleid;
	private String applicationid;
	private int importType;

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

	public int getImportType() {
		return importType;
	}

	public void setImportType(int importType) {
		this.importType = importType;
	}

	/**
	 * 获取导入(类型-名称)映射
	 * 
	 * @return
	 */
	public static Map<Integer, String> getImportTypeNameMap() {
		Map<Integer, String> typeNameMap = new LinkedHashMap<Integer, String>();
		// typeNameMap.put("", "{*[Select]*}");
		typeNameMap.put(0, "{*[Select]*}");
		typeNameMap.put(Integer.valueOf(IMPORT_TYPE_APPLICATION),
				"{*[Application]*}");
		typeNameMap.put(Integer.valueOf(IMPORT_TYPE_MODULE), "{*[Module]*}");
		typeNameMap.put(Integer.valueOf(IMPORT_TYPE_MODULE_ELEMENTS),
				"{*[Module]*}{*[Elements]*}");

		return typeNameMap;
	}
}
