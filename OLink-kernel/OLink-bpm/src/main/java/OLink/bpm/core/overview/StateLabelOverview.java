package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabelProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 状态标签的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class StateLabelOverview implements IOverview {

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
			Cell sc = new Cell();
			sc.setBackgroundColor(Color.gray);
			sc.addElement(new Paragraph("状态标签：", fontChinese));
			table.addCell(sc);

			sc = new Cell();
			StateLabelProcess sp = (StateLabelProcess) ProcessFactory
					.createProcess(StateLabelProcess.class);
			Collection<StateLabel> statelabels = sp.doSimpleQuery(
					new ParamsTable(), applicationId);
			if (statelabels != null && statelabels.size() > 0) {
				Table sTable = new Table(3);
				sTable.setPadding(0);
				sTable.setSpacing(0);
				sTable.setBorderWidth(1);
				sTable.setWidth(96);

				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("名称", fontChinese));
				sTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("值", fontChinese));
				sTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("排序号", fontChinese));
				sTable.addCell(cell);

				Iterator<StateLabel> it = statelabels.iterator();
				while (it.hasNext()) {
					StateLabel statelabel = it.next();
					// 名称
					cell = new Cell();
					String sName = statelabel.getName();
					cell.addElement(new Paragraph(sName != null ? sName : "",
							fontChinese));
					sTable.addCell(cell);

					// 值
					cell = new Cell();
					String sValue = statelabel.getValue();
					cell.addElement(new Paragraph(sValue != null ? sValue : "",
							fontChinese));
					sTable.addCell(cell);

					// 排序号
					cell = new Cell();
					String orderno = statelabel.getOrderNo();
					cell.addElement(new Paragraph(orderno != null ? orderno
							: "", fontChinese));
					sTable.addCell(cell);

					// 描述
					cell = new Cell();
					cell.setColspan(3);
					String description = statelabel.getDescription();
					cell.addElement(new Paragraph("描述：\n"
							+ (description != null ? description : ""),
							fontChinese));
					sTable.addCell(cell);
				}
				sc.addElement(sTable);
				table.addCell(sc);
			}
		}
		return table;
	}

}
