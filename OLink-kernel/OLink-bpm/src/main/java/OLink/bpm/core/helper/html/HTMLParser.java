package OLink.bpm.core.helper.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import OLink.bpm.util.StringUtil;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.htmlparser.visitors.TextExtractingVisitor;

public class HTMLParser {
	public static int SUMMARY_LENGTH = 200;
	
	HtmlPage visitor;
	TextExtractingVisitor textVisitor;

	StringBuffer title = new StringBuffer(SUMMARY_LENGTH);
	StringBuffer summary = new StringBuffer(SUMMARY_LENGTH * 2);

	public HTMLParser(java.io.InputStream stream) {
		this(stream, "UTF-8");
	}

	public HTMLParser(java.io.InputStream stream, String encoding) {
		StringBuffer sbStr = new StringBuffer();
		BufferedReader ins = null;
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(stream, encoding);
			ins = new BufferedReader(read);

			String dataLine = "";
			while (null != (dataLine = ins.readLine())) {
				sbStr.append(dataLine);
				sbStr.append("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				read.close();
				ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 解释HTML内容
		try {
			Parser parser = Parser.createParser(sbStr.toString(), encoding);
			Parser parser2 = Parser.createParser(sbStr.toString(), encoding);
			visitor = new HtmlPage(parser);
			textVisitor = new TextExtractingVisitor();

			parser.visitAllNodesWith(visitor);
			parser2.visitAllNodesWith(textVisitor);

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
//		System.out.println("Title: " + StringUtil.replaceBlank(visitor.getTitle()));
		return title.append(StringUtil.replaceBlank(visitor.getTitle())).toString();
	}

	public String getSummary() {
		summary.append(textVisitor.getExtractedText());
		summary.setLength(SUMMARY_LENGTH * 2);
		//System.out.println(summary.toString().trim());
		return summary.toString().trim();
	}

	public String getContent() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(textVisitor.getExtractedText());
//		System.out.println(textVisitor.getExtractedText());
//		System.out.println("***********************************************************");
//		NodeList nodeList = visitor.getBody();
//		for (int i = 0; i < nodeList.size(); i++) {
//			Node node = nodeList.elementAt(i);
//			System.out.println(node.getClass());
//			buffer.append(node.toPlainTextString());
//		}

		return buffer.toString();
	}

	public Reader getReader() {
		return new StringReader(getContent());
	}
}
