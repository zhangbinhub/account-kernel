// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\document\\ejb\\Document.java

package OLink.bpm.core.dynaform.document.ejb;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcessBean;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.core.workflow.utility.FieldPermissionList;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.element.Node;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.form.ejb.DateField;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.InputField;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.util.DateUtil;
import eWAP.core.Tools;

/**
 * @author nicholas
 */
public class Document extends ValueObject implements Cloneable {
	private static final Logger log = Logger.getLogger(Document.class);

	private static final long serialVersionUID = -2778186512863748786L;

	/**
	 * 表单模板名称
	 */
	private String formname;

	/**
	 * 表单模板id值
	 */
	private String formid;

	/**
	 * 文档ID
	 */
	private String id;

	/**
	 * 文档项的名称-值映射
	 */
	private Map<String, Item> _items_name = new HashMap<String, Item>();

	/**
	 * 当前流程实例(从T_FLOWSTATERT表中获取)
	 */
	private FlowStateRT state;

	/**
	 * 是否为子表单
	 */
	private boolean _issubdoc;

	/**
	 * 是否编辑
	 */
	private boolean isEditAble = true;

	/**
	 * 判断编辑字段是否已加载
	 */
	private boolean isEditAbleLoaded = false;

	/**
	 * 作者
	 */
	private BaseUser author;
	
	/**
	 * 作者默认部门的索引
	 */
	private String authorDeptIndex;

	/**
	 * 文档所在节点的编辑权限
	 */
	private FieldPermissionList fieldPermList;

	/**
	 * 创建日期
	 */
	private Date created;

	/**
	 * 最后一次修改日期
	 */
	private Date lastmodified;

	/**
	 * 页面参数
	 */

	private ParamsTable _params;
	/**
	 * 最后一次修改用户
	 */
	private String lastmodifier;

	/**
	 * 是否为新文档(在新建时就已创建文档id, 故保存时只能通过增加此属性判断是否为新文档)
	 */
	private boolean _new;

	/**
	 * 当前审核人名称(以分","号隔开)
	 */
	private String auditorNames;

	/**
	 * 最后审核时间
	 */
	private Date auditdate;

	/**
	 * 流程发起人姓名
	 */
	private String initiator;

	/**
	 * 最后审核人姓名
	 */
	private String audituser;

	/**
	 * 所有已审核人ID(以分","号隔开)
	 */
	private String auditusers;

	/**
	 * 所有子文档
	 */
	// private Collection<Document> childs;
	/**
	 * 父文档
	 */
	private Document parent;

	/**
	 * 是否为临时文档
	 */
	private boolean istmp = true;

	/**
	 * 文档版本
	 */
	private int versions;

	/**
	 * 流程ID
	 * 
	 * @deprecated since 2.6
	 */
	@Deprecated
	private String flowid;

	/**
	 * 流程状态标识
	 */
	private String stateLabel;

	/**
	 * 父文档ID
	 */
	private String parentid;

	/**
	 * 当前流程实例ID
	 */
	private String stateid;

	/**
	 * 审核用户ID
	 */
	private String audituserid;

	/**
	 * 最后一次流程处理的代码
	 */
	private String lastFlowOperation;
	/**
	 * 审批人列表
	 */
	private String auditorList;
	/**
	 * 关联流程
	 * 
	 * @deprecated since 2.6
	 */
	@Deprecated
	private transient BillDefiVO flowVO;

	/**
	 * 映射主键
	 */
	private String mappingId;

	/**
	 * 流程状态
	 */
	private int stateInt;

	/**
	 * 是否存在多实例
	 */
	private boolean mulitFlowState = false;

	/**
	 * 最后一次流程处理(运行,回退,暂停,结束等)
	 * 
	 * 
	 * @return Flow Operation Name
	 */
	public String getLastFlowOperation() {
		return lastFlowOperation;
	}

	public void setLastFlowOperation(String lastFlowOperation) {
		this.lastFlowOperation = lastFlowOperation;
	}

	/**
	 * 返回流程 ID
	 * 
	 * @return 流程 ID
	 * @throws Exception
	 * @uml.property name="flowid"
	 * @deprecated since 2.6
	 */
	@Deprecated
	public String getFlowid() throws Exception {
		if (flowid != null)
			return flowid;
		if (this.getStateid() != null && this.getState() != null) {
			this.setFlowid(this.getState().getFlowid());
		} else if(this.getForm() !=null){
			this.setFlowid(this.getForm().getOnActionFlow());
		}
		return flowid;
	}

	/**
	 * 设置流程 ID
	 * 
	 * @param flowid
	 * @uml.property name="flowid"
	 * @deprecated since 2.6
	 */
	@Deprecated
	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}

	/**
	 * 返回文档版本
	 * 
	 * @return 文档版本
	 * @uml.property name="version"
	 */
	public int getVersions() {
		return versions;
	}

	/**
	 * 设置文档版本
	 * 
	 * @param version
	 *            文档版本
	 * @uml.property name="version"
	 */
	public void setVersions(int versions) {
		this.versions = versions;
	}

	/**
	 * 根据子表单名，获取当前文档的子文档集合.
	 * 
	 * @param formName
	 *            表单名
	 * @return 子文档集合.
	 * @throws Exception
	 */
	public Collection<Document> getChilds(String formName) throws Exception {
		if (getApplicationid() != null) {
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,this.getApplicationid());
			return process.queryByParentID(getId(), formName);

		}
		return null;
	}

	/**
	 * 获取当前父文档的子文档集合.
	 * 
	 * @return 当前父文档的子文档集合.
	 * @throws Exception
	 */
	public Collection<Document> getChilds() throws Exception {
		if (getApplicationid() != null) {
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,this.getApplicationid());
			return process.queryByParentID(getId());

		}
		return null;
	}

	/**
	 * 根据父Document主键查询,获取父Document对象.
	 * 
	 * @return 父Document对象.
	 * @throws Exception
	 * @uml.property name="parent"
	 */
	public Document getParent() throws Exception {
		if (parent == null && parentid != null && parentid.length() > 0) {
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,getApplicationid());
			parent = (Document) process.doView(parentid);
		}
		return parent;
	}

	/**
	 * 设置 父Document.
	 * 
	 * @param parent
	 *            父Document
	 * @uml.property name="parent"
	 */
	public void setParent(Document parent) {
		this.parent = parent;
		if (parent != null) {
			this.parentid = parent.getId();
		}
	}

	/**
	 * 获取Document主键id
	 * 
	 * @return Document id
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置文档标识
	 * 
	 * @param id
	 *            the id to set
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 是否新文档(在新建时就已创建文档id, 故保存时只能通过增加此属性判断是否为新文档).
	 * 
	 * @return true 或者false(true为新文档，false为旧文档)
	 * @uml.property name="_new"
	 */
	public boolean is_new() {
		return _new;
	}

	/**
	 * set 是否新文档(在新建时就已创建文档id, 故保存时只能通过增加此属性判断是否为新文档).
	 * 
	 * @param _new
	 * @uml.property name="_new"
	 */
	public void set_new(boolean _new) {
		this._new = _new;
	}

	/**
	 * 返回修改Document最后的日期.
	 * 
	 * @return 修改Document最后的日期
	 * @uml.property name="lastmodified"
	 */
	public Date getLastmodified() {
		return lastmodified;
	}

	/**
	 * Set 修改Document最后的日期.
	 * 
	 * @param lastModified
	 *            Document最后的日期
	 * @uml.property name="lastmodified"
	 */
	public void setLastmodified(Date lastModified) {
		this.lastmodified = lastModified;
	}

	/**
	 * Document 构造函数
	 * 
	 */
	public Document() {
		_new = false;
		// _items_id = new HashMap(20);
		// _items_name = new HashMap(20);
	}

	/**
	 * 添加Document的Item(项目). 并根据相应的ITEM值类型设置相应item的值.
	 * ITEM的值类型为VALUE_TYPE_VARCHAR,此时将ITEM的值设置为字符串类型值.
	 * ITEM的值类型为VALUE_TYPE_NUMBER,此时将ITEM的值设置为数字类型值.
	 * ITEM的值类型为VALUE_TYPE_DATE,此时将ITEM的值设置为日期类型值.
	 * ITEM的值类型为VALUE_TYPE_TEXT,此时将ITEM的值设置为文本类型值.
	 * 
	 * @param item
	 *            Item对象
	 * @roseuid 41EBD760022F
	 */
	public void addItem(Item item) {
		if (item != null && item.getId() != null) {
			// Item old = findItemById(item.getId());
			Item old = findItem(item.getName().toUpperCase());
			if (old != null) {
				if (!old.getType().equals(Item.VALUE_TYPE_INCLUDE)) {
					if (old.getType().equals(Item.VALUE_TYPE_VARCHAR))
						old.setVarcharvalue(item.getVarcharvalue());
					else if (old.getType().equals(Item.VALUE_TYPE_NUMBER))
						old.setNumbervalue(item.getNumbervalue());
					else if (old.getType().equals(Item.VALUE_TYPE_DATE))
						old.setDatevalue(item.getDatevalue());
					else if (old.getType().equals(Item.VALUE_TYPE_TEXT))
						old.setTextvalue(item.getTextvalue());
					else
						old.setVarcharvalue(item.getVarcharvalue());
				}
				old.setValue(item.getValue());
				// _getItemNameMap(old.getName()).put(old.getName(), old);
			} else {
				// _items_id.put(item.getId() + "", item);
				_items_name.put(item.getName().toUpperCase(), item);
			}
		}
	}

	/**
	 * 添加Item集合
	 * 
	 * @param items
	 *            Item集合对象
	 */
	public void addItems(Collection<Item> items) {
		if (items != null && items.size() > 0) {
			Iterator<Item> iters = items.iterator();
			while (iters.hasNext()) {
				Item item = iters.next();
				if (item != null) {
					addItem(item);
				}
			}
		}
	}

	/**
	 * 根据项目item name 移除item
	 * 
	 * @param itemid
	 *            item id.
	 * @roseuid 41EBD7AF01CE
	 */
	// public void removeItem(long itemid) {
	// Item item = (Item) _items_id.get(itemid + "");
	// if (item != null) {
	// _items_id.remove(item.getId() + "");
	// _getItemNameMap(item.getName()).remove(item.getName());
	// }
	//
	// }
	public void removeItem(String itemname) {
		Item item = findItem(itemname);
		if (item != null) {
			_items_name.remove(itemname);
			_items_name.remove(item.getName());
		}
	}

	/**
	 * 根据Item对象实例移除item
	 * 
	 * @param item
	 *            Item对象
	 * @roseuid 41EBD7E70388
	 */
	public void removeItem(Item item) {
		if (item != null) {
			_items_name.remove(item.getName());
			// _items_id.remove(item.getId() + "");
		}
	}

	/**
	 * 根据字段名(fieldname)，获取Item. 如果没有相应名字的item,返回空.
	 * 
	 * @param fieldname
	 *            字列名
	 * @return item对象
	 * 
	 */
	public Item findItem(String fieldname) {
		if (fieldname != null) {
			Item item = _items_name.get(fieldname.toUpperCase());
			return item;
		}

		return null;
	}

	public String getFormShortName() {
		if (!StringUtil.isBlank(formname)) {
			return formname.substring((formname.lastIndexOf("/") + 1),
					formname.length());
		}
		return "";
	}

	/**
	 * 获取表单名
	 * 
	 * @return 表单名
	 * @uml.property name="formname"
	 */
	public String getFormname() {
		return formname;
	}

	/**
	 * 设置表单名
	 * 
	 * @param formname
	 *            表单名
	 * @uml.property name="formname"
	 */
	public void setFormname(String formname) {
		this.formname = formname;
	}

	/**
	 * 修改的日期
	 * 
	 * @return 修改的日期
	 * @uml.property name="auditdate"
	 */
	public Date getAuditdate() {
		return auditdate;
	}

	/**
	 * 设置修改的日期
	 * 
	 * @param auditdate
	 *            修改的日期
	 * @uml.property name="auditdate"
	 */
	public void setAuditdate(Date auditdate) {
		this.auditdate = auditdate;
	}

	/**
	 * 返回最后审核人姓名
	 * 
	 * @return 审核人姓名.
	 * @uml.property name="audituser"
	 */
	public String getAudituser() {
		return audituser;
	}

	/**
	 * 返回所有已审核人ID(以分号隔开)
	 * 
	 * @return 所有已审核人ID(以分号隔开).
	 * @uml.property name="auditusers"
	 */
	public String getAuditusers() {
		return auditusers;
	}

	/**
	 * 设置所有已审核人ID(以分号隔开)
	 * 
	 * @param auditusers
	 *            所有已审核人ID.
	 */
	public void setAuditusers(String auditusers) {
		this.auditusers = auditusers;
	}

	/**
	 * 获取流程发起人姓名
	 * 
	 * @return 流程发起人姓名
	 */
	public String getInitiator() {
		return initiator;
	}

	/**
	 * 设置流程发起人姓名
	 * 
	 * @param initiator
	 *            最后审核人姓名
	 */
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	/**
	 * 设置最后审核人姓名
	 * 
	 * @param audituser
	 *            最后审核人姓名
	 * @uml.property name="audituser"
	 */
	public void setAudituser(String audituser) {
		this.audituser = audituser;
	}

	/**
	 * 创建日期
	 * 
	 * @return 创建日期
	 * @uml.property name="created"
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * 设置创建日期
	 * 
	 * @param created
	 * @uml.property name="created"
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * 将Doc内容转换为String
	 */
	public String toString() {
		// // double i=1/0;
		StringBuffer sb = new StringBuffer();
		sb.append("Document:");
		sb.append("<formname>");
		sb.append(formname);
		sb.append("\n");
		// 获取所有item循环添加到Doc的StringBuffer中
		Iterator<Item> iter = this.getItems().iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			sb.append(item.toString());
			sb.append("\n");
		}
		// sb.append()
		return sb.toString();
	}

	/**
	 * 将Item内容转换为json形式
	 * 
	 * @return
	 */
	public String toJSON() {
		StringBuffer builder = new StringBuffer();
		builder.append("{");
		builder.append("'id': '" + this.getId() + "',");
		Collection<Item> items = getItems();
		if (items != null && items.size() > 0) {
			for (Iterator<?> iterator = getItems().iterator(); iterator
					.hasNext();) {
				Item item = (Item) iterator.next();
				builder.append("'").append(item.getName()).append("'");
				builder.append(": '");
				builder.append(item.getValue() != null ? item.getValue() : "");
				builder.append("',");
			}
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		builder.append("}");
		return builder.toString();
	}

	/**
	 * 获取所有参数
	 * 
	 * @return 所有参数.
	 * @uml.property name="_params"
	 */
	public ParamsTable get_params() {
		if (_params == null) {
			return new ParamsTable();
		}

		return _params;
	}

	/**
	 * 设置所有参数
	 * 
	 * @param _params
	 *            The _params to set.
	 * @uml.property name="_params"
	 */
	public void set_params(ParamsTable _params) {
		this._params = _params;
	}

	/**
	 * 根据Field name ,返回Double类型的item值.
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 整型值
	 * @throws Exception
	 */
	public int getItemValueAsInt(String fieldName) throws Exception {
		Item item = findItem(fieldName);
		if (item.getType() != null
				&& item.getType().equals(Item.VALUE_TYPE_NUMBER)) {
			Double rtn = item.getNumbervalue();
			return (rtn != null) ? rtn.intValue() : 0;

		} else {
			throw new Exception("Item " + fieldName
					+ "'s value is not a legal number!");
		}
	}

	/**
	 * 根据字段名（Field name），返回item值转换为字符串.
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 字符串
	 * @throws Exception
	 */
	public String getItemValueAsString(String fieldName) throws Exception {
		Item item = findItem(fieldName);
		String value = null;
		if (item != null && item.getType() != null) {
			if (item.getType().equals(Item.VALUE_TYPE_VARCHAR)) {
				value = item.getVarcharvalue();
			} else if (item.getType().equals(Item.VALUE_TYPE_NUMBER)) {
				if (item.getValue() != null
						&& item.getValue() instanceof Number) {
					DecimalFormat format = new DecimalFormat("##.##");
					value = format.format(item.getValue());
				} else {
					value = "";
				}
			} else if (item.getType().equals(Item.VALUE_TYPE_DATE)) {
				if (item.getDatevalue() != null) {
					value = DateUtil.getDateStr(item.getDatevalue());
				} else {
					value = "";
				}
			} else if (item.getType().equals(Item.VALUE_TYPE_TEXT)) {
				value = item.getTextvalue();
			} else if (item.getType().equals(Item.VALUE_TYPE_BLOB)) {
				value = getBlobItemValueAsString(fieldName, true);
			}
			return value == null ? "" : value;
		} else {
			log.warn("Item " + fieldName + "'s value can't get as string!");
			return "";
		}
	}

	/**
	 * 根据字段名（Field name），返回item值转换为字符串.
	 * 
	 * @param fieldName
	 *            字列名
	 * @param field
	 *            表单字段
	 * @return 字符串
	 * @throws Exception
	 */
	public String getItemValueAsString(String fieldName, FormField field)
			throws Exception {
		Item item = findItem(fieldName);
		String value = null;
		if (item != null && item.getType() != null) {
			if (item.getType().equals(Item.VALUE_TYPE_VARCHAR)) {
				value = item.getVarcharvalue();
			} else if (item.getType().equals(Item.VALUE_TYPE_NUMBER)) {
				if (item.getValue() != null
						&& item.getValue() instanceof Number) {
					String p = "##.##";
					if(field instanceof InputField){
						if(!StringUtil.isBlank(((InputField)field).getNumberPattern())){
							p = ((InputField)field).getNumberPattern();
						}
					}
					DecimalFormat format = new DecimalFormat(p);
					value = format.format(item.getValue());
				} else {
					value = "";
				}
			} else if (item.getType().equals(Item.VALUE_TYPE_DATE)) {
				if (item.getDatevalue() != null && field != null) {
					if (field instanceof DateField) {
						String pattern = ((DateField) field)
								.getDatePatternValue();
						if (StringUtil.isBlank(pattern))
							pattern = "yyyy-MM-dd";
						value = DateUtil.format(item.getDatevalue(), pattern);
					} else {
						value = DateUtil.getDateStr(item.getDatevalue());
					}
				} else {
					value = "";
				}
			} else if (item.getType().equals(Item.VALUE_TYPE_TEXT)) {
				value = item.getTextvalue();
			} else if (item.getType().equals(Item.VALUE_TYPE_BLOB)) {
				value = getBlobItemValueAsString(fieldName, true);
			}
			return value == null ? "" : value;
		} else {
			log.warn("Item " + fieldName + "'s value can't get as string!");
			return "";
		}
	}

	/**
	 * 根据字段名， 返回将图片url值转换为字符串型
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 字符串
	 * @throws Exception
	 */
	public String getImgItemUrlAsString(String fieldName) {
		Item item = findItem(fieldName);
		String value = "";
		if (item != null && item.getType() != null) {
			if (item.getType().equals(Item.VALUE_TYPE_VARCHAR)) {
				value = item.getVarcharvalue();
				if (value.indexOf("_") != -1) {
					value = value.substring(0, value.indexOf("_"));
				}
			}
		}
		return value;
	}

	/**
	 * 根据字段名，返回将item值转换为单精度浮点数型
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 单精度浮点数值
	 * @throws Exception
	 */
	public float getItemValueAsFloat(String fieldName) throws Exception {
		Item item = findItem(fieldName);
		if (item.getType() != null
				&& item.getType().equals(Item.VALUE_TYPE_NUMBER)) {
			Double rtn = item.getNumbervalue();
			return (rtn != null) ? rtn.floatValue() : 0;

		} else {
			throw new Exception("Item " + fieldName
					+ "'s value is not a legal number!");
		}
	}

	/**
	 * 根据字段名，返回将item值转换为双精度浮点数型
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 双精度浮点数值
	 * @throws Exception
	 */
	public double getItemValueAsDouble(String fieldName) throws Exception {
		Item item = findItem(fieldName);
		if (item != null && item.getType() != null) {
			if (item.getType().equals(Item.VALUE_TYPE_NUMBER)) {
				Double rtn = item.getNumbervalue();
				return (rtn != null) ? rtn.doubleValue() : 0;
			} else if (item.getType().equals(Item.VALUE_TYPE_VARCHAR)) {
				try {
					return Double.parseDouble(item.getVarcharvalue());
				} catch (Exception e) {
				}
			}
		}
		return 0.0;
	}

	/**
	 * 根据字段名，返回将item值转换为长整型
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 长整型值
	 * @throws Exception
	 */
	public long getItemValueAsLong(String fieldName) throws Exception {
		Item item = findItem(fieldName);
		if (item.getType() != null
				&& item.getType().equals(Item.VALUE_TYPE_NUMBER)) {
			Double rtn = item.getNumbervalue();
			return (rtn != null) ? rtn.longValue() : 0;

		} else {
			throw new Exception("Item " + fieldName
					+ "'s value is not a legal number!");
		}
	}

	/**
	 * 根据字段名，返回将item值转换为日期型
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 日期值
	 * @throws Exception
	 */
	public Date getItemValueAsDate(String fieldName) throws Exception {
		Item item = findItem(fieldName);

		if (item == null) {
			return null;
		}
		return item.getDatevalue();
		// if (item.getType() != null
		// && item.getType().equals(Item.VALUE_TYPE_DATE)) {
		// Date rtn = item.getDatevalue();
		// return rtn;
		//
		// } else {
		// throw new Exception("Item " + fieldName
		// + "'s value is not a legal date!");
		// }
	}

	/**
	 * 返回将BLOb类型item值转换为字符串型
	 * 
	 * @param fieldName
	 *            字列名
	 * @return 字符串型
	 * @throws Exception
	 */
	private String getBlobItemValueAsString(String fieldName, boolean pagediv) {
		// String _fieldname = fieldName;
		// String currpage = "";
		//
		// if (_params != null)
		// currpage = (String) _params.getParameter("_curpage");
		// if (pagediv && currpage != null && currpage.length() > 0)
		// _fieldname = formatFieldNameByPageDiv(fieldName, currpage);

		// Item item = findItem(_fieldname);

		return "";
	}

	/**
	 * 返回格式化后的字列名
	 * 
	 * @param fieldname
	 *            字列名
	 * @param page
	 * @return
	 */
	/*
	 * private String formatFieldNameByPageDiv(String fieldname, String page) {
	 * String _fieldname = fieldname;
	 * 
	 * int tagpos = _fieldname.indexOf("$"); if (tagpos > 0) { String prefix =
	 * _fieldname.substring(0, tagpos); _fieldname = prefix + "$" + page; }
	 * 
	 * return _fieldname; }
	 */

	/**
	 * 是否为子表单 ，true为子文档，返回false不为子文档
	 * 
	 * @return true为子文档，返回false不为子文档
	 * @uml.property name="_issubdoc"
	 */
	public boolean get_issubdoc() {
		return _issubdoc;
	}

	/**
	 * 设置是否为子文档 ，true为子文档，返回false不为子文档
	 * 
	 * @param _issubdoc
	 * @uml.property name="_issubdoc"
	 */
	public void set_issubdoc(boolean _issubdoc) {
		this._issubdoc = _issubdoc;
	}

	/**
	 * 返回表单主键
	 * 
	 * @return
	 * @uml.property name="formid"
	 */
	public String getFormid() {
		return formid;
	}

	/**
	 * 设置表单主键
	 * 
	 * @param formid
	 * @uml.property name="formid"
	 */
	public void setFormid(String formid) {
		this.formid = formid;
	}

	/**
	 * 返回用户
	 * 
	 * @return 返回用户
	 * @uml.property name="author"
	 */
	public BaseUser getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 * @uml.property name="author"
	 */
	public void setAuthor(BaseUser author) {
		this.author = author;
	}

	/**
	 * 设置作者
	 * 
	 * @param 作者
	 *            设置作者
	 * @uml.property name="author"
	 */
	public void setAuthor(String authorId) {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			if (authorId != null && authorId.trim().length() > 0) {
				this.author = (BaseUser) userProcess.doView(authorId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取作者部门索引
	 * @return
	 */
	public String getAuthorDeptIndex() {
		return authorDeptIndex;
	}

	/**
	 * 设置作者部门索引
	 * @param authorDeptIndex
	 */
	public void setAuthorDeptIndex(String authorDeptIndex) {
		this.authorDeptIndex = authorDeptIndex;
	}

	/**
	 * 返回流程状态FlowStateRT对象
	 * 
	 * @return 流程状态FlowStateRT对象
	 * @throws Exception
	 * @uml.property name="state"
	 */
	public FlowStateRT getState() throws Exception {
		if (state == null && getStateid() != null
				&& getStateid().trim().length() > 0) {
			FlowStateRTProcess stateProcess = new FlowStateRTProcessBean(
					getApplicationid());
			state = (FlowStateRT) stateProcess.doView(getStateid());
		}
		if (state != null)
			state.setDocument(this);
		return state;
	}
	
	/**
	 * 返回流程状态FlowStateRT对象
	 * 
	 * @return 流程状态FlowStateRT对象
	 * @throws Exception
	 * @uml.property name="state"
	 */
	public FlowStateRT getState(String currNodeId) throws Exception {
		if (state == null && getStateid() != null
				&& getStateid().trim().length() > 0) {
			FlowStateRTProcess stateProcess = new FlowStateRTProcessBean(getApplicationid());
			state = (FlowStateRT) stateProcess.doView(getStateid());
		}
		if (state != null){
			FlowDiagram fd = getFlowVO().toFlowDiagram();
			Element element = fd.getElementByID(currNodeId);
			if(element!=null && element instanceof ManualNode){
				ManualNode manualNode = (ManualNode)element;
				if(manualNode.isFrontEdit){
					FlowStateRTProcess stateProcess = new FlowStateRTProcessBean(getApplicationid());
					state = (FlowStateRT) stateProcess.doView(getStateid());
				}
			}
			state.setDocument(this);
		}
		return state;
	}

	/**
	 * Set 流程状态
	 * 
	 * @param state
	 * @uml.property name="state"
	 */
	public void setState(FlowStateRT state) {
		this.state = state;
		if (state != null)
			this.stateid = state.getId();
	}

	/**
	 * 添加字符串类型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addStringItem(String itemName, String itemValue)
			throws Exception {
		Item item = new Item();
		item.setType(Item.VALUE_TYPE_VARCHAR);
		item.setName(itemName);
		item.setValue(itemValue);
		item.setDocument(this);
		item.setFormname(this.getFormname());
		item.setId(Tools.getSequence());
		this.addItem(item);
	}

	/**
	 * 添加大文本类型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addTextItem(String itemName, String itemValue) throws Exception {
		Item item = new Item();
		item.setType(Item.VALUE_TYPE_TEXT);
		item.setName(itemName);
		item.setValue(itemValue);
		item.setDocument(this);
		item.setFormname(this.getFormname());
		item.setId(Tools.getSequence());
		this.addItem(item);
	}

	/**
	 * 添加日期类型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addDateItem(String itemName, Date itemValue) throws Exception {
		Item item = new Item();
		item.setType(Item.VALUE_TYPE_DATE);
		item.setName(itemName);
		item.setValue(itemValue);
		item.setDocument(this);
		item.setFormname(this.getFormname());
		item.setId(Tools.getSequence());
		this.addItem(item);
	}

	/**
	 * 添加双精度浮点类型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addDoubleItem(String itemName, double itemValue)
			throws Exception {
		Item item = new Item();
		item.setType(Item.VALUE_TYPE_NUMBER);
		item.setName(itemName);
		item.setValue(new Double(itemValue));
		item.setDocument(this);
		item.setFormname(this.getFormname());
		item.setId(Tools.getSequence());
		this.addItem(item);
	}

	/**
	 * 添加长整型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addLongItem(String itemName, long itemValue) throws Exception {
		addDoubleItem(itemName, (double) itemValue);
	}

	/**
	 * 添加整型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addIntItem(String itemName, int itemValue) throws Exception {
		addDoubleItem(itemName, (double) itemValue);
	}

	/**
	 * 添加单精度浮点类型item
	 * 
	 * @param itemName
	 *            item 名
	 * @param itemValue
	 *            item的值
	 * @throws Exception
	 */
	public void addFloatItem(String itemName, float itemValue) throws Exception {
		addDoubleItem(itemName, (double) itemValue);
	}

	/**
	 * 2.6版本新增方法
	 * 
	 * @param proName
	 * @return
	 */
	public Object getValueByProName(String proName) {
		if (proName.equalsIgnoreCase("StateLabel"))
			return getStateLabel();
		if (proName.equalsIgnoreCase("AuditDate"))
			return getAuditdate();
		if (proName.equalsIgnoreCase("LastModified"))
			return getLastmodified();
		if (proName.equalsIgnoreCase("Created"))
			return getCreated();
		if (proName.equalsIgnoreCase("Author")) {
			if (getAuthor() != null)
				return getAuthor().getName();
		}
		if (proName.equalsIgnoreCase("AuditorNames"))
			return getAuditorNames();
		if (proName.equalsIgnoreCase("LastFlowOperation"))
			return getLastFlowOperation();
		if (proName.equalsIgnoreCase("SortId"))
			return getSortId();
		if (proName.equalsIgnoreCase("FormName"))
			return getFormname();
		if (proName.equalsIgnoreCase("Id"))
			return getId();
		return null;
	}

	/**
	 * 根据常用属性名称,获取Document属性的值
	 * ($StateLabel,$AuditDate,$LastModified,$Created,$Author
	 * ,$AuditorNames,$LastFlowOperation,$SortId,$FormName,$Id) 以上分别为系统变量
	 * 查询时直接在前加$符
	 * 
	 * @param propName
	 *            常用属性名称
	 * @return 常用属性名称,获取与Document相关联的值
	 * @throws Exception
	 */
	public String getValueByPropertyName(String propName) throws Exception {
		StringBuffer buffer = new StringBuffer();

		if (propName.equalsIgnoreCase("StateLabel")) {
			buffer.append(stateLabel != null ? stateLabel : "");
		} else if (propName.equalsIgnoreCase("AuditDate")) {
			if (this.getAuditdate() != null) {
				String auditdate = "";
				try {
					auditdate = DateUtil.format(getAuditdate(), "yyyy-MM-dd");
				} catch (Exception e) {
					e.printStackTrace();
				}
				buffer.append(auditdate);
			}
		} else if (propName.equalsIgnoreCase("LastModified")) {
			if (this.getLastmodified() != null) {
				String lastmodified = "";
				try {
					lastmodified = DateUtil.format(getLastmodified(),
							"yyyy-MM-dd");
				} catch (Exception e) {
					e.printStackTrace();
				}
				buffer.append(lastmodified);
			}
		} else if (propName.equalsIgnoreCase("Created")) {
			if (this.getCreated() != null) {
				String created = "";
				try {
					created = DateUtil.format(getCreated(), "yyyy-MM-dd");
				} catch (Exception e) {
					e.printStackTrace();
				}
				buffer.append(created);
			}
		} else if (propName.equalsIgnoreCase("Author")) {
			if (this.getAuthor() != null) {
				buffer.append(getAuthor().getName());
			}
		} else if (propName.equalsIgnoreCase("AuditorNames")) {
			buffer.append(auditorNames != null ? auditorNames : "");
		} else if (propName.equalsIgnoreCase("LastFlowOperation")) {
			buffer.append(lastFlowOperation != null ? lastFlowOperation : "");
		} else if (propName.equalsIgnoreCase("SortId")) {
			buffer.append(sortId != null ? sortId : "");
		} else if (propName.equalsIgnoreCase("FormName")) {
			buffer.append(formname != null ? formname : "");
		} else if (propName.equalsIgnoreCase("Id")) {
			buffer.append(id != null ? id : "");
		}

		return buffer.toString();
	}

	/**
	 * 根据用户，获取第一结点列表
	 * 
	 * @param user
	 * @return 第一结点列表
	 * @throws Exception
	 */
	public Collection<? extends Node> getFirstNodeList(WebUser user)
			throws Exception {
		return StateMachine.getFirstNodeList(getId(), getFlowid(), user);
	}

	/**
	 * 返回是否显示Document
	 * 
	 * @return true or false
	 * @uml.property name="istmp"
	 */
	public boolean getIstmp() {
		return istmp;
	}

	/**
	 * 设置是否显示Document
	 * 
	 * @param istmp
	 * @uml.property name="istmp"
	 */
	public void setIstmp(boolean istmp) {
		this.istmp = istmp;
	}

	/**
	 * 浅克隆文档
	 * 
	 * @return (Document)克隆的文档
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			Document cloneDoc = (Document) ObjectUtil.clone(this);
			if (cloneDoc != null) {
				FlowStateRT state = cloneDoc.getState();
				if (state != null) {
					state.getNoderts();
					state.getActors();

					Collection<NodeRT> noderRTList = state.getNoderts();
					for (Iterator<NodeRT> iterator = noderRTList.iterator(); iterator
							.hasNext();) {
						NodeRT nodeRT = iterator.next();
						nodeRT.getActorrts();
					}
				}
				cloneDoc.getChilds();
				return cloneDoc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.clone();
	}

	/**
	 * 深度克隆
	 * 
	 * @return (Document)克隆的文档
	 */
	public Object deepClone() {
		return ObjectUtil.clone(this);
	}

	/**
	 * 获取所有item
	 * 
	 * @return item集合
	 */
	public Collection<Item> getItems() {
		return _items_name.values();
	}

	/**
	 * 设置 所有Item
	 * 
	 * @param items
	 *            collection of the item
	 */
	public void setItems(Collection<Item> items) {
		// this._items_id.clear();// = new HashMap();
		_items_name.clear();// = new HashMap();
		if (items != null) {
			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				// _items_id.put(item.getId(), item);
				addItem(item);
			}
		}
	}

	/**
	 * 返回状态标识
	 * 
	 * @return 状态标识
	 * @uml.property name="stateLabel"
	 */
	public String getStateLabel() {
		if (!StringUtil.isBlank(this.stateLabel))
			return stateLabel;

		try {
			FlowStateRTProcess stateProcess = new FlowStateRTProcessBean(getApplicationid());
			Collection<FlowStateRT> instances = stateProcess.getFlowStateRTsByDocId(id);
			StringBuffer label = new StringBuffer();
			for(Iterator<FlowStateRT> iter = instances.iterator();iter.hasNext();){
				FlowStateRT instance = iter.next();
				if (instance != null) {
					label.append(instance.getStateLabel()).append(":");
				}
			}
			if(label.length()>0) label.setLength(label.length()-1);
			stateLabel = label.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stateLabel;
	}

	/**
	 * 返回流程状态以逗号分割
	 * 
	 * @return
	 */
	public Collection<String> getStateLableList() {
		if (stateid != null) {
			if (stateLabel != null && stateLabel.trim().length() > 0) {
				String[] labelList = stateLabel.split(",");
				return Arrays.asList(labelList);
			} else {
				String[] labelList = getStateLabel().split(",");
				return Arrays.asList(labelList);
			}
		}
		return new ArrayList<String>();
	}

	/**
	 * 设置状态标识
	 * 
	 * @param stateLabel
	 * @uml.property name="stateLabel"
	 */
	public void setStateLabel(String stateLabel) {
		this.stateLabel = stateLabel;
	}

	/**
	 * 返回父Document主键
	 * 
	 * @return
	 * @uml.property name="parentid"
	 */
	public String getParentid() {
		return parentid;
	}

	/**
	 * 设置父Document 主键
	 * 
	 * @param parentid
	 * @uml.property name="parentid"
	 */
	public void setParent(String parentid) {
		this.parentid = parentid;
	}

	/**
	 * 返回当前实例ID
	 * 
	 * @return
	 * @uml.property name="stateid"
	 */
	public String getStateid() {
		return stateid;
	}

	/**
	 * 设置当前实例ID
	 * 
	 * @param stateid
	 */
	public void setState(String stateid) {
		this.stateid = stateid;
	}

	/**
	 * 返回设置审计者 id
	 * 
	 * @return 设置审计者 id
	 * @uml.property name="audituserid"
	 */

	public String getAudituserid() {
		return audituserid;
	}

	/**
	 * 设置审计者 id
	 * 
	 * @param audituserid
	 * @uml.property name="audituserid"
	 */
	public void setAudituserid(String audituserid) {
		this.audituserid = audituserid;
	}

	/**
	 * 判断当前用户是否可对文档进行编辑
	 * 
	 * @param webUser
	 *            当前在线用户
	 * @return
	 * @throws Exception
	 */
	public boolean isEditAble(WebUser webUser) throws Exception {
		if (!isEditAbleLoaded) {
			FormProcess formProcess = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);

			boolean isEdit_bs = true;
			// 根据表单打开字段,判断用户是否可编辑
			Form form = (Form) formProcess.doView(this.getFormid());
			if (form != null && form.getIseditablescript() != null
					&& form.getIseditablescript().trim().length() > 0) {
				IRunner runner = JavaScriptFactory.getInstance(this
						.get_params().getSessionid(), form.getApplicationid());
				runner.initBSFManager(this, this.get_params(), webUser,
						new ArrayList<ValidateMessage>());

				StringBuffer label = new StringBuffer();
				label.append("DocumentContent.Form(").append(form.getId())
						.append(")." + form.getName())
						.append(".runIsEditAbleScript");

				Object result = runner.run(label.toString(),
						form.getIseditablescript());
				if (result != null && result instanceof Boolean) {
					isEdit_bs = (((Boolean) result).booleanValue());
				}
			}

			// 根据流程权限,判断用户是否可编辑
			isEditAble = StateMachineHelper.isDocEditUser(this, webUser)
					&& isEdit_bs;
			isEditAbleLoaded = true;
		}

		return isEditAble;
	}

	/**
	 * 设置用户对文档进行编辑
	 * 
	 * @param isEditAble
	 */
	public void setEditAble(boolean isEditAble) {
		this.isEditAble = isEditAble;
	}

	/**
	 * 根据当前用户获取Document的流程权限
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public FieldPermissionList getFieldPermList(WebUser user) throws Exception {
		// 从Session缓存中获取Parent
		Document parent = (Document) user.getFromTmpspace(getParentid());
		if (parent == null) {
			// 从DataBase中获取Parent
			parent = getParent() != null ? getParent() : this;
		}

		String fieldpermlist = "";
		if (fieldPermList == null && parent.getFlowVO() != null) {
			if (parent.getId() != null && !(parent.getId()).equals("")) {
				NodeRT nodert = StateMachine.getCurrUserNodeRT(parent, user);

				FlowDiagram fd = parent.getFlowVO().toFlowDiagram();
				if (nodert != null) {
					Element element = fd.getElementByID(nodert.getNodeid());
					if (element instanceof ManualNode) {
						ManualNode node = ((ManualNode) element);
						fieldpermlist = getFieldPermListInJSON(node.fieldpermlist);
					}
				}
			}

			if (!StringUtil.isBlank(fieldpermlist)) {
				this.fieldPermList = FieldPermissionList.parser(fieldpermlist);
				log.info("[FORMNAME: " + this.getFormname()
						+ "]--->[FIELDPERMLIST: \"" + fieldpermlist + "\"]");
			}
		}

		return fieldPermList;
	}

	/**
	 * 根据流程定义权限描述,此描述为JSON结构,得出此Document读写权限.
	 * 
	 * @return 权限描述,得出此Document读写权限.
	 * @throws Exception
	 */
	private String getFieldPermListInJSON(String JSONStr) {
		String rtn = "";
		if (JSONStr == null || JSONStr.trim().length() == 0)
			return rtn;
		try {
			JSONArray jsonArray = JSONArray.fromObject(StringUtil
					.dencodeHTML(JSONStr));
			if (!jsonArray.isEmpty()) {
				for (Iterator<?> iter = jsonArray.iterator(); iter.hasNext();) {
					JSONObject obj = (JSONObject) iter.next();
					String formid = (String) obj.get("formid");
					if (formid.equals(this.getFormid())) {
						rtn = (String) obj.get("fieldPermList");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * 可以审核此文档的用户列表(冗余字段,以','号分隔)
	 * 
	 * @return auditor name list
	 */
	public String getAuditorNames() {
		if (!StringUtil.isBlank(this.auditorNames))
			return auditorNames;

		try {
			FlowStateRTProcess stateProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, getApplicationid());
			Collection<FlowStateRT> instances = stateProcess.getFlowStateRTsByDocId(id);
			StringBuffer names = new StringBuffer();
			for(Iterator<FlowStateRT> iter = instances.iterator();iter.hasNext();){
				FlowStateRT instance = iter.next();
				if (instance != null) {
					names.append(instance.getAuditorNames()).append(":");
				}
			}
			if(names.length()>0) names.setLength(names.length()-1);
			auditorNames = names.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return auditorNames;
	}
	

	/**
	 * 设置审批人
	 * 
	 * @param auditorNames
	 */
	public void setAuditorNames(String auditorNames) {
		this.auditorNames = auditorNames;
	}

	/**
	 * 最后修改人
	 * 
	 * @return
	 * @uml.property name="lastmodifiUser"
	 */

	public String getLastmodifier() {
		return lastmodifier;
	}

	/**
	 * 设置最后修改人
	 * 
	 * @param lastmodifiUser
	 * @uml.property name="lastmodifiUser"
	 */
	public void setLastmodifier(String lastmodifier) {
		this.lastmodifier = lastmodifier;
	}

	/**
	 * 比较上一个版本与现在版本的Field 的值是否一致
	 * 
	 * @return
	 */
	public boolean compareFieldValue(Document oldDoc, Document newDoc)
			throws Exception {

		boolean flag = true;

		if (oldDoc != null && newDoc != null) {
			if (oldDoc.getLastmodifier() != null
					&& newDoc.getLastmodifier() != null
					&& (!(oldDoc.getLastmodifier().equals(newDoc
							.getLastmodifier())))) {
				Collection<Item> items = oldDoc.getItems();
				Iterator<Item> iter = items.iterator();
				while (iter.hasNext()) {
					Item oldItem = iter.next();
					if (oldItem != null) {
						String itemName = oldItem.getName();
						Item newItem = newDoc.findItem(itemName);
						if (newItem != null) {
							if (oldItem.getValue() != null
									&& newItem.getValue() != null) {
								if ((oldItem.getValue() instanceof Date)
										&& (newItem.getValue() instanceof Date)) {
									Date oldItemValue = (Date) oldItem
											.getValue();
									Date newItemValue = (Date) newItem
											.getValue();
									if (oldItemValue.compareTo(newItemValue) != 0) {
										flag = false;
										break;
									}
								} else {
									if (!(oldItem.getValue().equals(newItem
											.getValue()))) {
										flag = false;
										break;
									}
								}

							} else if ((oldItem.getValue() == null && newItem
									.getValue() == null)) {

							} else {
								flag = false;
								break;
							}
						}

					}
				}

				return flag;
			}

		}
		return true;
	}

	/**
	 * 用比较器来比较Item名,用于排序Item
	 * 
	 * @param doc
	 *            文档
	 * @return 比较后的Item集合
	 */
	public Collection<String> compareTo(Document doc) {
		if (doc != null)
			return compareWith(doc.getItems());
		else {
			return this._items_name.keySet();
		}

	}

	/**
	 * 用比较器来比较Item名,用于排序Item
	 * 
	 * @param items
	 *            item集合
	 * @return 比较后的Item集合
	 */
	public Collection<String> compareWith(Collection<Item> items) {
		Collection<String> result = new ArrayList<String>();
		for (Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
			Item item = iterator.next();
			if (item != null) {
				Item item2 = this.findItem(item.getName());
				if (item2 == null) {
					result.add(item.getName());
				} else if ((item.getValue() == null && item2.getValue() != null)
						|| (item.getValue() != null && item2.getValue() == null)) {
					result.add(item.getName());
				} else if (item.getValue() != null && item2.getValue() != null) {
					if (!item.getValue().equals(item2.getValue()))
						result.add(item.getName());
				}
			}
		}

		return result;
	}

	/**
	 * 将文档转换成Map结构
	 * 
	 * @return Map形式的文档
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> rtn = new LinkedHashMap<String, Object>();
		// add properties to map
		rtn.put("id", this.getId());
		rtn.put("stateLabel", this.getStateLabel());

		// add items to map
		Iterator<Item> iter = this.getItems().iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			rtn.put(item.getName(), item.getValue());
		}

		return rtn;
	}

	/**
	 * 获取当前审批人列表
	 * 
	 * @return 当前审批人列表,以json的形式返回
	 * @throws Exception
	 * @uml.property name="auditroList"
	 */
	public String getAuditorList() throws Exception {
		if (StringUtil.isBlank(this.auditorList)) {
			auditorList = calculateAuditorList();
		}
		return auditorList;
	}

	/**
	 * 设置文档的处理人
	 * 
	 * @param auditorList
	 */
	public void setAuditorList(String auditorList) {
		this.auditorList = auditorList;
	}

	public boolean isMulitFlowState() {
		return mulitFlowState;
	}

	public void setMulitFlowState(boolean mulitFlowState) {
		this.mulitFlowState = mulitFlowState;
	}

	/**
	 * 获取流程定义对象
	 * 
	 * @return
	 * @deprecated since 2.6
	 */
	@Deprecated
	public BillDefiVO getFlowVO() {
		try {
			if (flowVO != null) {
				return flowVO;
			}else {
				FlowStateRT flowStateRT = getState();
				if(flowStateRT !=null) this.flowVO = flowStateRT.getFlowVO();
			}
			/*
			// 前台手动调整流程
			FlowStateRT flowStateRT = getState();
			if (flowStateRT != null) {
				if (flowStateRT.getFlowName() != null) {
					flowVO.setSubject(flowStateRT.getFlowName());
				}
				if (flowStateRT.getLastModifierId() != null) {
					flowVO.setAuthorname(flowStateRT.getLastModifierId());
				}
				if (flowStateRT.getFlowXML() != null) {
					flowVO.setFlow(flowStateRT.getFlowXML());
				}
				if (flowStateRT.getLastModified() != null) {
					flowVO.setLastmodify(flowStateRT.getLastModified());
				}
			}
			*/
		} catch (Exception e) {
			log.error("", e);
		}
		return flowVO;
	}

	/**
	 * 
	 * @param flowVO
	 * @deprecated since 2.6
	 */
	@Deprecated
	public void setFlowVO(BillDefiVO flowVO) {
		this.flowVO = flowVO;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public Form getForm() throws Exception {
		FormProcess process = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) process.doView(getFormid());

		return form;
	}

	/**
	 * 根据字段名称获取值($开头的为Document基本属性，其他为Item名称)
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return 字段值
	 */
	public String getValueByField(String fieldName) {
		StringBuffer rtn = new StringBuffer();
		try {
			if (fieldName.startsWith("$")) {
				String propName = fieldName.replace("$", "");
				rtn.append(getValueByPropertyName(propName));
			} else {
				rtn.append(getItemValueAsString(fieldName));
			}
		} catch (Exception e) {
			log.warn("getValueByField", e);
		}

		return rtn.toString();
	}

	/**
	 * 获取父流程文档
	 * 
	 * @return 父流程文档
	 */
	public Document getParentFlowDocument() {
		try {
			FlowStateRT state = this.getState();

			if (state != null && state.getParent() != null) {
				return state.getParent().getDocument();
			}
		} catch (Exception e) {
			log.error("getParentFlowDocument", e);
		}

		return null;
	}

	public Collection<Document> getSubFlowDocuments() {
		Collection<Document> rtn = new ArrayList<Document>();

		try {
			FlowStateRT state = this.getState();
			if (state != null) {
				DocumentProcess docProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,getApplicationid());
				Collection<FlowStateRT> states = state.getSubStates();
				if (states != null && !states.isEmpty()) {
					for (Iterator<FlowStateRT> iterator = states.iterator(); iterator
							.hasNext();) {
						FlowStateRT subflowState = iterator
								.next();
						String docid = subflowState.getDocid();
						rtn.add((Document) docProcess.doView(docid));
					}
				}
			}
		} catch (Exception e) {
			log.error("getSubFlowDocuments", e);
		}

		return rtn;
	}

	/**
	 * 通过查询获取审批人
	 * 
	 * @return 审批人 (JSON)
	 * @throws Exception
	 * @deprecated since 2.6
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public String calculateAuditorList() throws Exception {
		Map<String, Collection<?>> auditorJSON = new JSONObject();
		if (getState() != null) {
			Collection<NodeRT> noderts = getState().getNoderts();
			for (Iterator<NodeRT> iterator = noderts.iterator(); iterator
					.hasNext();) {
				NodeRT nodeRT = iterator.next();
				auditorJSON.put(nodeRT.getNodeid(), nodeRT.getActorIdList());
			}
		}
		return auditorJSON.toString();
	}

	/**
	 * 根据节点获取审批人
	 * 
	 * @return 审批人 (JSON)
	 * @throws Exception
	 * @deprecated since 2.6
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public String calculateAuditorList(NodeRT nodeRT) throws Exception {
		Map<String, Collection<?>> auditorJSON = new JSONObject();
		auditorJSON.put(nodeRT.getNodeid(), nodeRT.getActorIdList());
		return auditorJSON.toString();
	}

	public int getStateInt() {
		return stateInt;
	}

	public void setStateInt(int stateInt) {
		this.stateInt = stateInt;
	}
	
	/**
	 * 获取流程流程实例的序号
	 * @return
	 * @throws Exception
	 */
	public int getFlowPosition() throws Exception {
		if (this.getState()== null) return 0;
		return this.getState().getPosition();
	}
}
