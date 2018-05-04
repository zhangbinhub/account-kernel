package OLink.bpm.init;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;

/**
 * 更新RT动态表数据库结构
 * 
 * @author nicholas
 * 
 */
public class InitTable {

	public static void run() {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			// 更新软件基础表数据库结构
			DataPackage<ApplicationVO> appDataPackage = applicationProcess.doQuery(new ParamsTable());
			if (appDataPackage.rowCount > 0) {
				for (Iterator<ApplicationVO> iterator = appDataPackage.datas.iterator(); iterator.hasNext();) {
					ApplicationVO app = iterator.next();
					try {
						applicationProcess.doUpdate(app);

						// 更新动态表数据库结构
						Collection<Form> formList = formProcess.doSimpleQuery(new ParamsTable(), app.getId());
						if (formList.size() > 0) {
							for (Iterator<Form> iterator2 = formList.iterator(); iterator.hasNext();) {
								Form form = iterator2.next();
								try {
									if (form.getType() == Form.FORM_TYPE_NORMAL) {
										formProcess.doUpdate(form);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						PersistenceUtils.closeSessionAndConnection();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		run();
	}

}
