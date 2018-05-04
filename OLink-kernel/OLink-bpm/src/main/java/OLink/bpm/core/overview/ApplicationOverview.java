package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Date;

import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 应用的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class ApplicationOverview implements IOverview {

	public Table buildOverview(String applicationId) throws Exception {
		Table appTable = new Table(1);
		appTable.setPadding(0);
		appTable.setSpacing(0);
		appTable.setBorderWidth(1);
		appTable.setWidth(100);
		if (!StringUtil.isBlank(applicationId)) {
			ApplicationProcess ap = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ApplicationVO avo = (ApplicationVO) ap.doView(applicationId);
			if (avo != null) {
				BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
						"UniGB-UCS2-H", false);
				Font fontChinese = new Font(bfChinese, 10, Font.NORMAL,
						Color.BLACK);
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("软件基本信息：", fontChinese));
				appTable.addCell(cell);

				cell = new Cell();
				cell.setBorderWidth(0);
				String appName = avo.getName();
				Date createTime = avo.getCreateDate();
				DataSource datasource = avo.getDataSourceDefine();
				String description = avo.getDescription();

				cell.addElement(new Paragraph("软件名称："
						+ (appName != null ? appName : ""), fontChinese));
				cell.addElement(new Paragraph("创建时间："
						+ (createTime != null ? createTime.toString() : ""),
						fontChinese));
				cell.addElement(new Paragraph("作者：", fontChinese));
				cell.addElement(new Paragraph("数据源："
						+ (datasource != null ? datasource.getUrl() : ""),
						fontChinese));
				cell
						.addElement(new Paragraph("描述："
								+ (description != null ? description : ""),
								fontChinese));
				appTable.addCell(cell);
			}
		}
		return appTable;
	}
}
