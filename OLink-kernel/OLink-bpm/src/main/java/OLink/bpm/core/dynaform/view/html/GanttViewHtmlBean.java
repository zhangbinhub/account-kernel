package OLink.bpm.core.dynaform.view.html;

import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.ViewType;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;

public class GanttViewHtmlBean extends ViewHtmlBean {
	private static final Logger LOG = Logger.getLogger(ViewHtmlBean.class);

	public String toHtml() {
		StringBuffer html = new StringBuffer();
		try {
			ViewType viewType = view.getViewTypeImpl();
			Map<String, Column> columnMapping = viewType.getColumnMapping();

			DataPackage<Document> datas = viewType.getViewDatas(params, webUser, getSearchDocument(params, webUser));
			html.append("var g = new JSGantt.GanttChart('g', document.getElementById('dataTable'), 'day', 'zh-CN');\n");
			html.append("g.setShowRes(1);\n");
			html.append("g.setShowDur(1);\n");
			html.append("g.setShowComp(1);\n");
			html.append("g.setCaptionType('Resource');\n");

			IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
			// 循环添加Task
			if (datas.rowCount > 0) {
				for (Iterator<Document> iterator = datas.datas.iterator(); iterator.hasNext();) {
					Document doc = iterator.next();

					// 必须字段
					Column column1 = columnMapping.get("name");
					Column column2 = columnMapping.get("start");
					Column column3 = columnMapping.get("end");
					Column column6 = columnMapping.get("complete");
					Column column7 = columnMapping.get("group");
					Column column8 = columnMapping.get("milestone");
					Column column9 = columnMapping.get("resource");
					Column column10 = columnMapping.get("parent");
					Column column11 = columnMapping.get("open");
					Column column12 = columnMapping.get("dependency");

					// 非必须字段
					Column column4 = columnMapping.get("color");
					Column column5 = columnMapping.get("link");
					Column column13 = columnMapping.get("caption");

					String id = doc.getId(); // 任务ID
					String name = column1 != null ? column1.getTextString(doc, runner, webUser) : ""; // 名称
					String start = column2 != null ? column2.getTextString(doc, runner, webUser) : ""; // 开始日期
					String end = column3 != null ? column3.getTextString(doc, runner, webUser) : ""; // 结束日期
					String color = column4 != null ? column4.getTextString(doc, runner, webUser) : ""; // 颜色
					String link = column5 != null ? column5.getTextString(doc, runner, webUser) : ""; // 链接
					String complete = column6 != null ? column6.getTextString(doc, runner, webUser) : "0"; // 完成度
					String group = column7 != null ? column7.getTextString(doc, runner, webUser) : "0"; // 分组
					String mileStone = column8 != null ? column8.getTextString(doc, runner, webUser) : "0"; // 里程碑
					String resource = column9 != null ? column9.getTextString(doc, runner, webUser) : ""; // 资源
					String parent = column10 != null ? column10.getTextString(doc, runner, webUser) : ""; // 上级任务ID
					String open = column11 != null ? column11.getTextString(doc, runner, webUser) : "0"; // 是否展开
					String dependency = column12 != null ? column12.getTextString(doc, runner, webUser) : ""; // 依赖任务
					String caption = column13 != null ? column13.getTextString(doc, runner, webUser) : ""; // 标题

					String isGroup = StringUtil.isBlank(group) ? "0" : group; // 是否分组
					String isMileStone = StringUtil.isBlank(mileStone) ? "0" : mileStone; // 是否里程碑

					// 'id', 'name', 'start', 'end', 'color', 'link',
					// 'complete','group', 'milestone', 'resource', 'parent',
					// 'open', 'dependency', 'caption', 'formid'

					// 设置隐藏的列
					if (column9 != null && isHiddenColumn(column9))// 判断资源是否隐藏
						html.append("g.setShowRes(0);\n");
					if (column2 != null && isHiddenColumn(column2) && isHiddenColumn(column3))// 开始日期和结束日期都为隐藏时,隐藏持续时间
						html.append("g.setShowDur(0);\n");
					if (column6 != null && isHiddenColumn(column6))// 判断完成度是否隐藏
						html.append("g.setShowComp(0);\n");

					html.append("g.AddTaskItem(new JSGantt.TaskItem(");
					html.append("'" + id + "',");
					html.append("'" + name + "',");
					html.append("'" + start + "',");
					html.append("'" + end + "',");
					html.append("'" + getShowColor(isMileStone, isGroup, color) + "',");
					html.append("'" + link + "',");
					html.append(isMileStone + ",");
					html.append("'" + resource + "',");
					html.append("'" + (StringUtil.isBlank(complete) ? "0" : complete) + "',");
					html.append(isGroup + ",");
					html.append("'" + (StringUtil.isBlank(parent) ? "0" : parent) + "',");
					html.append((StringUtil.isBlank(open) ? "0" : open) + ",");
					html.append("'" + dependency + "',");
					html.append("'" + caption + "',");
					html.append("'" + doc.getFormid() + "'");
					html.append("));\n");
				}
			} else {
				// 空任务
				html.append("g.AddTaskItem(new JSGantt.TaskItem(");
				html.append("'',");
				html.append("'',");
				html.append("'',");
				html.append("'',");
				html.append("'',");
				html.append("'',");
				html.append(0 + ",");
				html.append("'',");
				html.append(0 + ",");
				html.append(1 + ",");
				html.append(1 + ",");
				html.append(1 + ",");
				html.append(0 + ",");
				html.append("'',");
				html.append("''");
				html.append("));\n");
			}
		} catch (Exception e) {
			LOG.error("toHtml", e);
		}

		html.append("g.Draw();\n");
		html.append("g.DrawDependencies();\n");

		return html.toString();
	}

	public String toPreviewHtml() {
		StringBuffer html = new StringBuffer();
		html.append("var g = new JSGantt.GanttChart('g', document.getElementById('dataTable'), 'day', 'zh-CN');\n");
		html.append("g.setShowRes(1);\n");
		html.append("g.setShowDur(1);\n");
		html.append("g.setShowComp(1);\n");
		html.append("g.setCaptionType('Resource');\n");
		html.append("g.AddTaskItem(new JSGantt.TaskItem(");
		html.append("'',");
		html.append("'',");
		html.append("'',");
		html.append("'',");
		html.append("'',");
		html.append("'',");
		html.append(0 + ",");
		html.append("'',");
		html.append(0 + ",");
		html.append(1 + ",");
		html.append(1 + ",");
		html.append(1 + ",");
		html.append(0 + ",");
		html.append("'',");
		html.append("''");
		html.append("));\n");
		html.append("g.Draw();\n");
		html.append("g.DrawDependencies();\n");
		return html.toString();
	}

	private String getShowColor(String isMileStone, String isGroup, String color) {
		if (StringUtil.isBlank(color)) {
			if (isMileStone.equals("1") || isGroup.equals("1")) {
				return "000000"; // 里程碑或分组默认为黑色
			} else {
				return "46A3FF"; // 任务为默认为红色
			}
		}
		return color;
	}

	protected Document getSearchDocument(ParamsTable params, WebUser user) {
		if (view.getSearchForm() != null) {
			try {
				return view.getSearchForm().createDocument(params, user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new Document();
	}

}
