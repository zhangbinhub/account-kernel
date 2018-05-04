package OLink.bpm.core.dynaform.printer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.IncludeField;
import OLink.bpm.util.StringUtil;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import OLink.bpm.util.DateUtil;

/**
 * @author Happy
 * 
 */

public class PrinterUtils {
	private static PrinterUtils printerUtils;

	public PrinterUtils() {

	}

	public static PrinterUtils getInstance() {
		if (printerUtils == null)
			printerUtils = new PrinterUtils();
		return printerUtils;
	}

	/**
	 * 返回 包含表单字段的XML字符串
	 * 
	 * @param fields
	 *            表单字段的map集合
	 * @return
	 */
	public String getFields(Map<String, String> fields) {
		StringBuffer strXML = new StringBuffer();
		strXML.append("<fields>\n");
	    Set<Map.Entry<String, String>> set = fields.entrySet();
        for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            //System.out.println(entry.getKey() + "--->" + entry.getValue());
            strXML.append("<field name=\"" + entry.getKey() + "\"/>\n");
        }
		strXML.append("</fields>");
		return strXML.toString();
	}
	
	
	/**返回 包含表单子视图的XML字符串
	 * @param views
	 * @return
	 */
	public String getSubViews(Map<String, String> views) {
		org.dom4j.Document document = DocumentHelper.createDocument();
		Element subView = document.addElement("subView");
		Iterator<String> keys = views.keySet().iterator();
		Iterator<String> values = views.values().iterator();
		for(int i=0;i<views.size();i++){
			Element view = subView.addElement("view");
			view.addAttribute("name", keys.next().toString());
			view.addAttribute("id", values.next().toString());
		}
		return document.asXML();
	}

	/**
	 * 返回 Report 打印数据
	 * @param template
	 * @param doc
	 * @param formid
	 * @param flowid
	 * @return
	 * @throws Exception 
	 */
	public String getReportData(String template, Document doc, String formid,
								String flowid, WebUser user, ParamsTable params) throws Exception {
		
		return createDocument(template,doc,user,params);
	}

/*
	public void parseForm(Node form){
		List fields = form.selectNodes("textbox");
		for(Iterator it = fields.iterator();it.hasNext();){
			Element field =(Element)it.next();
		}
	}
*/	
	
	/**
	 * 根据字段名 获取 Document 私有字段的值
	 * @param doc
	 * @param fieldName
	 * @return
	 */
	public String getDocPrivateItemValue(Document doc,String fieldName){
		if(fieldName.equals("AuditDate")){
			return DateUtil.getDateStr(doc.getAuditdate());
		}else if(fieldName.equals("AuditorNames")){
			return doc.getAuditorNames();
		}else if(fieldName.equals("Author")){
			return doc.getAuthor().getName();
		}else if(fieldName.equals("Created")){
			return DateUtil.getDateStr(doc.getCreated());
		}else if(fieldName.equals("FormName")){
			return doc.getFormname();
		}else if(fieldName.equals("LastFlowOperation")){
			return doc.getLastFlowOperation();
		}else if(fieldName.equals("LastModified")){
			return DateUtil.getDateStr(doc.getLastmodified());
		}else if(fieldName.equals("StateLabel")){
			return doc.getStateLabel();
		}else
			return "";
		
	}

	/**
	 * 创建 flexReport 打印数据 xml文档
	 * 
	 * @SuppressWarnings selectNodes方法不支持泛型
	 * @param strTemplate
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String createDocument(String strTemplate, Document doc, WebUser user, ParamsTable params) throws Exception {
		org.dom4j.Document document = DocumentHelper.createDocument();
		org.dom4j.Document template = DocumentHelper.parseText(strTemplate);
		
		Element report =createReport(document,template);
		
		List<Element> formList = template.selectNodes("//report/form");
		List<Element> detailList = template.selectNodes("//report/detail");
		List<Element> viewList = template.selectNodes("//report/view");
		List<Element> headerList = template.selectNodes("//report/header");
		List<Element> footerList = template.selectNodes("//report/footer");
		
		this.createSingleCanvas(formList, "form", report, doc);
		this.createDetail(detailList, report, doc);
		this.createView(viewList,report,doc,user,params);
		//this.createSingleCanvas(headerList, "header", report, doc);
		//this.createSingleCanvas(footerList, "footer", report, doc);
		this.createHeader(headerList, report, doc);
		this.createFooter(footerList, report, doc);
//System.out.println(document.asXML());
		return document.asXML();
	}
	
	/**
	 * @param document
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public Element createReport(org.dom4j.Document document,org.dom4j.Document template)throws Exception{
		Element report = document.addElement("report");
		String paperFormat = template.getRootElement().attributeValue("paperFormat");
		String width = template.getRootElement().attributeValue("width");
		String height = template.getRootElement().attributeValue("height");
		report.addAttribute("paperFormat", paperFormat);
		if(width !=null && height!=null){ //兼容 2.3
			report.addAttribute("width", width);
			report.addAttribute("height", height);
		}
		return report;
	}
	
	
	/**
	 * @param forms
	 * @param parentNode
	 * @param doc
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public void createSingleCanvas(List<Element> canvases,String tag,Element parentNode,Document doc)throws Exception{
		for(Iterator<Element> it = canvases.iterator();it.hasNext();){
			Element canvasNode = it.next();
			String name = canvasNode.attributeValue("name");
			String startX = canvasNode.attributeValue("startX");
			String startY = canvasNode.attributeValue("startY");
			String endX = canvasNode.attributeValue("endX");
			String endY = canvasNode.attributeValue("endY");
			Element canvas = parentNode.addElement(tag);
			canvas.addAttribute("name", name);
			canvas.addAttribute("startX", startX);
			canvas.addAttribute("startY", startY);
			canvas.addAttribute("endX", endX);
			canvas.addAttribute("endY", endY);
			createItems(canvasNode,canvas,doc,0);
			
		}
	}
	
	
	/**
	 * 创建 视图组件对象
	 * @param details
	 * @param parentNode
	 * @param doc
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public void createView(List<Element> views,Element parentNode,Document doc,WebUser user,ParamsTable params)throws Exception{
		for(Iterator<Element> it = views.iterator();it.hasNext();){
			Element viewNode = it.next();
			String viewId = viewNode.attributeValue("bindingView");
			int repeat = Integer.parseInt(viewNode.attributeValue("repeat"));
			String startX = viewNode.attributeValue("startX");
			String startY = viewNode.attributeValue("startY");
			String endX = viewNode.attributeValue("endX");
			String endY = viewNode.attributeValue("endY");
			if(viewId.trim().length()>0){
				
				Form form = doc.getForm();
				Collection<FormField> fileds = form.getFields();
				for(Iterator<FormField> iter = fileds.iterator();iter.hasNext();){
					FormField filed = iter.next();
					if(filed instanceof IncludeField){
						if(((IncludeField)filed).getIncludeView() !=null && ((IncludeField)filed).getIncludeView().getId().equals(viewId)){
							if(((IncludeField)filed).isRelate()){
								params.setParameter("parentid", doc.getId());
								break;
							}
						}
					}
				}
				
				params.setParameter("viewId", viewId);
				params.setParameter("isRelate","true");
				ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
//				PrinterProcess pp = (PrinterProcess) ProcessFactory.createProcess(PrinterProcess.class);
				View viewVO =(View)vp.doView(viewId);
				
				if(viewVO !=null){
					//DataPackage dataPackage = pp.getViewDatas(viewVO, repeat, user,params);
					DataPackage<Document> dataPackage = viewVO.getViewTypeImpl().getViewDatas(params, 1, repeat, user, new Document());
					if(dataPackage !=null && dataPackage.datas.size()>0){
						Element view = parentNode.addElement("view");
						view.addAttribute("startX", startX);
						view.addAttribute("startY", startY );
						view.addAttribute("endX", endX);
						view.addAttribute("endY",endY);
						Collection<Document> docs = dataPackage.datas;
						addSumRow(viewVO, dataPackage, docs, params, user);
						for(Iterator<Document> iter = docs.iterator();iter.hasNext();){
							Document idoc = iter.next();
							this.createViewItem(view,idoc, viewVO,params,user);
						}
					}
				}
			}
		}
	}
	
	protected void addSumRow(View view,DataPackage<Document> dataPackage,Collection<Document> docs,ParamsTable params, WebUser user) throws Exception {
		if(view.isSum()){
			Document doc = (Document) ((Document)docs.toArray()[0]).deepClone();
			IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
			runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());
			
			for (Iterator<Column> iterator = view.getColumns().iterator(); iterator.hasNext();) {
				Column col = iterator.next();
				String sum = col.getSumByDatas(dataPackage, runner, user);
				if(!StringUtil.isBlank(sum)){
					sum = sum.replace(col.getName()+"{*[Grant_Total]*}", "总计");
				}
				doc.findItem(col.getName()).setValue(sum);
			}
			
			docs.add(doc);
		}
	}
	
	public void createViewItem(Element parentNode,Document doc,View view,ParamsTable params, WebUser user) throws Exception {
		Element item = parentNode.addElement("item");
		Collection<Column> columns = view.getColumns();
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());
		for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
			Column col = iterator.next();
			Object result = col.getText(doc, runner, user);
//			String fieldName = col.getFieldName();
//			if(fieldName !=null && fieldName.trim().length()>0){
//			String value = this.getColumnValue(doc, fieldName);
			item.addAttribute(col.getName(), result.toString());
			
		}
	}
	
	//@SuppressWarnings("unchecked")
	public void createDetail(List<Element> details,Element parentNode,Document doc)throws Exception{
		for(Iterator<Element> it = details.iterator();it.hasNext();){
			Element detailNode = it.next();
			String repeatType = detailNode.attributeValue("repeatType");
			int repeat = Integer.parseInt(detailNode.attributeValue("repeat"));
			String name = detailNode.attributeValue("name");
			String startX = detailNode.attributeValue("startX");
			String startY = detailNode.attributeValue("startY");
			String endX = detailNode.attributeValue("endX");
			String endY = detailNode.attributeValue("endY");
			int height =Integer.parseInt(endY) - Integer.parseInt(startY);
			if(repeatType.equals("static")){
				for(int i=0;i<repeat;i++){
					int _height =height*i;
					Element detail = parentNode.addElement("detail");
					detail.addAttribute("name", name);
					detail.addAttribute("startX", startX);
					detail.addAttribute("startY", String.valueOf(Integer.parseInt(startY)+_height) );
					detail.addAttribute("endX", endX);
					detail.addAttribute("endY", String.valueOf(Integer.parseInt(endY)+_height));
					createItems(detailNode,detail,doc,_height);
				}
			}
			
			
		}
	}
	
	/**
	 * @param forms
	 * @param parentNode
	 * @param doc
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public void createForm(List<Element> forms,Element parentNode,Document doc)throws Exception{
		for(Iterator<Element> it = forms.iterator();it.hasNext();){
			Element formNode = it.next();
			String name = formNode.attributeValue("name");
			String startX = formNode.attributeValue("startX");
			String startY = formNode.attributeValue("startY");
			String endX = formNode.attributeValue("endX");
			String endY = formNode.attributeValue("endY");
			Element form = parentNode.addElement("form");
			form.addAttribute("name", name);
			form.addAttribute("startX", startX);
			form.addAttribute("startY", startY);
			form.addAttribute("endX", endX);
			form.addAttribute("endY", endY);
			createItems(formNode,form,doc,0);
			
		}
	}
	
	/**
	 * @param headers
	 * @param parentNode
	 * @param doc
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public void createHeader(List<Element> headers,Element parentNode,Document doc)throws Exception{
		for(Iterator<Element> it = headers.iterator();it.hasNext();){
			Element headerNode = it.next();
			String viewStyle = headerNode.attributeValue("viewStyle");
			String startX = headerNode.attributeValue("startX");
			String startY = headerNode.attributeValue("startY");
			String endX = headerNode.attributeValue("endX");
			String endY = headerNode.attributeValue("endY");
			Element header = parentNode.addElement("header");
			header.addAttribute("viewStyle", viewStyle);
			header.addAttribute("startX", startX);
			header.addAttribute("startY", startY);
			header.addAttribute("endX", endX);
			header.addAttribute("endY", endY);
			createItems(headerNode,header,doc,0);
			
		}
	}
	
	/**
	 * @param footers
	 * @param parentNode
	 * @param doc
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public void createFooter(List<Element> footers,Element parentNode,Document doc)throws Exception{
		for(Iterator<Element> it = footers.iterator();it.hasNext();){
			Element footerNode = it.next();
			String viewStyle = footerNode.attributeValue("viewStyle");
			String startX = footerNode.attributeValue("startX");
			String startY = footerNode.attributeValue("startY");
			String endX = footerNode.attributeValue("endX");
			String endY = footerNode.attributeValue("endY");
			Element footer = parentNode.addElement("footer");
			footer.addAttribute("viewStyle", viewStyle);
			footer.addAttribute("startX", startX);
			footer.addAttribute("startY", startY);
			footer.addAttribute("endX", endX);
			footer.addAttribute("endY", endY);
			createItems(footerNode,footer,doc,0);
			
		}
	}
	
	/**
	 * @SuppressWarnings selectNodes方法不支持泛型
	 * @param parentNode
	 * @param parent
	 * @param doc
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void createItems(Node parentNode,Element parent,Document doc,int _height)throws Exception{
		List<Element> fields = parentNode.selectNodes("textbox");//解析 field-----------
		createField(fields,parent,doc,_height);
		List<Element> staticLabels = parentNode.selectNodes("staticLabel");//解析 staticLabel-----------
		createStaticLabel(staticLabels,parent,doc,_height);
		List<Element> lines = parentNode.selectNodes("line");
		createLine(lines,parent,doc,_height);
		List<Element> pageNumber = parentNode.selectNodes("pageNumber");
		createPageNumber(pageNumber,parent,doc,_height);
	}
	
	/**
	 * @param fields
	 * @param parentNode
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public Element createField(List<Element> fields,Element parentNode,Document doc,int _height)throws Exception{
		Element field =null;
		for(Iterator<Element> it = fields.iterator();it.hasNext();){
			Element tfield = it.next();
			String fieldName = tfield.attributeValue("bindingField");
			String fontSize = tfield.attributeValue("fontSize");
			String color = tfield.attributeValue("color");
			String startX = tfield.attributeValue("startX");
			String startY = tfield.attributeValue("startY");
			String endX = tfield.attributeValue("endX");
			String endY = tfield.attributeValue("endY");
			String value = "";
			if(fieldName.startsWith("$")){
				value = getDocPrivateItemValue(doc,fieldName.substring(1, fieldName.length()));
			}else if(fieldName.trim().length()>0){
				value = doc.getItemValueAsString(fieldName);
			}
			
			field = parentNode.addElement("field");
			field.addAttribute("fieldName", fieldName);
			field.addAttribute("value", value);
			field.addAttribute("fontSize", fontSize);
			field.addAttribute("color", color);
			field.addAttribute("startX", startX);
			field.addAttribute("startY", String.valueOf(Integer.parseInt(startY)+_height));
			field.addAttribute("endX", endX);
			field.addAttribute("endY", String.valueOf(Integer.parseInt(endY)+_height));
		}
		return field;
	}
	
	/**
	 * @param staticLabels
	 * @param parentNode
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public Element createStaticLabel(List<Element> staticLabels,Element parentNode,Document doc,int _height)throws Exception{
		Element staticLabel =null;
		for(Iterator<Element> it = staticLabels.iterator();it.hasNext();){
			Element tstaticLabel = it.next();
			String text = tstaticLabel.attributeValue("text");
			String fontSize = tstaticLabel.attributeValue("fontSize");
			String color = tstaticLabel.attributeValue("color");
			String startX = tstaticLabel.attributeValue("startX");
			String startY = tstaticLabel.attributeValue("startY");
			String endX = tstaticLabel.attributeValue("endX");
			String endY = tstaticLabel.attributeValue("endY");
			staticLabel = parentNode.addElement("staticLabel");
			staticLabel.addAttribute("text", text);
			staticLabel.addAttribute("fontSize", fontSize);
			staticLabel.addAttribute("color", color);
			staticLabel.addAttribute("startX", startX);
			staticLabel.addAttribute("startY", String.valueOf(Integer.parseInt(startY)+_height));
			staticLabel.addAttribute("endX", endX);
			staticLabel.addAttribute("endY", String.valueOf(Integer.parseInt(endY)+_height));
			
		}
		return staticLabel;
	}
	
	
	/**
	 * @param lines
	 * @param parentNode
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public Element createLine(List<Element> lines,Element parentNode,Document doc,int _height)throws Exception{
		Element line =null;
		for(Iterator<Element> it = lines.iterator();it.hasNext();){
			Element tLine = it.next();
			String thickness = tLine.attributeValue("thickness");
			String color = tLine.attributeValue("color");
			String startX = tLine.attributeValue("startX");
			String startY = tLine.attributeValue("startY");
			String endX = tLine.attributeValue("endX");
			String endY = tLine.attributeValue("endY");
			line = parentNode.addElement("line");
			line.addAttribute("thickness", thickness);
			line.addAttribute("color", color);
			line.addAttribute("startX", startX);
			line.addAttribute("startY", String.valueOf(Integer.parseInt(startY)+_height));
			line.addAttribute("endX", endX);
			line.addAttribute("endY", String.valueOf(Integer.parseInt(endY)+_height));
			
		}
		return line;
	}
	
	//@SuppressWarnings("unchecked")
	public Element createPageNumber(List<Element> pageNumbers,Element parentNode,Document doc,int _height)throws Exception{
		Element pageNumber =null;
		for(Iterator<Element> it = pageNumbers.iterator();it.hasNext();){
			Element tPageNumber = it.next();
			String startX = tPageNumber.attributeValue("startX");
			String startY = tPageNumber.attributeValue("startY");
			String endX = tPageNumber.attributeValue("endX");
			String endY = tPageNumber.attributeValue("endY");
			pageNumber = parentNode.addElement("pageNumber");
			pageNumber.addAttribute("startX", startX);
			pageNumber.addAttribute("startY", String.valueOf(Integer.parseInt(startY)+_height));
			pageNumber.addAttribute("endX", endX);
			pageNumber.addAttribute("endY", String.valueOf(Integer.parseInt(endY)+_height));
			
		}
		return pageNumber;
	}
	
	
	
	
	@SuppressWarnings("unused")
	private String getColumnValue(Document doc,String fieldName) throws Exception{
		String result = null;
		if (!StringUtil.isBlank(fieldName)) {
			result = doc.getValueByField(fieldName);
		}
		return (result != null ? result.toString() : "");
	}

//	public static void main(String[] args){
//		String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
//		"<report  paperFormat=\"A4\">"+
//		"<detail repeatType=\"static\" repeat=\"3\" name=\"Detail782731\" startX=\"0\" startY=\"170\" endX=\"595\" endY=\"270\" >"+
//		    "<line name=\"Line154584\" fillColor=\"4f0dd\" startX=\"50\" startY=\"190\" endX=\"150\" endY=\"190\"></line>"+
//		"</detail>"+
//		"</report>";
//		try {
//			PrinterUtils.getInstance().createDocument(str, new Document());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
