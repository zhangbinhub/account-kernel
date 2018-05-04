package OLink.bpm.util;

import java.util.HashMap;
import java.util.Map;

public class TableMapping {

	private static Map<String, String[]> relation = new HashMap<String, String[]>();

	private static Map<String, String[]> fkMapping = new HashMap<String, String[]>();

	/**
	 * 主从表关系维护
	 */
	static {
		relation.put("T_APPLICATION", new String[] { "T_MODULE" });

		relation.put("T_MODULE", new String[] { "T_MODULE" });

		relation.put("T_BILLDEFI", new String[] { "T_MODULE" });

		relation.put("T_STYLEREPOSITORY", new String[] { "T_APPLICATION",
				"T_MODULE" });

		relation.put("T_IMAGEREPOSITORY", new String[] { "T_APPLICATION",
				"T_MODULE" });

		relation.put("T_REPOSITORY",
				new String[] { "T_APPLICATION", "T_MODULE" });

		relation.put("T_DYNAFORM_FORM", new String[] { "T_APPLICATION",
				"T_MODULE", "T_STYLEREPOSITORY", "T_USER" });

		relation.put("T_VIEW", new String[] { "T_DYNAFORM_FORM", "T_MODULE",
				"T_STYLEREPOSITORY" });

		relation.put("T_DOCUMENT", new String[] { "T_DOCUMENT",
				"T_FLOWSTATERT", "T_USER" });

		relation.put("T_QUERY", new String[] { "T_APPLICATION", "T_DATASOURCE",
				"T_DYNAFORM_FORM", "T_MODULE" });

		relation.put("T_REPORTCONFIG", new String[] { "T_APPLICATION",
				"T_DATASOURCE", "T_DYNAFORM_FORM", "T_MODULE", "T_QUERY" });

		relation.put("T_TABLECOLUMN", new String[] { "T_REPORTCONFIG" });

		relation.put("T_MAPPINGCONFIG", new String[] { "T_DATASOURCE",
				"T_REPORTCONFIG" });

		relation.put("T_COLUMNMAPPING", new String[] { "T_MAPPINGCONFIG" });
	}

	/**
	 * 表与外键映射维护
	 */
	static {
		fkMapping.put("T_MAPPINGCONFIG", new String[] { "MAPPINGCONFIGS_ID" });
		fkMapping.put("T_COLUMNMAPPING", new String[] { "MAPPINGCONFIG" });
	}

	public static String[] getSubTables(String foreignTableName) {
		String[] tables = new String[] {}; 
		
		Object values = relation.get(foreignTableName);
		
		if (values != null) {
			tables = (String[]) values;
		}
		
		return tables;
	}
}
