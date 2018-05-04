package OLink.bpm.core.dynaform.dts.export2.sql;

import java.util.Collection;

import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;

public class SqlServer extends BaseSql {

	public String createTable(String tableName, Collection<ColumnMapping> columnmappings) throws Exception {
		return null;
	}

	public String alertTable(String tableNalme, Collection<ColumnMapping> nedAdCols) throws Exception {
		return null;
	}

	public String transferType(ColumnMapping cm, String dataType) {
		return null;
	}

}
