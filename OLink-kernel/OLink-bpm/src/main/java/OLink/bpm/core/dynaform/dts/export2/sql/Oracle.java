package OLink.bpm.core.dynaform.dts.export2.sql;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;

public class Oracle extends BaseSql {

	/**
	 * 
	 */

	public String createTable(String tableName, Collection<ColumnMapping> columnmappings) throws Exception {

		StringBuffer sql = new StringBuffer();

		sql.append(getCreateHeadSql(tableName));
		sql.append(" ( ID Number(22,3)  PRIMARY KEY");

		if (columnmappings.size() > 0)
			sql.append(",");

		for (Iterator<ColumnMapping> iter = columnmappings.iterator(); iter.hasNext();) {

			ColumnMapping cm = iter.next();
			sql.append(getColDefineSql(cm));

			if (iter.hasNext())
				sql.append(", ");
		}
		sql.append(" ) ");
		return sql.toString();
	}

	/**
	 * 
	 */

	public String alertTable(String tableNalme, Collection<ColumnMapping> nedAdCols) throws Exception {

		StringBuffer sql = new StringBuffer();

		sql.append("ALTER TABLE ").append(tableNalme);
		sql.append(" ADD (");

		for (Iterator<ColumnMapping> iter = nedAdCols.iterator(); iter.hasNext();) {

			ColumnMapping cm = iter.next();
			sql.append(getColDefineSql(cm));

			if (iter.hasNext())
				sql.append(", ");
		}

		sql.append(" ) ");
		return sql.toString();
	}

	private String getColDefineSql(ColumnMapping cm) {
		String sql = "";
		String colName = cm.getToName().toUpperCase();
		String colType = cm.getToType();
		String colLength = cm.getLength();

		sql += "  " + colName;

		if (cm.getToName().equals("DOC_ID")) {
			sql += " VARCHAR ";
			sql += getLenthStr(colLength);
			sql += "  unique ";
		} else if (colType.equals(ColumnMapping.DATA_TYPE_VARCHAR)) {
			sql += " VARCHAR ";
			sql += getLenthStr(colLength);
		} else if (colType.equals(ColumnMapping.DATA_TYPE_NUMBER)) {
			sql += (" DECIMAL ");
			sql += " (" + colLength + ",";
			sql += cm.getPrecision() + ")";
		} else if (colType.equals(ColumnMapping.DATA_TYPE_DATE)) {
			sql += " Date ";
		} else if (colType.equals("TIMESTAMP")) {
			sql += " TIMESTAMP ";
			sql += getLenthStr(colLength);
		}

		return sql;
	}

	/**
	 * 
	 * @param length
	 * @return
	 */

	private String getLenthStr(String length) {
		return "(" + length + ")";
	}

	public String transferType(ColumnMapping cm, String dataType) {

		String type = cm.getToType();

		if ((dataType.indexOf("NUMBER") != -1 || dataType.indexOf("DECIMAL") != -1)
				&& !cm.getToType().equals(ColumnMapping.DATA_TYPE_NUMBER)) {

			type = ColumnMapping.DATA_TYPE_NUMBER;

		} else if (dataType.indexOf("VARCHAR") != -1 && !cm.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR)) {

			type = ColumnMapping.DATA_TYPE_VARCHAR;
		}

		else if ((dataType.indexOf("DATE") != -1 || dataType.indexOf("TIMESTAMP") != -1)
				&& !cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {

			type = ColumnMapping.DATA_TYPE_DATE;
		}
		return type;
	}

}
