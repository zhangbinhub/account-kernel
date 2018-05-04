package OLink.bpm.core.dynaform.view.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.type.CalendarType;
import OLink.bpm.util.xml.XmlUtil;
import OLink.bpm.core.dynaform.view.ejb.type.MapType;
import OLink.bpm.core.dynaform.view.ejb.type.TreeType;
import OLink.bpm.core.dynaform.view.ejb.type.GanttType;

public class ColumnUtil {
	
	public static void main(String[] args) {

	}

	/**
	 * @SuppressWarnings XmlUtil.toOjbect方法返回类型为Object的不定类型
	 * @param columnXML
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Column> parseXML(String columnXML) {
		return (Set<Column>) XmlUtil.toOjbect(columnXML);
	}

	public String parseObject(TreeSet<Column> columnSet) {
		return XmlUtil.toXml(columnSet);
	}

	public String toListHtml(TreeSet<Column> columnSet, HttpServletRequest request) {
		StringBuffer htmlBuilder = new StringBuffer();

		int index = 0;
		htmlBuilder.append("<table class='table_noborder' id='column_table'>");
		htmlBuilder
				.append("<tr><td class='column-head2'><input type='checkbox' onclick='selectAllCols(this.checked)'></td><td class='column-head'>{*[Column]*}{*[Name]*}</td><td class='column-head'>上移</td><td class='column-head'>下移</td></tr>");
		String contextPath = request.getContextPath();
		for (Iterator<Column> iterator = columnSet.iterator(); iterator.hasNext(); index++) {
			Column column = iterator.next();

			if (index % 2 == 0) {
				htmlBuilder.append("<tr class='table-text'>");
			} else {
				htmlBuilder.append("<tr class='table-text2'>");
			}

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<input type=\"checkbox\" name=\"columnSelects\" value=" + index + ">");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<a href=\"javascript:columnProcess.doEdit(" + index + ")\" style=\"cursor:hand\">"
					+ column.getName() + "</a>");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<a href=\"javascript:columnProcess.doOrderChange(" + index
					+ ", 'p')\"><img border=0 SRC=\"" + contextPath + "/resource/image/leftStep.GIF\"></a>");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<a href=\"javascript:columnProcess.doOrderChange(" + index
					+ ", 'n')\"><img border=0 SRC=\"" + contextPath + "/resource/image/rightStep.GIF\"></a>");
			htmlBuilder.append("</td>");
			htmlBuilder.append("</tr>");
		}
		htmlBuilder.append("</table>");

		return htmlBuilder.toString();
	}

	public Map<String, String> getFontCss() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "#66FF33;");
		map.put("1", "#FF66FF;");
		map.put("1", "#FF0066;");
		return map;
	}

	/**
	 * 获取甘特图所有的字段与对应的名称的map
	 * 
	 * @return
	 */
	public Map<String, String> getGanttFields() {
		return GanttType.ALL_FIELDS;
	}

	/**
	 * 获取甘特图的默认字段与对应名称的map
	 * 
	 * @return map
	 */
	public Map<String, String> getDetGanttFields() {
		return GanttType.DEFAULT_FIELDS;
	}

	/**
	 * 获取地图默认列的字段映射图
	 * 
	 * @return map
	 */
	public Map<String, String> getDefMapFields() {
		return MapType.DEFAULT_FIELDS;
	}

	/**
	 * 获取地图所有列的字段映射图
	 * 
	 * @return map
	 */
	public Map<String, String> getMapFields() {
		return MapType.ALL_FIELDS;
	}

	/**
	 * 获取默认tree列的字段映射图
	 * 
	 * @return map
	 */
	public Map<String, String> getDefaultTreeFields() {
		return TreeType.DEFAULT_FIELDS;
	}

	/**
	 * 获取tree列的字段映射图
	 * 
	 * @return map
	 */
	public Map<String, String> getTreeFields() {
		return TreeType.ALL_FIELDS;
	}

	/**
	 * 获取calendar列的字段映射图
	 * 
	 * @return map
	 */
	public Map<String, String> getDefaultCldFields() {
		return CalendarType.DEFAULT_FIELDS;
	}

	/**
	 * 获取calendar列的所有字段映射图
	 * 
	 * @return
	 */
	public Map<String, String> getCldFields() {
		return CalendarType.ALL_FIELDS;
	}

}
