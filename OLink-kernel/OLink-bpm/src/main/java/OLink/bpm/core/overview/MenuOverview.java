package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 菜单的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class MenuOverview implements IOverview {

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
			Cell mc = new Cell();
			mc.setBackgroundColor(Color.gray);
			mc.addElement(new Paragraph("菜单：", fontChinese));
			table.addCell(mc);

			ResourceProcess rp = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);
			Collection<ResourceVO> rs = rp.doSimpleQuery(new ParamsTable(),
					applicationId);
			if (rs != null) {
				mc = new Cell();
				Table rTable = new Table(6);
				rTable.setWidth(96);
				rTable.setPadding(0);
				rTable.setSpacing(0);
				rTable.setBorderWidth(0);

				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("名称", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("序列号", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("上级", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("类型", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("链接类型", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("打开方式", fontChinese));
				rTable.addCell(cell);

				Iterator<ResourceVO> it = rs.iterator();
				while (it.hasNext()) {
					ResourceVO resource = it.next();
					if (resource != null) {
						// 名称
						cell = new Cell();
						String rName = resource.getDescription();
						if (!StringUtil.isBlank(rName)) {
							cell.addElement(new Paragraph(rName, fontChinese));
						}
						rTable.addCell(cell);
						// 序列号
						cell = new Cell();
						String orderno = resource.getOrderno();
						if (!StringUtil.isBlank(orderno)) {
							cell
									.addElement(new Paragraph(orderno,
											fontChinese));
						}
						rTable.addCell(cell);
						// 上级
						ResourceVO superior = resource.getSuperior();
						cell = new Cell();
						if (superior != null) {
							String sName = superior.getDescription();
							if (!StringUtil.isBlank(sName)) {
								cell.addElement(new Paragraph(sName,
										fontChinese));
							}
						}
						rTable.addCell(cell);
						// 类型
						String type = resource.getType();
						cell = new Cell();
						if ("00".equals(type)) {
							cell.addElement(new Paragraph("菜单", fontChinese));
						} else if ("01".equals(type)) {
							cell.addElement(new Paragraph("页面", fontChinese));
						} else if ("100".equals(type)) {
							cell.addElement(new Paragraph("手机菜单", fontChinese));
						}
						rTable.addCell(cell);
						LinkVO link = resource.getLink();
						// 链接类型
						cell = new Cell();
						if (link != null) {
							String linkType = link.getType();
							Map<String, String> map = new HashMap<String, String>();
							map.put("00", "表单");
							map.put("01", "视图");
							map.put("02", "报表");
							map.put("03", "Excel导入");
							// map.put("04", "");
							map.put("05", "自定义内部链接");
							map.put("06", "自定义外部链接");
							map.put("07", "脚本链接");
							map.put("08", "邮件链接");
							map.put("09", "自定义报表");
							map.put("10", "论坛链接");
							if (!StringUtil.isBlank(linkType)) {
								cell.addElement(new Paragraph(
										map.get(linkType), fontChinese));
							}
						}
						rTable.addCell(cell);
						// 打开方式
						String opentarget = resource.getOpentarget();
						cell = new Cell();
						Map<String, String> map = new HashMap<String, String>();
						map.put("detail", "工作区域打开");
						map.put("target", "新窗口打开");
						if (StringUtil.isBlank(opentarget)) {
							opentarget = "detail";
						}
						cell.addElement(new Paragraph(map.get(opentarget),
								fontChinese));
						rTable.addCell(cell);
						// 查询参数
						if (link != null) {
							String queryString = link.getQueryString();
							if (!StringUtil.isBlank(queryString)) {
								cell = new Cell();
								cell.setColspan(6);
								cell.addElement(new Paragraph("参数：\n"
										+ queryString, fontChinese));
								rTable.addCell(cell);
							}
						}
					}
				}
				mc.addElement(rTable);
				table.addCell(mc);
			}
		}
		return table;
	}

}
