package OLink.bpm.util.web;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DWRHtmlUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public static String createOptions(Map<String, String> map, String selectFieldName, String def) {
		StringBuffer fun = new StringBuffer();
		fun.append("new Function(\"");
		fun.append(createOptionScript(map, selectFieldName, def));
		fun.append("\")");

		return fun.toString();
	}

	public static String createOptionScript(Map<String, String> map, String selectFieldName, String def) {
		StringBuffer fun = new StringBuffer();
		fun.append("var menuTemp=document.getElementsByName('" + selectFieldName + "')[0];");
		fun.append("for (var m = menuTemp.options.length - 1; m >= 0; m--) {menuTemp.options[m] = null;}");

		int i = 0;

		for (Iterator<Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext(); i++) {
			//Object key = iter.next();
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();

			fun.append("menuTemp.options[" + i + "] = new Option('" + value + "', '" + key + "');");

			if (key.equals(def)) {
				fun.append("menuTemp.options[" + i + "].selected = true;");
			}
		}

		return fun.toString();
	}

	/*
	 * public static String createCheckbox(Map map, String divid, String[] def)
	 * { StringBuffer fun = new StringBuffer(); fun.append("new Function(\"");
	 * fun.append("var div = document.all('" + divid + "');");
	 * fun.append("for(var i=div.childNodes.length-1;i>=0;i--){var
	 * s=div.childNodes[i];div.removeChild(s);}");
	 * 
	 * int i = 0; for (Iterator iter = map.keySet().iterator(); iter.hasNext();
	 * i++) { Object key = iter.next(); String name="c"+i; String labname="l"+i;
	 * fun.append(" var "+labname+"= document.createElement('label'); ");
	 * fun.append(labname+".innerHTML ='"+map.get(key)+"';"); fun.append(" var
	 * "+name+"=document.createElement('input');");
	 * fun.append(name+".type='checkbox' ;"+name+".name='roleids';
	 * "+name+".value='" + key + "';");
	 * fun.append("div.appendChild("+name+");");
	 * fun.append("div.appendChild("+labname+");"); if (def != null) { for (int
	 * j = 0; j < def.length; j++) { if (key.equals(def[j])) {
	 * fun.append(name+".checked=true; "); break; } } } }
	 * 
	 * fun.append("\")"); return fun.toString(); }
	 */
	public static String createHtmlStr(Map<String, String> map, String divid, String[] def) {
		StringBuffer fun = new StringBuffer();
		int imgid = 0;
		fun.append("{");
		fun.append("var div = document.getElementById('" + divid + "');");

		fun.append("var htmtext = \"");
		fun.append("<table cellpadding='0' cellspacing='0' class='checkbox-text'>");

		for (Iterator<Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();) {
			fun.append("<tr>");
			//String key = iter.next();
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			String checked = "";
			if (def != null) {
				for (int k = 0; k < def.length; k++) {
					if (def[k] != null && def[k].equals(key)) {
						checked = " checked ";
						break;
					}
				}
			}
			
			fun.append("<td><input name='_nextids' type='checkbox' value='");
			fun.append(key).append("'").append(checked).append(" />");
			fun.append(value).append("</td>");
			imgid++;

			fun.append("</tr>");
		}

		fun.append("</table>");
		fun.append("\";");
		fun.append("div.innerHTML = htmtext;");
		fun.append("}");
		return fun.toString();
	}

	public static String createCheckbox(Map<String, String> map, String divid, String[] def) {
		StringBuffer fun = new StringBuffer();
		fun.append("{");
		fun.append("var div = document.all('" + divid + "');");

		fun.append("var htmtext = \"");
		fun.append("<table cellpadding='0' cellspacing='0' class='checkbox-text'>");
		for (Iterator<Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();) {
			fun.append("<tr>");
			for (int j = 0; j < 3 && iter.hasNext(); j++) {
				//String key = iter.next();
				Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				String checked = "";
				if (def != null) {
					for (int k = 0; k < def.length; k++) {
						if (def[k] != null && def[k].equals(key)) {
							checked = " checked ";
							break;
						}
					}
				}
				fun.append("<td><input name='roleids' type='checkbox' value='").append(key).append("'").append(checked).append(" /></td>");
				fun.append("<td>").append(value).append("</td>");
			}
			fun.append("</tr>");
		}
		fun.append("</table>");
		fun.append("\";");
		fun.append("div.innerHTML = htmtext;");
		fun.append("}");
		return fun.toString();
	}

	/**
	 * 根据form filed生成checkbox
	 * 
	 * @param map
	 * @param divid
	 * @param def
	 * @return
	 */
	public static String createFiledCheckbox(Map<String, String> map, String divid, String[] def) {
		StringBuffer fun = new StringBuffer();
		fun.append("{");
		fun.append("var div = document.all('" + divid + "');");

		fun.append("var htmtext = \"");
		fun.append("<table>");
		for (Iterator<Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();) {
			fun.append("<tr >");
			for (int j = 0; j < 3 && iter.hasNext(); j++) {
				//String key = iter.next();
				Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				String checked = "";
				if (def != null) {
					for (int k = 0; k < def.length; k++) {
						if (def[k] != null && def[k].equals(key)) {
							checked = " checked ";
							break;
						}
					}
				}
				fun.append("<td><input name='colids' type='checkbox' value='").append(key).append("'").append(checked).append(" /></td>");
				fun.append("<td class='commFont'>").append(value).append("</td>");
			}
			fun.append("</tr>");
		}
		fun.append("</table>");
		fun.append("\";");
		fun.append("div.innerHTML = htmtext;");
		fun.append("}");
		return fun.toString();
	}

}
