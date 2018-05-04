package OLink.bpm.core.page.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.BaseFormProcess;

public interface PageProcess extends BaseFormProcess<Page> {
	/**
	 * 获取应用的默认页
	 * 
	 * @param application
	 *            应用标识
	 * @return 页
	 * @throws Exception
	 */
	Page getDefaultPage(String application) throws Exception;

	/**
	 * 根据页名称的和应用标识查询页
	 * 
	 * @param name
	 *            页名称
	 * @param application
	 *            应用标识
	 * @return 页
	 * @throws Exception
	 */
	Page doViewByName(String name, String application) throws Exception;

	/**
	 * 根据参数查询页,并对页的记录给予分页, 分布类(OLink.bpm.base.dao,DataPackage)
	 * 
	 * @param params
	 *            参数
	 * @param application
	 *            应用标识
	 * @return 数据集合()
	 * @throws Exception
	 */
	DataPackage<Page> doListExcludeMod(ParamsTable params, String application) throws Exception;

	/**
	 * 获取应用所属应用的下的所有页
	 * 
	 * @param application
	 *            应用标识
	 * @return 页集合
	 * @throws Exception
	 */
	Collection<Page> getPagesByApplication(String application) throws Exception;
}
