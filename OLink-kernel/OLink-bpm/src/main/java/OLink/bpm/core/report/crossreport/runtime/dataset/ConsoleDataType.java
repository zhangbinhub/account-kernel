package OLink.bpm.core.report.crossreport.runtime.dataset;

import java.sql.Types;

/**
 * The define of data type.
 * 
 */
public class ConsoleDataType implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3115292422058999835L;
	public final static ConsoleDataType String = new ConsoleDataType(0);
	public final static ConsoleDataType Integer = new ConsoleDataType(1);
	public final static ConsoleDataType Numberic = new ConsoleDataType(2);
	public final static ConsoleDataType Date = new ConsoleDataType(3);
	public final static ConsoleDataType DateTime = new ConsoleDataType(4);
	public final static ConsoleDataType Boolean = new ConsoleDataType(5);
	public final static ConsoleDataType Other = new ConsoleDataType(7);
	
	private int value;
	
	private ConsoleDataType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	/**
	 * Parse the sql column type to console data type.
	 * 
	 * @param sqlColumnType
	 *            The SQL column data type
	 * @return The console data type.
	 */
	public static ConsoleDataType toDataType(int sqlColumnType) {
		ConsoleDataType dataType;

		switch (sqlColumnType) {
		case Types.BIT:
			dataType = ConsoleDataType.Boolean;
			break;
		case Types.VARCHAR:
			dataType = ConsoleDataType.String;
			break;
		case Types.CHAR:
			dataType = ConsoleDataType.String;
			break;
//		case Types.NVARCHAR:
//			dataType = ConsoleDataType.String;
//			break;
//		case Types.NCHAR:
//			dataType = ConsoleDataType.String;
//			break;
//		case Types.LONGNVARCHAR:
//			dataType = ConsoleDataType.String;
//			break;
		case Types.LONGVARCHAR:
			dataType = ConsoleDataType.String;
			break;
		case Types.BIGINT:
			dataType = ConsoleDataType.Integer;
			break;
		case Types.REAL:
			dataType = ConsoleDataType.Numberic;
			break;
		case Types.DOUBLE:
			dataType = ConsoleDataType.Numberic;
			break;
		case Types.DECIMAL:
			dataType = ConsoleDataType.Numberic;
			break;
		case Types.FLOAT:
			dataType = ConsoleDataType.Numberic;
			break;
		case Types.INTEGER:
			dataType = ConsoleDataType.Integer;
			break;
		case Types.NUMERIC:
			dataType = ConsoleDataType.Numberic;
			break;
		case Types.SMALLINT:
			dataType = ConsoleDataType.Integer;
			break;
		case Types.TINYINT:
			dataType = ConsoleDataType.Integer;
			break;
		case Types.DATE:
			dataType = ConsoleDataType.Date;
			break;
		case Types.TIME:
			dataType = ConsoleDataType.DateTime;
			break;
		case Types.TIMESTAMP:
			dataType = ConsoleDataType.DateTime;
			break;
		case Types.CLOB:
		    dataType = ConsoleDataType.String;
		    break;
		default:
			dataType = ConsoleDataType.Other;
			break;
		}
		return dataType;
	}
	
}
