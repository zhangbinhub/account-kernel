package OLink.bpm.core.workflow.element;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Vector;

import OLink.bpm.core.workflow.utility.CommonUtil;
import OLink.bpm.util.xml.XmlUtil;
import OLink.bpm.core.workflow.element.mapping.FieldMappingItem;
import OLink.bpm.util.StringUtil;

public class SubFlow extends Node {
	
	private static final long serialVersionUID = -7299999235675136829L;
	
	
	/**
	 * 子流程绑定方式-自定义值
	 */
	public static final String SUBFLOW_DEFINITION_CUSTOM = "01";
	
	/**
	 * 子流程绑定方式-脚本返回值
	 */
	public static final String SUBFLOW_DEFINITION_SCRIPT = "02";
	
	
	/**
	 * 参数传递方式-共享父流程文档
	 */
	public static final String PARAM_PASSING_SHARE = "01";
	
	/**
	 * 参数传递方式-表单映射
	 */
	public static final String PARAM_PASSING_MAPPING = "02";
	
	/**
	 * 参数传递方式-脚本
	 */
	public static final String PARAM_PASSING_SCRIPT = "03";
	
	/**
	 * 实例启动次数设置方式-预定义值
	 */
	public static final String NUMBER_SETING_CUSTOM = "01";
	
	/**
	 * 实例启动次数设置方式-父流程表单字段值
	 */
	public static final String NUMBER_SETING_FIEDL = "02";
	
	/**
	 * 实例启动次数设置方式-脚本返回值
	 */
	public static final String NUMBER_SETING_SCRIPT = "03";
	
	/**
	 * 实例启动次数设置方式-审批人分组总数
	 */
	public static final String NUMBER_SETING_GROUP_TOTAL = "04";
	
	
	/**
	 * 绑定子流程定义的类型
	 */
	public String subFlowDefiType;

	/**
	 * 子流程定义id
	 */
	public String subflowid; 

	/**
	 * 子流程名称
	 */
	public String subflowname;
	
	/**
	 * 通过脚本返回值设置流程（返回流程ID）
	 */
	public String subflowScript;
	
	/**
	 * 参数传递设置方式  
	 */
	public String paramPassingType;
	
	/**
	 * 父流程关联表单ID
	 */
	public String parentFlowFormId;
	
	/**
	 * 父流程关联表单名称
	 */
	public String parentFlowFormName;
	
	/**
	 * 子流程关联表单ID
	 */
	public String subFlowFormId;
	
	/**
	 * 子流程关联表单名称
	 */
	public String subFlowFormName;
	
	/**
	 * 表单字段映射（XML格式）
	 */
	public String fieldMappingXML;
	
	/**
	 * 是否共享主流程文档
	 */
	public boolean shareDocument = false;
	
	/**
	 * 参数传递脚本
	 */
	public String paramPassingScript;
	
	
	/**
	 * 流程实例启动数量设置方式
	 */
	public String numberSetingType;
	
	/**
	 * 流程实例启动数量设置内容 （可以是数字、字段名和脚本）
	 */
	public String numberSetingContent;
	
	/**
	 * 是否回调
	 */
	public boolean callback;
	
	/**
	 * 回调脚本
	 */
	public String callbackScript;

	public boolean iscurrent;

	public boolean ispassed = false;// 是否审核
	
	/**
	 * 是否为聚合节点
	 */
	public boolean isgather; // 是否聚合节点
	
	/**
	 * 分散起始节点
	 */
	public String splitStartNode;
	
	/**
	 * 是否为分散节点(默认为true)
	 */
	public boolean issplit = true;

	/**
	 * 是否指的审批人
	 */
	public boolean isToPerson;

	@Deprecated 
	public String subflowFormid; // 子流程表单ID

	@Deprecated 
	public String subflowFormname; // 子流程表单名称

	/**
	 * @deprecated since 2.6
	 */
	@Deprecated 
	public boolean crossform; // 跨表单

	/**
	 * 子流程启动时执行的脚本
	 * @deprecated since 2.6
	 */
	@Deprecated 
	public String startupScript;

	/**
	 * private
	 * 
	 * @param owner
	 * @roseuid 3E0428DA0235
	 */
	public SubFlow(FlowDiagram owner) {
		super(owner);
	}

	/**
	 * @param g
	 * @roseuid 3E046AF60245
	 */
	// public void paint(OGraphics g) {
	// if (this.isreflow) {
	// _img = _owner.getImageResource("subreflow.gif");
	// } else {
	// _img = _owner.getImageResource("subflow.gif");
	// }
	//		
	//		
	// }
	public void paint(OGraphics g) {
		if (_img == null) {
			_img = _owner.getImageResource("subflow.gif");
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
		_img = _owner.getImageResource("subflow_m.gif");

		// Call All Sub Elements PAINT METHOD.
		// 保存当前背景颜色...
		Color old = this.bgcolor;
		if (_owner.isCurrentToEdit(this)) {
			bgcolor = DEF_CURREDITCOLOR;
		}

		if (_owner.isCurrentSelected(this)) {
			bgcolor = DEF_SELECTEDCOLOR;
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

		if (this.subflowname != null && !this.subflowname.trim().equals("")
				&& !this.subflowname.trim().equals("null")) {
			tips.append(CommonUtil.foldString("子流程名称：" + this.subflowname, 20));
			// tips.append(shortname);
			tips.append("\n");
		}

		drawTips(g, tips.toString());
	}

	public Vector<Element> getSubelems() {
		try {
			// if (_subelems.isEmpty()) {
			// BillDefiProcess defiProcess = new BillDefiProcessBean();
			// BillDefiVO flow = (BillDefiVO)
			// defiProcess.doView(this.subflowid);
			// FlowDiagram subFlowDiagram = flow.toFlowDiagram();
			// _subelems.addAll(subFlowDiagram.getAllElements());
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return _subelems;
	}
	
	/**
	 * 获取子流程参数映射信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<FieldMappingItem> getFieldMappingInfo()throws Exception{
		if(PARAM_PASSING_SHARE.equals(paramPassingType) || StringUtil.isBlank(fieldMappingXML)) return null;
		
		return (Collection<FieldMappingItem>) XmlUtil.toOjbect(fieldMappingXML);
	}
}
