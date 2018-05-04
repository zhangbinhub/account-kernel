package OLink.bpm.util.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import OLink.bpm.constans.Environment;

import eWAP.itext.text.DocumentException;
import eWAP.itext.text.pdf.PdfCopyFields;
import eWAP.itext.text.pdf.PdfReader;

public class AddPage {

	public AddPage() {
	}

	/**
	 * 多个PDF合并
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 * @throws DocumentException
	 */
	public void addPages(ArrayList<String> files, String targetFile)
			throws DocumentException, IOException {
		String importFile = getFileRealPath(targetFile).replaceAll("\\\\", "/");
		PdfCopyFields pdfCpy = new PdfCopyFields(new FileOutputStream(
				importFile));
		PdfReader pdfReader = null;

		for (Iterator<String> iterator = files.iterator(); iterator.hasNext();) {
			String temp = iterator.next().toString();
			String processPath = temp.substring(1);
			int lastStr = processPath.indexOf("_");
			if (lastStr != -1) {
				processPath = processPath.substring(0, lastStr);
			}
			String filePath = getFileRealPath(processPath).replace("\\", "/");
			pdfReader = new PdfReader(filePath);
			pdfCpy.addDocument(pdfReader);
		}
		pdfCpy.close();

	}

	/**
	 * 获取文件真实路径名称
	 * 
	 * @param webFileName
	 * @return
	 */
	public String getFileRealPath(String webFileName) {
		Environment env = Environment.getInstance();
		String realfilename = env.getRealPath(webFileName);

		return realfilename;
	}
}
