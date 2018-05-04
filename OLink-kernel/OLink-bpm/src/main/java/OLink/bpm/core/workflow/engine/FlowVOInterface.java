package OLink.bpm.core.workflow.engine;

import java.sql.Date;

public interface FlowVOInterface {
	long getId();

	void setId(long id);

	String get_recorddescription();// 记录相应描述（为每个模块的title或name等）

	String get_recordtype();// 类型（收文、发文、用车等）

	String get_recordcreator();// 记录作者

	Date get_recordcreatedate();// 记录创建日期

	String getOwner();
}
