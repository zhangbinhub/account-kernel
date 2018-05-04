package OLink.bpm.core.report.query.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.form.ejb.Form;

/**
 * @hibernate.class table="T_QUERY"
 * 
 */
public class Query extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;
	
	private String queryString;	
	
	private DataSource dataSource;
	
	private Form searchForm;
	
//    private ApplicationVO application;
 	private ModuleVO module;
 	
	private Collection<Parameter> paramters;
 	
	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @hibernate.property column="QUERYSTRING" type="text"
	 */
	public String getQueryString() {
		return queryString;
	}
	/**
	 * @param queryString The queryString to set.
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
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
	 * @hibernate.many-to-one column="FORM_ID"
	 *                        class="Form"
	 */
	public Form getSearchForm() {
		return searchForm;
	}
	/**
	 * @param searchForm The searchForm to set.
	 */
	public void setSearchForm(Form searchForm) {
		this.searchForm = searchForm;
	}
	
	
//	/**
//	 * @return ApplicationVO
//	 * @hibernate.many-to-one class="ApplicationVO"
//	 *                        column="APPLICATION"
//	 */
//	public ApplicationVO getApplication() {
//		return application;
//	}
//
//	public void setApplication(ApplicationVO application) {
//		this.application = application;
//	}

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
	 * @hibernate.collection-one-to-many class="Parameter"
	 * @hibernate.collection-key column="QUERY_ID"
	 * @hibernate.set name="paramters" table="T_QUERY_PARAMETER" 
	 *                 cascade="delete"
	 */
	public Collection<Parameter> getParamters() {
		return paramters;
	}
	/**
	 * @param paramters The paramters to set.
	 */
	public void setParamters(Collection<Parameter> paramters) {
		this.paramters = paramters;
	}

}
