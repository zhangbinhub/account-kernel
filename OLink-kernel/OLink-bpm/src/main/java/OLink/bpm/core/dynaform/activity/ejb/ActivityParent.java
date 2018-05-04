package OLink.bpm.core.dynaform.activity.ejb;

import java.util.Set;

public interface ActivityParent {
	String getId();
	
	String getName();

	void setActivitys(Set<Activity> activitys);

	Set<Activity> getActivitys();

	String getActivityXML();

	void setActivityXML(String activityXML);
	
	String getApplicationid();
	
	/**
	 * 根据ID获取按钮
	 * 
	 * @param id 按钮ID
	 *            
	 * @return 按钮
	 */
	Activity findActivity(String id);

	/**
	 * 获取完整名称
	 * 
	 * @return 元素全名，包括
	 */
	String getFullName();
	
	String getSimpleClassName();
}
