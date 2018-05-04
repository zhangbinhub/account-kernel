package OLink.bpm.core.dynaform.activity.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public interface ActivityDAO extends IDesignTimeDAO<Activity> {
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
	 * @return Activity的DataPackage
	 * @throws Exception
	 */
	DataPackage<Activity> findByViewid(String id, String application)
			throws Exception;

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
	 * @return Activity的DataPackage
	 * @throws Exception
	 */
	DataPackage<Activity> findByFormid(String id, String application)
			throws Exception;

	/**
	 * 根据View主键,应用标识与当前Activity对象的次序,获取上一个Activity .
	 * 
	 * @param viewid
	 *            View主键
	 * @param oderno
	 *            次序号
	 * @param flag
	 *            标志属于Form的Activity(按钮)或是属于View的Activity(按钮)
	 * @param application
	 *            应用标识
	 * @return 上一个Activity
	 * @throws Exception
	 */
	Activity getPreviousActivity(String viewid, int oderno, String flag)
			throws Exception;

	/**
	 * 根据View主键,应用标识与当前Activity对象的次序,获取下一个Activity .
	 * 
	 * @param viewid
	 *            View主键
	 * @param oderno
	 *            次序号
	 * @param flag
	 *            标志属于Form的Activity(按钮)或是属于View的Activity(按钮)
	 * @param application
	 *            应用标识
	 * @return 下一个Activity
	 * @throws Exception
	 */
	Activity getNextActivity(String viewid, int oderno, String flag)
			throws Exception;

	/**
	 * 根据所属视图,所属表单查询，返回最大排序的Activity(按钮).
	 * 
	 * @param viewid
	 *            the view 主键
	 * @param formid
	 *            the 表单form主键
	 * @param application
	 *            应用标识
	 * @return 最大排序的Activity
	 * @throws Exception
	 */
	Activity getActivityByMaxOderNO(String viewid, String formid)
			throws Exception;

	Collection<Activity> getActivityByViewId(String viewid, String application)
			throws Exception;
}
