package OLink.bpm.core.report.standardreport.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.DefaultProperty;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import OLink.bpm.core.report.standardreport.ejb.StandarReportProcessBean;
import eWAP.core.Tools;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public class StandardReportAction extends ActionSupport implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String _formid;

	public String application;

	public String startdate;

	public String enddate;
	
	public String dbmethod;

	public String filePath;

	Map<String, String> columns = new LinkedHashMap<String, String>();

	public String getDbmethod() {
		return dbmethod;
	}

	public void setDbmethod(String dbmethod) {
		this.dbmethod = dbmethod;
	}
	
	public String get_formid() {
		return _formid;
	}

	public void set_formid(String _formid) {
		this._formid = _formid;
	}

	public String doSummaryReport() throws Exception {
		setColMap();
		return SUCCESS;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @SuppressWarnings 使用了过时的POI API
	 *
	 */
	@SuppressWarnings("deprecation")
	public String doExportReport() throws Exception {
		Map<?, ?> m = getContext().getParameters();
		String selColNames[] = (String[]) m.get("col");
		String listColName[] = new String[selColNames.length + 1];
		int count = 0;
		String path = DefaultProperty.getProperty("EXPORT_PATH");
		String ctxPath = ServletActionContext.getServletContext().getRealPath(path);
		String fileName = Tools.getSequence() + ".xls";
		selColNames[0] = StringUtil.isBlank(selColNames[0]) ? "AUDITOR" : selColNames[0];

		setFilePath(path + fileName);
		String xlsFile = ctxPath + "/" + fileName;

		if (!(new File(ctxPath).isDirectory())) {
			File f = new File(ctxPath);
			if (!f.exists()) {
				if (!f.mkdirs())
					throw new IOException("create directory '" + ctxPath + "' failed!");
			}
		}

		for (int i = 0; i < selColNames.length; i++)
			listColName[i] = selColNames[i];

		listColName[selColNames.length] = "USEDTIME";

		StandarReportProcessBean spb = new StandarReportProcessBean(application);
		int index = Integer.valueOf(dbmethod);
		Collection<Map<String, String>> collection = spb.getSummaryReport(_formid, startdate, enddate, selColNames, ReportUtil.dbmethod[index]);

		FileOutputStream fos = new FileOutputStream(xlsFile);
		HSSFWorkbook wb = new HSSFWorkbook();

		// for list
		HSSFSheet s = wb.createSheet();
		wb.setSheetName(0, "list Excel ");//, HSSFWorkbook.ENCODING_UTF_16);

		// ��ӡ����ͷ
		HSSFRow rowheader = s.createRow(count);
		for (short j = 0; j < listColName.length; j++) {
			HSSFCell cell = rowheader.createCell(j);
//			cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);
			cell.setCellValue(listColName[j]);
		}

		count++;

		for (Iterator<Map<String, String>> iterator = collection.iterator(); iterator.hasNext();) {
			Map<String, String> rowValues = iterator.next();
			HSSFRow row = s.createRow(count);
			HSSFRow preRow = s.getRow(count - 1);

			for (short j = 0; j < listColName.length; j++) {
				HSSFCell cell = row.createCell(j);
//				cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);
				cell.setCellValue(rowValues.get(listColName[j]).toString());

				if (count != 1 && preRow.getCell(j).getStringCellValue().equals(cell.getStringCellValue())
						&& !listColName[j].equals("USEDTIME")) {
					s.addMergedRegion(new Region(count - 1, j, count, j));//
				}
			}
			count++;
		}

		// for bar
		wb.write(fos);
		fos.close();

		return SUCCESS;
	}

	public String[] getcolumsId() throws Exception {
		Map<?, ?> m = getContext().getParameters();
		Object obj = m.get("colids");
		String[] rolesid = null;

		if (obj != null && obj instanceof String[] && ((String[]) obj).length > 0) {
			rolesid = (String[]) obj;
		}
		return rolesid;
	}

	public Map<String, String> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, String> columns) {
		setColMap();
	}

	public void setColMap() {
		columns.put("", "");

		// ��̬��columns
		Map<?, ?> m = getContext().getParameters();
		Object obj = m.get("colids");
		String[] ids = null;

		if (obj != null && obj instanceof String[] && ((String[]) obj).length > 0) {
			ids = (String[]) obj;
			for (int i = 0; i < ids.length; i++)
				columns.put("ITEM_" + ids[i].toUpperCase(), ids[i]);
		}

		columns.put("ARRIVEDTIME", "ARRIVEDTIME");
		columns.put("SENDOUTTIME", "SENDOUTTIME");
		columns.put("AUDITOR", "AUDITOR");
		columns.put("NODENAME", "NODENAME");

	}

	public static ActionContext getContext() {
		ActionContext context = ActionContext.getContext();
		return context;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}