package OLink.bpm.core.dynaform.view.ejb;

import java.util.Map;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

public interface ViewType {
	long countViewDatas(ParamsTable params, WebUser user, Document sDoc) throws Exception;

	/**
	 * 获取视图数据
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档数据包
	 * @throws Exception
	 */
	DataPackage<Document> getViewDatas(ParamsTable params, WebUser user, Document sDoc) throws Exception;

	DataPackage<Document> getViewDatasPage(ParamsTable params, int page, int lines, WebUser user, Document sDoc)
			throws Exception;
	DataPackage<Document> getViewDatas(ParamsTable params, int page, int lines, WebUser user, Document sdoc)
	throws Exception;

	/**
	 * 视图类型int值
	 * 
	 * @return 类型int值
	 */
	int intValue();

	Map<String, Column> getColumnMapping();
}
