package OLink.bpm.core.overview;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Document;
import eWAP.itext.text.PageSize;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.PdfWriter;

/**
 * 概览的生成实现
 * 
 * 2.6版本新增
 * 
 * 抽象类,必须继承来实例化
 * 
 * @author keezzm
 *
 */
public abstract class AbstractOverviewBuilder {

	public void buildOverview(String applicationId, String pdfFileName) throws Exception {
		URL url = AbstractOverviewBuilder.class.getClassLoader().getResource(
				"eWAP.properties");//增加 by XGY
		if (url != null) {
			String fileName = url.getFile();
			String pdf = fileName.replaceAll("eWAP[.]properties",pdfFileName);//增加 by XGY
			File file = new File(pdf);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new Exception("Create pdf file [" + pdf + "] failed");
				}
			}
			// 创建文档
			Document document = new Document(PageSize.A4.rotate(), 5, 5, 30, 30);
			PdfWriter.getInstance(document, new FileOutputStream(file));
			// 打开文档
			document.open();

			Table table = new Table(1);
			table.setPadding(2);
			table.setSpacing(0);
			table.setBorderWidth(1);

			Collection<IOverview> overviers = OverviewFactory
					.createAllOverview();
			if (overviers != null) {
				Iterator<IOverview> it = overviers.iterator();
				if (it != null) {
					while (it.hasNext()) {
						IOverview overview = it.next();
						Cell cell = new Cell();
						// 创建当前概览表
						Table t = overview.buildOverview(applicationId);
						cell.addElement(t);
						table.addCell(cell);
					}
				}
			}
			document.add(table);
			document.close();
		}
	}
}
