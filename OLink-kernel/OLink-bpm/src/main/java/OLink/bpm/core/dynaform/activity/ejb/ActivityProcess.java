package OLink.bpm.core.dynaform.activity.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * 
 * @author marky
 * 
 */
public interface ActivityProcess extends IDesignTimeProcess<Activity> {

	/**
	 * 从Activity集合中获取最大的顺序号
	 * 
	 * @param activityList
	 *            Activity集合
	 * @return
	 */
	int getActivityMaxOrderNo(Collection<Activity> activityList);

	/**
	 * 更改Activity顺序号
	 * 
	 * @param id
	 *            要更改顺序的Activity的id
	 * @param parent
	 *            父元素(Form|View)
	 * @param flag
	 *            标记(前移|后移)
	 */
	void changeOrder(String id, ActivityParent parent, String flag);

	void doRemove(ActivityParent parent, String[] pks) throws Exception;

	void doUpdate(ActivityParent parent, ValueObject vo) throws Exception;
}
