package pers.acp.tools.file.word;

import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocToHtml {

    private static String encoding = FileCommon.getDefaultCharset();

    private static String basePath = "";

    /**
     * 转html
     */
    protected static String convert2Html(String wordPath, String foldPath, String basePath) throws Exception {
        if (CommonUtility.isNullStr(wordPath)) {
            return "";
        }
        String wordname = wordPath.substring(wordPath.lastIndexOf(File.separator) + 1, wordPath.lastIndexOf("."));
        if (!CommonUtility.isNullStr(basePath)) {
            DocToHtml.basePath = basePath;
        }
        File file = new File(wordPath);
        if (file.exists() && file.isFile()) {
            return convert2Html(new FileInputStream(file), wordname, foldPath);
        } else {
            return "";
        }
    }

    /**
     * 转html
     */
    private static String convert2Html(InputStream is, final String wordName, final String foldPath) throws Exception {
        String foldpath = foldPath;

        HWPFDocument wordDocument = new HWPFDocument(is);
        Document w3cDoc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        WordToHtmlConverter converter = new WordToHtmlConverter(w3cDoc);

        final String prefix = CommonUtility.getDateTimeString(null, "yyyyMMddHHmmssSSS");
        final Map<Object, String> suffixMap = new HashMap<>();

        foldpath += File.separator + prefix;

        converter.setPicturesManager((content, pictureType, suggestedName, widthInches, heightInches) -> {
            suffixMap.put(new String(content).replace(" ", "").length(),
                    suggestedName);
            if (CommonUtility.isNullStr(DocToHtml.basePath)) {
                return "img/" + prefix + "_" + suggestedName;
            } else {
                return DocToHtml.basePath + "/" + prefix + "/img/" + prefix + "_" + suggestedName;
            }
        });
        converter.processDocument(wordDocument);

        File fold = new File(foldpath);
        if (!fold.exists() || !fold.isDirectory()) {
            fold.mkdirs();
        }

        File baseFold;
        if (CommonUtility.isNullStr(DocToHtml.basePath)) {
            baseFold = new File(foldpath + File.separator + "img");
        } else {
            String basePath = FileCommon.getAbsPath(DocToHtml.basePath);
            baseFold = new File(basePath + File.separator + prefix + File.separator + "img");
        }
        if (!baseFold.exists() || !baseFold.isDirectory()) {
            baseFold.mkdirs();
        }

        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (Picture pic : pics) {
                OutputStream out = new FileOutputStream(baseFold.getAbsolutePath() + File.separator + prefix + "_" + suffixMap.get(new String(pic.getContent()).replace(" ", "").length()));
                pic.writeImageContent(out);
                out.close();
            }
        }

        StringWriter writer = new StringWriter();
        String charset = SystemConfig.getInstance().getDefaultCharset();
        if (!CommonUtility.isNullStr(charset)) {
            encoding = charset;
        }
        Transformer serializer = TransformerFactory.newInstance()
                .newTransformer();
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        serializer.setOutputProperty(OutputKeys.ENCODING, encoding);
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(new DOMSource(converter.getDocument()),
                new StreamResult(writer));
        writer.close();
        File outFile = new File(foldpath + File.separator + wordName + prefix
                + ".html");
        OutputStream out = new FileOutputStream(outFile);
        OutputStreamWriter osw = new OutputStreamWriter(out, encoding);
        osw.write(writer.toString());
        osw.flush();
        out.close();
        osw.close();
        return outFile.getAbsolutePath();
    }
}
