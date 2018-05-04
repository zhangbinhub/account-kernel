package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

public class DeveloperOverview implements IOverview {

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
			Cell dc = new Cell();
			dc.setBackgroundColor(Color.gray);
			dc.addElement(new Paragraph("开发者：", fontChinese));
			table.addCell(dc);

			ApplicationProcess ap = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ApplicationVO application = (ApplicationVO) ap
					.doView(applicationId);
			if (application != null) {
				dc = new Cell();
				Table oTable = new Table(3);
				oTable.setPadding(0);
				oTable.setSpacing(0);
				oTable.setBorderWidth(1);
				oTable.setWidth(96);

				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("名称", fontChinese));
				oTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("账号", fontChinese));
				oTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("电子邮箱", fontChinese));
				oTable.addCell(cell);

				Collection<SuperUserVO> owners = application.getOwners();
				if (owners != null && owners.size() > 0) {
					Iterator<SuperUserVO> it = owners.iterator();
					while (it.hasNext()) {
						SuperUserVO owner = it.next();
						if (owner != null && owner.isDeveloper()) {
							// 名称
							cell = new Cell();
							String oName = owner.getName();
							cell.addElement(new Paragraph(oName != null ? oName
									: "", fontChinese));
							oTable.addCell(cell);

							// 账号
							cell = new Cell();
							String loginno = owner.getLoginno();
							cell
									.addElement(new Paragraph(
											loginno != null ? loginno : "",
											fontChinese));
							oTable.addCell(cell);

							// 电子邮箱
							cell = new Cell();
							String email = owner.getEmail();
							cell.addElement(new Paragraph(email != null ? email
									: "", fontChinese));
							oTable.addCell(cell);
						}
					}
					dc.addElement(oTable);
					table.addCell(dc);
				}
			}
		}
		return table;
	}

}
