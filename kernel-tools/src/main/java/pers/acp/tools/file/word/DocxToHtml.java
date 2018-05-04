package pers.acp.tools.file.word;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

public class DocxToHtml {

    private static String basePath = "";

    protected static String convert2Html(String wordPath, String foldPath, String basePath) throws Exception {
        if (CommonUtility.isNullStr(wordPath)) {
            return "";
        }
        String wordname = wordPath.substring(wordPath.lastIndexOf(File.separator) + 1, wordPath.lastIndexOf("."));
        if (!CommonUtility.isNullStr(basePath)) {
            DocxToHtml.basePath = basePath;
        }
        File file = new File(wordPath);
        if (file.exists() && file.isFile()) {
            return doConvertHTMLFile(file, wordname, foldPath);
        } else {
            return "";
        }
    }

    private static String doConvertHTMLFile(File wordFile, String wordName, String foldPath) throws Exception {
        String foldpath = foldPath;

        final String prefix = CommonUtility.getDateTimeString(null, "yyyyMMddHHmmssSSS");

        foldpath += File.separator + prefix;

        File fold = new File(foldpath);
        if (!fold.exists() || !fold.isDirectory()) {
            fold.mkdirs();
        }

        File baseFold;
        if (CommonUtility.isNullStr(DocxToHtml.basePath)) {
            baseFold = new File(foldpath + File.separator + "img");
        } else {
            String basePath = FileCommon.getAbsPath(DocxToHtml.basePath);
            baseFold = new File(basePath + File.separator + prefix + File.separator + "img");
        }
        if (!baseFold.exists() || !baseFold.isDirectory()) {
            baseFold.mkdirs();
        }

        File outFile = new File(foldpath + File.separator + wordName + prefix
                + ".html");
        InputStream in = new FileInputStream(wordFile);

        XWPFDocument document = new XWPFDocument(in);
        XHTMLOptions options = XHTMLOptions.create().indent(4);
        options.setExtractor(new FileImageExtractor(baseFold));
        options.URIResolver(suggestedName -> {
            String imguri = "";
            if (CommonUtility.isNullStr(DocxToHtml.basePath)) {
                imguri = "img/" + suggestedName;
            } else {
                imguri = DocxToHtml.basePath + "/" + prefix + "/img/" + suggestedName;
            }
            return imguri;
        });
        OutputStream out = new FileOutputStream(outFile);
        XHTMLConverter.getInstance().convert(document, out, options);

        return outFile.getAbsolutePath();
    }
}
