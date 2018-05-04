package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.IncludeField;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 表单的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 * 
 */
public class FormOverview implements IOverview {

	public static class FormActivtyType {
		public static final Map<Integer, String> activity_map = new HashMap<Integer, String>();
		static {
			activity_map.put(ActivityType.NOTHING, "无");
			activity_map.put(ActivityType.DOCUMENT_UPDATE, "保存(不带流程)");
			activity_map.put(ActivityType.SAVE_SARTWORKFLOW, "保存(带流程)");
			activity_map.put(ActivityType.SAVE_BACK, "保存并返回");
			activity_map.put(ActivityType.SAVE_NEW_WITH_OLD, "保存并新建(带久数据)");
			activity_map.put(ActivityType.SAVE_NEW_WITHOUT_OLD, "保存并新建(不带久数据)");
			activity_map.put(ActivityType.SAVE_WITHOUT_VALIDATE, "保存(不进行校验)");
			activity_map.put(ActivityType.DOCUMENT_COPY, "保存并复制");
			activity_map.put(ActivityType.SAVE_CLOSE_WINDOW, "保存并关闭窗口");
			activity_map.put(ActivityType.DOCUMENT_BACK, "返回");
			activity_map.put(ActivityType.PRINT, "打印");
			activity_map.put(ActivityType.PRINT_WITHFLOWHIS, "带流程历史打印");
			activity_map.put(ActivityType.WORKFLOW_PROCESS, "流程处理");
			activity_map.put(ActivityType.WORKFLOW_RETRACEMENT, "流程回撤");
			activity_map.put(ActivityType.START_WORKFLOW, "流程启动");
			activity_map.put(ActivityType.CLOSE_WINDOW, "关闭窗口");
			activity_map.put(ActivityType.DOCUMENT_EDIT_AUDITOR, "编辑审批人");
			activity_map.put(ActivityType.EXPTOPDF, "PDF导出");
			activity_map.put(ActivityType.FILE_DOWNLOAD, "文件下载");
			activity_map.put(ActivityType.SIGNATURE, "电子签章");
			activity_map.put(ActivityType.FLEX_PRINT, "动态打印");
			activity_map.put(ActivityType.JUMP, "跳转");
		}
	}

	public static class FormFieldType {
		public static final Map<String, String> field_map = new HashMap<String, String>();
		static {
			field_map.put("InputField", "单行文本框");
			field_map.put("NumberField", "数字框");
			field_map.put("TextAreaField", "多行文本框");
			field_map.put("SelectField", "下拉框");
			field_map.put("DateField", "日期选择框");
			field_map.put("CheckboxField", "复选框");
			field_map.put("RadioField", "单选框");
			field_map.put("DepartmentField", "部门选择框");
			field_map.put("TreeDepartmentField", "树形部门选择框");
			field_map.put("UserField", "用户选择框");
			field_map.put("SelectAboutField", "左右选择框");
			field_map.put("SuggestField", "下拉提示框");
			field_map.put("ButtonField", "按钮");
			field_map.put("ViewDialogField", "视图选择框");
			field_map.put("TabField", "选项卡");
			field_map.put("CalctextField", "计算脚本");
			field_map.put("IncludeField", "包含元素");
			field_map.put("AttachmentUploadField", "文件上传");
			field_map.put("AttachmentUploadToDataBaseField", "文件上传到数据库");
			field_map.put("ImageUploadField", "图片上传");
			field_map.put("ImageUploadToDataBaseField", "图片上传到数据库");
			field_map.put("FileManagerField", "文件管理");
			field_map.put("OnLineTakePhotoField", "在线拍照");
			field_map.put("ReminderField", "待办提醒");
			field_map.put("MapField", "地图");
			field_map.put("WordField", "word编辑器");
			field_map.put("HTMLEditorField", "HTML编辑器");
			field_map.put("IncludeJsFile", "包含JavaScript文件");
		}
	}

	public Table buildOverview(String applicationId) throws Exception {
		Table table = new Table(1);
		table.setPadding(2);
		table.setSpacing(0);
		table.setBorderWidth(0);
		table.setWidth(100);
		if (!StringUtil.isBlank(applicationId)) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Cell fc = new Cell();
			fc.setBackgroundColor(Color.gray);
			fc.addElement(new Paragraph("表单：", fontChinese));
			table.addCell(fc);

			fc = new Cell();
			fc.setColspan(3);
			FormProcess fp = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			Collection<Form> forms = fp.doSimpleQuery(new ParamsTable(),
					applicationId);
			if (forms != null) {
				Table fTable = new Table(1);
				fTable.setPadding(0);
				fTable.setSpacing(0);
				fTable.setBorderWidth(1);
				fTable.setWidth(99);
				Cell f_cell = null;
				Iterator<Form> it = forms.iterator();
				while (it.hasNext()) {
					Form form = it.next();
					if (form != null) {
						f_cell = new Cell();
						f_cell.setBorderWidth(1);

						Table formTable = new Table(1);
						formTable.setWidth(99);
						formTable.setPadding(0);
						formTable.setSpacing(0);
						formTable.setBorderWidth(1);

						TableMapping tableMapping = new TableMapping(form);
						// 表单基本信息
						Cell cell = new Cell();
						cell.setBorderWidth(0);
						String fName = form.getName();
						String description = form.getDiscription();
						cell.addElement(new Paragraph("表单名称："
								+ (fName != null ? fName : ""), fontChinese));
						cell.addElement(new Paragraph("创建时间：", fontChinese));
						cell.addElement(new Paragraph("对应元数据："
								+ (fName != null ? tableMapping.getTableName()
										: ""), fontChinese));
						cell.addElement(new Paragraph("描述："
								+ (description != null ? description : ""),
								fontChinese));
						formTable.addCell(cell);

						cell = new Cell();
						cell.setBorderWidth(0);
						cell.addElement(new Paragraph("操作信息：", fontChinese));
						// 操作列表
						Table oprTable = createFormOpr(form.getActivitys());
						if (oprTable != null) {
							cell.addElement(oprTable);
						}
						formTable.addCell(cell);
						cell = new Cell();
						cell.setBorderWidth(0);
						cell.addElement(new Paragraph("字段信息：", fontChinese));
						// 字段列表
						Table fieldTable = createFormField(form.getFields(),
								tableMapping);
						if (fieldTable != null) {
							cell.addElement(fieldTable);
						}
						formTable.addCell(cell);
						f_cell.addElement(formTable);
						fTable.addCell(f_cell);
					}
				}
				fc.addElement(fTable);
				table.addCell(fc);
			}
		}
		return table;
	}

	// 表单操作按钮信息
	private Table createFormOpr(Collection<Activity> acts) throws Exception {
		if (acts != null) {
			Iterator<Activity> it_act = acts.iterator();
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Table opr_info = new Table(3);
			opr_info.setPadding(5);
			opr_info.setSpacing(0);
			opr_info.setBorderWidth(1);
			opr_info.setWidth(98);
			if (it_act.hasNext()) {
				// 操作列表表头
				Cell oprCell = new Cell();
				oprCell.setBackgroundColor(Color.gray);
				oprCell.addElement(new Paragraph("操作（按钮）", fontChinese));
				opr_info.addCell(oprCell);
				oprCell = new Cell();
				oprCell.setBackgroundColor(Color.gray);
				oprCell.addElement(new Paragraph("类型", fontChinese));
				opr_info.addCell(oprCell);
				oprCell = new Cell();
				oprCell.setBackgroundColor(Color.gray);
				oprCell.addElement(new Paragraph("描述", fontChinese));
				opr_info.addCell(oprCell);

				while (it_act.hasNext()) {
					// 操作列表内容
					Activity act = it_act.next();
					String desc = "";
					// 操作（按钮）
					oprCell = new Cell();
					String actName = act.getName();
					oprCell.addElement(new Paragraph(actName != null ? actName
							: "", fontChinese));
					opr_info.addCell(oprCell);
					// 类型
					oprCell = new Cell();
					int actType = act.getType();
					String actTypeName = getOperationType(actType);
					oprCell
							.addElement(new Paragraph(
									actTypeName != null ? actTypeName : "",
									fontChinese));
					opr_info.addCell(oprCell);
					// 描述
					oprCell = new Cell();
					oprCell.addElement(new Paragraph(desc != null ? desc : "",
							fontChinese));
					opr_info.addCell(oprCell);
					// 操作的脚本
					oprCell = new Cell();
					oprCell.setColspan(3);
					String actBeScript = act.getBeforeActionScript();
					String actAfScript = act.getAfterActionScript();
					String actReadOnlyScript = act.getReadonlyScript();
					String actHiddenScript = act.getHiddenScript();
					if (!StringUtil.isBlank(actBeScript)
							|| !StringUtil.isBlank(actAfScript)
							|| !StringUtil.isBlank(actReadOnlyScript)
							|| !StringUtil.isBlank(actHiddenScript)) {
						if (!StringUtil.isBlank(actBeScript))
							oprCell.addElement(new Paragraph("执行前脚本：\n"
									+ StringUtil.dencodeHTML(actBeScript),
									fontChinese));
						if (!StringUtil.isBlank(actAfScript))
							oprCell.addElement(new Paragraph("执行后脚本：\n"
									+ StringUtil.dencodeHTML(actAfScript),
									fontChinese));
						if (!StringUtil.isBlank(actReadOnlyScript))
							oprCell
									.addElement(new Paragraph(
											"只读脚本：\n"
													+ StringUtil
															.dencodeHTML(actReadOnlyScript),
											fontChinese));
						if (!StringUtil.isBlank(actHiddenScript))
							oprCell.addElement(new Paragraph("隐藏脚本：\n"
									+ StringUtil.dencodeHTML(actHiddenScript),
									fontChinese));
						opr_info.addCell(oprCell);// 操作列表封装完成
					}
				}
			}
			return opr_info;
		}
		return null;
	}

	// 表单字段信息
	private Table createFormField(Collection<FormField> fields,
			TableMapping tableMapping) throws Exception {
		if (fields != null) {
			Iterator<FormField> it_field = fields.iterator();
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Table field_info = new Table(4);
			field_info.setPadding(5);
			field_info.setSpacing(0);
			field_info.setBorderWidth(0);
			field_info.setWidth(98);
			if (it_field.hasNext()) {
				// 表头
				Cell fieldCell = new Cell();
				fieldCell.setBackgroundColor(Color.gray);
				fieldCell.addElement(new Paragraph("字段名称", fontChinese));
				field_info.addCell(fieldCell);
				fieldCell = new Cell();
				fieldCell.setBackgroundColor(Color.gray);
				fieldCell.addElement(new Paragraph("对应元数据字段", fontChinese));
				field_info.addCell(fieldCell);
				fieldCell = new Cell();
				fieldCell.setBackgroundColor(Color.gray);
				fieldCell.addElement(new Paragraph("类型", fontChinese));
				field_info.addCell(fieldCell);
				fieldCell = new Cell();
				fieldCell.setBackgroundColor(Color.gray);
				fieldCell.addElement(new Paragraph("描述", fontChinese));
				field_info.addCell(fieldCell);

				while (it_field.hasNext()) {
					FormField field = it_field.next();
					// 字段名
					fieldCell = new Cell();
					String fieldName = field.getName();
					fieldCell.addElement(new Paragraph(
							fieldName != null ? fieldName : "", fontChinese));
					field_info.addCell(fieldCell);// 1
					// 对应元数据字段
					fieldCell = new Cell();
					fieldCell
							.addElement(new Paragraph(
									fieldName != null ? tableMapping
											.getColumnName(fieldName) : "",
									fontChinese));
					field_info.addCell(fieldCell);// 2
					// 类型
					fieldCell = new Cell();
					// String fieldClassName = field.getClass().getName();
					String fieldTabName = field.getTagName();
					String fieldType = getFieldType(fieldTabName);
					if (field instanceof IncludeField) {// 包含元素
						String includeType = ((IncludeField) field)
								.getIncludeType();
						if (!StringUtil.isBlank(fieldType)
								&& IncludeField.INCLUDE_TYPE_VIEW
										.equals(includeType)) {
							fieldType = fieldType + "(视图)";
						} else if (!StringUtil.isBlank(fieldType)
								&& IncludeField.INCLUDE_TYPE_PAGE
										.equals(includeType)) {
							fieldType = fieldType + "(主页)";
						}
					}
					fieldCell.addElement(new Paragraph(
							fieldType != null ? fieldType : "", fontChinese));
					field_info.addCell(fieldCell);// 3
					// 描述
					fieldCell = new Cell();
					String des_field = field.getDiscript();
					fieldCell.addElement(new Paragraph(
							des_field != null ? des_field : "", fontChinese));
					field_info.addCell(fieldCell);// 4

					// 字段脚本
					fieldCell = new Cell();
					fieldCell.setColspan(4);
					String valueScript = field.getValueScript();
					String validateScript = field.getValidateRule();
					String hiddenScript = field.getHiddenScript();
					String hiddenPrintScript = field.getHiddenPrintScript();
					String readOnlyScript = field.getReadonlyScript();
					if (!StringUtil.isBlank(valueScript)
							|| !StringUtil.isBlank(validateScript)
							|| !StringUtil.isBlank(hiddenScript)
							|| !StringUtil.isBlank(hiddenPrintScript)
							|| !StringUtil.isBlank(readOnlyScript)) {
						if (!StringUtil.isBlank(valueScript))
							fieldCell.addElement(new Paragraph("值脚本：\n"
									+ StringUtil.dencodeHTML(valueScript),
									fontChinese));
						if (!StringUtil.isBlank(validateScript))
							fieldCell.addElement(new Paragraph("校验脚本：\n"
									+ StringUtil.dencodeHTML(validateScript),
									fontChinese));
						if (!StringUtil.isBlank(hiddenScript))
							fieldCell.addElement(new Paragraph("隐藏脚本：\n"
									+ StringUtil.dencodeHTML(hiddenScript),
									fontChinese));
						if (!StringUtil.isBlank(hiddenPrintScript))
							fieldCell
									.addElement(new Paragraph(
											"打印时隐藏脚本：\n"
													+ StringUtil
															.dencodeHTML(hiddenPrintScript),
											fontChinese));
						if (!StringUtil.isBlank(readOnlyScript))
							fieldCell.addElement(new Paragraph("只读脚本：\n"
									+ StringUtil.dencodeHTML(readOnlyScript),
									fontChinese));
						field_info.addCell(fieldCell);// 5,结束字段表
					}
				}
			}
			return field_info;
		}
		return null;
	}

	public String getOperationType(int keyType) {
		return FormActivtyType.activity_map.get(keyType);
	}

	public String getFieldType(String keyClassName) {
		return FormFieldType.field_map.get(keyClassName);
	}

}
