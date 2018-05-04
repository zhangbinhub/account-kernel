/*
 * Created on 2005-3-16
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.workflow.utility;

/**
 * @author Administrator
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class FlowType {

	// add by gusd
	// static final int FLOWSTATUS_OPEN_NOSTART = 0x00000001; //初始状态无当前结点

	// 流程状态
	// static final int CLOSE_END = 0x10000000;
	public static final int FLOWSTATUS_OPEN_NOSTART = 0x00000010; // 流程初始状态第一个结点为当前结点
	public static final int FLOWSTATUS_OPEN_RUN_RUNNING = 0x00000100; // 流程运转状态
	public static final int FLOWSTATUS_OPEN_RUN_SUSPEND = 0x00001000; // 流程挂起状态
	public static final int FLOWSTATUS_CLOSE_ABORT = 0x00010000; // 流程拒绝状态
	public static final int FLOWSTATUS_CLOSE_COMPLETE = 0x00100000; // 流程完成状态
	public static final int FLOWSTATUS_CLOSE_TERMINAT = 0x01000000; // 流程终止状态

	// 流程处理动作
	public static final String START2RUNNING = "1";// 开始
	public static final String START2TERMINATE = "2";// 终止1
	public static final String SUSPEND2RUNNING = "3";// 恢复
	public static final String RUNNING2SUSPEND = "4";// 挂起1
	public static final String SUSPEND2ABORT = "5";// 取消
	public static final String RUNNING2COMPLETE = "6";// 完成
	public static final String RUNNING2TERMIATE = "7";// 终止2
	public static final String RUNNING2RUNNING_NEXT = "80";// 运行/下一步
	public static final String RUNNING2RUNNING_BACK = "81";// 退回
	public static final String RUNNING2RUNNING_SELF = "82";// 自循环
	public static final String SUSPEND2SUSPEND = "9";// 挂起2

	// 自动处理类型
	public static final String NOTDO = "0";// 不处理
	public static final String DONEXT = "1";// 自动流转
	public static final String DOTERMINAT = "2";// 自动终止
	public static final String DOBACK = "3";// 自动回退
	public static final String DOBACKTONODE = "4";// 自动回退至指定节点

	// 审核通过条件
	public static final String ORCONDITION = "0"; // 审核通过条件－－或－－任一审核通过
	public static final String GROUPANDCNDT = "1";// 审核通过条件－－与－－每组（分号隔开）须至少有一人审核才可通过
	public static final String ANDCONDITION = "2";// 审核通过条件－－与－－每组所有人均需审核才可通过
	public static final String ORAND = "3";// 审核通过条件－－自定义－－“与”“或”混合

	// 提醒策略
	public static final String REMAINDER_NOT = "0";// 不提醒
	public static final String REMAINDER_AFTER = "1";// 到达后提醒
	public static final String REMAINDER_BEFORE = "2";// 提前提醒

	public static final String[] ACTIONCODES = { "1", "2", "3", "4", "5", "6",
			"7", "80", "81", "82", "9" };
	public static final String[] ACTIONNAMES = { "{*[Start]*}",
			"{*[Terminate]*}", "{*[Running]*}", "{*[Suspend]*}",
			"{*[Cancel]*}", "{*[Complete]*}", "{*[Terminate]*}",
			"{*[Running_Next]*}", "{*[Running_Back]*}", "{*[Loop_Self]*}",
			"{*[Suspend]*}" };

	public static String getActionName(String code) {
		if (code == null || code.trim().length() <= 0) {
			return ACTIONCODES[0];
		}

		for (int i = 0; i < ACTIONCODES.length; i++) {
			if (code.equals(ACTIONCODES[i])) {
				return ACTIONNAMES[i];
			}
		}

		return "";
	}

	public static String getActionCode(String name) {
		if (name == null || name.trim().length() <= 0) {
			return ACTIONNAMES[0];
		}

		for (int i = 0; i < ACTIONNAMES.length; i++) {
			if (name.equals(ACTIONCODES[i])) {
				return ACTIONCODES[i];
			}
		}

		return "";
	}

}
