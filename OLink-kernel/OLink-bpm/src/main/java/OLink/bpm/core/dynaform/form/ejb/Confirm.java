package OLink.bpm.core.dynaform.form.ejb;

import java.util.Collection;
import java.util.HashSet;

import OLink.bpm.core.table.constants.ConfirmConstant;
import org.apache.log4j.Logger;

/**
 * @author nicholas
 */
public class Confirm {

	private String id;

	private String oldFieldId;

	private String newFieldId;

	private int msgKeyCode;

	private String formName;

	private String fieldName;

	private boolean dropTable;

	private boolean dropColumn;
    private static final Logger log = Logger.getLogger(Confirm.class);
	/**
	 * 获取是否移除列
	 * 
	 * @return 移除列
	 * @uml.property name="dropColumn"
	 */
	public boolean isDropColumn() {
		return dropColumn;
	}

	/**
	 * 设置是否移除列
	 * 
	 * @param dropColumn
	 *            移除列
	 * @uml.property name="dropColumn"
	 */
	public void setDropColumn(boolean dropColumn) {
		this.dropColumn = dropColumn;
	}

	/**
	 * 获取是否移除表
	 * 
	 * @return 是否移除表
	 * @uml.property name="dropTable"
	 */
	public boolean isDropTable() {
		return dropTable;
	}

	/**
	 * 设置是否移除表
	 * 
	 * @param dropTable
	 *            是否移除表
	 * @uml.property name="dropTable"
	 */
	public void setDropTable(boolean dropTable) {
		this.dropTable = dropTable;
	}

	/**
	 * 构造方法,初始化参数
	 * 
	 * @param formName
	 *            表名
	 * @param msgKeyCode
	 *            主键
	 */
	public Confirm(String formName, int msgKeyCode) {
		this.formName = formName;
		this.msgKeyCode = msgKeyCode;
	}

	public Confirm() {
	}

	/**
	 * 获取消息的名称
	 * 
	 * @return 名称
	 */
	public String getMsgKeyName() {
		return ConfirmConstant.getMsgKeyName(msgKeyCode);
	}

	/**
	 * 获取消息的代码
	 * 
	 * @return the msgKeyCode
	 * @uml.property name="msgKeyCode"
	 */
	public int getMsgKeyCode() {
		return msgKeyCode;
	}

	/**
	 * 设置消息的代码
	 * 
	 * @param msgKeyCode
	 *            the msgKeyCode to set
	 * @uml.property name="msgKeyCode"
	 */
	public void setMsgKeyCode(int msgKeyCode) {
		this.msgKeyCode = msgKeyCode;
	}

	/**
	 * 获取表名
	 * 
	 * @return 表名
	 */
	public String getFormName() {
		return formName;
	}

	/**
	 * 设置表名
	 * 
	 * @param formName
	 *            表名
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}

	/**
	 * 获取字段名
	 * 
	 * @return 字段名
	 * @uml.property name="oldFieldName"
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * 设置字段名
	 * 
	 * @param oldFieldName
	 *            字段名
	 * @uml.property name="oldFieldName"
	 */
	public void setFieldName(String oldFieldName) {
		this.fieldName = oldFieldName;
	}

	/**
	 * 获取新的Field的标识
	 * 
	 * @return 新的Field的标识
	 * @uml.property name="newFieldId"
	 */
	public String getNewFieldId() {
		return newFieldId;
	}

	/**
	 * 设置新的Field的标识
	 * 
	 * @param newFieldId
	 *            新的Field的标识
	 * @uml.property name="newFieldId"
	 */
	public void setNewFieldId(String newFieldId) {
		this.newFieldId = newFieldId;
	}

	/**
	 * 获取旧的Field的标识
	 * 
	 * @return 旧的Field的标识
	 * @uml.property name="oldFieldId"
	 */
	public String getOldFieldId() {
		return oldFieldId;
	}

	/**
	 * 设置旧的Field的标识
	 * 
	 * @param oldFieldId
	 *            旧的Field的标识
	 * @uml.property name="oldFieldId"
	 */
	public void setOldFieldId(String oldFieldId) {
		this.oldFieldId = oldFieldId;
	}

	/**
	 * 获取标识
	 * 
	 * @return 标识
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置标识
	 * 
	 * @param id
	 *            标识
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Confirm && formName != null && fieldName != null) {
			return formName.equals(((Confirm) obj).getFormName()) && fieldName.equals(((Confirm) obj).getFieldName());
		}
		return false;
	}

	public int hashCode() {
		if (formName != null && fieldName != null) {
			int result = 17;
			result = 37 * result + formName.hashCode();
			result = 37 * result + fieldName.hashCode();
			return result;
		} else {
			return super.hashCode();
		}
	}

	/**
	 * 获取提示消息
	 * 
	 * @return 消息内容
	 */
	public String getMessage() {
		String message = "";
		switch (msgKeyCode) {
		case ConfirmConstant.FIELD_DATA_EXIST:
			message = formName + ".(" + fieldName + ") {*[core.field.has.datas]*}";
			break;
		case ConfirmConstant.FIELD_DUPLICATE:
			message = formName + ".(" + fieldName + ") {*[core.field.name.was.duplicate]*}";
			break;
		case ConfirmConstant.FIELD_EXIST:
			message = formName + ".(" + fieldName + ") {*[core.field.has.exist]*}";
			break;
		case ConfirmConstant.FIELD_TYPE_INCOMPATIBLE:
			message = formName + ".(" + fieldName + ") {*[core.field.type.incompatible]*}";
			break;
		case ConfirmConstant.FORM_DATA_EXIST:
			message = "(" + formName + ") {*[core.form.has.datas]*}";
			break;
		case ConfirmConstant.FORM_EXIST:
			message = "(" + formName + ") {*[core.form.has.exist]*}";
			break;

		default:
			break;
		}
		return message;
	}

	public static void main(String[] args) {
		Collection<Confirm> confirms = new HashSet<Confirm>();

		Confirm confirmA = new Confirm();
		confirmA.setMsgKeyCode(ConfirmConstant.FIELD_DATA_EXIST);
		confirmA.setFormName("FormA");
		confirmA.setFieldName("FieldA");

		Confirm confirmB = new Confirm();
		confirmB.setFormName("FormA");
		confirmB.setFieldName("FieldA");

		confirms.add(confirmA);
		confirms.add(confirmB);

		log.info("Contians: " + confirms.contains(confirmB));
		log.info("Size: " + confirms.size());
		log.info("Message: " + confirmA.getMessage());
	}
}
