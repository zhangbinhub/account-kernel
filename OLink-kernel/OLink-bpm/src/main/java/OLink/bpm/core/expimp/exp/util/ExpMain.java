package OLink.bpm.core.expimp.exp.util;

import OLink.bpm.core.expimp.exp.ejb.ExpSelect;
import OLink.bpm.core.expimp.exp.ejb.ExpProcess;
import OLink.bpm.core.expimp.exp.ejb.ExpProcessBean;
import OLink.bpm.util.StringUtil;

public class ExpMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = System.getProperty("filepath");
		String applicationid = System.getProperty("applicationid");
		try {
			if (!StringUtil.isBlank(applicationid)) {
				ExpSelect select = new ExpSelect();
				select.setApplicationid(applicationid);
				select.setExportType(ExpSelect.EXPROT_TYPE_APPLICATION);

				ExpProcess expProcess = new ExpProcessBean();
				expProcess.createZipFile(select, filePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
