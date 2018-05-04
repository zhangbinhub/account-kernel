package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.homepage.ejb.HomePage;
import OLink.bpm.core.homepage.ejb.HomePageProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 首页的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class HomePageOverview implements IOverview {

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
			Cell hc = new Cell();
			hc.setBackgroundColor(Color.gray);
			hc.addElement(new Paragraph("首页：", fontChinese));
			table.addCell(hc);

			HomePageProcess hp = (HomePageProcess) ProcessFactory
					.createProcess(HomePageProcess.class);
			Collection<HomePage> homepages = hp.doSimpleQuery(
					new ParamsTable(), applicationId);
			if (homepages != null) {
				hc = new Cell();
				Table hTable = new Table(3);
				hTable.setPadding(0);
				hTable.setSpacing(0);
				hTable.setWidth(96);
				hTable.setBorderWidth(1);

				if (homepages != null && homepages.size() > 0) {
					// 表头
					Cell cell = new Cell();
					cell.setBackgroundColor(Color.gray);
					cell.addElement(new Paragraph("名称", fontChinese));
					hTable.addCell(cell);

					cell = new Cell();
					cell.setBackgroundColor(Color.gray);
					cell.addElement(new Paragraph("发布", fontChinese));
					hTable.addCell(cell);

					cell = new Cell();
					cell.setBackgroundColor(Color.gray);
					cell.addElement(new Paragraph("定制模式", fontChinese));
					hTable.addCell(cell);

					Iterator<HomePage> it = homepages.iterator();
					while (it.hasNext()) {
						HomePage homepage = it.next();
						if (homepage != null) {
							// 名称
							cell = new Cell();
							String hName = homepage.getName();
							cell.addElement(new Paragraph(hName != null ? hName
									: "", fontChinese));
							hTable.addCell(cell);

							// 发布
							cell = new Cell();
							boolean published = homepage.getPublished();
							cell.addElement(new Paragraph(
									published ? "是" : "否", fontChinese));
							hTable.addCell(cell);

							// 定制模式
							cell = new Cell();
							int defineMode = homepage.getDefineMode();
							if (HomePage.REGULAR_MODE == defineMode) {
								cell
										.addElement(new Paragraph("常规",
												fontChinese));
							} else if (HomePage.CUSTOMIZE_MODE == defineMode) {
								cell.addElement(new Paragraph("自定义",
										fontChinese));
							}
							hTable.addCell(cell);

							cell = new Cell();
							cell.setColspan(3);
							String description = homepage.getDescription();
							cell.addElement(new Paragraph("描述：\n"
									+ (description != null ? description : ""),
									fontChinese));
							hTable.addCell(cell);

							cell = new Cell();
							cell.setColspan(3);
							String roles = homepage.getRoleNames();
							cell
									.addElement(new Paragraph("角色：\n"
											+ (roles != null ? roles : ""),
											fontChinese));
							hTable.addCell(cell);

							// 提醒列表

							Collection<Reminder> reminders = homepage
									.getReminders();
							if (reminders != null && reminders.size() > 0) {
								cell = new Cell();
								cell.setColspan(3);
								StringBuffer reminderList = new StringBuffer();
								Iterator<Reminder> r_it = reminders.iterator();
								boolean isFirst = true;
								while (r_it.hasNext()) {
									Reminder reminder = r_it.next();
									if (reminder != null) {
										String rTitle = reminder.getTitle();
										if (isFirst) {
											reminderList.append(rTitle);
											isFirst = false;
										} else {
											reminderList.append("," + rTitle);
										}
									}
								}
								cell
										.addElement(new Paragraph(
												"提醒列表：\n"
														+ (reminderList != null ? reminderList
																.toString()
																: ""),
												fontChinese));
								hTable.addCell(cell);
							}
						}
					}
				}
				hc.addElement(hTable);
				table.addCell(hc);
			}
		}
		return table;
	}

}
