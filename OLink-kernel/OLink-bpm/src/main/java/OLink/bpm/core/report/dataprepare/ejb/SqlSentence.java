package OLink.bpm.core.report.dataprepare.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class table="T_SQLSENTENCE"
 * 
 */

public class SqlSentence extends ValueObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private  String id;
	
	private String name;
	
	private   String  sentence;
	
	private   DataPrepare dataPrepare;
	
	private   String executeOrder;
	


	/**
	 * @hibernate.property column="EXECUTEORDER" 
	 */
	public String getExecuteOrder() {
		return executeOrder;
	}

	/**
	 * @param executeOrder The executeOrder to set.
	 */
	public void setExecuteOrder(String executeOrder) {
		this.executeOrder = executeOrder;
	}

	/**
	 * @hibernate.property column="SENTENCE" type = "text"
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * @param sentence The sentence to set.
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.many-to-one column="DATAPREPARE_ID"
	 *                        class="DataPrepare"
	 * 
	 */
	public DataPrepare getDataPrepare() {
		return dataPrepare;
	}

	/**
	 * @param dataPrepare The dataPrepare to set.
	 */
	public void setDataPrepare(DataPrepare dataPrepare) {
		this.dataPrepare = dataPrepare;
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
}
