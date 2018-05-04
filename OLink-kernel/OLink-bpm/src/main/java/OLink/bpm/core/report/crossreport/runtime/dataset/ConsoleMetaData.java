package OLink.bpm.core.report.crossreport.runtime.dataset;

/**
 * The meta data of the data set column.
 */
public class ConsoleMetaData implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2563084984932169539L;
	/**
	 * The column data type.
	 */
	private ConsoleDataType dataType;
	/**
	 * The column order.
	 */
	private int order;

	/**
	 * The column name
	 */
	private String columnName;

	/**
	 * Constructor with the column data type and column name.
	 * 
	 * @param dataType
	 *            The data type.
	 * @param columnName
	 *            The column name.
	 * @param order
	 *            The column order.
	 */
	public ConsoleMetaData(ConsoleDataType dataType, String columnName,
			int order) {
		this.dataType = dataType;
		this.columnName = columnName;
		this.order = order;
	}

	/**
	 * @return the dataType
	 */
	public ConsoleDataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(ConsoleDataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName
	 *            the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object otherValue) {

		if (!(otherValue instanceof ConsoleMetaData))
			return false;

		return dataType == ((ConsoleMetaData) otherValue).getDataType()
				&& columnName.equals(((ConsoleMetaData) otherValue)
						.getColumnName());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());

		return result;
	}	
}
