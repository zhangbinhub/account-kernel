package OLink.core.protection;

import eWAP.core.IDefIO;
import eWAP.core.InitResource;
import eWAP.core.ResourcePool;
import eWAP.core.Tools;
import eWAP.core.dbaccess.ConnectionFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefIO implements IDefIO
{
    private int startRow = 0;
    private int startCol = 0;
    private int dataRow = 0;
    private int dataCol = 1;
    private int endRow = 0;
    private int pageCurrRow = 0;
    private int dataNum = 0;
    private int minDataNum = 1;
    private boolean printSql = false;
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private HSSFCellStyle cellStyle = null;
    private ConnectionFactory dbclass;
    private Map<String, String> varMap;

    private boolean WriteSheetData(WritableSheet sheet, String CtrlStr)
    {
        String tStr = CtrlStr.replaceAll("\\]", "");
        String[] colCtrl = tStr.split("\\^");
        try
        {
            for (int i = 0; i < colCtrl.length; i++)
            {
                String cell = colCtrl[i].replaceAll("\\[", " ");
                cell = cell.replaceAll(" +", " ");
                String[] cellAttr = cell.split(" ", -1);
                int row = 0;
                int col = 0;
                int rowSpan = 0;
                int colSpan = 0;
                String align = null;
                String format = null;
                int width = 0;
                int font = 0;
                for (int j = 1; j < cellAttr.length; j++)
                {
                    if (cellAttr[j].startsWith("col="))
                    {
                        col = Integer.parseInt(cellAttr[j].substring(4));
                    }
                    else if (cellAttr[j].startsWith("row="))
                    {
                        row = Integer.parseInt(cellAttr[j].substring(4));
                    }
                    else if (cellAttr[j].startsWith("rowspan="))
                    {
                        rowSpan = Integer.parseInt(cellAttr[j].substring(8)) - 1;
                    }
                    else if (cellAttr[j].startsWith("colspan="))
                    {
                        colSpan = Integer.parseInt(cellAttr[j].substring(8)) - 1;
                    }
                    else if (cellAttr[j].startsWith("align="))
                    {
                        align = cellAttr[j].substring(6);
                    }
                    else if (cellAttr[j].startsWith("format="))
                    {
                        format = cellAttr[j].substring(7);
                    }
                    else if (cellAttr[j].startsWith("font="))
                    {
                        font = Integer.parseInt(cellAttr[j].substring(5));
                    }
                    else {
                        if (!cellAttr[j].startsWith("width="))
                            continue;
                        width = Integer.parseInt(cellAttr[j].substring(6));
                    }
                }

                if (row != 0) this.startRow = row;
                if (col != 0) this.startCol = col;
                if ((rowSpan != 0) || (colSpan != 0))
                {
                    if ((rowSpan != 0) && (colSpan != 0))
                    {
                        rowSpan = this.startRow + rowSpan;
                        colSpan = this.startCol + colSpan;
                    }
                    else if (rowSpan != 0)
                    {
                        rowSpan = this.startRow + rowSpan;
                        colSpan = this.startCol;
                    }
                    else
                    {
                        colSpan = this.startCol + colSpan;
                        rowSpan = this.startRow;
                    }
                    sheet.mergeCells(this.startCol - 1, this.startRow - 1, colSpan - 1, rowSpan - 1);
                }
                WritableFont cellFont;
                if (font == 0) cellFont = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD); else
                    cellFont = new WritableFont(WritableFont.TIMES, font, WritableFont.BOLD);
                WritableCellFormat cellformat;
                if (format == null) {
                    cellformat = new WritableCellFormat(cellFont);
                }
                else {
                    NumberFormat nf = new NumberFormat(format);
                    cellformat = new WritableCellFormat(cellFont, nf);
                }
                if ((align == null) || (align.equalsIgnoreCase("center")))
                    cellformat.setAlignment(Alignment.CENTRE);
                else if (align.equalsIgnoreCase("right"))
                    cellformat.setAlignment(Alignment.RIGHT);
                else
                    cellformat.setAlignment(Alignment.LEFT);
                cellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
                cellformat.setBorder(Border.ALL, BorderLineStyle.THIN);

                if (width > 0) sheet.setColumnView(this.startCol - 1, width);

                Label label = new Label(this.startCol - 1, this.startRow - 1, cellAttr[0], cellformat);
                sheet.addCell(label);
                if (rowSpan != 0) this.startRow = rowSpan;
                if (colSpan != 0) this.startCol = (colSpan + 1); else
                    this.startCol += 1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void CreateExcelBodyTitle(WritableSheet sheet, String CtrlStr, ArrayList<WritableCellFormat> colAttr, ArrayList<Boolean> colAttr1) throws Exception
    {
        String align = null;
        String format = null;
        int font = 0;
        int width = 0;
        try {
            String tStr = CtrlStr.replaceAll("\\]", "");
            this.startRow += 1;
            String[] colCtrl = tStr.split("\\^");
            for (int i = 0; i < colCtrl.length; i++)
            {
                align = null;
                format = null;
                font = 0;
                width = 0;
                String cell = colCtrl[i];
                cell = cell.replaceAll("\\[", " ");
                cell = cell.replaceAll(" +", " ");
                String[] cellAttr = cell.split(" ", -1);
                for (int j = 1; j < cellAttr.length; j++)
                {
                    if (cellAttr[j].startsWith("align="))
                        align = cellAttr[j].substring(6);
                    if (cellAttr[j].startsWith("format="))
                        format = cellAttr[j].substring(7);
                    if (cellAttr[j].startsWith("font="))
                        font = Integer.parseInt(cellAttr[j].substring(5));
                    if (cellAttr[j].startsWith("width="))
                        width = Integer.parseInt(cellAttr[j].substring(6));
                }
                this.startCol = 1;
                this.dataRow = this.startRow;
                WritableFont cellFont = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD);
                WritableCellFormat cellformat = new WritableCellFormat(cellFont);
                cellformat.setAlignment(Alignment.CENTRE);
                cellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
                cellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
                if (width > 0) sheet.setColumnView(this.dataCol - 1, width);

                Label label = new Label(this.dataCol - 1, this.dataRow - 1, cellAttr[0], cellformat);
                sheet.addCell(label);
                this.dataCol += 1;
                if (font == 0) cellFont = new WritableFont(WritableFont.TIMES, 10); else
                    cellFont = new WritableFont(WritableFont.TIMES, font);
                if (format == null)
                {
                    cellformat = new WritableCellFormat(cellFont);
                    colAttr1.add(Boolean.valueOf(true));
                }
                else
                {
                    NumberFormat nf = new NumberFormat(format);
                    cellformat = new WritableCellFormat(cellFont, nf);
                    colAttr1.add(Boolean.valueOf(false));
                }
                if ((align == null) || (align.equalsIgnoreCase("left")))
                    cellformat.setAlignment(Alignment.LEFT);
                else if (align.equalsIgnoreCase("right"))
                    cellformat.setAlignment(Alignment.RIGHT);
                else
                    cellformat.setAlignment(Alignment.CENTRE);
                cellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
                cellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
                colAttr.add(cellformat);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    @Override
    public ByteArrayOutputStream CreateExcelByCtrl(String HeadCtrl, String BodyCtrl, String TailCtrl, ArrayList value)
    {
        WritableWorkbook workbook = null;
        WritableSheet sheet = null;
        ArrayList colAttr = null;
        ArrayList colAttr1 = null;
        this.startRow = 1;
        this.startCol = 1;
        this.dataRow = 0;
        this.dataCol = 1;
        colAttr = new ArrayList();
        colAttr1 = new ArrayList();

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            workbook = Workbook.createWorkbook(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        sheet = workbook.createSheet("a", 0);
        sheet.getSettings().setDefaultColumnWidth(10);
        if ((HeadCtrl != null) && (!HeadCtrl.equals("")))
        {
            if (!WriteSheetData(sheet, HeadCtrl)) return null;
        }
        try
        {
            CreateExcelBodyTitle(sheet, BodyCtrl, colAttr, colAttr1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        for (Object[] field : (ArrayList<Object[]>)value)
        {
            for (int i = 1; i <= colAttr.size(); i++)
            {
                if (((Boolean)colAttr1.get(i - 1)).booleanValue())
                {
                    Label label = new Label(i - 1, this.dataRow, Tools.toString(field[(i - 1)], ""), (CellFormat)colAttr.get(i - 1));
                    try {
                        sheet.addCell(label);
                    } catch (RowsExceededException e) {
                        e.printStackTrace();
                        return null;
                    } catch (WriteException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                else
                {
                    Number label = new Number(i - 1, this.dataRow, Tools.String2Double(Tools.toString(field[(i - 1)], ""), 0.0D), (CellFormat)colAttr.get(i - 1));
                    try {
                        sheet.addCell(label);
                    } catch (RowsExceededException e) {
                        e.printStackTrace();
                        return null;
                    } catch (WriteException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            this.dataRow += 1;
        }

        if ((TailCtrl != null) && (!TailCtrl.equals("")))
        {
            if (!WriteSheetData(sheet, TailCtrl)) return null;
        }
        try
        {
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ByteArrayOutputStream CreateText(String HeadCtrl, String BodyCtrl, String TailCtrl, String fldField, String fldRow, boolean Title, ArrayList value)
    {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        String[] colCtrl = (String[])null;
        String tStr = null;
        String[] cell = (String[])null;
        if ((HeadCtrl != null) && (!HeadCtrl.equals("")))
        {
            colCtrl = HeadCtrl.split("\\^");
            for (int i = 0; i < colCtrl.length; i++)
            {
                tStr = colCtrl[i];
                cell = tStr.split("\\[");
                if (i < colCtrl.length - 1) tStr = cell[0] + fldField; else
                    tStr = cell[0] + "\r\n";
                try {
                    result.write(tStr.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        colCtrl = BodyCtrl.split("\\^");
        if (Title) for (int i = 0; i < colCtrl.length; i++)
        {
            tStr = colCtrl[i];
            cell = tStr.split("\\[");
            if (i < colCtrl.length - 1) tStr = cell[0] + fldField; else
                tStr = cell[0] + "\r\n";
            try {
                result.write(tStr.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        for (Object[] field : (ArrayList<Object[]>)value)
        {
            tStr = "";
            for (int i = 1; i <= colCtrl.length; i++)
            {
                String value1 = Tools.toString(field[(i - 1)], "");
                if (i < colCtrl.length)
                {
                    tStr = tStr + value1 + fldField;
                }
                else
                {
                    tStr = tStr + value1 + fldRow;
                }
            }
            try {
                result.write(tStr.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        if ((TailCtrl != null) && (!TailCtrl.equals("")))
        {
            colCtrl = TailCtrl.split("\\^");
            for (int i = 0; i < colCtrl.length; i++)
            {
                tStr = colCtrl[i];
                cell = tStr.split("\\[");
                if (i < colCtrl.length - 1) tStr = cell[0] + fldField; else
                    tStr = cell[0] + "\r\n";
                try {
                    result.write(tStr.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return result;
    }

    public HSSFWorkbook OpenWorkbook(String Path)
    {
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(Path));

            this.wb = new HSSFWorkbook(fs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        this.sheet = this.wb.getSheetAt(0);
        return this.wb;
    }

    public void setSheet(String sheetName)
    {
        this.sheet = this.wb.getSheet(sheetName);
    }

    public String[] getRowData(int rowNum, String[] col) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        if (row == null) return null;
        String[] value = new String[col.length];
        for (int i = 0; i < col.length; i++)
        {
            HSSFCell cell = row.getCell(getExcelCol(col[i]));
            value[i] = Tools.toString(getCellValue(cell), "");
        }
        return value;
    }

    public String GetSqlStr(String sqlStr)
    {
        if ((sqlStr == null) || (sqlStr.equals(""))) return sqlStr;
        StringBuffer value = new StringBuffer();
        String[] field = new String[2];
        int idx = sqlStr.indexOf("@sql@");
        if (idx > -1)
        {
            field[0] = sqlStr.substring(0, idx);
            field[1] = sqlStr.substring(idx + 5);
            idx = Integer.parseInt(field[0]);
            int sum = 0;
            for (int i = 0; i < field[1].length(); i++)
            {
                int ch = field[1].charAt(i);
                if ((ch >= 48) && (ch <= 52))
                {
                    ch += 5;
                    sum++;
                }
                else if ((ch >= 53) && (ch <= 57))
                {
                    ch -= 5;
                    sum++;
                }
                else if (((ch >= 65) && (ch <= 77)) || ((ch >= 97) && (ch <= 109)))
                {
                    ch += 13;
                    sum++;
                }
                else if (((ch >= 78) && (ch <= 90)) || ((ch >= 110) && (ch <= 122)))
                {
                    ch -= 13;
                    sum++;
                }
                value.append((char)ch);
            }
            if (idx != sum)
            {
                return null;
            }
            sqlStr = value.toString();
        }
        if (sqlStr.indexOf(":") < 0) return sqlStr;
        String[] tStr = sqlStr.split(",", -1);
        if (tStr.length < 2) return Tools.trimNull(tStr[0]);
        String xmlPath = null;
        String sqlId = null;
        String sql = null;
        for (int i = 0; i < tStr.length; i++)
        {
            idx = tStr[i].indexOf(":");
            switch (i)
            {
                case 0:
                    xmlPath = Tools.trimNull(tStr[i].substring(idx + 1));
                    break;
                case 1:
                    sqlId = Tools.trimNull(tStr[i].substring(idx + 1));
                    sql = ResourcePool.GetConfigInfo(ResourcePool.getRootpath() + "/" + xmlPath, "SQLINFO", sqlId)[0];
                    break;
                default:
                    sqlId = Tools.trimNull(tStr[i]);
                    if (idx < 0) continue;
                    String name = tStr[1].substring(0, idx);
                    String val = tStr[1].substring(idx + 1);
                    val = val.replaceAll("char\\(44\\)", ",");
                    sql = sql.replaceAll(":" + name, val);
            }
        }

        sql = sql.replaceAll("\r", " ");
        sql = sql.replaceAll("\n", " ");
        return sql;
    }

    @Override
    public ByteArrayOutputStream CreateExcelByTemplate(ResourcePool pool, String templatePath, String xmlPath, Map varMap)
    {
        this.varMap = varMap;
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(templatePath));

            this.wb = new HSSFWorkbook(fs);
        }
        catch (Exception e)
        {
            pool.setTransMsg("模板未找到");
            e.printStackTrace();
            return null;
        }
        HSSFFont font = this.wb.createFont();
        font.setFontHeightInPoints((short)20);
        font.setColor((short)10);
        font.setFontName("黑体");
        font.setBoldweight((short)700);
        font.setItalic(true);

        this.cellStyle = this.wb.createCellStyle();
        this.cellStyle.setFont(font);
        this.cellStyle.setAlignment((short)2);
        this.cellStyle.setWrapText(true);
        HSSFDataFormat format = this.wb.createDataFormat();

        this.cellStyle.setDataFormat(format.getFormat("@"));
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        NodeList start;
        try
        {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            File file = new File(xmlPath);
            Document doc = docBuilder.parse(file);

            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NamedNodeMap rAttr = root.getAttributes();
            Node t = rAttr.getNamedItem("minRecords");
            if (t != null) this.minDataNum = Tools.String2Int(t.getNodeValue(), 0);
            t = rAttr.getNamedItem("printSql");
            if ((t != null) && (t.getNodeValue().equalsIgnoreCase("true"))) this.printSql = true;

            start = root.getChildNodes();
        }
        catch (Exception e)
        {
            pool.setTransMsg("xml文件解析错误:" + e);
            e.printStackTrace();
            return null;
        }
        if (start != null)
        {
            for (int i = 0; i < start.getLength(); i++)
            {
                Node sheetNode = start.item(i);
                if (sheetNode.getNodeType() != 1)
                    continue;
                int head = 0;
                int pageRow = 0;
                int unitRow = 0;
                int pageUnit = 0;
                NamedNodeMap attr = sheetNode.getAttributes();
                String sheetName = attr.getNamedItem("name").getNodeValue();
                Node node1 = attr.getNamedItem("pageHeadRows");
                if (node1 != null) head = Tools.String2Int(node1.getNodeValue(), 0);
                node1 = attr.getNamedItem("pageRows");
                if (node1 != null) pageRow = Tools.String2Int(node1.getNodeValue(), 0);
                node1 = attr.getNamedItem("unitRows");
                if (node1 != null) unitRow = Tools.String2Int(node1.getNodeValue(), 0);
                node1 = attr.getNamedItem("pageUnits");
                if (node1 != null) pageUnit = Tools.String2Int(node1.getNodeValue(), 0);
                this.sheet = this.wb.getSheet(sheetName);
                if (this.sheet == null)
                {
                    pool.setTransMsg(sheetName + "不存在");
                    return null;
                }
                node1 = attr.getNamedItem("pageCode");
                if (node1 != null)
                {
                    String page = node1.getNodeValue();
                    page = page.replaceAll("\\$PAGE", HSSFFooter.page());
                    page = page.replaceAll("\\$TOTALPAGE", HSSFFooter.numPages());
                    HSSFFooter footer = this.sheet.getFooter();
                    footer.setRight(page);
                }
                if (head != 0)
                    this.wb.setRepeatingRowsAndColumns(this.wb.getSheetIndex(sheetName), -1, -1, 0, head);
                this.sheet.setForceFormulaRecalculation(true);
                this.startRow = 0;
                this.endRow = 0;
                this.pageCurrRow = 0;

                for (Node node = sheetNode.getFirstChild(); node != null; node = node.getNextSibling())
                {
                    if (node.getNodeType() == 1) {
                        if (node.getNodeName().equalsIgnoreCase("sql")) {
                            attr = node.getAttributes();
                            String ds = null;
                            int fixrows = 1;
                            String exit = null;
                            if (attr != null)
                            {
                                node1 = attr.getNamedItem("dsNo");
                                if (node1 != null) ds = node1.getNodeValue();

                                node1 = attr.getNamedItem("fixrows");
                                if (node1 != null)
                                {
                                    fixrows = Integer.parseInt(node1.getNodeValue());
                                }

                                node1 = attr.getNamedItem("exit");
                                if (node1 != null) exit = node1.getNodeValue();
                            }
                            this.dbclass = pool.getConnectionFactory(Tools.String2Int(ds, ResourcePool.getSysDsNo()));
                            if (this.dbclass == null)
                            {
                                pool.setTransMsg("数据源" + ds + "定义错误");
                                return null;
                            }
                            String sqlStr = node.getFirstChild().getNodeValue();

                            sqlStr = ParaProcess(sqlStr, varMap);
                            if (!sqlStr.trim().equals("")) {
                                if (this.printSql) System.out.println("Sql:[" + sqlStr + "]");
                                if (unitRow < 1)
                                {
                                    if (!operate(sqlStr, fixrows, pool, pageRow, head)) return null;

                                }
                                else if (!operate1(sqlStr, pool, unitRow, pageUnit)) return null;

                                if ((exit == null) || (exit.equals("")))
                                    continue;
                                String cls = exit.split(",")[0];
                                String method = exit.split(",")[1];
                                try
                                {
                                    Class ownerClass = Class.forName(cls);
                                    pool.setResultObj(this);
                                    Method clsMethod = ownerClass.getMethod(method, new Class[] { pool.getClass() });
                                    clsMethod.invoke(ownerClass.newInstance(), new Object[] { pool });
                                } catch (Exception e) {
                                    pool.setTransMsg("出口" + cls + "." + method + "不存在");
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        } else {
                            if (!node.getNodeName().equalsIgnoreCase("Formula")) {
                                continue;
                            }
                            attr = node.getAttributes();
                            int fixrows = 1;
                            if (attr != null)
                            {
                                node1 = attr.getNamedItem("fixrows");
                                if (node1 != null)
                                {
                                    fixrows = Integer.parseInt(node1.getNodeValue());
                                }
                            }
                            String funcStr = node.getFirstChild().getNodeValue();
                            funcStr = ParaProcess(funcStr, varMap);
                            if (!operateFormula(funcStr, fixrows, pool)) return null;
                        }
                    }
                }
            }
        }

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            this.wb.write(result);
        } catch (IOException e) {
            pool.setTransMsg("Excel流写入错误");
            e.printStackTrace();
        }
        return result;
    }

    private boolean operate(String sqlStr, int fixrows, ResourcePool pool, int pageRow, int head)
    {
        HSSFRow srcrow = null;

        ArrayList datas = this.dbclass.doQuery(sqlStr, null, 0, 0, true);
        if (datas == null)
        {
            pool.setTransMsg("SQL定义错误");
            return false;
        }

        int recordNum = this.dbclass.getRow();
        if (recordNum > 0) this.dataNum += recordNum;

        String[] colName = this.dbclass.getColName();
        int cols = colName.length;
        int[] row = new int[cols];
        int[] col = new int[cols];
        int[] sRow = new int[cols];
        Object[] sValue = new String[cols];
        boolean off = false;
        try
        {
            for (int i = 0; i < cols; i++)
            {
                sRow[i] = -1;
                if (colName[i].indexOf("#") >= 0)
                {
                    colName[i] = colName[i].replaceFirst("\\#", "");
                    sRow[i] = -2;
                    sValue[i] = "";
                }
                String[] str = colName[i].split("\\$", -1);
                if (str.length < 2)
                {
                    col[i] = -1;
                }
                else
                {
                    String tStr = str[0].substring(1);
                    row[i] = (Tools.String2Int(tStr, 1) - 1);
                    if ((colName[i].charAt(0) == 'O') || (colName[i].charAt(0) == 'o'))
                    {
                        if (i == 0)
                        {
                            this.startRow = (this.endRow + row[0]);
                            off = true;
                        }

                    }
                    else if (i == 0) this.startRow = row[0];

                    tStr = str[1];
                    col[i] = getExcelCol(tStr);
                }
            }

            int currRow = 0;
            int copyRow = off ? this.endRow + row[0] : row[0];
            ArrayList r = new ArrayList();
            if (fixrows == 0) copyRow--;
            if ((recordNum > fixrows) && (fixrows > -1))
            {
                for (int j = 0; j < this.sheet.getNumMergedRegions(); j++)
                {
                    Region region = this.sheet.getMergedRegionAt(j);
                    if ((region.getRowFrom() < copyRow) || (region.getRowTo() > copyRow))
                        continue;
                    r.add(region);
                }

                int lastRow = this.sheet.getLastRowNum();
                if (this.startRow + fixrows <= lastRow) this.sheet.shiftRows(this.startRow + fixrows, lastRow, recordNum - fixrows, true, false);
            }
            this.dataRow = -1;
            HSSFCell beforeCell = null;
            for (int i = 0; i < recordNum; i++)
            {
                if (fixrows > 0) this.pageCurrRow += 1;
                Object[] data = (Object[])datas.get(i);
                if ((i >= fixrows) && (fixrows >= 0))
                {
                    currRow = off ? this.endRow + row[0] + i : row[0] + i;
                    copyRows(this.sheet, this.sheet, copyRow, copyRow, currRow, false, r);
                }
                for (int j = 0; j < cols; j++)
                {
                    if (col[j] != -1) {
                        currRow = off ? this.endRow + row[j] + i : row[j] + i;
                        if (this.dataRow != currRow)
                        {
                            srcrow = this.sheet.getRow(currRow);
                            if (srcrow == null) srcrow = this.sheet.createRow(currRow);
                            this.dataRow = currRow;
                        }
                        HSSFCell srccell = srcrow.getCell(col[j]);
                        if (srccell == null)
                        {
                            srccell = srcrow.createCell(col[j]);
                            if (j != 0)
                            {
                                srccell.setCellStyle(beforeCell.getCellStyle());
                                srccell.setCellType(beforeCell.getCellType());
                            }
                            else {
                                srccell.setCellStyle(this.cellStyle);
                            }
                        }
                        beforeCell = srccell;
                        if ((data != null) && (data[j] != null) && (data[j].equals("$ROWNUM"))) data[j] = Integer.valueOf(i + 1);
                        setExcelColValue(srccell, srccell.getCellStyle(), data[j]);
                        if ((sRow[j] == -1) || ((sValue[j].equals(data[j])) && (i != recordNum - 1)))
                            continue;
                        int eRow = i != recordNum - 1 ? currRow - 1 : currRow;
                        if ((sRow[j] > 0) && (eRow != sRow[j]))
                            this.sheet.addMergedRegion(new Region(sRow[j], (short)col[j], eRow, (short)col[j]));
                        sValue[j] = data[j];
                        sRow[j] = currRow;
                    }
                }
                if (pageRow <= 0)
                    continue;
                if (this.pageCurrRow < pageRow - head) continue; this.sheet.setRowBreak(col[0]);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            pool.setTransMsg("模板错误：" + e);
            return false;
        }
        this.endRow = (recordNum > fixrows ? this.startRow + recordNum : this.startRow + fixrows);
        return true;
    }

    private boolean operate1(String sqlStr, ResourcePool pool, int unitRow, int pageUnit)
    {
        HSSFRow srcrow = null;

        int unit = 0;

        ArrayList datas = this.dbclass.doQuery(sqlStr, null, 0, 0, true);
        if (datas == null)
        {
            pool.setTransMsg("SQL定义错误");
            return false;
        }

        int recordNum = this.dbclass.getRow();
        if (recordNum > 0) this.dataNum += recordNum;

        String[] colName = this.dbclass.getColName();
        int cols = colName.length;
        int[] row = new int[cols];
        int[] col = new int[cols];
        this.startRow = row[0];
        try
        {
            for (int i = 0; i < cols; i++)
            {
                int loc = colName[i].lastIndexOf("$");
                if (loc <= 0)
                {
                    col[i] = -1;
                }
                else
                {
                    String tStr = colName[i].substring(1, loc);
                    row[i] = (Tools.String2Int(tStr, 1) - 1);

                    tStr = colName[i].substring(loc + 1);
                    col[i] = getExcelCol(tStr);
                }
            }
            this.endRow = 0;
            ArrayList r = new ArrayList();
            for (int j = 0; j < this.sheet.getNumMergedRegions(); j++)
            {
                Region region = this.sheet.getMergedRegionAt(j);
                if ((region.getRowFrom() < 0) || (region.getRowTo() > unitRow - 1))
                    continue;
                r.add(region);
            }

            for (int i = 0; i < recordNum; i++)
            {
                Object[] data = (Object[])datas.get(i);
                for (int j = 0; j < cols; j++)
                {
                    if (col[j] != -1) {
                        srcrow = this.sheet.getRow(row[j] + this.endRow);
                        HSSFCell srccell = srcrow.getCell(col[j]);
                        if ((data != null) && (data[j] != null) && (data[j].equals("$ROWNUM"))) data[j] = Integer.valueOf(i + 1);
                        setExcelColValue(srccell, srccell.getCellStyle(), data[j]);
                    }
                }
                this.endRow += unitRow;
                unit++;
                if (unit >= pageUnit)
                {
                    this.sheet.setRowBreak(this.endRow - 1);
                    unit = 0;
                }
                if (i >= recordNum - 1)
                    continue;
                copyRows(this.sheet, this.sheet, 0, unitRow - 1, this.endRow, true, r);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            pool.setTransMsg("模板错误：" + e);
            return false;
        }
        return true;
    }

    private boolean operateFormula(String formulaStr, int fixrows, ResourcePool pool)
    {
        formulaStr = formulaStr.replaceAll("\\$FIRSTROW", (this.startRow + 1)+"");
        formulaStr = formulaStr.replaceAll("\\$LASTROW", this.endRow+"");
        String[] func = formulaStr.split(",", -1);
        int row = 0;
        String[] tStr = (String[])null;
        for (int i = 0; i < func.length; i++)
        {
            tStr = func[i].split(":=", -1);
            tStr[0] = tStr[0].trim();
            tStr[1] = tStr[1].trim();
            row = Tools.String2Int(tStr[0].substring(1, tStr[0].lastIndexOf("$")), 1);
            if ((tStr[0].charAt(0) == 'O') || (tStr[0].charAt(0) == 'o')) row += this.endRow;
            if (i == 0) this.startRow = row;

            tStr[0] = tStr[0].substring(tStr[0].lastIndexOf("$") + 1);
            setCellFormula(row, tStr[0], tStr[1]);
        }

        this.endRow = (this.startRow + fixrows);
        return true;
    }

    private void setExcelColValue(HSSFCell srccell, HSSFCellStyle st, Object data)
    {
        String temp = Tools.toString(data);

        if (!temp.equals(""))
        {
            String f = st.getDataFormatString();
            int index = f.indexOf("0");
            if (index >= 0)
            {
                if (f.indexOf('.') > -1)
                    srccell.setCellValue(Tools.String2Double(temp, 0.0D));
                else {
                    srccell.setCellValue(Tools.String2Int(temp, 0));
                }
            }
            else
                srccell.setCellValue(temp);
        }
    }

    private String ParaProcess(String sql, Map<String, String> var)
    {
        sql = sql.replaceAll("\n", "");
        Set<Entry<String, String>> entries = var.entrySet();

        for (Entry entry : entries) {
            String key = (String)entry.getKey();
            String value = ((String)var.get(key)).toString();
            try
            {
                sql = sql.replaceAll(":" + key, value);
            }
            catch (Exception e)
            {
                int idx = sql.indexOf(":" + key);
                if (idx < 0) continue; sql = value;
            }
        }
        return sql;
    }

    private int getExcelCol(String col)
    {
        int count = Tools.String2Int(col, 0);
        if (count > 0) return count - 1;

        col = col.toUpperCase();

        count = -1;

        char[] cs = col.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            count = (int)(count + (cs[i] - '@') * Math.pow(26.0D, cs.length - 1 - i));
        }
        return count;
    }

    private Object getCellValue(HSSFCell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == 1)
            return cell.getStringCellValue();
        if (cell.getCellType() == 4)
            return Boolean.valueOf(cell.getBooleanCellValue());
        if (cell.getCellType() == 2)
            return cell.getCellFormula();
        if (cell.getCellType() == 0) {
            double val = cell.getNumericCellValue();
            String str = Double.toString(val);
            if (str.endsWith(".0"))
                str = str.substring(0, str.lastIndexOf("."));
            return str;
        }
        return "";
    }
    public Object getCellValue(int rowNum, String col) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        HSSFCell cell = row.getCell(getExcelCol(col));
        return getCellValue(cell);
    }
    public void setCellValue(int rowNum, String col, Object value) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        if (row == null) row = this.sheet.createRow(rowNum - 1);
        int colnum = getExcelCol(col);
        HSSFCell cell = row.getCell(colnum);
        if (cell == null) cell = row.createCell(colnum);
        setExcelColValue(cell, cell.getCellStyle(), value);
    }
    public void setCellFormula(int rowNum, String col, String func) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        if (row == null) row = this.sheet.createRow(rowNum - 1);
        int colnum = getExcelCol(col);
        HSSFCell cell = row.getCell(colnum);
        if (cell == null) cell = row.createCell(colnum);
        cell.setCellType(2);
        cell.setCellFormula(func);
    }

    public void setRowHeight(int rowNum, int height) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        if (row == null) row = this.sheet.createRow(rowNum - 1);
        row.setHeight((short)height);
    }

    public void setColumnWidth(String col, int width)
    {
        this.sheet.setColumnWidth(getExcelCol(col), (short)width);
    }

    public void setTextColour(int rowNum, String col, short iColor) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        if (row == null) row = this.sheet.createRow(rowNum - 1);
        int colnum = getExcelCol(col);
        HSSFCell cell = row.getCell(colnum);
        if (cell == null) cell = row.createCell(colnum);
        HSSFCellStyle t = this.wb.createCellStyle();
        t.cloneStyleFrom(cell.getCellStyle());
        HSSFFont font = this.wb.createFont();
        HSSFFont font1 = cell.getCellStyle().getFont(this.wb);
        font.setBoldweight(font1.getBoldweight());
        font.setFontHeight(font1.getFontHeight());
        font.setColor(iColor);
        t.setFont(font);
        cell.setCellStyle(t);
    }
    public void setCellColour(int rowNum, String col, short iColor) {
        HSSFRow row = this.sheet.getRow(rowNum - 1);
        if (row == null) row = this.sheet.createRow(rowNum - 1);
        int colnum = getExcelCol(col);
        HSSFCell cell = row.getCell(colnum);
        if (cell == null) cell = row.createCell(colnum);
        HSSFCellStyle t = this.wb.createCellStyle();
        t.cloneStyleFrom(cell.getCellStyle());
        t.setFillForegroundColor(iColor);
        t.setFillPattern((short)1);
        cell.setCellStyle(t);
    }

    public HSSFWorkbook getWorkbook()
    {
        return this.wb;
    }

    public HSSFSheet getSheet() {
        return this.sheet;
    }

    public int getStartRow() {
        return this.startRow;
    }

    public int getEndRow() {
        return this.endRow;
    }

    public Map<String, String> getVarMap() {
        return this.varMap;
    }

    public boolean hasRecord() {
        return this.dataNum >= this.minDataNum;
    }

    public int getRecordSum() {
        return this.dataNum;
    }
    public void setRangeStyle(CellStyle rangeStyle, int row1, String col1) throws Exception {
        this.sheet.getRow(row1).getCell(getExcelCol(col1)).setCellStyle(rangeStyle);
    }

    public void setRangeStyle(CellStyle rangestyle, int row1, String col1, int row2, String col2) throws Exception
    {
        for (int i = row1; i < row2; i++)
            for (int j = getExcelCol(col1); j < getExcelCol(col2); j++)
                this.sheet.getRow(i).getCell(j).setCellStyle(rangestyle);
    }

    public CellStyle getRangeStyle(int row1, String col1) throws Exception
    {
        CellStyle style = this.sheet.getRow(row1).getCell(getExcelCol(col1)).getCellStyle();
        return style;
    }
    public void hiddenSheets(int[] indexs) throws Exception {
        for (int i = 0; i < indexs.length; i++)
            this.wb.setSheetHidden(indexs[i], true);
    }

    public int getLastRow() {
        return this.sheet.getLastRowNum();
    }
    public int getLastCol() {
        return this.sheet.getLeftCol();
    }
    public boolean isInMergerCellRegion(int intCellRow, String Col) {
        boolean retVal = false;
        int intCellCol = getExcelCol(Col);
        int sheetMergerCount = this.sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress cra = this.sheet.getMergedRegion(i);

            int firstRow = cra.getFirstRow();

            int firstCol = cra.getFirstColumn();

            int lastRow = cra.getFirstColumn();

            int lastCol = cra.getLastColumn();

            if ((intCellRow < firstRow) || (intCellRow > lastRow) ||
                    (intCellCol < firstCol) || (intCellCol > lastCol)) continue;
            retVal = true;
            break;
        }

        return retVal;
    }
    public void setColHidden(String col) throws Exception {
        this.sheet.getRow(0).getCell(getExcelCol(col)).getCellStyle().setHidden(true);
    }
    public void setRowHidden(int row) throws Exception {
        HSSFRow row2 = this.sheet.getRow(row);
        row2.getRowStyle().setHidden(true);
    }

    private void copyRows(HSSFSheet sourceSheet, HSSFSheet targetSheet, int pStartRow, int pEndRow, int pPosition, boolean fillValue, ArrayList<Region> r)
    {
        HSSFRow sourceRow = null;
        HSSFRow targetRow = null;
        HSSFCell sourceCell = null;
        HSSFCell targetCell = null;

        if ((pStartRow < 0) || (pEndRow < 0)) return;

        for (int i = 0; i < r.size(); i++)
        {
            Region region = (Region)r.get(i);
            int RowFrom = region.getRowFrom();
            int RowTo = region.getRowTo();
            int targetRowFrom = RowFrom - pStartRow + pPosition;
            int targetRowTo = RowTo - pStartRow + pPosition;
            region.setRowFrom(targetRowFrom);
            region.setRowTo(targetRowTo);
            targetSheet.addMergedRegion(region);
            region.setRowFrom(RowFrom);
            region.setRowTo(RowTo);
        }

        for (int i = pStartRow; i <= pEndRow; i++)
        {
            sourceRow = sourceSheet.getRow(i);
            if (sourceRow == null) {
                continue;
            }
            targetRow = targetSheet.createRow(i - pStartRow + pPosition);
            targetRow.setHeight(sourceRow.getHeight());
            for (short j = sourceRow.getFirstCellNum(); j < sourceRow.getPhysicalNumberOfCells(); j = (short)(j + 1))
            {
                sourceCell = sourceRow.getCell(j);
                if (sourceCell == null) {
                    continue;
                }
                targetCell = targetRow.createCell(j);
                targetCell.setCellStyle(sourceCell.getCellStyle());
                int cType = sourceCell.getCellType();
                targetCell.setCellType(cType);
                switch (cType)
                {
                    case 4:
                        if (!fillValue) continue; targetCell.setCellValue(sourceCell.getBooleanCellValue());

                        break;
                    case 5:
                        if (!fillValue) continue; targetCell.setCellErrorValue(sourceCell.getErrorCellValue());

                        break;
                    case 2:
                        targetCell.setCellFormula(sourceCell.getCellFormula());

                        break;
                    case 0:
                        if (!fillValue) continue; targetCell.setCellValue(sourceCell.getNumericCellValue());

                        break;
                    case 1:
                        String v = sourceCell.getStringCellValue();
                        if (v == null) v = "";
                        if (!fillValue) continue; targetCell.setCellValue(sourceCell.getStringCellValue());
                    case 3:
                }
            }
        }
    }

    public String getFileContent(String content)
    {
        String sName = InitResource.getRootPath() + content;
        try
        {
            FileReader fr = new FileReader(sName);
            char[] cbuf = new char[1024];
            String tmpStr = "";
            while (true)
            {
                int len = fr.read(cbuf);
                if (len < 0) break;
                tmpStr = tmpStr + new String(cbuf, 0, len);
            }
            fr.close();
            return tmpStr;
        } catch (Exception e) {
        }
        return "";
    }
}