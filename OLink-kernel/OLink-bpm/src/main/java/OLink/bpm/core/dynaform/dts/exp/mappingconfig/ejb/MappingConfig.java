package OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @hibernate.class  table="T_MAPPINGCONFIG"
 */
public class MappingConfig extends ValueObject {
	
	private static final long serialVersionUID = -4491946986232330475L;

	private String id;

	private String valuescript;

	private String name;

	private String description;

	private String tablename;

	private DataSource datasource;

	private Set<ColumnMapping> columnMappings;

	private Date lastRun;

	private Collection<ReportConfig> reports;

	/*
	 * 所属Module
	 */
	private ModuleVO module;

	/**
	 * @return  ModuleVO
	 * @hibernate.many-to-one  class="ModuleVO"  column="MODULE"
	 * @uml.property  name="module"
	 */
	public ModuleVO getModule() {
		return module;
	}

	/**
	 * @param module  the module to set
	 * @uml.property  name="module"
	 */
	public void setModule(ModuleVO module) {
		this.module = module;
	}

	/**
	 * @hibernate.property  column="LASTRUN"
	 * @uml.property  name="lastRun"
	 */
	public Date getLastRun() {
		return lastRun;
	}

	/**
	 * @param lastRun  The lastRun to set.
	 * @uml.property  name="lastRun"
	 */
	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	/**
	 * @hibernate.property  column="DESCRIPTION"
	 * @uml.property  name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description  The description to set.
	 * @uml.property  name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @hibernate.id  column="ID" generator-class="assigned"
	 * @uml.property  name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id  The id to set.
	 * @uml.property  name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property  column="NAME"
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name  The name to set.
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property  column="VALUESCRIPT"
	 * @uml.property  name="valuescript"
	 */
	public String getValuescript() {
		return valuescript;
	}

	/**
	 * @param valuescript  The valuescript to set.
	 * @uml.property  name="valuescript"
	 */
	public void setValuescript(String valuescript) {
		this.valuescript = valuescript;
	}

	/**
	 * @hibernate.set  name="columnMappings" table="T_COLUMNMAPPING"  cascade="delete" inverse="true"
	 * @hibernate.collection-key  column="MAPPINGCONFIG"
	 * @hibernate.collection-one-to-many  class="ColumnMapping"
	 * @return
	 * @uml.property  name="columnMappings"
	 */
	public Set<ColumnMapping> getColumnMappings() {
		return columnMappings;
	}

	/**
	 * @param columnMappings  The columnMappings to set.
	 * @uml.property  name="columnMappings"
	 */
	public void setColumnMappings(Set<ColumnMapping> columnMappings) {
		this.columnMappings = columnMappings;
	}

	/**
	 * @hibernate.property  column="TABLENAME"
	 * @uml.property  name="tablename"
	 */
	public String getTablename() {
		return tablename;
	}

	/**
	 * @param tablename  The tablename to set.
	 * @uml.property  name="tablename"
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	/**
	 * @hibernate.many-to-one  column="DATASOURCE"  class="DataSource"
	 * @uml.property  name="datasource"
	 */
	public DataSource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource  The datasource to set.
	 * @uml.property  name="datasource"
	 */
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return  java.util.Collection
	 * @hibernate.set  name="reports" table="T_REPORT_MAPPING_SET" cascade="none"  inverse="true"
	 * @hibernate.collection-key  column="MAPPINGCONFIGS_ID"
	 * @hibernate.collection-many-to-many  class="ReportConfig"  column="REPORT_ID"
	 * @uml.property  name="reports"
	 */

	public Collection<ReportConfig> getReports() {
		if (reports == null) {
			reports = new HashSet<ReportConfig>();
		}
		return reports;
	}

	/**
	 * @param reports  The reports to set.
	 * @uml.property  name="reports"
	 */
	public void setReports(Collection<ReportConfig> reports) {
		this.reports = reports;
	}
}
