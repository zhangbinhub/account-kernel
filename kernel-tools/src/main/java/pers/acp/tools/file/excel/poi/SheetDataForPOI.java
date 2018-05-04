package pers.acp.tools.file.excel.poi;

import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import pers.acp.tools.file.excel.common.CellPoint;
import pers.acp.tools.file.excel.common.PrintSetting;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public final class SheetDataForPOI {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    private static short FONT_SIZE = 11;

    /**
     * 生成sheet内数据
     *
     * @param wb          工作簿对象
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected Sheet generateSheetData(Workbook wb, Sheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 1)) {
            JSONObject datas = sheetConfig.getJSONObject("datas");
            int defaultRowIndex = 0;
            int defaultCellIndex = 0;
            if (datas.has("defaultRowIndex")) {
                defaultRowIndex = datas.getInt("defaultRowIndex");
            }
            if (datas.has("defaultCellIndex")) {
                defaultCellIndex = datas.getInt("defaultCellIndex");
            }
            sheet = generateSheetDataByJSON(wb, sheet,
                    datas.getJSONArray("jsonDatas"), datas.getString("names"),
                    datas.getString("titleCtrl"), datas.getString("bodyCtrl"),
                    datas.getString("footCtrl"),
                    datas.getBoolean("showBodyHead"), defaultRowIndex,
                    defaultCellIndex);
            return sheet;
        } else {
            throw new Exception("config is not complete!");
        }
    }

    /**
     * 生成sheet内数据
     *
     * @param wb               工作簿对象
     * @param sheet            需要填充数据的sheet对象
     * @param jsonArray        数据
     * @param names            名称
     * @param titleCtrl        标题行
     * @param bodyCtrl         表头
     * @param footCtrl         脚
     * @param showBodyHead     是否显示表头
     * @param defaultRowIndex  默认起始行号
     * @param defaultCellIndex 默认起始列号
     * @return sheet对象
     */
    protected Sheet generateSheetDataByJSON(Workbook wb, Sheet sheet, JSONArray jsonArray, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, int defaultRowIndex, int defaultCellIndex) throws Exception {
        int rowIndex = defaultRowIndex;
        int cellIndex = defaultCellIndex;
        /* 插入标题 start ****/
        if (!CommonUtility.isNullStr(titleCtrl)) {
            String[] titles = titleCtrl.split("\\^", -1);
            for (String title1 : titles) {
                String title = title1.substring(0, title1.indexOf("["));// 获取标题
                String titleStyle = title1.substring(title1.indexOf("[") + 1, title1.indexOf("]"));// 获取标题样式字符串
                String rowConfig = getConfig(titleStyle, "row");// 获取起始行号
                if (!CommonUtility.isNullStr(rowConfig)) {
                    rowIndex = Integer.valueOf(rowConfig);
                }
                String colConfig = getConfig(titleStyle, "col");// 获取起始列号
                if (!CommonUtility.isNullStr(colConfig)) {
                    cellIndex = Integer.valueOf(colConfig);
                }
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                Cell cell = row.createCell(cellIndex);
                CellStyle cellStyle = createStyle(wb, titleStyle, 0);// 标题样式
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(title);

                String widthConfig = getConfig(titleStyle, "width");// 获取宽度
                if (!CommonUtility.isNullStr(widthConfig)) {
                    sheet.setColumnWidth(cellIndex, Integer.valueOf(widthConfig) * 256);
                }
                String heightConfig = getConfig(titleStyle, "height");// 获取高度
                if (!CommonUtility.isNullStr(heightConfig)) {
                    row.setHeightInPoints(Integer.valueOf(heightConfig));
                }
                String colspanConfig = getConfig(titleStyle, "colspan");// 获取合并列数
                String rowspanConfig = getConfig(titleStyle, "rowspan");// 获取合并行数
                if (!CommonUtility.isNullStr(colspanConfig) && !CommonUtility.isNullStr(rowspanConfig)) {// 合并单元格
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + Integer.valueOf(rowspanConfig) - 1, cellIndex, cellIndex + Integer.valueOf(colspanConfig) - 1));
                } else if (!CommonUtility.isNullStr(colspanConfig)) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, cellIndex, cellIndex + Integer.valueOf(colspanConfig) - 1));
                } else if (!CommonUtility.isNullStr(rowspanConfig)) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + Integer.valueOf(rowspanConfig) - 1, cellIndex, cellIndex));
                }
                if (!CommonUtility.isNullStr(rowspanConfig)) {
                    rowIndex += Integer.valueOf(rowspanConfig);
                } else {
                    rowIndex++;
                }
            }
        }
        /* 插入标题 end ****/
        cellIndex = defaultCellIndex;
        /* 填充数据的内容 start ****/
        if (showBodyHead) {
            if (CommonUtility.isNullStr(bodyCtrl)) {
                rowIndex = createBodyHeadByName(wb, names, rowIndex, cellIndex, sheet);
            } else {
                rowIndex = createBodyHead(wb, bodyCtrl, rowIndex, cellIndex, sheet);
            }
        }
        String[] bodys = bodyCtrl.split("\\^", -1);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject rowData = jsonArray.getJSONObject(i);
            int cellindex = cellIndex;
            String[] name = names.split(",", -1);
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            for (String aName : name) {
                Cell cell = row.createCell(cellindex);
                CellStyle cellStyle;
                if (!CommonUtility.isNullStr(bodyCtrl) && bodys[cellindex - cellIndex].contains("[")) {
                    String bodyStr = bodys[cellindex - cellIndex];
                    String bodyStyle = bodyStr.substring(bodyStr.indexOf("[") + 1, bodyStr.indexOf("]"));// 获取标题样式字符串
                    cellStyle = createStyle(wb, bodyStyle, 1);// 标题样式
                    String widthConfig = getConfig(bodyStyle, "width");// 获取宽度
                    if (!CommonUtility.isNullStr(widthConfig)) {
                        sheet.setColumnWidth(cellindex, Integer.valueOf(widthConfig) * 256);
                    }
                    String heightConfig = getConfig(bodyStyle, "height");// 获取高度
                    if (!CommonUtility.isNullStr(heightConfig)) {
                        row.setHeightInPoints(Integer.valueOf(heightConfig));
                    }
                } else {
                    cellStyle = wb.createCellStyle();
                    Font font = wb.createFont();
                    /* 设置字体大小 */
                    font.setFontHeightInPoints(FONT_SIZE);
                    /* 设置字体颜色 */
                    font.setColor(IndexedColors.BLACK.getIndex());
                    /* 设置字体加粗 */
                    font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
                    cellStyle.setFont(font);
                    /* 设置单元格对齐方式 */
                    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                    cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
                    /* 设置单元格自动换行 */
                    cellStyle.setWrapText(true);
                    /* 边框 */
                    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                }
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(rowData.getString(aName));
                cellindex++;
            }
            rowIndex++;
        }
        /* 填充数据的内容 end ****/
        cellIndex = defaultCellIndex;
        /* 插入脚部数据 start ****/
        if (!CommonUtility.isNullStr(footCtrl)) {
            String[] foots = footCtrl.split("\\^", -1);
            for (String foot1 : foots) {
                String foot = foot1.substring(0, foot1.indexOf("["));// 获取页脚
                String footStyle = foot1.substring(foot1.indexOf("[") + 1, foot1.indexOf("]"));// 获取页脚样式字符串
                String rowConfig = getConfig(footStyle, "row");// 获取起始行号
                if (!CommonUtility.isNullStr(rowConfig)) {
                    rowIndex = Integer.valueOf(rowConfig);
                }
                String colConfig = getConfig(footStyle, "col");// 获取起始列号
                if (!CommonUtility.isNullStr(colConfig)) {
                    cellIndex = Integer.valueOf(colConfig);
                }
                String padding_rowConfig = getConfig(footStyle, "padding_row");// 获取相对行数
                int paddingrow = 0;
                if (!CommonUtility.isNullStr(padding_rowConfig)) {
                    paddingrow = Integer.valueOf(padding_rowConfig);
                }
                String widthConfig = getConfig(footStyle, "width");// 获取宽度
                if (!CommonUtility.isNullStr(widthConfig)) {
                    sheet.setColumnWidth(cellIndex, Integer.valueOf(widthConfig) * 256);
                }

                Row row = sheet.getRow(rowIndex + paddingrow);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                String heightConfig = getConfig(footStyle, "height");// 获取高度
                if (!CommonUtility.isNullStr(heightConfig)) {
                    row.setHeightInPoints(Integer.valueOf(heightConfig));
                }
                CellStyle cellStyle = createStyle(wb, footStyle, 2);// 页脚样式
                String colspanConfig = getConfig(footStyle, "colspan");// 获取合并列数
                String rowspanConfig = getConfig(footStyle, "rowspan");// 获取合并行数
                if (!CommonUtility.isNullStr(colspanConfig) && !CommonUtility.isNullStr(rowspanConfig)) {// 合并单元格
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex + paddingrow, rowIndex + paddingrow + Integer.valueOf(rowspanConfig) - 1, cellIndex, cellIndex + Integer.valueOf(colspanConfig) - 1));
                } else if (!CommonUtility.isNullStr(colspanConfig)) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex + paddingrow, rowIndex + paddingrow, cellIndex, cellIndex + Integer.valueOf(colspanConfig) - 1));
                } else if (!CommonUtility.isNullStr(rowspanConfig)) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex + paddingrow, rowIndex + paddingrow + Integer.valueOf(rowspanConfig) - 1, cellIndex, cellIndex));
                }
                Cell cell = row.createCell(cellIndex);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(foot);
            }
        }
        /* 插入页脚 end ****/
        return sheet;
    }

    /**
     * 生成sheet页眉
     *
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected Sheet generateSheetHeader(Sheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 2)) {
            JSONObject header = sheetConfig.getJSONObject("header");
            Header head = sheet.getHeader();
            if (header.has("left")) {
                String headStr = header.getString("left");
                head = buildHeadFooter(head, headStr, 0);
            }
            if (header.has("center")) {
                String headStr = header.getString("center");
                head = buildHeadFooter(head, headStr, 1);
            }
            if (header.has("right")) {
                String headStr = header.getString("right");
                buildHeadFooter(head, headStr, 2);
            }
        }
        return sheet;
    }

    /**
     * 生成sheet页脚
     *
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected Sheet generateSheetFooter(Sheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 3)) {
            JSONObject footer = sheetConfig.getJSONObject("footer");
            Footer foot = sheet.getFooter();
            if (footer.has("left")) {
                String footStr = footer.getString("left");
                foot = buildHeadFooter(foot, footStr, 0);
            }
            if (footer.has("center")) {
                String footStr = footer.getString("center");
                foot = buildHeadFooter(foot, footStr, 1);
            }
            if (footer.has("right")) {
                String footStr = footer.getString("right");
                buildHeadFooter(foot, footStr, 2);
            }
        }
        return sheet;
    }

    /**
     * 设置sheet合并单元格
     *
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected Sheet generateSheetMerge(Sheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 4)) {
            JSONArray mergeCells = sheetConfig.getJSONArray("mergeCells");
            for (int j = 0; j < mergeCells.size(); j++) {
                JSONObject mergeCellsInfo = mergeCells.getJSONObject(j);
                CellPoint cellPoint = buildCellPoint(mergeCellsInfo);
                if (cellPoint.getFirstCol() > -1 && cellPoint.getFirstRow() > -1 && cellPoint.getLastCol() > -1 && cellPoint.getLastRow() > -1) {
                    sheet.addMergedRegion(new CellRangeAddress(cellPoint.getFirstRow(), cellPoint.getLastRow(), cellPoint.getFirstCol(), cellPoint.getLastCol()));
                } else {
                    throw new Exception("merge cell config is not complete!");
                }
            }
        }
        return sheet;
    }

    /**
     * 设置sheet打印配置
     *
     * @param wb          工作簿对象
     * @param sheetIndex  sheet编号
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected Sheet generateSheetPrintSetting(Workbook wb, int sheetIndex, Sheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 5)) {
            JSONObject jsonPrintSetting = sheetConfig.getJSONObject("printSetting");
            PrintSetting printSetting = buildPrintSetting(jsonPrintSetting);
            PrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
            if (printSetting.isHorizontal()) {
                printSetup.setLandscape(true);
            } else {
                printSetup.setLandscape(false);
            }
            if (printSetting.getPageWidth() > -1) {
                printSetup.setFitWidth((short) printSetting.getPageWidth());
            }
            if (printSetting.getPageHeight() > -1) {
                printSetup.setFitHeight((short) printSetting.getPageHeight());
            }
            sheet.setMargin(Sheet.TopMargin, printSetting.getTopMargin());
            sheet.setMargin(Sheet.BottomMargin, printSetting.getBottomMargin());
            sheet.setMargin(Sheet.LeftMargin, printSetting.getLeftMargin());
            sheet.setMargin(Sheet.RightMargin, printSetting.getRightMargin());
            if (printSetting.getHorizontalCentre() != null) {
                sheet.setHorizontallyCenter(Boolean.valueOf(printSetting.getHorizontalCentre().toString()));
            }
            if (printSetting.getVerticallyCenter() != null) {
                sheet.setVerticallyCenter(Boolean.valueOf(printSetting.getVerticallyCenter().toString()));
            }
            CellPoint printArea = printSetting.getPrintArea();
            if (printArea != null) {
                if (printArea.getFirstCol() > -1 && printArea.getFirstRow() > -1 && printArea.getLastCol() > -1 && printArea.getLastRow() > -1) {
                    wb.setPrintArea(sheetIndex, printArea.getFirstCol(), printArea.getLastCol(), printArea.getFirstRow(), printArea.getLastRow());
                } else {
                    throw new Exception("print config is not complete!");
                }
            }
            CellPoint printTitles = printSetting.getPrintTitles();
            if (printTitles != null) {
                if (printTitles.getFirstCol() > -1 && printTitles.getFirstRow() > -1 && printTitles.getLastCol() > -1 && printTitles.getLastRow() > -1) {
                    sheet.setRepeatingRows(new CellRangeAddress(printTitles.getFirstRow(), printTitles.getLastRow(), printTitles.getFirstCol(), printTitles.getLastCol()));
                } else if (printTitles.getFirstCol() > -1 && printTitles.getLastCol() > -1) {
                    sheet.setRepeatingColumns(new CellRangeAddress(0, 0, printTitles.getFirstCol(), printTitles.getLastCol()));
                } else if (printTitles.getFirstRow() > -1 && printTitles.getLastRow() > -1) {
                    sheet.setRepeatingRows(new CellRangeAddress(printTitles.getFirstRow(), printTitles.getLastRow(), 0, 0));
                } else {
                    throw new Exception("print titles is not complete!");
                }
            }
        }
        return sheet;
    }

    /**
     * 设置sheet窗口冻结
     *
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected Sheet generateSheetFreeze(Sheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 6)) {
            int row = 0;
            int col = 0;
            JSONObject freeze = sheetConfig.getJSONObject("freeze");
            if (freeze.has("row")) {
                row = freeze.getInt("row");
                if (row < 0) {
                    throw new Exception("freeze row config is not complete!");
                }
            }
            if (freeze.has("col")) {
                col = freeze.getInt("col");
                if (col < 0) {
                    throw new Exception("freeze cell config is not complete!");
                }
            }
            sheet.createFreezePane(col, row, col, row);
        }
        return sheet;
    }

    /**
     * 校验配置信息
     *
     * @param sheetConfig sheet配置
     * @param flag        0-sheet基本配置信息 1-数据配置信息 2-页眉配置信息 3-页脚配置信息 4-合并单元格配置信息 5-打印配置信息
     *                    6-冻结窗口配置信息
     * @return 校验是否通过
     */
    protected boolean validationConfig(JSONObject sheetConfig, int flag) {
        if (flag == 0) {
            if (!sheetConfig.has("sheetName")) {
                return false;
            }
        } else if (flag == 1) {
            if (!sheetConfig.has("datas")) {
                log.error("generate Excel faild: don't find datas!");
                return false;
            } else {
                JSONObject datas = sheetConfig.getJSONObject("datas");
                if (!datas.has("jsonDatas")) {
                    log.error("generate Excel faild: don't find [jsonDatas] in datas");
                    return false;
                }
                if (!datas.has("names")) {
                    log.error("generate Excel faild: don't find [names] in datas");
                    return false;
                }
                if (!datas.has("titleCtrl")) {
                    log.error("generate Excel faild: don't find [titleCtrl] in datas");
                    return false;
                }
                if (!datas.has("bodyCtrl")) {
                    log.error("generate Excel faild: don't find [bodyCtrl] in datas");
                    return false;
                }
                if (!datas.has("footCtrl")) {
                    log.error("generate Excel faild: don't find [footCtrl] in datas");
                    return false;
                }
                if (!datas.has("showBodyHead")) {
                    log.error("generate Excel faild: don't find [showBodyHead] in datas");
                    return false;
                }
            }
        } else if (flag == 2) {
            if (!sheetConfig.has("header")) {
                return false;
            }
        } else if (flag == 3) {
            if (!sheetConfig.has("footer")) {
                return false;
            }
        } else if (flag == 4) {
            if (!sheetConfig.has("mergeCells")) {
                return false;
            }
        } else if (flag == 5) {
            if (!sheetConfig.has("printSetting")) {
                return false;
            }
        } else if (flag == 6) {
            if (!sheetConfig.has("freeze")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取配置信息
     *
     * @param styleStr   样式配置信息
     * @param configName 配置名称
     * @return 配置信息值
     */
    private String getConfig(String styleStr, String configName) {
        String[] styles = styleStr.split(",", -1);
        for (String style : styles)
            if (style.contains(configName) && style.substring(0, style.indexOf("=")).length() == configName.length()) {
                return style.substring(configName.length() + 1);
            }
        return "";
    }

    /**
     * 创建样式
     *
     * @param wb    工作簿对象
     * @param style 样式信息
     * @param flag  0-标题,1-数据,2-脚
     * @return 单元格样式
     */
    private CellStyle createStyle(Workbook wb, String style, int flag) throws Exception {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        /* 设置字体大小 */
        short fontSize = FONT_SIZE;
        String fontConfig = getConfig(style, "font");
        if (!CommonUtility.isNullStr(fontConfig)) {
            fontSize = Short.valueOf(fontConfig);
        }
        font.setFontHeightInPoints(fontSize);
        /* 设置字体颜色 */
        short color = IndexedColors.BLACK.getIndex();
        String colorConfig = getConfig(style, "color");
        if (!CommonUtility.isNullStr(colorConfig)) {
            switch (colorConfig) {
                case "black":
                    color = IndexedColors.BLACK.getIndex();
                    break;
                case "blue":
                    color = IndexedColors.BLUE.getIndex();
                    break;
                case "red":
                    color = IndexedColors.RED.getIndex();
                    break;
                case "green":
                    color = IndexedColors.GREEN.getIndex();
                    break;
            }
        }
        font.setColor(color);
        /* 设置字体加粗 */
        String boldConfig = getConfig(style, "bold");
        if (flag == 0) {
            if (boldConfig.equals("false")) {
                font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
            } else {
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            }
        } else {
            if (boldConfig.equals("true")) {
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            } else {
                font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
            }
        }
        /* 设置字体下划线 */
        String underlineConfig = getConfig(style, "underline");
        if (underlineConfig.equals("true")) {
            font.setUnderline(Font.U_SINGLE);
        }
        cellStyle.setFont(font);
        /* 设置单元格对齐方式 */
        String alignConfig = getConfig(style, "align");
        short align = CellStyle.ALIGN_LEFT;
        if (flag == 0) {
            align = CellStyle.ALIGN_CENTER;
        }
        if (!CommonUtility.isNullStr(alignConfig)) {
            switch (alignConfig) {
                case "center":
                    align = CellStyle.ALIGN_CENTER;
                    break;
                case "left":
                    align = CellStyle.ALIGN_LEFT;
                    break;
                case "right":
                    align = CellStyle.ALIGN_RIGHT;
                    break;
            }
        }
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellStyle.setAlignment(align);
        /* 设置单元格自动换行 */
        cellStyle.setWrapText(true);
        /* 设置单元格边框 */
        if (flag != 2) {
            String borderConfig = getConfig(style, "border");
            if (!CommonUtility.isNullStr(borderConfig)) {
                switch (borderConfig) {
                    case "no":
                        cellStyle.setBorderTop(CellStyle.BORDER_NONE);
                        cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
                        cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
                        cellStyle.setBorderRight(CellStyle.BORDER_NONE);
                        break;
                    case "all":
                        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                        break;
                    case "top":
                        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                        break;
                    case "bottom":
                        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                        break;
                    case "right":
                        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                        break;
                    case "left":
                        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                        break;
                }
            } else {
                cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            }
        } else {
            String borderConfig = getConfig(style, "border");
            if (!CommonUtility.isNullStr(borderConfig)) {
                switch (borderConfig) {
                    case "no":
                        cellStyle.setBorderTop(CellStyle.BORDER_NONE);
                        cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
                        cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
                        cellStyle.setBorderRight(CellStyle.BORDER_NONE);
                        break;
                    case "all":
                        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                        break;
                    case "top":
                        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                        break;
                    case "bottom":
                        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                        break;
                    case "right":
                        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                        break;
                    case "left":
                        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                        break;
                }
            } else {
                cellStyle.setBorderTop(CellStyle.BORDER_NONE);
                cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
                cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
                cellStyle.setBorderRight(CellStyle.BORDER_NONE);
            }
        }
        return cellStyle;
    }

    /**
     * 通过配置信息生成表头
     *
     * @param wb 工作簿对象
     */
    private int createBodyHead(Workbook wb, String bodyCtrl, int rowIndex, int cellIndex, Sheet sheet) throws Exception {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        String[] bodys = bodyCtrl.split("\\^", -1);
        for (int i = 0; i < bodys.length; i++) {
            Cell cell = row.createCell(cellIndex + i);
            CellStyle cellStyle;
            String headStr = bodys[i];
            String headtitle;
            if (headStr.contains("[")) {
                headtitle = headStr.substring(0, headStr.indexOf("["));// 获取标题
                String headStyle = headStr.substring(headStr.indexOf("[") + 1, headStr.indexOf("]"));// 获取标题样式字符串
                cellStyle = createStyle(wb, headStyle, 0);// 标题样式
                String widthConfig = getConfig(headStyle, "width");// 获取宽度
                if (!CommonUtility.isNullStr(widthConfig)) {
                    sheet.setColumnWidth(cellIndex + i, Integer.valueOf(widthConfig) * 256);
                }
            } else {
                headtitle = headStr;// 获取标题
                cellStyle = wb.createCellStyle();
                Font font = wb.createFont();
                /* 设置字体大小 */
                font.setFontHeightInPoints(FONT_SIZE);
                /* 设置字体颜色 */
                font.setColor(IndexedColors.BLACK.getIndex());
                /* 设置字体加粗 */
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                cellStyle.setFont(font);
                /* 设置单元格对齐方式 */
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                /* 设置单元格自动换行 */
                cellStyle.setWrapText(true);
                /* 边框 */
                cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            }
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(headtitle);
            cell.setCellStyle(cellStyle);
        }
        return ++rowIndex;
    }

    /**
     * 通过数据列名生成表头
     *
     * @param wb
     * @param names
     * @param rowIndex
     * @param cellIndex
     * @param sheet
     * @return
     * @throws Exception
     */
    private int createBodyHeadByName(Workbook wb, String names, int rowIndex, int cellIndex, Sheet sheet) throws Exception {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        /** 设置字体大小 */
        font.setFontHeightInPoints(FONT_SIZE);
        /** 设置字体颜色 */
        font.setColor(IndexedColors.BLACK.getIndex());
        /** 设置字体加粗 */
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);
        /** 设置单元格对齐方式 */
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        /** 设置单元格自动换行 */
        cellStyle.setWrapText(true);
        /** 边框 */
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        int cellindex = cellIndex;
        String[] name = names.split(",", -1);
        for (String aName : name) {
            Cell cell = row.createCell(cellindex);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(aName);
            cell.setCellStyle(cellStyle);
            cellindex++;
        }
        return ++rowIndex;
    }

    /**
     * 构建页眉页脚
     *
     * @param headerfooter 页眉页脚对象
     * @param info         配置信息字符串
     * @param flag         0-左 1-中 2-右
     * @return
     */
    private Header buildHeadFooter(Header headerfooter, String info, int flag) {
        StringBuilder contents;
        if (flag == 0) {
            contents = new StringBuilder(headerfooter.getLeft());
        } else if (flag == 2) {
            contents = new StringBuilder(headerfooter.getRight());
        } else {
            contents = new StringBuilder(headerfooter.getCenter());
        }
        int pageNumberBegin = info.indexOf("[pageNumber]");
        int pageTotalBegin = info.indexOf("[pageTotal]");
        if (pageNumberBegin > -1 && pageTotalBegin < 0) {
            contents.append(info.substring(0, pageNumberBegin));
            contents.append(HSSFHeader.page());
            contents.append(info.substring(pageNumberBegin + 12));
        } else if (pageNumberBegin > -1 && pageTotalBegin > -1) {
            if (pageNumberBegin < pageTotalBegin) {
                contents.append(info.substring(0, pageNumberBegin));
                contents.append(HSSFHeader.page());
                contents.append(info.substring(pageNumberBegin + 12, pageTotalBegin));
                contents.append(HSSFHeader.numPages());
                contents.append(info.substring(pageTotalBegin + 11));
            } else {
                contents.append(info.substring(0, pageTotalBegin));
                contents.append(HSSFHeader.numPages());
                contents.append(info.substring(pageTotalBegin + 11, pageNumberBegin));
                contents.append(HSSFHeader.page());
                contents.append(info.substring(pageNumberBegin + 12));
            }
        } else if (pageNumberBegin < 0 && pageTotalBegin > -1) {
            contents.append(info.substring(0, pageTotalBegin));
            contents.append(HSSFHeader.numPages());
            contents.append(info.substring(pageTotalBegin + 11));
        } else {
            contents.append(info);
        }
        if (flag == 0) {
            headerfooter.setLeft(contents.toString());
        } else if (flag == 2) {
            headerfooter.setRight(contents.toString());
        } else {
            headerfooter.setCenter(contents.toString());
        }
        return headerfooter;
    }

    /**
     * 构建页眉页脚
     *
     * @param headerfooter 页眉页脚对象
     * @param info         配置信息字符串
     * @param flag         0-左 1-中 2-右
     * @return
     */
    private Footer buildHeadFooter(Footer headerfooter, String info, int flag) {
        StringBuilder contents;
        if (flag == 0) {
            contents = new StringBuilder(headerfooter.getLeft());
        } else if (flag == 2) {
            contents = new StringBuilder(headerfooter.getRight());
        } else {
            contents = new StringBuilder(headerfooter.getCenter());
        }
        int pageNumberBegin = info.indexOf("[pageNumber]");
        int pageTotalBegin = info.indexOf("[pageTotal]");
        if (pageNumberBegin > -1 && pageTotalBegin < 0) {
            contents.append(info.substring(0, pageNumberBegin));
            contents.append(HSSFFooter.page());
            contents.append(info.substring(pageNumberBegin + 12));
        } else if (pageNumberBegin > -1 && pageTotalBegin > -1) {
            if (pageNumberBegin < pageTotalBegin) {
                contents.append(info.substring(0, pageNumberBegin));
                contents.append(HSSFFooter.page());
                contents.append(info.substring(pageNumberBegin + 12, pageTotalBegin));
                contents.append(HSSFFooter.numPages());
                contents.append(info.substring(pageTotalBegin + 11));
            } else {
                contents.append(info.substring(0, pageTotalBegin));
                contents.append(HSSFFooter.numPages());
                contents.append(info.substring(pageTotalBegin + 11, pageNumberBegin));
                contents.append(HSSFFooter.page());
                contents.append(info.substring(pageNumberBegin + 12));
            }
        } else if (pageNumberBegin < 0 && pageTotalBegin > -1) {
            contents.append(info.substring(0, pageTotalBegin));
            contents.append(HSSFFooter.numPages());
            contents.append(info.substring(pageTotalBegin + 11));
        } else {
            contents.append(info);
        }
        if (flag == 0) {
            headerfooter.setLeft(contents.toString());
        } else if (flag == 2) {
            headerfooter.setRight(contents.toString());
        } else {
            headerfooter.setCenter(contents.toString());
        }
        return headerfooter;
    }

    /**
     * 构建起止单元格坐标类
     *
     * @param jsonCellsInfo 起止信息
     * @return
     */
    private CellPoint buildCellPoint(JSONObject jsonCellsInfo) {
        CellPoint cellPoint = new CellPoint();
        if (jsonCellsInfo.has("firstCol")) {
            cellPoint.setFirstCol(jsonCellsInfo.getInt("firstCol"));
        }
        if (jsonCellsInfo.has("firstRow")) {
            cellPoint.setFirstRow(jsonCellsInfo.getInt("firstRow"));
        }
        if (jsonCellsInfo.has("lastCol")) {
            cellPoint.setLastCol(jsonCellsInfo.getInt("lastCol"));
        }
        if (jsonCellsInfo.has("lastRow")) {
            cellPoint.setLastRow(jsonCellsInfo.getInt("lastRow"));
        }
        return cellPoint;
    }

    /**
     * 构建打印配置信息类
     *
     * @param jsonPrintSetting 打印配置信息
     * @return
     */
    private PrintSetting buildPrintSetting(JSONObject jsonPrintSetting) {
        PrintSetting printSetting = new PrintSetting();
        if (jsonPrintSetting.has("isHorizontal")) {
            printSetting.setHorizontal(jsonPrintSetting.getBoolean("isHorizontal"));
        }
        if (jsonPrintSetting.has("pageWidth")) {
            printSetting.setPageWidth(jsonPrintSetting.getInt("pageWidth"));
        }
        if (jsonPrintSetting.has("pageHeight")) {
            printSetting.setPageHeight(jsonPrintSetting.getInt("pageHeight"));
        }
        if (jsonPrintSetting.has("topMargin")) {
            printSetting.setTopMargin(jsonPrintSetting.getDouble("topMargin"));
        }
        if (jsonPrintSetting.has("bottomMargin")) {
            printSetting.setBottomMargin(jsonPrintSetting.getDouble("bottomMargin"));
        }
        if (jsonPrintSetting.has("leftMargin")) {
            printSetting.setLeftMargin(jsonPrintSetting.getDouble("leftMargin"));
        }
        if (jsonPrintSetting.has("rightMargin")) {
            printSetting.setRightMargin(jsonPrintSetting.getDouble("rightMargin"));
        }
        if (jsonPrintSetting.has("horizontalCentre")) {
            printSetting.setHorizontalCentre(jsonPrintSetting.getBoolean("horizontalCentre"));
        }
        if (jsonPrintSetting.has("verticallyCenter")) {
            printSetting.setVerticallyCenter(jsonPrintSetting.getBoolean("verticallyCenter"));
        }
        if (jsonPrintSetting.has("printArea")) {
            JSONObject printArea = jsonPrintSetting.getJSONObject("printArea");
            CellPoint cellPoint = buildCellPoint(printArea);
            printSetting.setPrintArea(cellPoint);
        }
        if (jsonPrintSetting.has("printTitles")) {
            JSONObject printTitles = jsonPrintSetting.getJSONObject("printTitles");
            CellPoint cellPoint = buildCellPoint(printTitles);
            printSetting.setPrintTitles(cellPoint);
        }
        return printSetting;
    }
}
