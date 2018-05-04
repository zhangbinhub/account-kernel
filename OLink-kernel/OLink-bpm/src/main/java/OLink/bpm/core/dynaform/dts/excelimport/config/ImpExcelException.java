package OLink.bpm.core.dynaform.dts.excelimport.config;

import java.util.Collection;

public class ImpExcelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2493251030372806806L;

	private Collection<String> rowErrors;

	public ImpExcelException(Collection<String> rowErrors) {
		this.rowErrors = rowErrors;
	}

	public Collection<String> getRowErrors() {
		return rowErrors;
	}

	public void setRowErrors(Collection<String> rowErrors) {
		this.rowErrors = rowErrors;
	}
}
