package pers.acp.tools.file.word;

import org.apache.log4j.Logger;

import pers.acp.tools.file.common.FileCommon;

public class WordService {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    /**
     * word 转 html
     *
     * @param filePath word文件全路径
     * @param foldPath 生成HTML所在路径，相对于webroot，默认为系统临时文件夹 files/tmp/html
     * @param basePath word中图片附件保存的相对地址，默认为html所在路径的img下
     */
    public String wordToHTML(String filePath, String foldPath, String basePath) {
        try {
            String ext = FileCommon.getFileExt(filePath);
            switch (ext) {
                case "doc": {
                    return DocToHtml.convert2Html(filePath, foldPath, basePath);
                }
                case "docx": {
                    return DocxToHtml.convert2Html(filePath, foldPath, basePath);
                }
                default:
                    log.error("fileType [" + ext + "] is not word file!");
                    return "";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
