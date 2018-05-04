//Source file: D:\\excelimport\\src\\excelimport\\BFApplet.java

//Source file: E:\\excelimport\\src\\excelimport\\BFApplet.java

package OLink.bpm.core.dynaform.dts.excelimport;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;

import netscape.javascript.JSObject;

/**
 * @author nicholas
 */
public class ExcelMappingApplet extends Applet {
	
	private static final long serialVersionUID = -6777181627389731963L;

	boolean isStandalone = false;

	ExcelMappingPanel bppanel = new ExcelMappingPanel();

	ExcelMappingDiagram fd = null;

	BorderLayout borderLayout1 = new BorderLayout();

	JSObject win = null;

	private MediaTracker tracker;

	/**
	 * Construct the applet
	 * 
	 * @roseuid 3E0A6E1602A2
	 */
	public ExcelMappingApplet() {

	}

	/**
	 * Get a parameter value
	 * 
	 * @param key
	 * @param def
	 * @return java.lang.String
	 * @roseuid 3E0A6E16028E
	 */
	public String getParameter(String key, String def) {
		return isStandalone ? System.getProperty(key, def) : (getParameter(key) != null ? getParameter(key) : def);
	}

	/**
	 * Initialize the applet
	 * 
	 * @roseuid 3E0A6E1602AC
	 */
	public void init() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------
	protected void loading(Image image1) {
		// wait for the image to load completely
		synchronized (tracker) {
			tracker.addImage(image1, 0);
			try {
				tracker.waitForID(0);
			} catch (InterruptedException interruptedexception) {
				interruptedexception.printStackTrace();
			}
			tracker.removeImage(image1, 0);
		}
	}

	// -----------------------------------------------

	/**
	 * Component initialization
	 * 
	 * @throws Exception
	 * @roseuid 3E0A6E1602AD
	 */
	private void jbInit() throws Exception {
		tracker = new MediaTracker(this);

		String xmlStr = "";

		if (getParameter("xmlStr") != null && !getParameter("xmlStr").toLowerCase().equals("null")) {
			xmlStr = getParameter("xmlStr");
		}

		/** *********************** */
		// xmlStr = getTestXmlStr();
		/** ************************ */
		
		
		fd = Factory.trnsXML2Dgrm(xmlStr);

		this.setLayout(borderLayout1);

		Panel btnbar = new Panel();
		this.add(btnbar, BorderLayout.NORTH);
		//
		// {
		// Button btn = new Button();
		// btn.setLabel("add master sheet");
		// btn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// addMasterSheet("hello master sheet", "formName", "");
		// }
		// });
		// btnbar.add(btn);
		// this.add(btnbar, BorderLayout.NORTH);
		// }
		//
		// {
		// Button btn = new Button();
		// btn.setLabel("add detail sheet");
		// btn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// addDetailSheet("hello detail sheet", "formName", "");
		// }
		// });
		// btnbar.add(btn);
		// this.add(btnbar, BorderLayout.NORTH);
		// }
		//
		// {
		// Button btn = new Button();
		// btn.setLabel("add column");
		// btn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// addColumn("hello column", "fieldName", "valueScript", "validateRule",
		// "");
		// }
		// });
		// btnbar.add(btn);
		// this.add(btnbar, BorderLayout.NORTH);
		// }
		//
		// {
		// Button btn = new Button();
		// btn.setLabel("add relation");
		// btn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// addRelation();
		// }
		// });
		// btnbar.add(btn);
		// this.add(btnbar, BorderLayout.NORTH);
		// }
		//
		// {
		// Button btn = new Button();
		// btn.setLabel("delete element");
		// btn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// removeElement();
		// }
		// });
		// btnbar.add(btn);
		// this.add(btnbar, BorderLayout.NORTH);
		// }
		//
		// {
		// Button btn = new Button();
		// btn.setLabel("print xml");
		// btn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// String xml = saveToXML();		
		// }
		// });
		// btnbar.add(btn);
		// this.add(btnbar, BorderLayout.NORTH);
		// }
		//
		fd.setBackground(Color.white);
		try {
			win = JSObject.getWindow(this);
			fd.setJSObject(win);
		} catch (Exception e) {
		}

		this.add(bppanel, BorderLayout.CENTER);
		bppanel.add(fd);

	}

	public void loadXML(String xmlStr) throws Exception {
		tracker = new MediaTracker(this);
		fd = Factory.trnsXML2Dgrm(xmlStr);

		this.setLayout(borderLayout1);

		Panel btnbar = new Panel();
		this.add(btnbar, BorderLayout.NORTH);
		
		fd.setBackground(Color.white);
		try {
			win = JSObject.getWindow(this);
			fd.setJSObject(win);
		} catch (Exception e) {
		}

		this.add(bppanel, BorderLayout.CENTER);
		bppanel.add(fd);
	}

	/**
	 * Get Applet information
	 * 
	 * @return java.lang.String
	 * @roseuid 3E0A6E1602C0
	 */
	public String getAppletInfo() {
		return "Applet Information";
	}

	/**
	 * Get parameter info
	 * 
	 * @return String[][]
	 * @roseuid 3E0A6E1602CA
	 */
	public String[][] getParameterInfo() {
		return null;
	}

	public void addMasterSheet(String name, String formName, String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_ADD_NODE);

		MasterSheet node = new MasterSheet(fd);
		node.name = name;
		node.formName = formName;
		node.description = description;
		node.x = 100;
		node.y = 100;

		fd.appendElement(node);
		fd.editNode(node);
	}

	public void addDetailSheet(String name, String formName, String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_ADD_NODE);

		DetailSheet node = new DetailSheet(fd);
		node.name = name;
		node.formName = formName;
		node.description = description;
		node.x = 100;
		node.y = 100;

		fd.appendElement(node);
		fd.editNode(node);
	}

	public void addColumn(String name, String fieldName, String valueScript, String validateRule, boolean primaryKey,
			String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_ADD_NODE);

		Column node = new Column(fd);
		node.name = name;
		node.fieldName = fieldName;
		node.valueScript = valueScript;
		node.validateRule = validateRule;
		node.primaryKey = primaryKey;
		node.description = description;
		node.x = 100;
		node.y = 100;

		fd.appendElement(node);
		fd.editNode(node);
	}

	public void addRelation() {
		fd.changeStatues(ExcelMappingDiagram.ACTION_ADD_RELATION);

		fd.addRelation();
	}

	public void removeElement() {
		// fd.changeStatues(fd.ACTION_REMOVE);
		Element elm = fd.getCurrToEdit();
		fd.removeElement(elm);
		fd.repaint();
	}

	public String saveToXML() throws Exception{
		return fd.toXML();
	}

	public boolean xmlChanged() { // 检查流程图是否已经修改
		return fd.getChanged();
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public Element getCurrToEditElement() {
		Element currToEdit = fd.getCurrToEdit();

		return currToEdit;
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public Relation getCurrToEditRelation() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof Relation) {
			return (Relation) currToEdit;
		}

		else {
			return null;
		}
	}

	public void editMasterSheet(String name, String formName, String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof MasterSheet) {
			MasterSheet grp = (MasterSheet) fd.getCurrToEdit();
			grp.name = name;
			grp.formName = formName;
			grp.description = description;
			fd.editNode(grp);
		}
	}

	public void editDetailSheet(String name, String formName, String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof DetailSheet) {
			DetailSheet grp = (DetailSheet) fd.getCurrToEdit();
			grp.name = name;
			grp.formName = formName;
			grp.description = description;
			fd.editNode(grp);
		}
	}

	public void editColumn(String name, String fieldName, String valueScript, String validateRule, boolean primaryKey,
			String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof Column) {
			Column grp = (Column) fd.getCurrToEdit();
			grp.name = name;
			grp.fieldName = fieldName;
			grp.valueScript = valueScript;
			grp.validateRule = validateRule;
			grp.primaryKey = primaryKey;
			grp.description = description;
			fd.editNode(grp);
		}
	}

	public void editRelation(String name, String description) {
		fd.changeStatues(ExcelMappingDiagram.ACTION_EDIT_RELATION);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof Relation) {
			Relation rlt = (Relation) fd.getCurrToEdit();
			fd.editRelation(rlt);
		}
	}

	public static void main(String[] args) {

	}

	/*
	private static String getTestXmlStr() {
		StringBuffer tmp = new StringBuffer();
		tmp.append("<excelimport.ExcelMappingDiagram>");
		tmp.append("<flowstatus>16</flowstatus>");
		tmp.append("<flowpath></flowpath>");
		tmp.append("<deleteMSG>null</deleteMSG>");
		tmp.append("<TOP_ALIGNMENT>0.0</TOP_ALIGNMENT>");
		tmp.append("<CENTER_ALIGNMENT>0.5</CENTER_ALIGNMENT>");
		tmp.append("<BOTTOM_ALIGNMENT>1.0</BOTTOM_ALIGNMENT>");
		tmp.append("<LEFT_ALIGNMENT>0.0</LEFT_ALIGNMENT>");
		tmp.append("<RIGHT_ALIGNMENT>1.0</RIGHT_ALIGNMENT>");
		tmp.append("<WIDTH>1</WIDTH>");
		tmp.append("<HEIGHT>2</HEIGHT>");
		tmp.append("<PROPERTIES>4</PROPERTIES>");
		tmp.append("<SOMEBITS>8</SOMEBITS>");
		tmp.append("<FRAMEBITS>16</FRAMEBITS>");
		tmp.append("<ALLBITS>32</ALLBITS>");
		tmp.append("<ERROR>64</ERROR>");
		tmp.append("<ABORT>128</ABORT>");
		tmp.append("<excelimport.MasterSheet>");
		tmp.append("<formName>formName</formName>");
		tmp.append("<x>183</x>");
		tmp.append("<y>181</y>");
		tmp.append("<scale>0</scale>");
		tmp.append("<id>1156842568047</id>");
		tmp.append("<name>hellomastersheet</name>");
		tmp.append("<description></description>");
		tmp.append("</excelimport.MasterSheet>");
		tmp.append("<excelimport.Column>");
		tmp.append("<fieldName>fieldName</fieldName>");
		tmp.append("<x>490</x>");
		tmp.append("<y>170</y>");
		tmp.append("<scale>0</scale>");
		tmp.append("<id>1156842570170</id>");
		tmp.append("<name>hellocolumn</name>");
		tmp.append("<description></description>");
		tmp.append("</excelimport.Column>");
		tmp.append("<excelimport.Relation>");
		tmp.append("<state></state>");
		tmp.append("<startnodeid>1156842568047</startnodeid>");
		tmp.append("<endnodeid>1156842570170</endnodeid>");
		tmp.append("<ispassed>false</ispassed>");
		tmp.append("<isreturn>false</isreturn>");
		tmp.append("<condition></condition>");
		tmp.append("<pointstack>183;181;490;170</pointstack>");
		tmp.append("<scale>0</scale>");
		tmp.append("<id>1156842571702</id>");
		tmp.append("<name></name>");
		tmp.append("<description></description>");
		tmp.append("</excelimport.Relation>");
		tmp.append("</excelimport.ExcelMappingDiagram>");
		return tmp.toString();
	}
	*/
}
