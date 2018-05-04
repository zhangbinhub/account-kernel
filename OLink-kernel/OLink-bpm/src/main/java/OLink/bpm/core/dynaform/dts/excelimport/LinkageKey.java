package OLink.bpm.core.dynaform.dts.excelimport;

/**
 * @author  nicholas
 */
public class LinkageKey {
	MasterSheet masterSheet;
	Column masterSheetKeyColumn;
	
	DetailSheet detailSheet;
	Column detailSheetKeyColumn;
	/**
	 * @hibernate.property  column="detailSheet"
	 * @uml.property  name="detailSheet"
	 */
	public DetailSheet getDetailSheet() {
		return detailSheet;
	}
	/**
	 * @param detailSheet  the detailSheet to set
	 * @uml.property  name="detailSheet"
	 */
	public void setDetailSheet(DetailSheet detailSheet) {
		this.detailSheet = detailSheet;
	}
	/**
	 * @hibernate.property  column="detailSheetKeyColumn"
	 * @uml.property  name="detailSheetKeyColumn"
	 */
	public Column getDetailSheetKeyColumn() {
		return detailSheetKeyColumn;
	}
	/**
	 * @param detailSheetKeyColumn  the detailSheetKeyColumn to set
	 * @uml.property  name="detailSheetKeyColumn"
	 */
	public void setDetailSheetKeyColumn(Column detailSheetKeyColumn) {
		this.detailSheetKeyColumn = detailSheetKeyColumn;
	}
	/**
	 * @hibernate.property  column="masterSheet"
	 * @uml.property  name="masterSheet"
	 */
	public MasterSheet getMasterSheet() {
		return masterSheet;
	}
	/**
	 * @param masterSheet  the masterSheet to set
	 * @uml.property  name="masterSheet"
	 */
	public void setMasterSheet(MasterSheet masterSheet) {
		this.masterSheet = masterSheet;
	}
	/**
	 * @hibernate.property  column="masterSheetKeyColumn"
	 * @uml.property  name="masterSheetKeyColumn"
	 */
	public Column getMasterSheetKeyColumn() {
		return masterSheetKeyColumn;
	}
	/**
	 * @param masterSheetKeyColumn  the masterSheetKeyColumn to set
	 * @uml.property  name="masterSheetKeyColumn"
	 */
	public void setMasterSheetKeyColumn(Column masterSheetKeyColumn) {
		this.masterSheetKeyColumn = masterSheetKeyColumn;
	}

}
