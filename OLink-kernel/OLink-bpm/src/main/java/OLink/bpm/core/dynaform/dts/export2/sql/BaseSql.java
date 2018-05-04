package OLink.bpm.core.dynaform.dts.export2.sql;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;

public abstract class BaseSql {

	/**
	 * 
	 * @param tableName
	 * @param columnmappings
	 * @return
	 * @throws Exception
	 */
	public abstract String createTable(String tableName,
			Collection<ColumnMapping> columnmappings) throws Exception;

	/**
	 * 
	 * @param tableNalme
	 * @param nedAdCols
	 * @return
	 * @throws Exception
	 */
	public abstract String alertTable(String tableNalme, Collection<ColumnMapping> nedAdCols)
			throws Exception;

	public abstract String transferType(ColumnMapping cm,String dataType);
	/**
	 * 
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public static String creatInsertSQL(String tableName, Collection<ColumnMapping> columns) {

		StringBuffer sql = new StringBuffer();
		sql.append(" insert into ");
		sql.append(tableName);
		sql.append(" (ID,");

		for (Iterator<ColumnMapping> iter = columns.iterator(); iter.hasNext();) {
			ColumnMapping cm = iter.next();
			sql.append(cm.getToName());
			if (iter.hasNext())
				sql.append(", ");
		}
		sql.append(" ) values (?,");

		for (Iterator<ColumnMapping> iter = columns.iterator(); iter.hasNext();) {
			iter.next();
			sql.append("?");
			if (iter.hasNext())
				sql.append(", ");

		}
		sql.append(" ) ");
		return sql.toString();
	}

	protected String getCreateHeadSql(String tableName) {
		return " CREATE TABLE " + tableName;

	}

}
