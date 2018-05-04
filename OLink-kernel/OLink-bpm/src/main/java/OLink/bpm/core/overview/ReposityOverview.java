package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 函数库的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class ReposityOverview implements IOverview {

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
			rc.addElement(new Paragraph("函数库：", fontChinese));
			table.addCell(rc);

			rc = new Cell();
			RepositoryProcess rp = (RepositoryProcess) ProcessFactory
					.createProcess(RepositoryProcess.class);
			Collection<RepositoryVO> repositories = rp.doSimpleQuery(
					new ParamsTable(), applicationId);
			if (repositories != null && repositories.size() > 0) {
				Table rTable = new Table(2);
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
				cell.addElement(new Paragraph("版本", fontChinese));
				rTable.addCell(cell);

				Iterator<RepositoryVO> it = repositories.iterator();
				while (it.hasNext()) {
					RepositoryVO repository = it.next();
					// 名称
					cell = new Cell();
					String rName = repository.getName();
					cell.addElement(new Paragraph(rName != null ? rName : "",
							fontChinese));
					rTable.addCell(cell);
					// 版本
					cell = new Cell();
					int version = repository.getVersion();
					cell.addElement(new Paragraph(String.valueOf(version),
							fontChinese));
					rTable.addCell(cell);
					// 内容
					cell = new Cell();
					cell.setColspan(2);
					String content = repository.getContent();
					cell.addElement(new Paragraph("内容：\n"
							+ (content != null ? content : ""), fontChinese));
					rTable.addCell(cell);
				}
				rc.addElement(rTable);
				table.addCell(rc);
			}
		}
		return table;
	}

}
