//Source file: D:\\BILLFLOW\\src\\billflow\\Person.java

//Source file: E:\\billflow\\src\\billflow\\Person.java

package OLink.bpm.core.workflow.element;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import OLink.bpm.core.workflow.utility.CommonUtil;
import OLink.bpm.core.workflow.utility.NameList;
import OLink.bpm.core.workflow.utility.Sequence;
import OLink.bpm.util.json.JsonUtil;
import OLink.bpm.util.StringUtil;

public class ManualNode extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = -30256673843955856L;
	/**
	 * 审批人编辑模式, 角色设计
	 */
	public static final int ACTOR_EDIT_MODE_DESIGN = 0;
	/**
	 * 审批人编辑模式, 脚本
	 */
	public static final int ACTOR_EDIT_MODE_CODE = 1;
	
	/**
	 * 审批人编辑模式, 用户设计
	 */
	public static final int ACTOR_EDIT_MODE_USER_DESIGN = 2;
	
	/**
	 * 审批人编辑模式, 组织
	 */
	public static final int ACTOR_EDIT_MODE_ORGANIZATION_DESIGN = 3;
	
	/**
	 * 抄送人编辑模式, 角色设计
	 */
	public static final int CIRCULATOR_EDIT_MODE_DESIGN = 0;
	/**
	 * 抄送人编辑模式, 脚本
	 */
	public static final int CIRCULATOR_EDIT_MODE_CODE = 1;
	
	/**
	 * 抄送人编辑模式, 用户设计
	 */
	public static final int CIRCULATOR_EDIT_MODE_USER_DESIGN = 2;

	/**
	 * 任意, 任意一个负责人处理后通过
	 */
	public static final int PASS_CONDITION_OR = 0;
	/**
	 * 会签, 每个负责人处理后通过
	 */
	public static final int PASS_CONDITION_AND = 1;
	/**
	 * 有顺序的会签, 每个负责人按顺序处理后通过
	 */
	public static final int PASS_CONDITION_ORDERLY_AND = 2;
	
	/**
	 * 组织-作者
	 */
	public static final String ORG_AUTHOR = "author";
	
	/**
	 * 组织-提交者
	 */
	public static final String ORG_AUDITOR = "auditor";
	
	/**
	 * 上级用户
	 */
	public static final String ORG_SCOPE_SUPERIOR = "superior";
	
	/**
	 * 下级用户
	 */
	public static final String ORG_SCOPE_LOWER = "lower";
	
	/**
	 * 本级默认部门
	 */
	public static final String ORG_SCOPE_DEPT_DEFAULT = "default";
	
	/**
	 * 直属上级部门
	 */
	public static final String ORG_SCOPE_DEPT_LINE_SUPERIOR = "lineSuperior";

	/**
	 * 直属下级部门
	 */
	public static final String ORG_SCOPE_DEPT_LINE_LOWER = "lineLower";
	/**
	 * 所有上级部门
	 */
	public static final String ORG_SCOPE_DEPT_ALL_SUPERIOR = "allSuperior";

	/**
	 * 所有下级部门
	 */
	public static final String ORG_SCOPE_DEPT_ALL_LOWER = "allLower";

	// 催办功能相关属性
	// public String remaindertype;// 提醒类型 0为不提醒，1为到达后多少小时内提醒，2为提前多少小时提醒

	// public String beforetime;// 提前多少小时提醒

	// public String limittimecount;// 限定审核时间(如48小时, 按工作时间计算）

	// public String sendmodelist; // 提醒的发送方式 0为Email, 1为SMS

	// public String timeunit; // 提醒时间的单位 0为小时，1为天.

	// public boolean isnotifysuperior; // 是否提醒上级

	// public boolean istimelimit; // 是否限时

	// public boolean notifiable; // 是否需要提醒

	public String actorListScript; // 获取角色列表的脚本

	public int actorEditMode = ACTOR_EDIT_MODE_DESIGN; // 角色编辑模式(0:角色定制, 1:脚本,2:用户定制)

	// 详见FlowType.java类

	public String namelist;// actor选择
	
	/**
	 * 组织字段(auditor：流程提交者|author：表单作者)
	 */
	public String orgField;
	
	/**
	 * 作用范围
	 */
	public String orgScope;
	
	/**
	 * 审批人
	 * 		数据规则：(U11e0-7c45-c6138387-a878-afad62721f2a|用户名称1;U11e0-7c45-c3be48e6-a878-afad62721f2a|用户名称2;)
	 */
	public String userList;

	public String realnamelist; // 当前结点真正审核者，形如{U|admin;D-05-02|副总师;}，“与”条件及代理人已转成具体处理人ID

	public String passcondition;// 审核通过条件 0->或 1->与 2->强制与 3->自定义（与或混合）
	// 详见FlowType.java类

	public String exceedaction;// 超时处理类型
	// 0为不处理，1为自动流转，2为自动终止，3为自动回退，4为自动回退至指定结节

	// 详见FlowType.java类
	public boolean issetcurruser;// 流程转入时是否将writers强制设为当前用户

	/**
	 * 使用完整名， 如：11000|赛百威公司/开发部/部门经理 多项值之间使用分号隔开，
	 * 如：11000|赛百威公司/开发部/部门经理；12001|赛百威公司/市场部/部门经理
	 */
	// public String namelist;
	public String inputform;

	// 角色列表，使用完整名，多项值之间使用分号隔开，如：11000|赛百威公司/开发部/部门经理；12001|赛百威公司/市场部/部门经理
	// public String actorlist;
	// 人员列表，使用完整名，多项值之间使用分号隔开，如：11000|赛百威公司/开发部/周志军；12001|赛百威公司/市场部/刘永勤
	// public String personlist;
	/**
	 * @param owner
	 * @roseuid 3E0428DB0132
	 */
	// private Vector<NodeActivity> temp = new Vector<NodeActivity>(); //
	// 临时保存NodeActivity；

	public boolean isgather; // 是否聚合节点

	public boolean issplit; // 是否分散节点

	public boolean cBack;// 可否回退

	public int backType;// 回退模式

	public boolean isToPerson;// 是否指的审批人
	
	public String bnodelist; // 回退定制的节点

	public int retracementEditMode; // 回撤编辑模式

	public boolean cRetracement;// 可否回撤

	public String retracementScript;// 回撤条件脚本

	/**
	 * 流程提醒策略, 分为三种Arrive, OverDue, Reject, <br />
	 * 数据结构如下: <br />
	 * { <br />
	 * arrive: {sendModeCodes:[0, 1, 2], template:reminderId, smsApproval:0or1}, <br />
	 * overdue: {sendModeCodes:[0, 1, 2], limittimecount:12, timeunit:0,
	 * isnotifysuperior:true, template:reminderId}, <br />
	 * reject: {sendModeCodes:[0, 1, 2], responsibleType:256,
	 * template:reminderId} <br />
	 * }
	 */
	public String notificationStrategyJSON;
	
	/**
	 * 分散起始节点
	 * @return
	 */
	public String splitStartNode;
	
	public boolean isFrontEdit;//可否允许前台手动调整流程
	
	/**
	 * 是否开启抄送功能
	 */
	public boolean isCarbonCopy;
	
	/**
	 * 是否指定抄送人
	 */
	public boolean isSelectCirculator;
	
	/**
	 * 抄送人编辑模式(0:角色定制, 1:脚本 ,2：用户定制)
	 */
	public int circulatorEditMode = CIRCULATOR_EDIT_MODE_DESIGN;
	/**
	 * 抄送人脚本
	 */
	public String circulatorListScript; // 获取角色列表的脚本
	/**
	 * 抄送人按角色设计模式的数据模型
	 */
	public String circulatorNamelist;
	
	/**
	 * 抄送人按用户设计模式的数据模型
	 * 
	 * 数据规则：(U11e0-7c45-c6138387-a878-afad62721f2a|用户名称1;U11e0-7c45-c3be48e6-a878-afad62721f2a|用户名称2;)
	 */
	public String circulatorNamelistByUser;
	

	public NameList toNameList() {// 经解释后的nameList对象
		NameList nameList = NameList.parser(this.namelist);
		return nameList;
	}
	
	public NameList toUserList(){
		NameList nameList = NameList.parser(this.userList);
		return nameList;
	}
	
	/**
	 * 经解释后的nameList对象
	 * @return
	 */
	public NameList toCirculatorNameList() {
		NameList nameList = NameList.parser(this.circulatorNamelist);
		return nameList;
	}
	
	public NameList toCirculatorNameListByUser(){
		NameList nameList = NameList.parser(this.circulatorNamelistByUser);
		return nameList;
	}

	public ManualNode(FlowDiagram owner) {
		super(owner);
	}

	public void addNodeActivity(String name) {
		NodeActivity act = new NodeActivity(_owner);
		act.id = Sequence.getSequence();
		act.name = name;
		this.getSubelems().add(act);
	}

	// public String[] getIdList() {
	// if (namelist == null || namelist.trim().equals("")) {
	// return null;
	// }
	// String[] tmp = CommonUtil.split(namelist, ';');
	// String[] rtn = new String[tmp.length];
	// for (int i = 0; i < tmp.length; i++) {
	// rtn[i] = NameListUtil.getId(tmp[i]);
	// }
	//    
	//    
	// return rtn;
	// }

	// public String[] getShortNameList() {
	// if (namelist == null || namelist.trim().equals("")) {
	// return null;
	// }
	// String[] tmp = CommonUtil.split(namelist, ';');
	// String[] rtn = new String[tmp.length];
	// for (int i = 0; i < tmp.length; i++) {
	// NameNode node = new NameNode(tmp[i]);
	// rtn[i] = node.getShortName();
	// // rtn[i] = NameListUtil.getShortName(tmp[i]);
	// }
	// return rtn;
	// }

	public String getShortNameListStr() {
		StringBuffer shortName = new StringBuffer();
		NameList nl = NameList.parser(namelist);

		Collection<String[]> colls = nl.toCollection();
		if (colls != null) {
			Iterator<String[]> iters = colls.iterator();
			while (iters.hasNext()) {
				String[] tmp = iters.next();
				shortName.append(shortName).append(tmp[1]).append(";");
			}
		}
		return shortName.toString();
		// return CommonUtil.arrayToString(getShortNameList(), ';');
	}

	public String getFormatShortNameListStr() {
		String str = getShortNameListStr();
		StringBuffer rtn = new StringBuffer();
		int pos = 0;
		while (pos <= str.length()) {
			if (pos + 10 > str.length()) {
				rtn.append(str.substring(pos, str.length()));
			} else {
				rtn.append(str.substring(pos, pos + 10));
				rtn.append('\n');
			}
			pos += 10;
		}
		return rtn.toString();
	}

	/**
	 * @param g
	 * @roseuid 3E043760021D
	 */
	public void paint(OGraphics g) {
		if (_img == null) {
			_img = _owner.getImageResource("actor.gif");
		}

		// Call All Sub Elements PAINT METHOD.
		// 保存当前背景颜色...
		Color old = this.bgcolor;
		if (_owner.isCurrentToEdit(this)) {
			bgcolor = DEF_CURREDITCOLOR;
		}

		if (_owner.isCurrentSelected(this)) {
			bgcolor = DEF_SELECTEDCOLOR;
		}

		for (Enumeration<Element> e = _subelems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement se = (PaintElement) te;
				se.paint(g);
			}
		}

		// Fill background
		this.width = WIDTH;
		this.m_width = M_WIDTH;
		this.m_height = M_HEIGHT;
		this.height = HEIGHT;
		resize();
		g.setColor(bgcolor);
		g.fillRect(this.x, this.y, this.width, this.height);

		// Draw Image
		g.drawImage(_img, _imgrect.x, _imgrect.y, _imgrect.width,
				_imgrect.height, null, this._owner);

		if (this.name != null) {
			java.awt.FontMetrics fm = _owner.getFontMetrics(font);
			int tx = _txtrect.x + (_txtrect.width - fm.stringWidth(name)) / 2;
			int ty = _txtrect.y + 2 * _txtrect.height;
			if (this._iscurrent) {
				g.drawImage(_owner.getImageResource("current.gif"), _txtrect.x,
						_txtrect.y, _txtrect.width + 30, 10 + _txtrect.height,
						null, this._owner);
			} else {
				g.drawImage(_owner.getImageResource("background.gif"),
						_txtrect.x, _txtrect.y, _txtrect.width + 30,
						10 + _txtrect.height, null, this._owner);

			}
			g.setColor(Color.black);
			g.drawString(name, tx + 13 + this.name.length(), ty - 10);
		}

		// 恢复当前背景颜色
		this.bgcolor = old;
	}

	public void paintMobile(OGraphics g) {
		_img = _owner.getImageResource("actor_m.gif");

		// Call All Sub Elements PAINT METHOD.
		// 保存当前背景颜色...
		Color old = this.bgcolor;
		if (_owner.isCurrentToEdit(this)) {
			bgcolor = DEF_CURREDITCOLOR;
		}

		if (_owner.isCurrentSelected(this)) {
			bgcolor = DEF_SELECTEDCOLOR;
		}

		for (Enumeration<Element> e = _subelems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement se = (PaintElement) te;
				se.paintMobile(g);
			}
		}

		// Fill background
		this.width = WIDTH;
		this.m_width = M_WIDTH;
		this.m_height = M_HEIGHT;
		this.height = HEIGHT;
		resizeForMobile();
		g.setColor(bgcolor);
		g.fillRect(this.x, this.y, this.width, this.height);

		// Draw Image

		if (_iscurrent) {
			_img = _owner.getImageResource("current_m.gif");
		}
		g.drawImage(_img, _imgrect.x, _imgrect.y, _imgrect.width,
				_imgrect.height, null, this._owner);

		if (this.name != null) {
			// java.awt.FontMetrics fm = _owner.getFontMetrics(font);
			g.setColor(Color.black);
			g.drawString(name, _txtrect.x + name.length(), _txtrect.y + 30);
		}

		// 恢复当前背景颜色
		this.bgcolor = old;
	}

	public void showTips(Graphics g) {
		StringBuffer tips = new StringBuffer();

		String shortname = getFormatShortNameListStr();
		if (shortname != null && !shortname.trim().equals("")
				&& !shortname.trim().equals("null")) {
			tips.append(CommonUtil.foldString("操作者：" + shortname, 20));
			// tips.append(shortname);
			tips.append("\n");
		}
		drawTips(g, tips.toString());

	}

	public static void main(String[] args) {
		/*
		 * FlowDiagram fd = new FlowDiagram(); ManualNode g = new
		 * ManualNode(fd); // g.namelist = //
		 * "P1000|赛百威公司/开发部/周志军;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理"
		 * ; g.namelist = "P1000|赛百威公司";//
		 * /开发部/周志军;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司
		 * /开发部/部门经理;A1000|赛百威公司/开发部/部门经理
		 * ;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理"; String str =
		 * g.getFormatShortNameListStr();
		 */
	}

	public int getPassCondition() {
		try {
			return Integer.parseInt(passcondition);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public Map<String, Object> getNotificationStrategyMap() {
		try {
			// 测试数据
			// notificationStrategyJSON = "{"
			// + "arrive: {sendModeCodes:[0, 1]}, "
			// + "overdue: {sendModeCodes:[0, 1], limittimecount:12, timeunit:0,
			// isnotifysuperior:true},"
			// + "reject: {sendModeCodes:[0, 1], responsibleType: 256}"
			// + "}";
			String jsonStr = "";
			if (!StringUtil.isBlank(notificationStrategyJSON)) {
				jsonStr = StringUtil.dencodeHTML(notificationStrategyJSON);
			}

			return JsonUtil.toMap(jsonStr);
		} catch (Exception e) {
			return new HashMap<String, Object>();
		}
	}
}
