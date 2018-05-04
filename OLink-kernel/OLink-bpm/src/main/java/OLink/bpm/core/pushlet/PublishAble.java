package OLink.bpm.core.pushlet;

import eWAP.core.ResourcePool;

/**
 * 发布允许接口
 * 
 * @author nicholas
 * 
 */
public interface PublishAble {
	
	/**站内短信提醒主题*/
	String SUBJECT_TYPE_PERSONALMESSAGE = ResourcePool.getRootpath()+"/personalmessage";
	
	/**待办提醒主题*/
	String SUBJECT_TYPE_PENDING = ResourcePool.getRootpath()+"/pending";
	
	/**发布允许接口方法*/
	void publish();
	
}
