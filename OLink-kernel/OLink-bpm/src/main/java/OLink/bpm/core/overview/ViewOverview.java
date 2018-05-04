package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 视图的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 * 
 */
public class ViewOverview implements IOverview {

	public static class ViewActivityType {
		public static final Map<Integer, String> activity_map = new HashMap<Integer, String>();
		static {
			activity_map.put(ActivityType.DOCUMENT_QUERY, "查询");
			activity_map.put(ActivityType.DOCUMENT_CREATE, "创建");
			activity_map.put(ActivityType.DOCUMENT_DELETE, "删除");
			activity_map.put(ActivityType.BATCH_APPROVE, "批量提交");
			activity_map.put(ActivityType.EXPTOEXCEL, "导出Excel");
			activity_map.put(ActivityType.CLEAR_ALL, "清空所有数据");
			activity_map.put(ActivityType.EXCEL_IMPORT, "导入Excel");
			activity_map.put(ActivityType.FILE_DOWNLOAD, "文件下载");
			activity_map.put(ActivityType.BATCHSIGNATURE, "批量签章");
		}
	}

	public Table buildOverview(String applicationId) throws Exception {
		Table table = new Table(1);
		table.setPadding(2);
		table.setSpacing(0);
		table.setBorderWidth(1);
		table.setWidth(100);
		if (!StringUtil.isBlank(applicationId)) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Cell vc = new Cell();
			vc.setBackgroundColor(Color.gray);
			vc.addElement(new Paragraph("视图：", fontChinese));
			table.addCell(vc);

			ViewProcess vp = (ViewProcess) ProcessFactory
					.createProcess(ViewProcess.class);
			Collection<View> views = vp.doSimpleQuery(new ParamsTable(),
					applicationId);
			if (views != null) {
				vc = new Cell();
				Iterator<View> it = views.iterator();
				Table vTable = new Table(1);
				vTable.setWidth(99);
				vTable.setPadding(0);
				vTable.setSpacing(0);
				vTable.setBorderWidth(1);
				Cell v_cell = null;
				while (it.hasNext()) {
					View view = it.next();
					if (view != null) {
						v_cell = new Cell();
						Table vt = new Table(1);
						vt.setWidth(99);
						vt.setPadding(0);
						vt.setSpacing(0);
						vt.setBorderWidth(1);

						Cell cell = new Cell();
						cell.setBorderWidth(0);
						String vName = view.getName();
						cell.addElement(new Paragraph("视图名称："
								+ (vName != null ? vName : ""), fontChinese));
						String createTime = "";
						cell.addElement(new Paragraph("创建时间："
								+ (createTime != null ? createTime : ""),
								fontChinese));
						String description = "";
						cell.addElement(new Paragraph("描述："
								+ (description != null ? description : ""),
								fontChinese));
						vt.addCell(cell);

						cell = new Cell();
						cell.addElement(new Paragraph("查询定义：", fontChinese));
						Table qt = createQueryDefi(view);
						if (qt != null) {
							cell.addElement(qt);
						}
						vt.addCell(cell);

						cell = new Cell();
						cell.addElement(new Paragraph("操作信息：", fontChinese));
						Table ot = createViewOpr(view);
						if (ot != null) {
							cell.addElement(ot);
						}
						vt.addCell(cell);

						v_cell.addElement(vt);
						vTable.addCell(v_cell);
					}
				}
				vc.addElement(vTable);
				table.addCell(vc);
			}
		}
		return table;
	}

	// 视图过滤条件信息
	private Table createQueryDefi(View view) throws Exception {
		if (view != null) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Table query_info = new Table(3);
			query_info.setWidth(99);
			query_info.setPadding(0);
			query_info.setSpacing(0);
			query_info.setBorderWidth(1);

			// 表头
			Cell cell = new Cell();
			cell.setBackgroundColor(Color.gray);
			cell.addElement(new Paragraph("模式", fontChinese));
			query_info.addCell(cell);

			cell = new Cell();
			cell.setBackgroundColor(Color.gray);
			cell.addElement(new Paragraph("对应表单", fontChinese));
			query_info.addCell(cell);

			cell = new Cell();
			cell.setBackgroundColor(Color.gray);
			cell.addElement(new Paragraph("描述", fontChinese));
			query_info.addCell(cell);

			cell = new Cell();
			Map<String, String> mode_map = new HashMap<String, String>();
			mode_map.put(View.EDIT_MODE_DESIGN, "设计模式");
			mode_map.put(View.EDIT_MODE_CODE_DQL, "DQL模式");
			mode_map.put(View.EDIT_MODE_CODE_SQL, "SQL模式");
			mode_map.put(View.EDIT_MODE_CODE_PROCEDURE, "存储过程模式");
			String model = mode_map.get(view.getEditMode());
			cell.addElement(new Paragraph(model != null ? model : "",
					fontChinese));
			query_info.addCell(cell);

			cell = new Cell();
			String relatedForm = view.getRelatedForm();
			FormProcess fp = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			Form form = (Form) fp.doView(relatedForm);
			String relationFormName = "";
			if (form != null) {
				relationFormName = form.getName();
			}
			cell.addElement(new Paragraph(
					relationFormName != null ? relationFormName : "",
					fontChinese));
			query_info.addCell(cell);

			cell = new Cell();
			String description = view.getDescription();
			cell.addElement(new Paragraph(description != null ? description
					: "", fontChinese));
			query_info.addCell(cell);

			// 设计模式
			String codition = view.getFilterCondition();
			if (!StringUtil.isBlank(codition)) {
				cell = new Cell();
				cell.setColspan(3);
				cell.addElement(new Paragraph("条件描述（设计模式）：\n" + codition,
						fontChinese));
				query_info.addCell(cell);
			}

			// DQL模式
			String filterScript = view.getFilterScript();
			if (!StringUtil.isBlank(filterScript)) {
				cell = new Cell();
				cell.setColspan(3);
				cell.addElement(new Paragraph("条件脚本（DQL模式）：\n"
						+ StringUtil.dencodeHTML(filterScript), fontChinese));
				query_info.addCell(cell);
			}

			// SQL模式
			String sqlFilterScript = view.getSqlFilterScript();
			if (!StringUtil.isBlank(sqlFilterScript)) {
				cell = new Cell();
				cell.setColspan(3);
				cell
						.addElement(new Paragraph("条件脚本（SQL模式）：\n"
								+ StringUtil.dencodeHTML(sqlFilterScript),
								fontChinese));
				query_info.addCell(cell);
			}

			// 存储过程模式
			String procedureFilterScript = view.getProcedureFilterScript();
			if (!StringUtil.isBlank(procedureFilterScript)) {
				cell = new Cell();
				cell.setColspan(3);
				cell.addElement(new Paragraph("条件脚本（存储过程模式）：\n"
						+ StringUtil.dencodeHTML(procedureFilterScript),
						fontChinese));
				query_info.addCell(cell);
			}
			return query_info;
		}
		return null;
	}

	// 视图操作按钮信息
	private Table createViewOpr(View view) throws Exception {
		if (view != null) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Table opr_table = new Table(3);
			opr_table.setWidth(99);
			opr_table.setPadding(0);
			opr_table.setSpacing(0);
			opr_table.setBorderWidth(1);

			if (view.getActivitys() != null) {
				Collection<Activity> acts = view.getActivitys();
				Iterator<Activity> it = acts.iterator();
				Cell ct = new Cell();
				ct.setBackgroundColor(Color.gray);
				ct.addElement(new Paragraph("操作（按钮）：", fontChinese));
				opr_table.addCell(ct);

				ct = new Cell();
				ct.setBackgroundColor(Color.gray);
				ct.addElement(new Paragraph("类型", fontChinese));
				opr_table.addCell(ct);

				ct = new Cell();
				ct.setBackgroundColor(Color.gray);
				ct.addElement(new Paragraph("描述", fontChinese));
				opr_table.addCell(ct);
				while (it.hasNext()) {
					Activity act = it.next();

					ct = new Cell();
					String aName = act.getName();
					ct.addElement(new Paragraph(aName != null ? aName : "",
							fontChinese));
					opr_table.addCell(ct);

					ct = new Cell();
					int actType = act.getType();
					String actTypeName = getOperationType(actType);
					ct
							.addElement(new Paragraph(
									actTypeName != null ? actTypeName : "",
									fontChinese));
					opr_table.addCell(ct);

					ct = new Cell();
					String desc = "";
					ct.addElement(new Paragraph(desc != null ? desc : "",
							fontChinese));
					opr_table.addCell(ct);

					ct = new Cell();
					ct.setColspan(3);
					String actBeScript = act.getBeforeActionScript();
					String actAfScript = act.getAfterActionScript();
					String actReadOnlyScript = act.getReadonlyScript();
					String actHiddenScript = act.getHiddenScript();
					if (!StringUtil.isBlank(actBeScript)
							|| !StringUtil.isBlank(actAfScript)
							|| !StringUtil.isBlank(actReadOnlyScript)
							|| !StringUtil.isBlank(actHiddenScript)) {
						if (!StringUtil.isBlank(actBeScript))
							ct.addElement(new Paragraph("执行前脚本：\n"
									+ StringUtil.dencodeHTML(actBeScript),
									fontChinese));
						if (!StringUtil.isBlank(actAfScript))
							ct.addElement(new Paragraph("执行后脚本：\n"
									+ StringUtil.dencodeHTML(actAfScript),
									fontChinese));
						if (!StringUtil.isBlank(actReadOnlyScript))
							ct
									.addElement(new Paragraph(
											"只读脚本：\n"
													+ StringUtil
															.dencodeHTML(actReadOnlyScript),
											fontChinese));
						if (!StringUtil.isBlank(actHiddenScript))
							ct.addElement(new Paragraph("隐藏脚本：\n"
									+ StringUtil.dencodeHTML(actHiddenScript),
									fontChinese));
						opr_table.addCell(ct);// 操作列表封装完成
					}
				}
			}
			return opr_table;
		}
		return null;
	}

	private String getOperationType(int keyType) {
		return ViewActivityType.activity_map.get(keyType);
	}

}
