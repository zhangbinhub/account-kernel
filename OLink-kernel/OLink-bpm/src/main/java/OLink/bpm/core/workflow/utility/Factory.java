//Source file: D:\\BILLFLOW\\src\\billflow\\Factory.java

//Source file: E:\\billflow\\src\\billflow\\Factory.java

package OLink.bpm.core.workflow.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import OLink.bpm.core.workflow.element.FlowDiagram;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class Factory {
	// private XMLOperate _xmlopt;

	/**
	 * @roseuid 3E0428D90017
	 */
	public Factory() {

	}

	public static FlowDiagram trnsXML2Dgrm(File xml) {
		StringBuffer sb = new StringBuffer();
		BufferedReader fr = null;
		try {
			FileReader fis = new FileReader(xml);
			fr = new BufferedReader(fis);
			String tmp = null;
			do {
				tmp = fr.readLine();
				sb.append(tmp);
			} while (tmp != null);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		String xmlStr = sb.toString();
		return trnsXML2Dgrm(xmlStr);

	}

	/**
	 * @param xml
	 * @return OLink.bpm.core.workflow.FlowDiagram
	 * @roseuid 3E0404D902E6
	 */
	public static FlowDiagram trnsXML2Dgrm(String xml) {
		XMLOperate xmlopt = new XMLOperate();
		// InputStream in = new
		// StringBufferInputStream(CommonUtil.gbTo8859(xml));
		InputStream in = new java.io.ByteArrayInputStream(xml.getBytes());
		// InputStream in = new StringBufferInputStream(xml);
		/*
		 * FlowDiagram fd = new FlowDiagram(); Node n = new Actor(fd); n.name =
		 * "这是一个测试"; n.x = 50; n.y = 50;
		 */
		FlowDiagram fd = null;
		// Graphics g=fd.getGraphics();
		Parser parser;
		try {
			parser = makeParser("uk.co.wilson.xml.MinML");
			parser.setDocumentHandler(xmlopt);
			parser.parse(new InputSource(in));
			fd = xmlopt.getResult();
			// Graphics g=fd.getGraphics();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sxe) {
			sxe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fd;
	}

	/**
	 * @param elms
	 * @return java.lang.String
	 * @roseuid 3E0405E502CD
	 */
	public static String trnsDgrm2XML(Vector<?> elms) {
		return null;
	}

	/**
	 * Create a XML Parser. It can use the simplest Parser Driver like MinML.
	 * 
	 * @param className
	 * @return org.xml.sax.Parser
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassCastException
	 * @roseuid 3E0A6E1800BB
	 */
	private static Parser makeParser(String className)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, ClassCastException {
		return (Parser) (Class.forName(className).newInstance());
	}

	public static void main(String[] args) {
		/*
		 * FlowDiagram fd = trnsXML2Dgrm(new File("f:\\temp\\abdc2.xml"));
		 * String f = "f:\\temp\\abdc2.xml"; try { java.io.FileOutputStream os =
		 * new java.io.FileOutputStream(f); java.io.PrintStream osw = new
		 * java.io.PrintStream(os); osw.println(fd.toXML()); } catch (Exception
		 * ex) { ex.printStackTrace(); }
		 */
	}
}
