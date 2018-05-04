package OLink.bpm.core.homepage.action;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import OLink.bpm.core.dynaform.form.action.FormHelper;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.util.StringUtil;


public class ReminderHelper {
	public static final TreeMap<String, String> subjectList = new TreeMap<String, String>();

	static {
		subjectList.put("1", "{*[Default]*}");
		subjectList.put("2", "{*[Style_one]*}");
		subjectList.put("3", "{*[Style_two]*}");
		subjectList.put("4", "{*[Style_three]*}");
		subjectList.put("5", "{*[Style_four]*}");
	}

	public static TreeMap<String, String> getStyleList() {
		return subjectList;
	}
	
	public String doRemindListToHtml(Collection<Reminder> collection){
		StringBuffer html =new StringBuffer();
		try {
			for(Iterator<Reminder> iter=collection.iterator();iter.hasNext();){
				Reminder reminder= iter.next();
				html.append("<div class='pendingitem'><div class='pendingitemtitle'>");
				html.append("<table class='table_noborder' height='18px'><tr>");
				html.append("<td width='70px'>{*[Reminder]*}：</td>");
				html.append("<td align='left'>"+reminder.getTitle()+"</td>");
				html.append("<td width='100px' align='right'><input name='remindItemCheckbox' type='checkbox' id='"+reminder.getId()+"'></td>");
				html.append("</tr></table></div>");
				html.append("<div class='pendingitemtitlecontent'>"+reminder.getTitle()+"</div></div>");
			}
		} catch (Exception e) {
			return "";
		}
		return html.toString();
	}
	
	public String getDisplayFileds(Reminder reminder) {
		StringBuffer buffer = new StringBuffer();
		if (reminder != null 
				&& !StringUtil.isBlank(reminder.getSummaryFieldNames())) {
			String[] fields = reminder.getSummaryFieldNames().split(";");
			for (int i = 0; i < fields.length; i++) {
				String display = FormHelper.getDisplayFieldNameByName(fields[i]);
				buffer.append(display).append(";");
			}
		}
		if (buffer.length() > 1) {
			return buffer.substring(0, buffer.length() -1);
		}
		return buffer.toString();
	}

}
