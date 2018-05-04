package OLink.bpm.core.dynaform.activity.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateActivityDAO extends HibernateBaseDAO<Activity> implements
		ActivityDAO {
	/**
	 * HibernateActivityDAO构造函数,继承父类构造函数
	 * 
	 * @see HibernateBaseDAO#getClass()
	 * @param voClassName
	 *            值对象类名
	 */
	public HibernateActivityDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * 根据所属view主键与应用标识查询,返回Activity的DataPackage.
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的Activity数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @param id
	 *            所属view主键
	 * @param application
	 *            应用标识
	 * @return Activity的DataPackage.
	 * @throws Exception
	 */
	public DataPackage<Activity> findByViewid(String id, String application)
			throws Exception {
		String hql = "from " + _voClazzName + " act where act.parentView.id='"
				+ id + "'";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		params.setParameter("_orderby", "orderno");
		return super.getDatapackage(hql, params);
	}

	/**
	 * 根据所属表单主键与应用标识查询,返回Activity的DataPackage.
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的Activity数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @param id
	 *            表单主键
	 * @param application
	 *            应用标识
	 * @return Activity的DataPackage.
	 * @throws Exception
	 */
	public DataPackage<Activity> findByFormid(String id, String application)
			throws Exception {
		String hql = "from " + _voClazzName + " act where act.parentForm.id='"
				+ id + "'";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		params.setParameter("_orderby", "orderno");
		return super.getDatapackage(hql, params);
	}

	/**
	 * 根据View主键,应用标识与当前Activity对象的次序,获取上一个Activity .
	 * 
	 * @param viewid
	 *            View主键
	 * @param oderno
	 *            次序号
	 * @param flag
	 *            标志属于Form的Activity(按钮)或是属于View的Activity(按钮).
	 * @param application
	 *            应用标识
	 * @return 上一个Activity 对象
	 * @throws Exception
	 */
	public Activity getPreviousActivity(String id, int oderno, String flag)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.parentView.id='"
				+ id + "'" + " and vo.orderno in (select max(act.orderno) "
				+ "from " + _voClazzName + " act where act.parentView.id='"
				+ id + "'" + " and act.orderno <" + oderno + ")";

		if (flag.equals(Activity.ACTIVITY_BELONGTO_FORM))
			hql = "FROM " + _voClazzName + " vo where vo.parentForm.id='" + id
					+ "'" + " and vo.orderno in (select max(act.orderno) "
					+ "from " + _voClazzName + " act where act.parentForm.id='"
					+ id + "'" + " and act.orderno <" + oderno + ")";

		return (Activity) getData(hql);

	}

	/**
	 * 根据View主键,应用标识与当前Activity对象的次序,获取下一个Activity.
	 * 
	 * @param viewid
	 *            View主键
	 * @param oderno
	 *            次序号
	 * @param flag
	 *            标志属于Form的Activity(按钮)或是属于View的Activity(按钮).
	 * @param application
	 *            应用标识
	 * @return 下一个Activity
	 * @throws Exception
	 */
	public Activity getNextActivity(String id, int oderno, String flag)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.parentView.id='"
				+ id + "'" + " and vo.orderno in (select min(act.orderno) "
				+ "from " + _voClazzName + " act where act.parentView.id='"
				+ id + "'" + " and act.orderno >" + oderno + ")";
		if (flag.equals(Activity.ACTIVITY_BELONGTO_FORM))
			hql = "FROM " + _voClazzName + " vo where vo.parentForm.id='" + id
					+ "'" + " and vo.orderno in (select min(act.orderno) "
					+ "from " + _voClazzName + " act where act.parentForm.id='"
					+ id + "'" + " and act.orderno >" + oderno + ")";

		return (Activity) getData(hql);

	}

	/**
	 * 根据所属视图,所属表单查询，返回最大排序的Activity(按钮).
	 * 
	 * @param viewid
	 *            the view 主键
	 * @param formid
	 *            the 表单form主键
	 * @param application
	 *            应用标识
	 * @return 最大排序的Activity(按钮)
	 * @throws Exception
	 */
	public Activity getActivityByMaxOderNO(String viewid, String formid)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.parentView.id='"
				+ viewid + "'" + " and vo.orderno in (select max(act.orderno)"
				+ " from " + _voClazzName + " act where act.parentView.id='"
				+ viewid + "') ";
		if (formid != null && formid.trim().length() > 0)
			hql = "FROM " + _voClazzName + " vo where vo.parentForm.id='"
					+ formid + "'"
					+ " and vo.orderno in (select max(act.orderno)" + " from "
					+ _voClazzName + " act where act.parentForm.id='" + formid
					+ "') ";

		return (Activity) getData(hql);
	}

	public Collection<Activity> getActivityByViewId(String viewId, String application)
			throws Exception {
		return getDatas(viewId, application);
	}

	/**
	 * 
	 * @param viewId
	 * @param application
	 * @return
	 * @throws Exception
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<Activity> getDatas(String viewId, String application)
			throws Exception {
		Session session = currentSession();
		session.flush();
		String sql = "SELECT * from t_activity act";
		if (viewId != null && application != null) {
			sql += " where act.view_id='" + viewId + "'";
			sql += " and act.applicationid='" + application + "'";

		}
		Query query = session.createSQLQuery(sql).addEntity(Activity.class);
		Collection<Activity> rtn = query.list();

		return rtn;

	}
}
