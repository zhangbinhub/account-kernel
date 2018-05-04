package OLink.bpm.util.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import eWAP.itext.text.DocListener;
import eWAP.itext.text.ElementTags;
import eWAP.itext.text.html.HtmlParser;
import eWAP.itext.text.html.HtmlPeer;
import eWAP.itext.text.html.HtmlTagMap;
import eWAP.itext.text.html.HtmlTags;
import eWAP.itext.text.html.SAXmyHtmlHandler;
import eWAP.itext.text.pdf.BaseFont;

public class PdfUtil {

	public static ObpmPdfDocument createDocument(String webFilename,
			String watermark) {
		return new ObpmPdfDocument(webFilename, watermark);
	}

	public static String getTestString() {
		StringBuffer content = new StringBuffer();
		content.append("<html><head>");
		// content
		content.append("</head><body>");

		content.append("</body></html>");
		return content.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// args = new String[] { "D:/PDFRenderStr.html", "D:/PDFRenderStr.pdf"
		// };
		File pdf = new File("test.pdf");
		if (!pdf.exists()) {
			pdf.createNewFile();
		}
		OutputStream os = new FileOutputStream(pdf);
		htmlToPDF(getTestString(), os);
	}

	public static void htmlToPDF(String html, OutputStream os) throws Exception {
		// html = getHtmlStr();
		// html = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" + html;
		html = html.replace("&nbsp;", "");
		html = html.replace("rowSpan", "rowspan");
		html = html.replace("colSpan", "colspan");
		html = html.replace("<br>", "<br/>");
		html = html.replace("&ldquo;", "&quot;");
		html = html.replace("&rdquo;", "&quot;");
		//html = html.replace("//", "/");
		try {
			// os = new FileOutputStream(pdf);
			ITextRenderer renderer = new ITextRenderer();
//			ITextFontResolver fontResolver = renderer.getFontResolver();
//			fontResolver.addFont(PdfUtil.class.getResource("simsun.ttc")
//					.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			/*
			 * PdfUtil.class.getResource("simsun.ttc")
					.getPath()
			 * standard approach ITextRenderer renderer = new ITextRenderer();
			 * 
			 * renderer.setDocument(url); renderer.layout();
			 * renderer.createPDF(os);
			 */

			ResourceLoaderUserAgent callback = new ResourceLoaderUserAgent(
					renderer.getOutputDevice());
			callback.setSharedContext(renderer.getSharedContext());
			renderer.getSharedContext().setUserAgentCallback(callback);
			org.w3c.dom.Document doc = XMLResource.load(new StringReader(html))
					.getDocument();
			// goGB(doc,);
			renderer.setDocument(doc, html);
			renderer.layout();
//			renderer.createPDF(os);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	private static class ResourceLoaderUserAgent extends ITextUserAgent {
		public ResourceLoaderUserAgent(ITextOutputDevice outputDevice) {
			super(outputDevice);
		}

		protected InputStream resolveAndOpenStream(String uri) {
			InputStream is = super.resolveAndOpenStream(uri);
			return is;
		}
	}

	public static String getHtmlStr() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<html>");

		stringBuffer.append("<body><h1>aa中文</h1>");
		stringBuffer.append("<table width='394' height='117' border='1'>");
		stringBuffer.append("<tbody>");
		stringBuffer.append("<tr>");
		stringBuffer.append("<td rowspan='2'>显示中文测试</td>");
		stringBuffer.append("<td>aa</td>");
		stringBuffer.append("<td></td>");
		stringBuffer.append("</tr>");
		stringBuffer.append("<tr>");
		stringBuffer.append("<td></td>");
		stringBuffer.append("<td></td>");
		stringBuffer.append("</tr>");
		stringBuffer.append("<tr>");
		stringBuffer.append("<td></td>");
		stringBuffer.append("<td>aaaaaaa</td>");
		stringBuffer.append("<td>aaaaaaa</td>");
		stringBuffer.append("</tr>");
		stringBuffer.append("</tbody>");
		stringBuffer.append("</table>");
		stringBuffer.append("</body>");

		stringBuffer.append("</html>");

		return stringBuffer.toString();
	}
}

/**
 * The inner class extend the eWAP.itext.text.html.HtmlParser, its purpose is
 * to support the Chinese.
 */
class ITextSurportHtmlParser extends HtmlParser {
	public ITextSurportHtmlParser() {
		super();
	}

	public void goGB(DocListener document, InputStream is) {
		try {
			// BaseFont bfChinese = BaseFont.createFont(
			// "c:\\windows\\fonts\\simsun.ttc,1", BaseFont.IDENTITY_H,
			// BaseFont.EMBEDDED);
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

			// 去除p
			HtmlTagMap myTags = new HtmlTagMap();
			HtmlPeer peer = new HtmlPeer(ElementTags.PARAGRAPH,
					HtmlTags.PARAGRAPH);
			myTags.remove(peer.getAlias());

			parser.parse(new InputSource(is), new SAXmyHtmlHandler(document,
					bfChinese));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}