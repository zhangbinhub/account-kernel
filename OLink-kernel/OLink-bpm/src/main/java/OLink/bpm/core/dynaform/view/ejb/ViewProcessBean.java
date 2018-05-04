package OLink.bpm.core.dynaform.view.ejb;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.view.dao.ViewDAO;
import OLink.bpm.core.dynaform.view.ejb.type.CalendarType;
import OLink.bpm.core.links.dao.LinkDAO;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.document.dql.SQLFunction;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.resource.dao.ResourceDAO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.user.action.WebUser;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;

public class ViewProcessBean extends AbstractDesignTimeProcessBean<View> implements ViewProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2497021753868939721L;
	static final Logger log = Logger.getLogger(ViewProcessBean.class);

	/**
	 * @see IDesignTimeDAO#buildSessionFactory()
	 */
	protected IDesignTimeDAO<View> getDAO() throws Exception {
		return (ViewDAO) DAOFactory.getDefaultDAO(View.class.getName());
	}

	/**
	 * 根据视图名与应用标识查询,返回视图对象
	 * 
	 * @param name
	 *            视图名
	 * @param application
	 *            应用标识
	 * @return 视图对象
	 * @throws Exception
	 */
	public View getViewByName(String name, String application) throws Exception {
		View view = ((ViewDAO) getDAO()).findViewByName(name, application);
		return view;
	}

	/**
	 * 根据应用标识查询，返回视图集合
	 * 
	 * @param application
	 *            应用标识
	 * @return 视图集合
	 * @throws Exception
	 */
	public Collection<View> get_viewList(String application) throws Exception {
		return this.doSimpleQuery(null, application);
	}

	/**
	 * 根据模块主键与应用标识查询，返回视图(view)的DataPackage.
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)的DataPackage
	 * @throws Exception
	 */
	public DataPackage<View> getViewsByModuleId(String moduleid, String application) throws Exception {
		return ((ViewDAO) getDAO()).getViewsByModuleId(moduleid, application);
	}

	/**
	 * 根据模块主键与应用标识查询，返回视图(view)的集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)的集合
	 * @throws Exception
	 */
	public Collection<View> getViewsByModule(String moduleid, String application) throws Exception {
		return ((ViewDAO) getDAO()).getViewByModule(moduleid, application);
	}

	/**
	 * 把视图中显示的文档转换为Excel文件
	 * 
	 * @param viewid
	 * @param user
	 * @param params
	 *            {filename: 文件名, filedir: 文件所在目录, formfiled: 表单各字段值}
	 * @throws Exception
	 */
	public String expDocToExcel(String viewid, WebUser user, ParamsTable params) throws Exception {
		View view = (View) doView(viewid);// 查找出相应的视图

		String fileName = "";
		if (view != null) {
			ExcelFileBuilder builder = new ExcelFileBuilder(user);
			builder.buildSheet(view, params);
			File file = builder.toExcelFile();
			fileName = file.getName();

			return fileName;
		} else {
			throw new Exception("{*[View.is.not.exist]*}");
		}
	}

	/**
	 * 获得Excel的头
	 * 
	 * @param datas
	 * @param view
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getHeads(View view) throws Exception {
		Collection<String> heads = new ArrayList<String>();// excel头
		Collection<Column> columns = view.getColumns();

		for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
			Column col = iter.next();
			heads.add(col.getName());
		}

		return heads;
	}

	/**
	 * 
	 * @param view
	 *            视图
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return
	 */
	public String getQueryString(View view, ParamsTable params, WebUser user, Document sDoc) {
		try {
			return view.getEditModeType().getQueryString(params, user, sDoc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public DataPackage<Document> getDataPackage(View view, ParamsTable params, WebUser user, String applicationid,
			Date stDate, Date endDate, int lines) throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,applicationid);
		DataPackage<Document> datas = null;
		Document doc = null;
		String _currpage = params.getParameterAsString("_currpage");
		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		if (view.getSearchForm() != null) {
			try {
				doc = view.getSearchForm().createDocument(params, user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			doc = new Document();
		}
		SQLFunction sqlFuction = DbTypeUtil.getSQLFunction(applicationid);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fielter = "";
		// String fieldName = view.getRelationDateColum();
		Object column = view.getViewTypeImpl().getColumnMapping().get(CalendarType.DEFAULT_KEY_FIELDS[0]);
		String fieldName = "";
		if (column instanceof String)
			fieldName = column.toString();
		else if (column instanceof Column)
			fieldName = ((Column) column).getFieldName();

		if (fieldName.toUpperCase().trim().startsWith("$")) {
			fielter = sqlFuction.toChar(fieldName.substring(1), "yyyy-MM-dd HH:mm:ss") + ">= '" + format.format(stDate)
					+ "' and ";
			fielter += sqlFuction.toChar(fieldName.substring(1), "yyyy-MM-dd HH:mm:ss") + "<= '"
					+ format.format(endDate) + "'";
		} else {
			fielter = sqlFuction.toChar("ITEM_" + fieldName, "yyyy-MM-dd HH:mm:ss") + ">= '" + format.format(stDate)
					+ "' and ";
			fielter += sqlFuction.toChar("ITEM_" + fieldName, "yyyy-MM-dd HH:mm:ss") + "<= '" + format.format(endDate)
					+ "'";
		}

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		if (view.getEditMode().equals(View.EDIT_MODE_DESIGN)) {
			String sql = getQueryString(view, params, user, doc);
			sql = sqlUtil.appendCondition(sql, fielter);
			datas = dp.queryBySQLPage(sql, params, page, lines, user.getDomainid());
		} else if (view.getEditMode().equals(View.EDIT_MODE_CODE_DQL)) {
			String dql = getQueryString(view, params, user, doc);
			dql += " AND #T" + fieldName + ">= '" + format.format(stDate) + "' and #T" + fieldName + "<= '"
					+ format.format(endDate) + "'";
			datas = dp.queryByDQLPage(dql, params, page, lines, user.getDomainid());
		} else if (view.getEditMode().equals(View.EDIT_MODE_CODE_SQL)) {
			String sql = getQueryString(view, params, user, doc);
			sql = sqlUtil.appendCondition(sql, fielter);
			datas = dp.queryBySQLPage(sql, params, page, lines, user.getDomainid());
		}
		return datas;
	}

	/**
	 * 根据视图名与模块标识查询,返回是否视图对象
	 * 
	 * @param name
	 *            视图名
	 * @param module
	 *            模块标识
	 * @return 是否视图对象
	 * @throws Exception
	 */
	public boolean get_existViewByNameModule(String name, String module) throws Exception {
		return ((ViewDAO) getDAO()).existViewByNameModule(name, module);
	}

	/**
	 * 重载父类doRemove方法 删除视图,并删除关联的Link(链接)/Ressource(菜单)
	 */
	//@SuppressWarnings("unchecked")
	public void doRemove(String viewId) {
		try {
			PersistenceUtils.beginTransaction();// 启动事务
			IDesignTimeDAO<LinkVO> linkDAO = (LinkDAO) DAOFactory.getDefaultDAO(LinkVO.class.getName());
			// 查询视图id(viewid) = "viewId" 的所有关联链接(Link)
			Iterator<LinkVO> it_Link = linkDAO.getDatas("from LinkVO lv where lv.actionContent='" + viewId + "'")
					.iterator();
			while (it_Link.hasNext()) {// 迭代删除所有关联的链接(Link)
				LinkVO link = it_Link.next();
				IDesignTimeDAO<ResourceVO> resourceDAO = (ResourceDAO) DAOFactory.getDefaultDAO(ResourceVO.class
						.getName());
				// 查询链接id(linkid) = 当前link的id的所有关联菜单(Resource)
				Iterator<ResourceVO> it_Resource = resourceDAO.getDatas(
						"from ResourceVO rv where rv.link='" + link.getId() + "'").iterator();
				while (it_Resource.hasNext()) {// 迭代删除所有关联的菜单(Resource)
					resourceDAO.remove(it_Resource.next());// 删除该菜单(Resource)
				}
				linkDAO.remove(link);// 删除当前的链接(Link)
			}
			getDAO().remove(viewId);// 删除该视图
			PersistenceUtils.commitTransaction();// 提交事务
		} catch (Exception e) {
			try {// 事务回滚
				PersistenceUtils.rollbackTransaction();
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public String getViewNameById(String viewid) throws Exception {
		return ((ViewDAO) getDAO()).findViewNameById(viewid);
	}
}
