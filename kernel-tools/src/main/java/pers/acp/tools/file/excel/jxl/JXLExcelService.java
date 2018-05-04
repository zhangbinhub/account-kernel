package pers.acp.tools.file.excel.jxl;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import jxl.*;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

public final class JXLExcelService {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    /**
     * 创建Excel文件
     *
     * @param filename         文件绝对路径
     * @param jsonArray        数据
     * @param names            名称
     * @param titleCtrl        标题行
     *                         "内容[row=,col=,colspan=,rowspan=,width=,height=,font=,align=,border=no|all|top|left|right|bottom,bold=true|false]^..."
     * @param bodyCtrl         表头
     * @param footCtrl         脚
     * @param showBodyHead     是否显示表头
     * @param isHorizontal     是否是横向模式
     * @param defaultRowIndex  默认起始行号
     * @param defaultCellIndex 默认起始列号
     * @return 文件绝对路径
     */
    public String createExcelFile(String filename, JSONArray jsonArray, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, boolean isHorizontal, int defaultRowIndex, int defaultCellIndex) {
        WritableWorkbook wwb = null;
        if (CommonUtility.isNullStr(filename)) {
            return "";
        }
        File file = new File(filename);
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                wwb = Workbook.createWorkbook(file);
                /*创建一个工作表 ****/
                WritableSheet sheet = wwb.createSheet("sheet1", 0);
                sheet.getSettings().setPaperSize(PaperSize.A4);
                if (isHorizontal) {
                    sheet.getSettings().setOrientation(PageOrientation.LANDSCAPE);
                } else {
                    sheet.getSettings().setOrientation(PageOrientation.PORTRAIT);
                }
                SheetDataForJXL sheetData = new SheetDataForJXL();
                sheetData.generateSheetDataByJSON(sheet, jsonArray, names, titleCtrl, bodyCtrl, footCtrl, showBodyHead, defaultRowIndex, defaultCellIndex);
                wwb.write();
                wwb.close();
                return filename;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("generate Excel Exception:" + e.getMessage(), e);
            if (file.exists()) {
                try {
                    wwb.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.error(e1.getMessage(), e1);
                }
                file.delete();
            }
            return "";
        }
    }

    /**
     * 创建Excel文件
     *
     * @param fileName   文件绝对路径
     * @param configJSON 配置信息 [{ "sheetName":String, "printSetting":{
     *                   "isHorizontal":boolean, "pageWidth":int, "pageHeight":int,
     *                   "topMargin":double, "bottomMargin":double,
     *                   "leftMargin":double, "rightMargin":double,
     *                   "horizontalCentre":boolean, "verticallyCenter":boolean,
     *                   "printArea"
     *                   :{"firstCol":int,"firstRow":int,"lastCol":int,"lastRow":int},
     *                   "printTitles"
     *                   :{"firstRow":int,"lastRow":int,"firstCol":int,"lastCol":int}
     *                   },
     *                   "header":{"left":"***[pageNumber]***[pageTotal]***","center"
     *                   :"***[pageNumber]***[pageTotal]***"
     *                   ,"right":"***[pageNumber]***[pageTotal]***"},
     *                   "footer":{"left":"***[pageNumber]***[pageTotal]***","center":
     *                   "***[pageNumber]***[pageTotal]***"
     *                   ,"right":"***[pageNumber]***[pageTotal]***"}, "datas":{
     *                   "jsonDatas":[{"name":value,"name":value,...},{...}...],
     *                   "names":String, "titleCtrl":
     *                   "内容[row=,col=,colspan=,rowspan=,width=,height=,font=,align=,border=no|all|top|left|right|bottom,bold=true|false]^..."
     *                   , "bodyCtrl":String, "footCtrl":String,
     *                   "showBodyHead":boolean, "defaultRowIndex":int,
     *                   "defaultCellIndex":int },"mergeCells":[{
     *                   "firstCol":int,"firstRow":int,"lastCol":int,"lastRow"
     *                   :int},{...},...], "freeze":{"row":int,"col":int} }, {...},...]
     * @return 文件绝对路径
     */
    public String createExcelFile(String fileName, JSONArray configJSON) {
        SheetDataForJXL sheetData = new SheetDataForJXL();
        String resultFile = fileName;
        WritableWorkbook wwb = null;
        if (CommonUtility.isNullStr(resultFile)) {
            return "";
        }
        File file = new File(resultFile);
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                wwb = Workbook.createWorkbook(file);
                /* 循环生成sheet ****/
                for (int i = 0; i < configJSON.size(); i++) {
                    JSONObject sheetConfig = configJSON.getJSONObject(i);
                    String sheetName = "sheet" + i;
                    if (sheetData.validationConfig(sheetConfig, 0)) {
                        sheetName = sheetConfig.getString("sheetName");
                    }
                    /* 创建sheet **/
                    WritableSheet sheet = wwb.createSheet(sheetName, i);
                    /* 生成sheet内数据 **/
                    sheet = sheetData.generateSheetData(sheet, sheetConfig);
                    /* 设置sheet页眉 **/
                    sheet = sheetData.generateSheetHeader(sheet, sheetConfig);
                    /* 设置sheet页脚 **/
                    sheet = sheetData.generateSheetFooter(sheet, sheetConfig);
                    /* 设置sheet合并单元格 **/
                    sheet = sheetData.generateSheetMerge(sheet, sheetConfig);
                    /* 设置sheet打印配置 **/
                    sheet = sheetData.generateSheetPrintSetting(sheet, sheetConfig);
                    /* 设置sheet窗口冻结 **/
                    sheetData.generateSheetFreeze(sheet, sheetConfig);
                }
                wwb.write();
                wwb.close();
                return resultFile;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("generate Excel Exception:" + e.getMessage(), e);
            if (file.exists()) {
                try {
                    wwb.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.error(e1.getMessage(), e1);
                }
                file.delete();
            }
            return "";
        }
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径
     * @param sheetNo  工作表序号
     * @param beginRow 读取的起始行
     * @param beginCol 读取的起始列
     * @param rowNo    读取的行数，0则表示读取全部
     * @param colNo    读取的列数，0则表示读取全部
     * @param isDelete 是否读取完数据后删除文件
     * @return 结果集
     */
    public JSONArray readExcelData(String filePath, int sheetNo, int beginRow, int beginCol, int rowNo, int colNo, boolean isDelete) {
        JSONArray result = new JSONArray();
        File excelFile = null;
        File f = new File(filePath);
        String fileName = f.getName();
        String prefix = FileCommon.getFileExt(fileName);
        if (prefix.equals("xlsx") || prefix.equals("xls")) {
            if (prefix.equals("xlsx")) {
                XSSFWorkbook xwb;
                try {
                    excelFile = new File(filePath);
                    xwb = new XSSFWorkbook(new FileInputStream(excelFile));
                    XSSFSheet sheet = xwb.getSheetAt(sheetNo);
                    int lastrownum = sheet.getLastRowNum() + 1;
                    int rowCount = lastrownum - beginRow;
                    if (rowNo > 0) {
                        rowCount = rowNo;
                    }
                    int colCont = 0;
                    for (int i = sheet.getFirstRowNum(); i < lastrownum; i++) {
                        XSSFRow row = sheet.getRow(i);
                        if (row != null) {
                            int cellCount = row.getLastCellNum();
                            if (cellCount >= colCont) {
                                colCont = cellCount;
                            }
                        }
                    }
                    colCont = colCont + 1 - beginCol;
                    if (colNo > 0) {
                        colCont = colNo;
                    }
                    for (int i = beginRow; i < beginRow + rowCount; i++) {
                        JSONArray rowData = new JSONArray();
                        XSSFRow row = sheet.getRow(i);
                        for (int j = beginCol; j < colCont; j++) {
                            JSONObject cellData = new JSONObject();
                            if (row == null) {
                                cellData.put("type", "string");
                                cellData.put("value", "");
                                rowData.add(cellData);
                                continue;
                            }
                            XSSFCell cell = row.getCell(j);
                            if (cell != null) {
                                switch (cell.getCellType()) {
                                    case XSSFCell.CELL_TYPE_STRING:
                                        cellData.put("type", "string");
                                        cellData.put("value", cell.getStringCellValue());
                                        break;
                                    case XSSFCell.CELL_TYPE_NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            cellData.put("type", "date");
                                            cellData.put("value", DateUtil.getJavaDate(cell.getNumericCellValue()));
                                        } else {
                                            cellData.put("type", "number");
                                            cellData.put("value", cell.getNumericCellValue());
                                        }
                                        break;
                                    case XSSFCell.CELL_TYPE_BOOLEAN:
                                        cellData.put("type", "boolean");
                                        cellData.put("value", cell.getBooleanCellValue());
                                        break;
                                    case XSSFCell.CELL_TYPE_BLANK:
                                        cellData.put("type", "string");
                                        cellData.put("value", "");
                                        break;
                                    case XSSFCell.CELL_TYPE_FORMULA:
                                        cellData.put("type", "formula");
                                        cellData.put("value", cell.getCellFormula());
                                        break;
                                    default:
                                        cellData.put("type", "string");
                                        cellData.put("value", "");
                                        break;
                                }
                            } else {
                                cellData.put("type", "string");
                                cellData.put("value", "");
                            }
                            rowData.add(cellData);
                        }
                        result.add(rowData);
                    }
                    if (isDelete) {
                        excelFile.delete();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (isDelete) {
                        if (excelFile.exists()) {
                            excelFile.delete();
                        }
                    }
                    result = new JSONArray();
                }
            } else {
                Workbook book = null;
                try {
                    excelFile = new File(filePath);
                    book = Workbook.getWorkbook(excelFile);
                    Sheet sheet = book.getSheet(sheetNo);
                    int rowCount = sheet.getRows() - beginRow;
                    if (rowNo > 0) {
                        rowCount = rowNo;
                    }
                    int colCont = sheet.getColumns() - beginCol;
                    if (colNo > 0) {
                        colCont = colNo;
                    }
                    for (int i = beginRow; i < rowCount; i++) {
                        JSONArray rowData = new JSONArray();
                        for (int j = beginCol; j < colCont; j++) {
                            Cell cell = sheet.getCell(j, i);
                            JSONObject cellData = new JSONObject();
                            CellType type = cell.getType();
                            if (type == CellType.EMPTY) {
                                cellData.put("type", "string");
                                cellData.put("value", "");
                            } else if (type == CellType.DATE) {
                                cellData.put("type", "date");
                                cellData.put("value", ((DateCell) cell).getDate().getTime());
                            } else if (type == CellType.NUMBER) {
                                cellData.put("type", "number");
                                double value = ((NumberCell) cell).getValue();
                                BigDecimal bd = new BigDecimal(value);
                                cellData.put("value", bd.toPlainString());
                            } else {
                                cellData.put("type", "string");
                                cellData.put("value", cell.getContents());
                            }
                            rowData.add(cellData);
                        }
                        result.add(rowData);
                    }
                    book.close();
                    if (isDelete) {
                        excelFile.delete();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (book != null) {
                        book.close();
                    }
                    if (isDelete) {
                        if (excelFile.exists()) {
                            excelFile.delete();
                        }
                    }
                    result = new JSONArray();
                }
            }
        }
        return result;
    }
}
