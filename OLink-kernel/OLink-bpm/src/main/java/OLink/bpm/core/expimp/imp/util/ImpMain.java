package OLink.bpm.core.expimp.imp.util;

import java.io.File;

import OLink.bpm.core.expimp.imp.ejb.ImpProcess;
import OLink.bpm.core.expimp.imp.ejb.ImpProcessBean;
import OLink.bpm.core.expimp.imp.ejb.ImpSelect;
import OLink.bpm.util.StringUtil;

public class ImpMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = System.getProperty("filepath");
		String applicationid = System.getProperty("applicationid");
		try {
			if (!StringUtil.isBlank(applicationid)) {
				ImpSelect select = new ImpSelect();
				select.setImportType(ImpSelect.IMPORT_TYPE_APPLICATION);
				select.setApplicationid(applicationid);
				File importFile = new File(filePath);

				ImpProcess impProcess = new ImpProcessBean();
				impProcess.doImport(select, importFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
