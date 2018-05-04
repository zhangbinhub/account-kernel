package OLink.bpm.core.report.dataprepare.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;

/**
 * @hibernate.class table="T_DATAPREPARE"
 * 
 */

public class DataPrepare extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private  String id;
	
	private String name;
	
	private  Collection<SqlSentence> sqlSentences;
	
	private ModuleVO module;
	
	private DataSource dataSource;
	
	private String clearDataSql;
	
	
	
	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @hibernate.collection-one-to-many class="SqlSentence"
	 * @hibernate.collection-key column="DATAPREPARE_ID" 
	 * @hibernate.set name="sqlSentences" table="T_SQLSENTENCE" 
	 *                 cascade="delete"
	 */

	public Collection<SqlSentence> getSqlSentences() {
		return sqlSentences;
	}

	/**
	 * @param sqlSentences The sqlSentences to set.
	 */
	public void setSqlSentences(Collection<SqlSentence> sqlSentences) {
		this.sqlSentences = sqlSentences;
	}

	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @hibernate.many-to-one column="DATASOURCE_ID"
	 *                        class="DataSource"
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource The dataSource to set.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @return ModuleVO
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        column="MODULE"
	 */
	public ModuleVO getModule() {
		return module;
	}

	public void setModule(ModuleVO module) {
		this.module = module;
	}

	/**
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="CLEARDATASQL" type = "text"
	 */
	public String getClearDataSql() {
		return clearDataSql;
	}

	/**
	 * @param clearDataSql The clearDataSql to set.
	 */
	public void setClearDataSql(String clearDataSql) {
		this.clearDataSql = clearDataSql;
	}

	
}
