package pers.acp.tools.file;

import pers.acp.tools.dbconnection.ConnectionFactory;
import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.file.excel.common.ExcelType;
import pers.acp.tools.file.excel.jxl.JXLExcelService;
import pers.acp.tools.file.excel.poi.POIExcelService;
import pers.acp.tools.file.pdf.PDFService;
import pers.acp.tools.file.pdf.PageNumberHandle;
import pers.acp.tools.file.pdf.PermissionType;
import pers.acp.tools.file.pdf.fonts.FontLoader;
import pers.acp.tools.file.templete.TemplateService;
import pers.acp.tools.file.word.WordService;
import pers.acp.tools.file.word.WordType;
import pers.acp.tools.utility.CommonUtility;
import pers.acp.tools.utility.JSONUtility;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public final class FileTools {

    private static Logger log = Logger.getLogger(FileTools.class);// 日志对象

    /**
     * 初始化文件工具类
     */
    public static void InitTools() {
        FileCommon.initSystemProperties();
        FontLoader.InitFonts();
    }

    private static String generateNowTimeString() {
        return CommonUtility.getDateTimeString(null, "yyyyMMddHHmmssSSS");
    }

    private static String formatFileName(String fileName) {
        if (!CommonUtility.isNullStr(fileName)) {
            fileName = fileName.replace("/", File.separator).replace("\\", File.separator);
        }
        return fileName;
    }

    private static String generateExcelResultFileName(ExcelType fileType, String fileName) {
        fileName = formatFileName(fileName);
        String nowstr = generateNowTimeString();
        String resultfile;
        if (CommonUtility.isNullStr(fileName)) {
            resultfile = FileCommon.buildTmpDir() + File.separator + nowstr + fileType.getName();
        } else if (!FileCommon.isAbsPath(fileName)) {
            resultfile = FileCommon.buildTmpDir() + File.separator + fileName + fileType.getName();
        } else {
            resultfile = FileCommon.getAbsPath(fileName) + fileType.getName();
        }
        return resultfile;
    }

    private static String generatePDFResultFileName(String resultFileName) {
        resultFileName = formatFileName(resultFileName);
        String nowstr = generateNowTimeString();
        String resultfile;
        if (CommonUtility.isNullStr(resultFileName)) {
            resultfile = FileCommon.buildTmpDir() + File.separator + nowstr + ".pdf";
        } else if (!FileCommon.isAbsPath(resultFileName)) {
            resultfile = FileCommon.buildTmpDir() + File.separator + resultFileName + ".pdf";
        } else {
            resultfile = FileCommon.getAbsPath(resultFileName) + ".pdf";
        }
        return resultfile;
    }

    /**
     * 获取配置信息
     *
     * @param key 键
     * @return 值
     */
    public static String getProperties(String key) {
        return FileCommon.getProperties(key);
    }

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getProperties(String key, String defaultValue) {
        return FileCommon.getProperties(key, defaultValue);
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    public static boolean isAbsPath(String path) {
        return FileCommon.isAbsPath(path);
    }

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    public static String getAbsPath(String srcPath) {
        return FileCommon.getAbsPath(srcPath);
    }

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    public static String replaceVar(String varFormula, Map<String, String> data) {
        return FileCommon.replaceVar(varFormula, data);
    }

    /**
     * 获取webroot绝对路径
     *
     * @return webroot绝对路径
     */
    public static String getWebRootAbsPath() {
        return FileCommon.getWebRootAbsPath();
    }

    /**
     * 获取文件中的内容
     *
     * @param filePath 文件绝对路径
     * @return 内容
     */
    public static String getFileContent(String filePath) {
        return FileCommon.getFileContent(filePath);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名称
     * @return 扩展名（小写）
     */
    public static String getFileExt(String fileName) {
        return FileCommon.getFileExt(fileName);
    }

    /**
     * 十六进制字符串转图片文件
     *
     * @param HexString      十六进字符串
     * @param FileName       文件名
     * @param ExtensionsName 扩展名
     * @param PathFlag       生成图片文件路径标志:0-相对于WebRoot；1-自定义
     * @param ResultPathFlag 返回文件路径标志:0-相对于WebRoot；1-绝对路径
     * @param ParentPath     生成图片所在目录
     * @param isDelete       是否异步删除临时图片
     * @return 临时图片路径
     */
    public static String HexToImage(String HexString, String FileName, String ExtensionsName, int PathFlag, int ResultPathFlag, String ParentPath, boolean isDelete) {
        return FileCommon.HexToImage(HexString, FileName, ExtensionsName, PathFlag, ResultPathFlag, ParentPath, isDelete);
    }

    /**
     * 使用freemarker模板，生成文件
     *
     * @param templatePath 模板路径（绝对路径，或相对于系统模板根路径）
     * @param data         数据
     * @param fileName     目标文件名，带扩展名
     * @return 相对于webroot路径
     */
    public static String exportToFileFromTemplete(String templatePath, Map<String, Object> data, String fileName) {
        try {
            fileName = formatFileName(fileName);
            String nowstr = generateNowTimeString();
            String webRootAdsPath = getWebRootAbsPath();
            String exName = getFileExt(templatePath);
            String resultfile;
            if (CommonUtility.isNullStr(fileName)) {
                resultfile = FileCommon.buildTmpDir() + File.separator + nowstr + exName;
            } else if (!FileCommon.isAbsPath(fileName)) {
                resultfile = FileCommon.buildTmpDir() + File.separator + fileName;
            } else {
                resultfile = FileCommon.getAbsPath(fileName);
            }
            String filepath;
            filepath = TemplateService.generateFile(templatePath, data, resultfile);
            return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * word转html
     *
     * @param filePath word文件
     * @param foldPath 生成HTML所在路径，相对于webroot，默认为系统临时文件夹 files/tmp/html
     * @param basePath word中图片附件保存的相对地址，默认为html所在路径的img下
     * @param isDelete 是否删除word文件
     * @return 相对于webroot路径
     */
    public static String wordToHTML(String filePath, String foldPath, String basePath, boolean isDelete) {
        if (CommonUtility.isNullStr(filePath)) {
            filePath = "/files/tmp/html";
        }
        String webRootAdsPath = getWebRootAbsPath();
        if (CommonUtility.isNullStr(foldPath)) {
            foldPath = FileCommon.buildTmpDir() + File.separator + "html";
        } else {
            foldPath = foldPath.replace("/", File.separator).replace("\\", File.separator);
            if (FileCommon.isAbsPath(foldPath)) {
                log.error("wordToHTML foldPath need relative path!");
                return "";
            }
            foldPath = webRootAdsPath + foldPath;
        }
        if (!CommonUtility.isNullStr(basePath)) {
            if (FileCommon.isAbsPath(basePath)) {
                log.error("wordToHTML basePath need relative path!");
                return "";
            }
        }
        String wordPath = FileCommon.getAbsPath(filePath);
        WordService ws = new WordService();
        String htmlfile = ws.wordToHTML(wordPath, foldPath, basePath);
        if (isDelete) {
            FileCommon.doDeleteFile(new File(wordPath), false);
        }
        return htmlfile.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 使用freemarker模板，生成word
     *
     * @param wordType     word类型
     * @param templatePath 模板路径（绝对路径，或相对于webroot/files/template）
     * @param data         数据
     * @param fileName     目标文件名
     * @return 相对于webroot路径
     */
    public static String exportToWordFromTemplete(WordType wordType, String templatePath, Map<String, Object> data, String fileName) {
        fileName = formatFileName(fileName);
        String nowstr = generateNowTimeString();
        String extName;
        if (wordType.equals(WordType.WORD_TYPE_DOCX)) {
            extName = ".xml";
        } else {
            extName = ".doc";
        }
        String resultfile;
        if (CommonUtility.isNullStr(fileName)) {
            resultfile = FileCommon.buildTmpDir() + File.separator + nowstr + extName;
        } else if (!FileCommon.isAbsPath(fileName)) {
            resultfile = FileCommon.buildTmpDir() + File.separator + fileName + extName;
        } else {
            resultfile = FileCommon.getAbsPath(fileName) + extName;
        }
        return exportToFileFromTemplete(templatePath, data, resultfile);
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径:相对于webroot
     * @param sheetNo  工作表序号
     * @param beginRow 读取的起始行
     * @param beginCol 读取的起始列
     * @param rowNo    读取的行数，0则表示读取全部
     * @param colNo    读取的列数，0则表示读取全部
     * @param isDelete 是否读取完数据后删除文件
     * @return 结果集
     */
    public static String readExcelDataByJXL(String filePath, int sheetNo, int beginRow, int beginCol, int rowNo, int colNo, boolean isDelete) {
        String excelPath = (getWebRootAbsPath() + filePath).replace("\\", File.separator).replace("/", File.separator);
        JXLExcelService excelService = new JXLExcelService();
        JSONArray result = excelService.readExcelData(excelPath, sheetNo, beginRow, beginCol, rowNo, colNo, isDelete);
        return result.toString();
    }

    /**
     * 导出Excel文件
     *
     * @param jsonStr      json数据
     * @param names        数据列名
     * @param titleCtrl    标题
     * @param bodyCtrl     数据
     * @param footCtrl     页脚
     * @param showBodyHead 是否显示表头
     * @param isHorizontal 是否为横向
     * @return 相对于webroot的文件位置
     */
    public static String exportToExcelByJsonJXL(String jsonStr, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, boolean isHorizontal) {
        String webRootAdsPath = getWebRootAbsPath();
        String filename = generateExcelResultFileName(ExcelType.EXCEL_TYPE_XLS, null);
        JXLExcelService es = new JXLExcelService();
        String filepath = es.createExcelFile(filename, JSONUtility.getJsonArrayFromStr(jsonStr), names, titleCtrl, bodyCtrl, footCtrl, showBodyHead, isHorizontal, 0, 0);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 导出Excel文件
     *
     * @param sqlStr       查询数据的sql语句
     * @param names        查询结果集中的列名
     * @param titleCtrl    标题
     * @param bodyCtrl     数据
     * @param footCtrl     页脚
     * @param showBodyHead 是否显示表头
     * @param isHorizontal 是否为横向
     * @return 相对于webroot的文件位置
     */
    public static String exportToExcelByJXL(String sqlStr, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, boolean isHorizontal) {
        String webRootAdsPath = getWebRootAbsPath();
        String filename = generateExcelResultFileName(ExcelType.EXCEL_TYPE_XLS, null);
        ConnectionFactory dbcon = new ConnectionFactory();
        JXLExcelService es = new JXLExcelService();
        String filepath = es.createExcelFile(filename, dbcon.doQueryForJSON(sqlStr), names, titleCtrl, bodyCtrl, footCtrl, showBodyHead, isHorizontal, 0, 0);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 生成Excel文件
     *
     * @param jsonStr  配置信息 [{ "sheetName":String, "printSetting":{
     *                 "isHorizontal":boolean, "pageWidth":int, "pageHeight":int,
     *                 "topMargin":double, "bottomMargin":double,
     *                 "leftMargin":double, "rightMargin":double,
     *                 "horizontalCentre":boolean, "verticallyCenter":boolean,
     *                 "printArea"
     *                 :{"firstCol":int,"firstRow":int,"lastCol":int,"lastRow":int},
     *                 "printTitles"
     *                 :{"firstRow":int,"lastRow":int,"firstCol":int,"lastCol":int}
     *                 },
     *                 "header":{"left":"***[pageNumber]***[pageTotal]***","center"
     *                 :"***[pageNumber]***[pageTotal]***"
     *                 ,"right":"***[pageNumber]***[pageTotal]***"},
     *                 "footer":{"left":"***[pageNumber]***[pageTotal]***","center":
     *                 "***[pageNumber]***[pageTotal]***"
     *                 ,"right":"***[pageNumber]***[pageTotal]***"}, "datas":{
     *                 "jsonDatas":[{"name":value,"name":value,...},{...}...],
     *                 "names":String, "titleCtrl":
     *                 "内容[row=,col=,colspan=,rowspan=,width=,height=,font=,align=,border=no|all|top|left|right|bottom,bold=true|false]^..."
     *                 , "bodyCtrl":String, "footCtrl":String,
     *                 "showBodyHead":boolean, "defaultRowIndex":int,
     *                 "defaultCellIndex":int },"mergeCells":[{
     *                 "firstCol":int,"firstRow":int,"lastCol":int,"lastRow"
     *                 :int},{...},...], "freeze":{"row":int,"col":int} }, {...},...]
     * @param fileName 目标文件名
     * @return 相对于webroot路径
     */
    public static String exportToExcelByJXL(String jsonStr, String fileName) {
        String webRootAdsPath = getWebRootAbsPath();
        String resultfile = generateExcelResultFileName(ExcelType.EXCEL_TYPE_XLS, fileName);
        JXLExcelService es = new JXLExcelService();
        String filepath = es.createExcelFile(resultfile, JSONUtility.getJsonArrayFromStr(jsonStr));
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径:相对于webroot
     * @param sheetNo  工作表序号
     * @param beginRow 读取的起始行
     * @param beginCol 读取的起始列
     * @param rowNo    读取的行数，0则表示读取全部
     * @param colNo    读取的列数，0则表示读取全部
     * @param isDelete 是否读取完数据后删除文件
     * @return 相对于webroot路径
     */
    public static String readExcelDataByPOI(String filePath, int sheetNo, int beginRow, int beginCol, int rowNo, int colNo, boolean isDelete) {
        String excelPath = (getWebRootAbsPath() + filePath).replace("\\", File.separator).replace("/", File.separator);
        POIExcelService excelService = new POIExcelService();
        JSONArray result = excelService.readExcelData(excelPath, sheetNo, beginRow, beginCol, rowNo, colNo, isDelete);
        return result.toString();
    }

    /**
     * 导出Excel文件
     *
     * @param fileType     文件类型
     * @param jsonStr      json数据
     * @param names        数据列名
     * @param titleCtrl    标题
     * @param bodyCtrl     数据
     * @param footCtrl     页脚
     * @param showBodyHead 是否显示表头
     * @param isHorizontal 是否为横向
     * @return 相对于webroot的文件位置
     */
    public static String exportToExcelByJsonPOI(ExcelType fileType, String jsonStr, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, boolean isHorizontal) {
        String webRootAdsPath = getWebRootAbsPath();
        String filename = generateExcelResultFileName(fileType, null);
        POIExcelService es = new POIExcelService();
        String filepath = es.createExcelFile(filename, JSONUtility.getJsonArrayFromStr(jsonStr), names, titleCtrl, bodyCtrl, footCtrl, showBodyHead, isHorizontal, 0, 0);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 导出Excel文件
     *
     * @param fileType     文件类型
     * @param sqlStr       查询数据的sql语句
     * @param names        查询结果集中的列名
     * @param titleCtrl    标题
     * @param bodyCtrl     数据
     * @param footCtrl     页脚
     * @param showBodyHead 是否显示表头
     * @param isHorizontal 是否为横向
     * @return 相对于webroot的文件位置
     */
    public static String exportToExcelByPOI(ExcelType fileType, String sqlStr, String names, String titleCtrl, String bodyCtrl, String footCtrl, boolean showBodyHead, boolean isHorizontal) {
        String webRootAdsPath = getWebRootAbsPath();
        String filename = generateExcelResultFileName(fileType, null);
        ConnectionFactory dbcon = new ConnectionFactory();
        POIExcelService es = new POIExcelService();
        String filepath = es.createExcelFile(filename, dbcon.doQueryForJSON(sqlStr), names, titleCtrl, bodyCtrl, footCtrl, showBodyHead, isHorizontal, 0, 0);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 生成Excel文件
     *
     * @param fileType 文件类型
     * @param jsonStr  配置信息 [{ "sheetName":String, "printSetting":{
     *                 "isHorizontal":boolean, "pageWidth":int, "pageHeight":int,
     *                 "topMargin":double, "bottomMargin":double,
     *                 "leftMargin":double, "rightMargin":double,
     *                 "horizontalCentre":boolean, "verticallyCenter":boolean,
     *                 "printArea"
     *                 :{"firstCol":int,"firstRow":int,"lastCol":int,"lastRow":int},
     *                 "printTitles"
     *                 :{"firstRow":int,"lastRow":int,"firstCol":int,"lastCol":int}
     *                 },
     *                 "header":{"left":"***[pageNumber]***[pageTotal]***","center"
     *                 :"***[pageNumber]***[pageTotal]***"
     *                 ,"right":"***[pageNumber]***[pageTotal]***"},
     *                 "footer":{"left":"***[pageNumber]***[pageTotal]***","center":
     *                 "***[pageNumber]***[pageTotal]***"
     *                 ,"right":"***[pageNumber]***[pageTotal]***"}, "datas":{
     *                 "jsonDatas":[{"name":value,"name":value,...},{...}...],
     *                 "names":String, "titleCtrl":
     *                 "内容[row=,col=,colspan=,rowspan=,width=,height=,font=,align=,border=no|all|top|left|right|bottom,bold=true|false]^..."
     *                 , "bodyCtrl":String, "footCtrl":String,
     *                 "showBodyHead":boolean, "defaultRowIndex":int,
     *                 "defaultCellIndex":int },"mergeCells":[{
     *                 "firstCol":int,"firstRow":int,"lastCol":int,"lastRow"
     *                 :int},{...},...], "freeze":{"row":int,"col":int} }, {...},...]
     * @param fileName 目标文件名
     * @return 相对于webroot路径
     */
    public static String exportToExcelByPOI(ExcelType fileType, String jsonStr, String fileName) {
        String webRootAdsPath = getWebRootAbsPath();
        String resultfile = generateExcelResultFileName(fileType, fileName);
        POIExcelService es = new POIExcelService();
        String filepath = es.createExcelFile(resultfile, JSONUtility.getJsonArrayFromStr(jsonStr));
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 通过模板创建Excel
     *
     * @param fileType     文件类型
     * @param templatePath 模板绝对路径
     * @param data         数据，数据键必须大写
     * @param fileName     生成的文件名
     * @return 相对于webroot路径
     */
    public static String exportToExcelByPOI(ExcelType fileType, String templatePath, Map<String, String> data, String fileName) {
        String webRootAdsPath = getWebRootAbsPath();
        String resultfile = generateExcelResultFileName(fileType, fileName);
        POIExcelService es = new POIExcelService();
        String filepath = es.createExcelFile(resultfile, templatePath, data);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 通过freemarker模板创建Excel
     *
     * @param fileType     文件类型
     * @param templatePath 模板路径（绝对路径，或相对于webroot/files/template）
     * @param data         数据
     * @param fileName     生成的文件名
     * @return
     */
    public static String exportToExcelFromTemplate(ExcelType fileType, String templatePath, Map<String, Object> data, String fileName) {
        return exportToFileFromTemplete(templatePath, data, generateExcelResultFileName(fileType, fileName));
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlstr html页面源码，必须完整
     * @return 相对于webroot的文件位置
     */
    public static String htmlToPDF(String htmlstr) {
        return htmlToPDF(htmlstr, null);
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlstr  html页面源码，必须完整
     * @param fileName 文件名
     * @return 相对于webroot的文件位置
     */
    public static String htmlToPDF(String htmlstr, String fileName) {
        String resultfile = generatePDFResultFileName(fileName);
        PDFService pdfService = new PDFService();
        return pdfService.htmlToPDF(htmlstr, resultfile, null).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlFilePath html文件路径
     * @param fileName     文件名
     * @param basePath     图片相对路径，为空则默认html文件所在路径
     * @return 相对于webroot路径
     */
    public static String htmlFileToPDF(String htmlFilePath, String fileName, String basePath, boolean isDelete) {
        String webRootAdsPath = getWebRootAbsPath();
        String resultfile = generatePDFResultFileName(fileName);
        htmlFilePath = FileCommon.getAbsPath(htmlFilePath);
        File htmlFile = new File(htmlFilePath);
        String prefixName = htmlFile.getParentFile().getName();
        String foldpath;
        if (!CommonUtility.isNullStr(basePath)) {
            basePath = basePath.replace("/", File.separator).replace("\\", File.separator);
            if (FileCommon.isAbsPath(basePath)) {
                log.error("htmlFileToPDF basePath need relative path!");
                return "";
            } else {
                basePath = webRootAdsPath + basePath + File.separator + prefixName;
            }
            foldpath = basePath;
            basePath = webRootAdsPath;
        } else {
            foldpath = htmlFile.getParentFile().getAbsolutePath();
            basePath = foldpath;
        }
        PDFService pdfService = new PDFService();
        String result = pdfService.htmlFileToPDF(htmlFilePath, resultfile, basePath);
        if (isDelete) {
            FileCommon.doDeleteDir(new File(foldpath));
            FileCommon.doDeleteDir(htmlFile.getParentFile());
        }
        return result.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 使用freemarker模板，HTML页面转为PDF
     *
     * @param templatePath 模板路径（绝对路径，或相对于webroot/files/template）
     * @param data         数据
     * @param fileName     目标文件名称
     * @return 相对于webroot路径
     */
    public static String htmlToPDFFromTemplete(String templatePath, Map<String, Object> data, String fileName) {
        String resultfile = generatePDFResultFileName(fileName);
        PDFService pdfService = new PDFService();
        return pdfService.htmlToPDFForTemplate(resultfile, templatePath, data).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 图片转PDF
     *
     * @param imageFileNames 图片数组
     * @param resultFileName 目标文件名
     * @param flag           0-自动压缩图片 1-按照宽度压缩图片（所有图片宽度一致）
     * @param isHorizontal   图片是否是横向
     * @param top            上边距（单位磅）
     * @param right          右边距（单位磅）
     * @param bottom         下边距（单位磅）
     * @param left           左边距（单位磅）
     * @return
     */
    public static String ImageToPDF(String[] imageFileNames, String resultFileName, int flag, boolean isHorizontal, float left, float right, float top, float bottom) {
        String resultfile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.ImageToPDF(imageFileNames, resultfile, flag, isHorizontal, left, right, top, bottom).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * PDF增加页码
     *
     * @param pdfFilePath    PDF文件路径，绝对路径或相对路径
     * @param resultFileName 目标文件名称
     * @param isDeleteFile   是否删除源文件
     * @param orientation    0-自动 1-纵向 2-横向
     * @return 相对于webroot路径
     */
    public static String PDFAddPageNumber(String pdfFilePath, String resultFileName, boolean isDeleteFile, int orientation) {
        String resultfile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFAddPageEvent(pdfFilePath, resultfile, new PageNumberHandle(), isDeleteFile, orientation).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 给PDF增加水印
     *
     * @param pdfFilePath    pdf源文件
     * @param waterMarkPath  水印图片
     * @param resultFileName 目标文件名
     * @param isDeleteFile   是否删除源文件
     * @return 相对于webroot路径
     */
    public static String PDFAddWaterMark(String pdfFilePath, String waterMarkPath, String resultFileName, boolean isDeleteFile) {
        String resultfile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFAddWaterMark(pdfFilePath, waterMarkPath, resultfile, isDeleteFile).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * PDF加密
     *
     * @param pdfFilePath    PDF源文件路径
     * @param resultFileName 目标文件名称
     * @param isDeleteFile   是否删除源文件
     * @return 相对于webroot路径
     */
    public static String PDFEncrypt(String pdfFilePath, String resultFileName, boolean isDeleteFile) {
        String resultfile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFEncrypt(pdfFilePath, "", resultfile, true, null, PDFService.PDFOWNERPASSWORD, PermissionType.ALLOW_COPY.getValue() | PermissionType.ALLOW_PRINTING.getValue(), isDeleteFile).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 合并多个PDF为一个
     *
     * @param fileNames      源文件，绝对路径或相对路径
     * @param resultFileName 目标文件名称
     * @param isDeleteFile   是否删除源文件
     * @return 相对于webroot路径
     */
    public static String PDFToMerge(String[] fileNames, String resultFileName, boolean isDeleteFile) {
        String resultfile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFToMerge(fileNames, resultfile, isDeleteFile).replace(getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 压缩文件
     *
     * @param fileNames      需要压缩的文件路径数组，可以是全路径也可以是相对于webroot的路径
     * @param isDeleteFile   压缩完后是否删除原文件
     * @param resultFileName 目标文件名
     * @return 相对于webroot的压缩文件位置
     */
    public static String filesToZIP(String[] fileNames, String resultFileName, boolean isDeleteFile) {
        resultFileName = formatFileName(resultFileName);
        String nowstr = generateNowTimeString();
        String webRootAdsPath = getWebRootAbsPath();
        String zipfile;
        if (CommonUtility.isNullStr(resultFileName)) {
            zipfile = FileCommon.buildTmpDir() + File.separator + nowstr + ".zip";
        } else if (!FileCommon.isAbsPath(resultFileName)) {
            zipfile = FileCommon.buildTmpDir() + File.separator + resultFileName + ".zip";
        } else {
            zipfile = FileCommon.getAbsPath(resultFileName) + ".zip";
        }
        String[] files = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            files[i] = FileCommon.getAbsPath(fileNames[i]);
        }
        return FileCommon.filesToZIP(files, zipfile, isDeleteFile).replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param isDeleteFile 解压完成是否删除压缩文件
     */
    public static void ZIPToFiles(String zipFileName, String parentFold, boolean isDeleteFile) {
        FileCommon.ZIPToFiles(FileCommon.getAbsPath(zipFileName), FileCommon.getAbsPath(parentFold), isDeleteFile);
    }

    /**
     * 删除文件
     *
     * @param file   待删除文件
     * @param isSync 是否异步删除
     */
    public static void doDelete(final File file, boolean isSync) {
        FileCommon.doDeleteFile(file, isSync);
    }

    /**
     * 删除文件
     *
     * @param file     待删除文件
     * @param isSync   是否异步删除
     * @param waitTime 异步删除等待时间
     */
    public static void doDelete(final File file, boolean isSync, long waitTime) {
        FileCommon.doDeleteFile(file, isSync, waitTime);
    }

}
