package pers.acp.tools.file.excel.poi;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

public final class POIExcelService {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    /**
     * 通过模板创建Excel文件
     *
     * @param filename     文件绝对路径
     * @param templatePath 模板绝对路径
     * @param data         数据
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String filename, String templatePath, Map<String, String> data) {
        if (CommonUtility.isNullStr(filename)) {
            return "";
        }
        if (CommonUtility.isNullStr(templatePath)) {
            return "";
        }
        String disfix = FileCommon.getFileExt(filename);
        String tempfix = FileCommon.getFileExt(templatePath);
        if (disfix.equals(tempfix)) {
            Workbook wb;
            try {
                /* 源文件（模板） */
                File sourceFile = new File(templatePath);
                /* 读入源文件 */
                if (tempfix.equals("xlsx")) {
                    wb = new XSSFWorkbook(new FileInputStream(sourceFile));
                } else {
                    wb = new HSSFWorkbook(new FileInputStream(sourceFile));
                }
                /* 目标文件 */
                File targetFile = new File(filename);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                /* 源文件内容导入目标文件 */
                for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                    Sheet sheet = wb.getSheetAt(s);
                    int rowCount = sheet.getLastRowNum() + 1;
                    for (int i = 0; i < rowCount; i++) {
                        Row row = sheet.getRow(i);
                        int colCount = row.getLastCellNum() + 1;
                        for (int j = 0; j < colCount; j++) {
                            Cell cell = row.getCell(j);
                            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                cell.setCellValue(FileCommon.replaceVar(cell.getStringCellValue(), data));
                            }
                        }
                    }
                }
                wb.write(new FileOutputStream(targetFile));
                return filename;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return "";
            }
        } else {
            log.error("目标文件格式和模板文件格式不一致！");
            return "";
        }
    }

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
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String filename, JSONArray jsonArray, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, boolean isHorizontal, int defaultRowIndex, int defaultCellIndex) {
        if (CommonUtility.isNullStr(filename)) {
            return "";
        }
        String fileType = FileCommon.getFileExt(filename);
        File file = new File(filename);
        Workbook wb;
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                switch (fileType) {
                    case "xls":
                        wb = new HSSFWorkbook();
                        break;
                    case "xlsx":
                        wb = new XSSFWorkbook();
                        break;
                    default:
                        log.error("file type [" + fileType + "] is not support! ");
                        return "";
                }
                Sheet sheet = wb.createSheet("sheet1");
                /* 创建一个工作表 ****/
                PrintSetup printSetup = sheet.getPrintSetup();
                printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
                if (isHorizontal) {
                    printSetup.setLandscape(true);
                } else {
                    printSetup.setLandscape(false);
                }
                SheetDataForPOI sheetData = new SheetDataForPOI();
                sheetData.generateSheetDataByJSON(wb, sheet, jsonArray, names, titleCtrl, bodyCtrl, footCtrl, showBodyHead, defaultRowIndex, defaultCellIndex);
                wb.write(new FileOutputStream(file));
                return filename;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("generate Excel Exception:" + e.getMessage(), e);
            if (file.exists()) {
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
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String fileName, JSONArray configJSON) {
        SheetDataForPOI sheetData = new SheetDataForPOI();
        String fileType = FileCommon.getFileExt(fileName);
        Workbook wb;
        if (CommonUtility.isNullStr(fileName)) {
            return "";
        }
        File file = new File(fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                switch (fileType) {
                    case "xls":
                        wb = new HSSFWorkbook();
                        break;
                    case "xlsx":
                        wb = new XSSFWorkbook();
                        break;
                    default:
                        log.error("file type [" + fileType + "] is not support! ");
                        return "";
                }
                /* 循环生成sheet ****/
                for (int i = 0; i < configJSON.size(); i++) {
                    JSONObject sheetConfig = configJSON.getJSONObject(i);
                    String sheetName = "sheet" + i;
                    if (sheetData.validationConfig(sheetConfig, 0)) {
                        sheetName = sheetConfig.getString("sheetName");
                    }
                    /* 创建sheet **/
                    Sheet sheet = wb.createSheet(sheetName);
                    /* 生成sheet内数据 **/
                    sheet = sheetData.generateSheetData(wb, sheet, sheetConfig);
                    /* 设置sheet页眉 **/
                    sheet = sheetData.generateSheetHeader(sheet, sheetConfig);
                    /* 设置sheet页脚 **/
                    sheet = sheetData.generateSheetFooter(sheet, sheetConfig);
                    /* 设置sheet合并单元格 **/
                    sheet = sheetData.generateSheetMerge(sheet, sheetConfig);
                    /* 设置sheet打印配置 **/
                    sheet = sheetData.generateSheetPrintSetting(wb, i, sheet, sheetConfig);
                    /* 设置sheet窗口冻结 **/
                    sheetData.generateSheetFreeze(sheet, sheetConfig);
                }
                wb.write(new FileOutputStream(file));
                return fileName;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("generate Excel Exception:" + e.getMessage());
            if (file.exists()) {
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
        File excelFile;
        File f = new File(filePath);
        String fileName = f.getName();
        String prefix = FileCommon.getFileExt(fileName);
        if (prefix.equals("xlsx") || prefix.equals("xls")) {
            Workbook wb;
            excelFile = new File(filePath);
            try {
                if (prefix.equals("xlsx")) {
                    wb = new XSSFWorkbook(new FileInputStream(excelFile));
                } else {
                    wb = new HSSFWorkbook(new FileInputStream(excelFile));
                }
                Sheet sheet = wb.getSheetAt(sheetNo);
                int lastrownum = sheet.getLastRowNum() + 1;
                int rowCount = lastrownum - beginRow;
                if (rowNo > 0) {
                    rowCount = rowNo;
                }
                int colCont = 0;
                for (int i = sheet.getFirstRowNum(); i < lastrownum; i++) {
                    Row row = sheet.getRow(i);
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
                    Row row = sheet.getRow(i);
                    for (int j = beginCol; j < colCont; j++) {
                        JSONObject cellData = new JSONObject();
                        if (row == null) {
                            cellData.put("type", "string");
                            cellData.put("value", "");
                            rowData.add(cellData);
                            continue;
                        }
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    cellData.put("type", "string");
                                    cellData.put("value", cell.getStringCellValue());
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        cellData.put("type", "date");
                                        cellData.put("value", DateUtil.getJavaDate(cell.getNumericCellValue()));
                                    } else {
                                        cellData.put("type", "number");
                                        cellData.put("value", cell.getNumericCellValue());
                                    }
                                    break;
                                case Cell.CELL_TYPE_BOOLEAN:
                                    cellData.put("type", "boolean");
                                    cellData.put("value", cell.getBooleanCellValue());
                                    break;
                                case Cell.CELL_TYPE_BLANK:
                                    cellData.put("type", "string");
                                    cellData.put("value", "");
                                    break;
                                case Cell.CELL_TYPE_FORMULA:
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
        }
        return result;
    }
}
