package pers.acp.tools.file.excel.jxl;

import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import pers.acp.tools.file.excel.common.CellPoint;
import pers.acp.tools.file.excel.common.PrintSetting;

import jxl.HeaderFooter;
import jxl.HeaderFooter.Contents;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public final class SheetDataForJXL {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    private static short FONT_SIZE = 11;

    /**
     * 生成sheet内数据
     *
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected WritableSheet generateSheetData(WritableSheet sheet, JSONObject sheetConfig) throws Exception {
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
            sheet = generateSheetDataByJSON(sheet,
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
    protected WritableSheet generateSheetDataByJSON(WritableSheet sheet, JSONArray jsonArray, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, int defaultRowIndex, int defaultCellIndex) throws Exception {
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
                WritableCellFormat wcfTitle = createStyle(titleStyle, 0);// 标题样式

                String widthConfig = getConfig(titleStyle, "width");// 获取宽度
                if (!CommonUtility.isNullStr(widthConfig)) {
                    sheet.setColumnView(cellIndex, Integer.valueOf(widthConfig));
                }
                String heightConfig = getConfig(titleStyle, "height");// 获取高度
                if (!CommonUtility.isNullStr(heightConfig)) {
                    sheet.setRowView(rowIndex, Integer.valueOf(heightConfig));
                }

                String colspanConfig = getConfig(titleStyle, "colspan");// 获取合并列数
                String rowspanConfig = getConfig(titleStyle, "rowspan");// 获取合并行数
                if (!CommonUtility.isNullStr(colspanConfig) && !CommonUtility.isNullStr(rowspanConfig)) {// 合并单元格
                    sheet.mergeCells(cellIndex, rowIndex, cellIndex + Integer.valueOf(colspanConfig) - 1, rowIndex + Integer.valueOf(rowspanConfig) - 1);
                } else if (!CommonUtility.isNullStr(colspanConfig)) {
                    sheet.mergeCells(cellIndex, rowIndex, cellIndex + Integer.valueOf(colspanConfig) - 1, rowIndex);
                } else if (!CommonUtility.isNullStr(rowspanConfig)) {
                    sheet.mergeCells(cellIndex, rowIndex, cellIndex, rowIndex + Integer.valueOf(rowspanConfig) - 1);
                }

                sheet.addCell(new Label(cellIndex, rowIndex, title, wcfTitle));
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
                rowIndex = createBodyHeadByName(names, rowIndex, cellIndex,
                        sheet);
            } else {
                rowIndex = createBodyHead(bodyCtrl, rowIndex, cellIndex, sheet);
            }
        }
        String[] bodys = bodyCtrl.split("\\^", -1);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject rowData = jsonArray.getJSONObject(i);
            int cellindex = cellIndex;
            String[] name = names.split(",", -1);
            for (String aName : name) {
                WritableCellFormat wcfBody;
                if (!CommonUtility.isNullStr(bodyCtrl) && bodys[cellindex - cellIndex].contains("[")) {
                    String bodyStr = bodys[cellindex - cellIndex];
                    String bodyStyle = bodyStr.substring(bodyStr.indexOf("[") + 1, bodyStr.indexOf("]"));// 获取标题样式字符串
                    wcfBody = createStyle(bodyStyle, 1);// 标题样式

                    String widthConfig = getConfig(bodyStyle, "width");// 获取宽度
                    if (!CommonUtility.isNullStr(widthConfig)) {
                        sheet.setColumnView(cellindex, Integer.valueOf(widthConfig));
                    }
                    String heightConfig = getConfig(bodyStyle, "height");// 获取高度
                    if (!CommonUtility.isNullStr(heightConfig)) {
                        sheet.setRowView(rowIndex, Integer.valueOf(heightConfig));
                    }
                } else {
                    WritableFont wf = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                    wcfBody = new WritableCellFormat(wf);
                    wcfBody.setVerticalAlignment(VerticalAlignment.CENTRE);
                    wcfBody.setAlignment(Alignment.LEFT);
                    wcfBody.setBorder(Border.ALL, BorderLineStyle.THIN);
                }
                sheet.addCell(new Label(cellindex, rowIndex, rowData.getString(aName), wcfBody));
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
                    sheet.setColumnView(cellIndex, Integer.valueOf(widthConfig));
                }
                String heightConfig = getConfig(footStyle, "height");// 获取高度
                if (!CommonUtility.isNullStr(heightConfig)) {
                    sheet.setRowView(rowIndex + paddingrow, Integer.valueOf(heightConfig));
                }
                WritableCellFormat wcfTitle = createStyle(footStyle, 2);// 页脚样式
                String colspanConfig = getConfig(footStyle, "colspan");// 获取合并列数
                String rowspanConfig = getConfig(footStyle, "rowspan");// 获取合并行数
                if (!CommonUtility.isNullStr(colspanConfig) && !CommonUtility.isNullStr(rowspanConfig)) {// 合并单元格
                    sheet.mergeCells(cellIndex, rowIndex + paddingrow, cellIndex + Integer.valueOf(colspanConfig) - 1, rowIndex + paddingrow + Integer.valueOf(rowspanConfig) - 1);
                } else if (!CommonUtility.isNullStr(colspanConfig)) {
                    sheet.mergeCells(cellIndex, rowIndex + paddingrow, cellIndex + Integer.valueOf(colspanConfig) - 1, rowIndex + paddingrow);
                } else if (!CommonUtility.isNullStr(rowspanConfig)) {
                    sheet.mergeCells(cellIndex, rowIndex + paddingrow, cellIndex, rowIndex + paddingrow + Integer.valueOf(rowspanConfig) - 1);
                }
                sheet.addCell(new Label(cellIndex, rowIndex + paddingrow, foot, wcfTitle));
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
    protected WritableSheet generateSheetHeader(WritableSheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 2)) {
            JSONObject header = sheetConfig.getJSONObject("header");
            HeaderFooter head = new HeaderFooter();
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
                head = buildHeadFooter(head, headStr, 2);
            }
            sheet.getSettings().setHeader(head);
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
    protected WritableSheet generateSheetFooter(WritableSheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 3)) {
            JSONObject footer = sheetConfig.getJSONObject("footer");
            HeaderFooter foot = new HeaderFooter();
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
                foot = buildHeadFooter(foot, footStr, 2);
            }
            sheet.getSettings().setFooter(foot);
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
    protected WritableSheet generateSheetMerge(WritableSheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 4)) {
            JSONArray mergeCells = sheetConfig.getJSONArray("mergeCells");
            for (int j = 0; j < mergeCells.size(); j++) {
                JSONObject mergeCellsInfo = mergeCells.getJSONObject(j);
                CellPoint cellPoint = buildCellPoint(mergeCellsInfo);
                if (cellPoint.getFirstCol() > -1 && cellPoint.getFirstRow() > -1 && cellPoint.getLastCol() > -1 && cellPoint.getLastRow() > -1) {
                    sheet.mergeCells(cellPoint.getFirstCol(), cellPoint.getFirstRow(), cellPoint.getLastCol(), cellPoint.getLastRow());
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
     * @param sheet       sheet对象
     * @param sheetConfig sheet配置
     * @return sheet对象
     */
    protected WritableSheet generateSheetPrintSetting(WritableSheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 5)) {
            JSONObject jsonPrintSetting = sheetConfig.getJSONObject("printSetting");
            PrintSetting printSetting = buildPrintSetting(jsonPrintSetting);
            sheet.getSettings().setPaperSize(PaperSize.A4);
            if (printSetting.isHorizontal()) {
                sheet.getSettings().setOrientation(PageOrientation.LANDSCAPE);
            } else {
                sheet.getSettings().setOrientation(PageOrientation.PORTRAIT);
            }
            if (printSetting.getPageWidth() > -1) {
                sheet.getSettings().setFitWidth(printSetting.getPageWidth());
            }
            if (printSetting.getPageHeight() > -1) {
                sheet.getSettings().setFitHeight(printSetting.getPageHeight());
            }
            sheet.getSettings().setTopMargin(printSetting.getTopMargin());
            sheet.getSettings().setBottomMargin(printSetting.getBottomMargin());
            sheet.getSettings().setLeftMargin(printSetting.getLeftMargin());
            sheet.getSettings().setRightMargin(printSetting.getRightMargin());
            if (printSetting.getHorizontalCentre() != null) {
                sheet.getSettings().setHorizontalCentre(Boolean.valueOf(printSetting.getHorizontalCentre().toString()));
            }
            if (printSetting.getVerticallyCenter() != null) {
                sheet.getSettings().setVerticalCentre(Boolean.valueOf(printSetting.getVerticallyCenter().toString()));
            }
            CellPoint printArea = printSetting.getPrintArea();
            if (printArea != null) {
                if (printArea.getFirstCol() > -1 && printArea.getFirstRow() > -1 && printArea.getLastCol() > -1 && printArea.getLastRow() > -1) {
                    sheet.getSettings().setPrintArea(printArea.getFirstCol(), printArea.getFirstRow(), printArea.getLastCol(), printArea.getLastRow());
                } else {
                    throw new Exception("print config is not complete!");
                }
            }
            CellPoint printTitles = printSetting.getPrintTitles();
            if (printTitles != null) {
                if (printTitles.getFirstCol() > -1 && printTitles.getFirstRow() > -1 && printTitles.getLastCol() > -1 && printTitles.getLastRow() > -1) {
                    sheet.getSettings().setPrintTitles(printTitles.getFirstRow(), printTitles.getLastRow(), printTitles.getFirstCol(), printTitles.getLastCol());
                } else if (printTitles.getFirstCol() > -1 && printTitles.getLastCol() > -1) {
                    sheet.getSettings().setPrintTitlesCol(printTitles.getFirstCol(), printTitles.getLastCol());
                } else if (printTitles.getFirstRow() > -1 && printTitles.getLastRow() > -1) {
                    sheet.getSettings().setPrintTitlesRow(printTitles.getFirstRow(), printTitles.getLastRow());
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
    protected WritableSheet generateSheetFreeze(WritableSheet sheet, JSONObject sheetConfig) throws Exception {
        if (validationConfig(sheetConfig, 6)) {
            JSONObject freeze = sheetConfig.getJSONObject("freeze");
            if (freeze.has("row")) {
                int row = freeze.getInt("row");
                if (row >= 0) {
                    sheet.getSettings().setVerticalFreeze(row);
                } else {
                    throw new Exception("freeze row config is not complete!");
                }
            }
            if (freeze.has("col")) {
                int col = freeze.getInt("col");
                if (col >= 0) {
                    sheet.getSettings().setHorizontalFreeze(col);
                } else {
                    throw new Exception("freeze cell config is not complete!");
                }
            }
        }
        return sheet;
    }

    /**
     * 校验配置信息
     *
     * @param sheetConfig sheet配置
     * @param flag        0-sheet基本配置信息 1-数据配置信息 2-页眉配置信息 3-页脚配置信息 4-合并单元格配置信息 5-打印配置信息
     *                    6-冻结窗口配置信息
     * @return 校验是否成功
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
     * @param styleStr   配置信息
     * @param configName 配置名称
     * @return 配置值
     */
    private String getConfig(String styleStr, String configName) {
        String[] styles = styleStr.split(",", -1);
        for (String style : styles) {
            if (style.contains(configName) && style.substring(0, style.indexOf("=")).length() == configName.length()) {
                return style.substring(configName.length() + 1);
            }
        }
        return "";
    }

    /**
     * 创建样式
     *
     * @param style 样式字符串
     * @param flag  0-标题,1-数据,2-脚
     * @return 单元格对象
     */
    private WritableCellFormat createStyle(String style, int flag) throws Exception {
        int font = FONT_SIZE;
        String fontConfig = getConfig(style, "font");
        if (!CommonUtility.isNullStr(fontConfig)) {
            font = Integer.valueOf(fontConfig);
        }
        Colour color = Colour.BLACK;
        String colorConfig = getConfig(style, "color");
        if (!CommonUtility.isNullStr(colorConfig)) {
            switch (colorConfig) {
                case "black":
                    color = Colour.BLACK;
                    break;
                case "blue":
                    color = Colour.BLUE;
                    break;
                case "red":
                    color = Colour.RED;
                    break;
                case "green":
                    color = Colour.GREEN;
                    break;
            }
        }
        WritableFont wf;
        String boldConfig = getConfig(style, "bold");
        String underlineConfig = getConfig(style, "underline");
        UnderlineStyle underline = UnderlineStyle.NO_UNDERLINE;
        if (underlineConfig.equals("true")) {
            underline = UnderlineStyle.SINGLE;
        }
        if (flag == 0) {
            if (boldConfig.equals("false")) {
                wf = new WritableFont(WritableFont.ARIAL, font, WritableFont.NO_BOLD, false, underline, color);
            } else {
                wf = new WritableFont(WritableFont.ARIAL, font, WritableFont.BOLD, false, underline, color);
            }
        } else {
            if (boldConfig.equals("true")) {
                wf = new WritableFont(WritableFont.ARIAL, font, WritableFont.BOLD, false, underline, color);
            } else {
                wf = new WritableFont(WritableFont.ARIAL, font, WritableFont.NO_BOLD, false, underline, color);
            }
        }
        WritableCellFormat wcf = new WritableCellFormat(wf);
        wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
        Alignment align = Alignment.LEFT;
        if (flag == 0) {
            align = Alignment.CENTRE;
        }
        String alignConfig = getConfig(style, "align");
        if (!CommonUtility.isNullStr(alignConfig)) {
            switch (alignConfig) {
                case "center":
                    align = Alignment.CENTRE;
                    break;
                case "left":
                    align = Alignment.LEFT;
                    break;
                case "right":
                    align = Alignment.RIGHT;
                    break;
            }
        }
        wcf.setWrap(true);
        wcf.setAlignment(align);
        if (flag != 2) {
            String borderConfig = getConfig(style, "border");
            Border border = Border.ALL;
            if (!CommonUtility.isNullStr(borderConfig)) {
                switch (borderConfig) {
                    case "no":
                        border = Border.NONE;
                        break;
                    case "all":
                        border = Border.ALL;
                        break;
                    case "top":
                        border = Border.TOP;
                        break;
                    case "bottom":
                        border = Border.BOTTOM;
                        break;
                    case "right":
                        border = Border.RIGHT;
                        break;
                    case "left":
                        border = Border.LEFT;
                        break;
                }
            }
            wcf.setBorder(border, BorderLineStyle.THIN);
        } else {
            String borderConfig = getConfig(style, "border");
            Border border = Border.NONE;
            if (!CommonUtility.isNullStr(borderConfig)) {
                switch (borderConfig) {
                    case "no":
                        border = Border.NONE;
                        break;
                    case "all":
                        border = Border.ALL;
                        break;
                    case "top":
                        border = Border.TOP;
                        break;
                    case "bottom":
                        border = Border.BOTTOM;
                        break;
                    case "right":
                        border = Border.RIGHT;
                        break;
                    case "left":
                        border = Border.LEFT;
                        break;
                }
            }
            wcf.setBorder(border, BorderLineStyle.THIN);
        }
        return wcf;
    }

    /**
     * 通过配置信息生成表头
     *
     * @param bodyCtrl
     * @param rowIndex
     * @param cellIndex
     * @param sheet
     * @return
     */
    private int createBodyHead(String bodyCtrl, int rowIndex, int cellIndex, WritableSheet sheet) throws Exception {
        String[] bodys = bodyCtrl.split("\\^", -1);
        for (int i = 0; i < bodys.length; i++) {
            String headStr = bodys[i];
            String headtitle;
            WritableCellFormat wcfTitle;
            if (headStr.contains("[")) {
                headtitle = headStr.substring(0, headStr.indexOf("["));// 获取标题
                String headStyle = headStr.substring(headStr.indexOf("[") + 1, headStr.indexOf("]"));// 获取标题样式字符串
                wcfTitle = createStyle(headStyle, 0);// 标题样式
                String widthConfig = getConfig(headStyle, "width");// 获取宽度
                if (!CommonUtility.isNullStr(widthConfig)) {
                    sheet.setColumnView(cellIndex + i, Integer.valueOf(widthConfig));
                }
            } else {
                headtitle = headStr;// 获取标题
                WritableFont wf = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                wcfTitle = new WritableCellFormat(wf);
                wcfTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
                wcfTitle.setAlignment(Alignment.LEFT);
                wcfTitle.setBorder(Border.ALL, BorderLineStyle.THIN);
            }
            sheet.addCell(new Label(cellIndex + i, rowIndex, headtitle, wcfTitle));

        }
        return ++rowIndex;
    }

    /**
     * 通过数据列名生成表头
     *
     * @param names
     * @param rowIndex
     * @param cellIndex
     * @param sheet
     * @return
     */
    private int createBodyHeadByName(String names, int rowIndex, int cellIndex, WritableSheet sheet) throws Exception {
        WritableFont wf = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat wcf = new WritableCellFormat(wf);
        wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
        wcf.setAlignment(Alignment.CENTRE);
        wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
        int cellindex = cellIndex;
        String[] name = names.split(",", -1);
        for (String aName : name) {
            sheet.addCell(new Label(cellindex, rowIndex, aName, wcf));
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
    private HeaderFooter buildHeadFooter(HeaderFooter headerfooter, String info, int flag) {
        Contents contents;
        if (flag == 0) {
            contents = headerfooter.getLeft();
        } else if (flag == 2) {
            contents = headerfooter.getRight();
        } else {
            contents = headerfooter.getCentre();
        }
        int pageNumberBegin = info.indexOf("[pageNumber]");
        int pageTotalBegin = info.indexOf("[pageTotal]");
        if (pageNumberBegin > -1 && pageTotalBegin < 0) {
            contents.append(info.substring(0, pageNumberBegin));
            contents.appendPageNumber();
            contents.append(info.substring(pageNumberBegin + 12));
        } else if (pageNumberBegin > -1 && pageTotalBegin > -1) {
            if (pageNumberBegin < pageTotalBegin) {
                contents.append(info.substring(0, pageNumberBegin));
                contents.appendPageNumber();
                contents.append(info.substring(pageNumberBegin + 12, pageTotalBegin));
                contents.appendTotalPages();
                contents.append(info.substring(pageTotalBegin + 11));
            } else {
                contents.append(info.substring(0, pageTotalBegin));
                contents.appendTotalPages();
                contents.append(info.substring(pageTotalBegin + 11, pageNumberBegin));
                contents.appendPageNumber();
                contents.append(info.substring(pageNumberBegin + 12));
            }
        } else if (pageNumberBegin < 0 && pageTotalBegin > -1) {
            contents.append(info.substring(0, pageTotalBegin));
            contents.appendTotalPages();
            contents.append(info.substring(pageTotalBegin + 11));
        } else {
            contents.append(info);
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
