package OLink.bpm.core.macro.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import OLink.bpm.core.macro.runner.AbstractRunner;

/**
 * @author Happy
 *
 */
public class EditorUtil {
	
	private static final ArrayList<String> clazzes;
	
	private static final String COLOR_OBPM ="#3a5358";
	
	private static final String COLOR_BASELIB ="#3a5358";
	
	private static final String COLOR_TOOLS ="#fa080e";
	
	static{
		clazzes =  new ArrayList<String>();
		clazzes.add("Document");
		clazzes.add("DocumentProcessBean");
		clazzes.add("Item");
	}
	
	
	public static String getHighlightObpm() throws Exception {
		StringBuffer highlightKey = new StringBuffer();
		highlightKey.append("<c color=\""+COLOR_OBPM+"\">\n\t");
		for(Iterator<String> iter = clazzes.iterator();iter.hasNext();){
			Class<?> clazz = Class.forName((String)iter.next());
			Method methlist[] = clazz.getDeclaredMethods(); 
			for (int i = 0; i < methlist.length; i++) {
				highlightKey.append("<w>"+methlist[i].getName()+"</w>\n\t");
			} 
		}
		highlightKey.append("</c>");
		
		
		return highlightKey.toString();
	}
	
	public static String getHighlightBaseLib() throws Exception{
		StringBuffer highlightKey = new StringBuffer();
		highlightKey.append("<c color=\""+COLOR_BASELIB+"\">\n\t");
		URL url = AbstractRunner.class.getResource("baselib.js");
		if (url != null) {
			InputStream is = url.openStream();
			InputStreamReader reader = new InputStreamReader(is, "UTF-8");

			BufferedReader bfReader = new BufferedReader(reader);

			String tmp = null;

			do {
				tmp = bfReader.readLine();
				if (tmp != null) {
					if(tmp.indexOf("function")>-1){
						highlightKey.append("<w>"+(tmp.substring(tmp.indexOf("function")+8, tmp.indexOf("(")).trim())+"</w>\n\t");
					}
				}
			} while (tmp != null);

			bfReader.close();
			reader.close();
			is.close();
		}
		highlightKey.append("</c>");
		
		return highlightKey.toString();
	}
	
	
	public static String getHighlightTools() throws Exception{
		StringBuffer highlightKey = new StringBuffer();
		highlightKey.append("<c color=\""+COLOR_TOOLS+"\">\n\t");
		highlightKey.append("<w>$EMAIL</w>\n\t");
		highlightKey.append("<w>$MESSAGE</w>\n\t");
		highlightKey.append("<w>$FTP</w>\n\t");
		highlightKey.append("<w>$CURRDOC</w>\n\t");
		highlightKey.append("<w>$PRINTER</w>\n\t");
		highlightKey.append("<w>$WEB</w>\n\t");
		highlightKey.append("<w>$TOOLS</w>\n\t");
		highlightKey.append("<w>$BEANFACTORY</w>\n\t");
		highlightKey.append("<w>$PROCESSFACTORY</w>\n\t");
		highlightKey.append("<w>$WEB</w>\n\t");
		highlightKey.append("<w>$WEB</w>\n\t");
		highlightKey.append("<w>#include</w>\n\t");
		
		highlightKey.append("</c>");
		
		return highlightKey.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println(EditorUtil.getHighlightObpm()+"\n"+EditorUtil.getHighlightBaseLib()+"\n"+getHighlightTools());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
