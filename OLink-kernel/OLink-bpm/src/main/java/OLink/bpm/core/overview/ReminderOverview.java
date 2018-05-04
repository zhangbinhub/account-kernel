package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 提醒的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class ReminderOverview implements IOverview {

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
			Cell rc = new Cell();
			rc.setBackgroundColor(Color.gray);
			rc.addElement(new Paragraph("提醒：", fontChinese));
			table.addCell(rc);

			rc = new Cell();
			ReminderProcess rp = (ReminderProcess) ProcessFactory
					.createProcess(ReminderProcess.class);
			Collection<Reminder> reminders = rp.doSimpleQuery(
					new ParamsTable(), applicationId);
			if (reminders != null && reminders.size() > 0) {
				Table rTable = new Table(4);
				rTable.setPadding(0);
				rTable.setSpacing(0);
				rTable.setBorderWidth(1);
				rTable.setWidth(96);

				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("标题", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("排序根据", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("样式", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("模式", fontChinese));
				rTable.addCell(cell);

				Iterator<Reminder> it = reminders.iterator();
				while (it.hasNext()) {
					Reminder reminder = it.next();
					// 标题
					cell = new Cell();
					String title = reminder.getTitle();
					cell.addElement(new Paragraph(title != null ? title : "",
							fontChinese));
					rTable.addCell(cell);

					// 排序根据
					cell = new Cell();
					String orderby = reminder.getOrderby();
					cell.addElement(new Paragraph(orderby != null ? orderby
							: "", fontChinese));
					rTable.addCell(cell);

					// 样式
					cell = new Cell();
					String style = reminder.getStyle();
					Map<String, String> map = new HashMap<String, String>();
					map.put("1", "默认");
					map.put("2", "样式一");
					map.put("3", "样式二");
					map.put("4", "样式三");
					map.put("5", "样式四");
					String s = map.get(style);
					cell.addElement(new Paragraph(s != null ? s : "",
							fontChinese));
					rTable.addCell(cell);

					// 模式
					cell = new Cell();
					String mode = reminder.getType();
					if (Reminder.REMINDER_TYPE_DESC.equals(mode)) {
						cell.addElement(new Paragraph("设计模式", fontChinese));
					} else if (Reminder.REMINDER_TYPE_CODE.equals(mode)) {
						cell.addElement(new Paragraph("代码模式", fontChinese));
					}
					rTable.addCell(cell);

					// 代码
					if (Reminder.REMINDER_TYPE_CODE.equals(mode)) {
						cell = new Cell();
						cell.setColspan(4);
						String filterScript = reminder.getFilterScript();
						cell.addElement(new Paragraph("代码：\n"
								+ (filterScript != null ? filterScript : ""),
								fontChinese));
						rTable.addCell(cell);
					}
				}
				rc.addElement(rTable);
				table.addCell(rc);
			}
		}
		return table;
	}

}
