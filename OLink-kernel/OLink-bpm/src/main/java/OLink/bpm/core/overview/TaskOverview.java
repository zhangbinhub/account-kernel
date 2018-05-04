package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskConstants;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 任务的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class TaskOverview implements IOverview {

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
			Cell tc = new Cell();
			tc.setBackgroundColor(Color.gray);
			tc.addElement(new Paragraph("任务：", fontChinese));
			table.addCell(tc);

			TaskProcess tp = (TaskProcess) ProcessFactory
					.createProcess(TaskProcess.class);
			Collection<Task> tasks = tp.doSimpleQuery(new ParamsTable(),
					applicationId);
			if (tasks != null) {
				tc = new Cell();
				Iterator<Task> it = tasks.iterator();
				Table tTable = new Table(1);
				tTable.setWidth(99);
				tTable.setPadding(0);
				tTable.setSpacing(0);
				tTable.setBorderWidth(0);

				while (it.hasNext()) {
					Task task = it.next();
					Cell tCell = new Cell();
					tCell.setBorderWidth(0);

					Table tt = new Table(3);
					tt.setWidth(99);
					tt.setPadding(0);
					tt.setSpacing(0);
					tt.setBorderWidth(0);

					Cell cell = new Cell();
					cell.setBorderWidth(0);
					cell.setColspan(3);
					String tName = task.getName();
					cell.addElement(new Paragraph("任务名称："
							+ (tName != null ? tName : ""), fontChinese));
					String creator = task.getCreator();
					cell.addElement(new Paragraph("创建人："
							+ (creator != null ? creator : ""), fontChinese));
					String description = task.getDescription();
					cell.addElement(new Paragraph("描述："
							+ (description != null ? description : ""),
							fontChinese));
					tt.addCell(cell);

					cell = new Cell();
					cell.setBackgroundColor(Color.gray);
					cell.addElement(new Paragraph("类型", fontChinese));
					tt.addCell(cell);
					cell = new Cell();
					cell.setBackgroundColor(Color.gray);
					cell.addElement(new Paragraph("启动类型", fontChinese));
					tt.addCell(cell);
					cell = new Cell();
					cell.setBackgroundColor(Color.gray);
					cell.addElement(new Paragraph("重复", fontChinese));
					tt.addCell(cell);

					// 类型
					int type = task.getType();
					cell = new Cell();
					if (TaskConstants.TASK_TYPE_SCRIPT == type) {
						cell.addElement(new Paragraph("脚本", fontChinese));
					}
					tt.addCell(cell);

					// 启动类型
					int startTime = task.getStartupType();
					Map<Integer, String> sMap = new HashMap<Integer, String>();
					sMap.put(TaskConstants.STARTUP_TYPE_MANUAL, "手动");
					sMap.put(TaskConstants.STARTUP_TYPE_AUTO, "自动");
					sMap.put(TaskConstants.STARTUP_TYPE_BANNED, "禁止");
					String st = sMap.get(startTime);
					cell = new Cell();
					if (!StringUtil.isBlank(st)) {
						cell.addElement(new Paragraph(st, fontChinese));
					}
					tt.addCell(cell);

					// 重复
					int period = task.getPeriod();
					Map<Integer, String> pMap = new HashMap<Integer, String>();
					pMap.put(TaskConstants.REPEAT_TYPE_DAILY, "每天");
					pMap.put(TaskConstants.REPEAT_TYPE_DAILY_MINUTES, "每分");
					pMap.put(TaskConstants.REPEAT_TYPE_DAILY_HOURS, "每时");
					pMap.put(TaskConstants.REPEAT_TYPE_WEEKLY, "每周");
					pMap.put(TaskConstants.REPEAT_TYPE_MONTHLY, "每月");
					pMap.put(TaskConstants.REPEAT_TYPE_IMMEDIATE, "立刻");
					pMap.put(TaskConstants.REPEAT_TYPE_NONE, "不重复");
					String p = pMap.get(period);
					cell = new Cell();
					if (!StringUtil.isBlank(p)) {
						cell.addElement(new Paragraph(p, fontChinese));
					}
					tt.addCell(cell);

					// 任务内容
					String content = task.getTaskScript();
					if (!StringUtil.isBlank(content)) {
						cell = new Cell();
						cell
								.addElement(new Paragraph("任务内容：\n"
										+ StringUtil.dencodeHTML(content),
										fontChinese));
						tt.addCell(cell);
					}

					// 终止条件
					String terminalScript = task.getTerminateScript();
					if (!StringUtil.isBlank(terminalScript)) {
						cell = new Cell();
						cell.addElement(new Paragraph("终止条件：\n"
								+ StringUtil.dencodeHTML(terminalScript),
								fontChinese));
						tt.addCell(cell);
					}

					tCell.addElement(tt);
					tTable.addCell(tCell);
				}
				tc.addElement(tTable);
				table.addCell(tc);
			}
		}
		return table;
	}

}
