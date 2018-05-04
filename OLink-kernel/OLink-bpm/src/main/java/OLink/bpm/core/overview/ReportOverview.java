package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 报表的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 * 
 */
public class ReportOverview implements IOverview {

	public static class ReportType {
		public static final Map<String, String> type_map = new HashMap<String, String>();
		static {
			type_map.put("CrossReport", "交叉报表");
			type_map.put("CustomizeReport", "自定义报表");
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
			Cell rc = new Cell();
			rc.setBackgroundColor(Color.gray);
			rc.addElement(new Paragraph("报表：", fontChinese));
			table.addCell(rc);

			rc = new Cell();
			CrossReportProcess rp = (CrossReportProcess) ProcessFactory
					.createProcess(CrossReportProcess.class);
			Collection<CrossReportVO> rs = rp
					.doSimpleQuery(null, applicationId);
			if (rs != null && rs.size() > 0) {
				Table rTable = new Table(5);
				rTable.setPadding(0);
				rTable.setSpacing(0);
				rTable.setBorderWidth(1);
				rTable.setWidth(96);

				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("名称", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("标题", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("类型", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("关联视图", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("描述", fontChinese));
				rTable.addCell(cell);

				Iterator<CrossReportVO> it = rs.iterator();
				while (it.hasNext()) {
					CrossReportVO report = it.next();
					// 名称
					cell = new Cell();
					String name = report.getName();
					cell.addElement(new Paragraph(name != null ? name : "",
							fontChinese));
					rTable.addCell(cell);

					// 标题
					cell = new Cell();
					String title = report.getReportTitle();
					cell.addElement(new Paragraph(title != null ? title : "", fontChinese));
					rTable.addCell(cell);

					// 类型
					cell = new Cell();
					String t = report.getType();
					if (StringUtil.isBlank(t)) {
						t = "CrossReport";
					}
					String type = ReportType.type_map.get(report.getType());
					cell.addElement(new Paragraph(type != null ? type : "", fontChinese));
					rTable.addCell(cell);

					// 关联视图
					cell = new Cell();
					String viewId = report.getView();
					View view = null;
					if (viewId != null) {
						ViewProcess vp = (ViewProcess) ProcessFactory
								.createProcess(ViewProcess.class);
						view = (View) vp.doView(viewId);
					}
					cell.addElement(new Paragraph(view != null ? view.getName()
							: "", fontChinese));
					rTable.addCell(cell);

					// 描述
					cell = new Cell();
					String note = report.getNote();
					cell.addElement(new Paragraph(note != null ? note : "", fontChinese));
					rTable.addCell(cell);
				}
				rc.addElement(rTable);
				table.addCell(rc);
			}
		}
		return table;
	}

}
