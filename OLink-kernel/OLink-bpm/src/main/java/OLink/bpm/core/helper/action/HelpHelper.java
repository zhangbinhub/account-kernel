package OLink.bpm.core.helper.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.helper.toc.IUAElement;
import OLink.bpm.core.helper.toc.impl.Toc;
import OLink.bpm.core.helper.toc.impl.Topic;
import OLink.bpm.constans.Web;
import OLink.bpm.core.helper.toc.ITopic;
import OLink.bpm.core.helper.toc.TocFileParser;
import org.xml.sax.SAXException;

public class HelpHelper {
	/*
	 * 获取平台帮助主索引
	 */
	
	//for dwr to get help index
	public String doHelpTreeIndex(String searchParem, HttpServletRequest request) throws IOException {
		String html = "";
		String language = (String) request.getSession()
				.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
		try {
			html = getHelpTreeIndex(searchParem, language);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public String getIscriptHelp(String params, HttpServletRequest request)throws IOException{
		String iscriptHtml = "";
		HelpHelper hhelper = new HelpHelper();
		
		String language = (String) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
		try {
			iscriptHtml = hhelper.getIscriptHelp(language);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return iscriptHtml;
	}
	
	public String getHelpTreeIndex(String seachParem, String language) throws ParserConfigurationException, SAXException,
			IOException {
		StringBuffer html = new StringBuffer();
		TocFileParser tocp = TocFileParser.getInstance();
		Toc toc = tocp.getToc(language);
		if (toc != null) {
			// html.append("<div>"+toc.getLabel()+"</a></div>");
			IUAElement[] children = toc.getChildren();
			if (children.length > 0) {
				html.append("<ul>");
				for (int i = 0; i < children.length; i++) {
					Topic subToc = (Topic) children[i];
					if(subToc.getId().equals("help")){
						doObjectToHtml(subToc, html, seachParem,1);
					}
				}
				html.append("</ul>");
			}
		}
		return html.toString();
	}

	/*
	 * 获取Iscript帮助主索引
	 */
	public String getIscriptHelp(String language) throws ParserConfigurationException, SAXException,
			IOException {
		StringBuffer iscripthtml = new StringBuffer();
		TocFileParser tocp = TocFileParser.getInstance();
		Toc toc = tocp.getToc(language);
		if (toc != null) {
			IUAElement[] children = toc.getChildren();
			if (children.length > 0) {
				iscripthtml.append("<ul>");
				for (int i = 0; i < children.length; i++) {
					Topic subToc = (Topic) children[i];
					if(subToc.getId().equals("iscripthelp")){
						doObjectToIscriptHtml(subToc, iscripthtml, "");
					}
				}
				iscripthtml.append("</ul>");
			}
		}
		return iscripthtml.toString();
	}
	
	/*
	 * 把Topic对象转为html输出String
	 */
	public String doObjectToHtml(Topic topic, StringBuffer html, String seachParem,int level) {
		Environment ev = Environment.getInstance();
		boolean noChild = true;
		if (topic != null) {
			IUAElement[] children = topic.getChildren();
			noChild = children.length > 0 ? false : true;
			html.append("<li>");
			if (!noChild) {
				if(level>=3){
					html.append("<img class='imghelpmenustopic' onclick='isShowSubHelpMenus(jQuery(this))' width='16px' height='15px' src='"
							+ ev.getContextPath() + "/resource/imgnew/plus.gif' />");
					html.append("<img class='imghelpmenus' src='" + ev.getContextPath() + "/resource/imgnew/toc_closed.gif' />");
				}else{
				html.append("<img class='imghelpmenustopic' onclick='isShowSubHelpMenus(jQuery(this))' width='16px' height='15px' src='"
								+ ev.getContextPath() + "/resource/imgnew/minus.gif' />");
				html.append("<img class='imghelpmenus' src='" + ev.getContextPath() + "/resource/imgnew/toc_open.gif' />");
				}
			} else {
				html.append("<img class='imghelpmenus2' src='" + ev.getContextPath() + "/resource/imgnew/topic.gif' />");
			}
			
//			if (!"".equals(seachParem) && topic.getLabel().indexOf(seachParem) != -1) {
//				html.append("<a class='helpmenus' ");
//			} else {
//				html.append("<a class='helpmenus' ");
//			}
			
			html.append("<a class='helpmenus' ");
			
			/** add a 标签的属性* */
			html.append("onclick=\"showHelpContentJSP('{*["+topic.getLabel()+"]*}','" + topic.getHref() + "');\" ");
			html.append("title='{*[" + topic.getLabel() + "]*}' ");

			html.append(">");
			if (validateString(topic.getLabel())) {
				html.append("{*["+topic.getLabel()+"]*}");
			}
			html.append("</a><br>");
			if (!noChild) {
				//设置默认打开三级
				if(level>=3){
					html.append("<ul style='display:none'>");
				}else{
					html.append("<ul>");
				}
				level++;
				for (int i = 0; i < children.length; i++) {
					Topic subTopic = (Topic) children[i];
					doObjectToHtml(subTopic, html, seachParem,level);
				}
				html.append("</ul>");
			}
			html.append("</li>");
		}
		return html.toString();
	}
	
	/*
	 * 把Topic对象转为Iscripthtml输出String
	 */
	public String doObjectToIscriptHtml(Topic topic, StringBuffer html,String seachParem) {
		Environment ev = Environment.getInstance();
		boolean noChild = true;
		
		if (topic != null) {
			IUAElement[] children = topic.getChildren();
			noChild = children.length > 0 ? false : true;
			html.append("<li>");
			if (!noChild) {
				html
						.append("<img class='imghelpmenustopic' onclick='isShowSubHelpMenus(jQuery(this))' width='16px' height='15px' src='"
								+ ev.getContextPath() + "/resource/imgnew/minus.gif' />");
				html.append("<img class='imghelpmenus' src='" + ev.getContextPath() + "/resource/imgnew/toc_open.gif' />");
			} else {
				html.append("<img class='imghelpmenus2' src='" + ev.getContextPath() + "/resource/imgnew/topic.gif' />");
			}
			// if (seachParem != "" && topic.getLabel().indexOf(seachParem) != -1) {
			if (!"".equals(seachParem) && topic.getLabel().indexOf(seachParem) != -1) {
				html.append("<a class='helpmenus searchOnfocus' ");
			} else {
				html.append("<a class='helpmenus' ");
			}
			/** add a 标签的属性* */
			html.append("href=\"" + topic.getHref() + "\"");
			html.append("title='{*[" + topic.getLabel() + "]*}' ");

			html.append(">");
			if (validateString(topic.getLabel())) {
				html.append("{*["+topic.getLabel()+"]*}");
			}
			html.append("</a>");
			if (!noChild) {
				html.append("<ul>");
				for (int i = 0; i < children.length; i++) {
					Topic subTopic = (Topic) children[i];
					doObjectToIscriptHtml(subTopic, html, "");
				}
				html.append("</ul>");
			}
			html.append("</li>");
		}
		return html.toString();
	}
	
	//for dwr to get iscript help
	public String getTipicHrefById(String helpid,HttpServletRequest request) throws IOException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SAXException, ParserConfigurationException {
		HelpHelper hhelper = new HelpHelper();
		String language = (String) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
		return hhelper.getTopicHrefByIdAndLanguage(helpid, language);
	}

	public String getTopicHrefByIdAndLanguage(String id, String language) throws IOException, SAXException,
			ParserConfigurationException {
		String html = "";
		TocFileParser tocp = TocFileParser.getInstance();
		Toc toc = tocp.getToc(language);
		if (validateString(id)) {
			ITopic topic = toc.getTopicById(id);
			
			if (topic !=null && validateString(topic.getId())) {
				html = "{href:\""+topic.getHref()+"\",label:\"{*["+topic.getLabel()+"]*}\"}";
			}
		}
		return html;
	}

	public Boolean validateString(String str) {
		Boolean rtn = false;
		if (str != null && !str.equals("") && !str.equals("undefined")) {
			rtn = true;
		}
		return rtn;

	}
}
