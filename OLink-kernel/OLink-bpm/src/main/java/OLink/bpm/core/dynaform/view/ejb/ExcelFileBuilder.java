package OLink.bpm.core.dynaform.view.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.property.DefaultProperty;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;


public class ExcelFileBuilder {
	private static final Logger LOG = Logger.getLogger(ExcelFileBuilder.class);

	private WebUser user;

	// 每页总行数1000条
	private final int LINES = 5;

	private String fileRealPath = "";

	private boolean isWorkbookCreated = false;

	private Map<String, Integer> view2rowCountMap = new HashMap<String, Integer>();
	
	private static Set<String> fileNameSet = new CopyOnWriteArraySet<String>();

	public ExcelFileBuilder(WebUser user) {
		this.user = user;
	}

	/**
	 * 创建工作薄
	 * 
	 * @param view
	 * @param params
	 * @throws Exception
	 */
	public void buildSheet(View view, HSSFWorkbook workbook, ParamsTable params) throws Exception {
		try {
			Document currdoc = new Document();
			if (view.getSearchForm() != null) {
				currdoc = view.getSearchForm().createDocument(params, user);
			}

			IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
			// 获得总条数
			int count = (int) view.getViewTypeImpl().countViewDatas(params, user, currdoc);
			if (count > 0) {
				// 获得总页数
				int pageSize = (int) Math.ceil((double) count / (double) LINES);

				Collection<String> heads = this.getHeads(view, runner);
				// Collection<View> subViewList = view.getSubViewList();

				// 分页查询写Excel文件
				for (int tempPage = 1; tempPage <= pageSize; tempPage++) {
					// 1000条
					DataPackage<Document> datas = view.getViewTypeImpl().getViewDatasPage(params, tempPage, LINES,
							user, currdoc);
					HSSFSheet sheet = getSheet(view, workbook);

					for (Iterator<Document> iterator = datas.datas.iterator(); iterator.hasNext();) {
						Document doc = iterator.next();
						Map<String, Object> excelData = convertToExcelData(doc, view, user, params, runner,
								new ArrayList<ValidateMessage>());

						// 判断是否第一次加载数据
						if (getRowCount(view) == 0) {
							// 添加工作薄头
							addSheetHead(view, runner, workbook, sheet);
						}
						// 添加工作薄数据
						addSheetData(workbook, view, sheet, heads, excelData);

						if (params.getParameterAsBoolean("isExpSub")) {
							// 添加子工作薄
							Collection<View> subViewList = view.getSubViewList();
							if (subViewList != null && !subViewList.isEmpty()) {
								for (Iterator<View> viewIterator = subViewList.iterator(); viewIterator.hasNext();) {
									View subView = viewIterator.next();
									ParamsTable newParams = new ParamsTable();
									newParams.setParameter("parentid", doc.getId());
									newParams.setParameter("isRelate", "true");

									buildSheet(subView, workbook, newParams);
								}
							}
						}
					}
					writeToFilePath(fileRealPath, workbook);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建工作薄
	 * 
	 * @param view
	 * @param params
	 * @throws Exception
	 */
	public void buildSheet(View view, ParamsTable params) throws Exception {
		String fileName = getFileName(params); // 文件名
		String path = getSavePath(params); // 文件完整路径
		
		StringBuffer sb = new StringBuffer();
		getFileNameOther(sb,path,fileName);
		fileRealPath = path +sb.toString();
		
		fileNameSet.add(sb.toString());
		buildSheet(view, getWorkbook(), params);
		fileNameSet.remove(sb.toString());
	}

	private HSSFSheet getSheet(View view, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet(view.getName());
		if (sheet == null) {
			System.out.println("Create Sheet: " + view.getName());
			sheet = workbook.createSheet(view.getName());
		}
		return sheet;
	}

	private HSSFWorkbook getWorkbook() throws IOException {
		HSSFWorkbook workbook = null;
		if (!isWorkbookCreated) {
			workbook = new HSSFWorkbook();
			isWorkbookCreated = true;

			// 写入一个空文件
			writeToFilePath(fileRealPath, workbook);
		} else {
			// 从原有文件中打开workbook
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileRealPath));
			workbook = new HSSFWorkbook(fs);
		}

		return workbook;
	}

	/**
	 * 生成Excel文件
	 * 
	 * @return
	 */
	public File toExcelFile() {
		if (!StringUtil.isBlank(fileRealPath)) {
			File file = new File(fileRealPath);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}
		return null;
	}

	protected Map<String, Object> convertToExcelData(Document doc, View view, WebUser user, ParamsTable params,
			IRunner runner, ArrayList<ValidateMessage> errors) throws Exception {
		Collection<Column> columns = view.getDisplayColumns(runner);
		runner.initBSFManager(doc, params, user, errors);
		Map<String, Object> line = new HashMap<String, Object>();
		for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
			Column col = iterator.next();
			Object result = col.getTextString(doc, runner, user);
			line.put(col.getName(), result);
		}

		return line;
	}

	/**
	 * 获得存入Excel数据
	 * 
	 * @param datas
	 * @param view
	 * @param user
	 * @param params
	 * @param runner
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	public Collection<Map<String, Object>> getExcelData(DataPackage<Document> datas, View view, WebUser user,
			ParamsTable params, IRunner runner, ArrayList<ValidateMessage> errors) throws Exception {
		Collection<Map<String, Object>> excelData = new ArrayList<Map<String, Object>>();// excel数据
		Collection<Column> columns = view.getDisplayColumns(runner);

		for (Iterator<Document> iter = datas.datas.iterator(); iter.hasNext();) {
			Document doc = iter.next();
			runner.initBSFManager(doc, params, user, errors);
			Object result = null;

			Map<String, Object> line = new HashMap<String, Object>();
			for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
				Column col = iterator.next();
				result = col.getTextString(doc, runner, user);
				line.put(col.getName(), result);
			}

			excelData.add(line);
		}

		return excelData;
	}

	/**
	 * 获得Excel的头
	 * 
	 * @param datas
	 * @param view
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getHeads(View view, IRunner runner) throws Exception {
		Collection<String> heads = new ArrayList<String>();// excel头
		Collection<Column> columns = view.getDisplayColumns(runner);

		for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
			Column col = iter.next();
			if(!Column.COLUMN_TYPE_LOGO.equals(col.getType()) && !Column.COLUMN_TYPE_OPERATE.equals(col.getType())){
				heads.add(col.getName());
			}
		}

		return heads;
	}

	public String getFileName(ParamsTable params) throws Exception {
		// 生成文件
		String fileName = params.getParameterAsString("filename").toLowerCase().trim();
		if (StringUtil.isBlank(fileName)) {
			fileName = Tools.getSequence();
		}

		return fileName;
	}

	/**
	 * 获得文件真实路径
	 * 
	 * @param excelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String getSavePath(ParamsTable params) throws Exception {

		// 生成保存目录
		String fileDir = params.getParameterAsString("filedir");
		if (StringUtil.isBlank(fileDir)) {
			fileDir = DefaultProperty.getProperty("REPORT_PATH");
		}

		String savePath = Environment.getInstance().getRealPath(fileDir);
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			if (!saveDir.mkdirs())
				throw new Exception("Folder create failure");
		}

		if (savePath.lastIndexOf("/") < 0 && savePath.lastIndexOf("\\") < 0) {
			savePath += savePath + "/";
		}
		
		return savePath;
	}
	
	/**
	 * 判断文件存在并处于打开的时候就要重新生成另外的文件名
	 * @param sb
	 * @param savePath
	 * @param fileName
	 * @throws Exception 
	 */
	public void getFileNameOther(StringBuffer sb,String savePath,String fileName) throws Exception{
		int i = 1;
		File file = new File(savePath + fileName+".xls");
		if(file.exists()&&file.isFile()){
			while(fileNameSet.contains(fileName+".xls")){
				fileName = fileName+"("+i+")";
				i++;
			}
		}
		sb.append(fileName+".xls");
	}

	/**
	 * 创建Excel工作薄标题行
	 * 
	 * @param view
	 * @param sheet
	 * @param rowCount
	 * @SuppressWarnings("deprecation") 使用了过时的API
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void addSheetHead(View view, IRunner runner, HSSFWorkbook workbook, HSSFSheet sheet) throws Exception {
		HSSFRow headrow = sheet.createRow((short) 0);
		int i = 0;
		Collection<String> heads = getHeads(view, runner);
		// HSSFCellStyle style = createCellStyle(workbook);
		for (Iterator<String> iter = heads.iterator(); iter.hasNext();) {
			String head = iter.next();
			HSSFCell cell = headrow.createCell((short) i);
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setCellStyle(style);
			cell.setCellValue(head);
			i++;
		}

		increaseRowCount(view);
	}

	/**
	 * 增加行数
	 * 
	 * @param view
	 */
	private void increaseRowCount(View view) {
		if (view2rowCountMap.containsKey(view.getId())) {
			int rowCount = view2rowCountMap.get(view.getId());
			view2rowCountMap.put(view.getId(), rowCount + 1);
		} else {
			view2rowCountMap.put(view.getId(), 1);
		}
	}

	/**
	 * 获取行数
	 * 
	 * @param view
	 * @return
	 */
	private int getRowCount(View view) {
		return view2rowCountMap.get(view.getId()) != null ? view2rowCountMap.get(view.getId()) : 0;
	}

	/**
	 * 创建Excel Cell Style
	 * 
	 * @param workbook
	 * @return
	 */
	private HSSFCellStyle createCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

		return cellStyle;
	}

	/**
	 * 
	 * @param workbook
	 * @param sheet
	 * @param heads
	 * @param datas
	 * @param outputFile
	 * @param rowCount
	 * @return
	 * @SuppressWarnings("deprecation") 使用了过时的API
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public void addSheetData(HSSFWorkbook workbook, View view, HSSFSheet sheet, Collection<String> heads,
			Map<String, Object> map) throws IOException {

		HSSFRow row = sheet.createRow((short) getRowCount(view));
		int k = 0;

		for (Iterator<String> iterator = heads.iterator(); iterator.hasNext();) {
			String columnName = iterator.next();
			Object result = map.get(columnName);
			HSSFCell cell = row.createCell((short) k);
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);

			if (result instanceof String) {
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String) result);
			} else if (result instanceof Double) {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(((Double) result).doubleValue());
			} else if (result instanceof Date) {
				cell.setCellStyle(createCellStyle(workbook));
				cell.setCellValue((Date) result);
			}
			k++;
		}

		LOG.info(view.getName() + ": line(" + getRowCount(view) + ")..........");
		increaseRowCount(view);
	}

	/**
	 * 把workbook写到具体的文件路径
	 * 
	 * @param fileRealPath
	 * @param workbook
	 * @throws IOException
	 */
	private void writeToFilePath(String fileRealPath, HSSFWorkbook workbook) throws IOException {
		// 新建一输出文件流
		FileOutputStream fOut = new FileOutputStream(fileRealPath);
		// 把相应的Excel 工作簿存盘
		workbook.write(fOut);
		fOut.flush();

		// 操作结束，关闭文件
		fOut.close();
		LOG.info("Excel文件追加成功.................");
		//new TestThread(fileRealPath,workbook).run();
	}
}
