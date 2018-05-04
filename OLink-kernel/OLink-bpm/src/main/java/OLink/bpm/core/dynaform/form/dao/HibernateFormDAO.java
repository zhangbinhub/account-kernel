package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;

//import com.cyberway.utility.Debug;
//import com.cyberway.utility.dao.DAOUtils;
/**
 * @author Marky
 */

public class HibernateFormDAO<E> extends HibernateBaseDAO<E> implements FormDAO<E> {
	/**
	 * HibernateFormDAO构造函数
	 * 
	 * @param voClassName
	 *            值对象类名
	 * @see HibernateBaseDAO#HibernateBaseDAO(String)
	 */
	public HibernateFormDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * 更新表单值对象. 若更新的表单(Form)的表单类型为:普通(NORMAL)或子表单(SUBFORM)时,更新表结构.
	 * 
	 * @param vo
	 *            表单值对象
	 */

	// public void update(ValueObject vo) throws Exception {
	// super.update(vo);
	//
	// Form form = (Form) vo;
	//
	// if (form.getType() == Form.FORM_TYPE_NORMAL
	// || form.getType() == Form.FORM_TYPE_SUBFORM) {
	// // 更新表结构
	// updateDynaTable(form, PersistenceUtils.getDBConnection(), false);
	//
	// }
	//
	// }
	/**
	 * 根据表单名以及应用标识查询,返回相应表单对象.
	 * 
	 * @param formName
	 *            表单名
	 * @param application
	 *            应用标识
	 * @return 表单对象
	 */
	public Form findByFormName(String formName, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.name = '"
				+ formName + "' and vo.applicationid = '" + application + "' ";

		return (Form) getData(hql);
		// ParamsTable params=new ParamsTable();
		// params.setParameter("application",application);
		// Collection list = getDatas(hql, params);
		// if (list != null && !list.isEmpty()) {
		// Iterator iter = list.iterator();
		// return (Form) iter.next();
		// } else {
		// return null;
		// }
	}

	/**
	 * 根据所属模块以及应用标识查询,返回相应表单集合.
	 * 
	 * @param application
	 *            应用标识
	 * @param moduleid
	 *            所属模块主键
	 * @return 表单集合
	 * @throws Exception
	 */
	public Collection<E> getFormsByModule(String moduleid, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='"
				+ moduleid + "' AND vo.type <> 0";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	/**
	 * 根据所属Module以及应用标识查询,返回查询表单集合.
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return Search Form 集合
	 * @throws Exception
	 */
	public Collection<E> getSearchFormsByModule(String moduleid, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='"
				+ moduleid + "'" + " and vo.type=" + Form.FORM_TYPE_SEARCHFORM;
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	/**
	 * 根据所属Module以及应用标识查询,返回查询表单集合.
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return Search Form 集合
	 * @throws Exception
	 */
	public Collection<E> getRelatedFormsByModule(String moduleid,
			String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='"
				+ moduleid + "'" + " and vo.type=" + Form.FORM_TYPE_NORMAL;
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	/**
	 * 根据应用标识查询,返回相应查询表单集合.
	 * 
	 * @param application
	 *            应用标识
	 * @param appid
	 *            应用标识
	 * @return 查询表单集合
	 * @throws Exception
	 */

	public Collection<E> getSearchFormsByApplication(String appid,
			String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.application.id='"
				+ appid + "'" + " and vo.type=" + Form.FORM_TYPE_SEARCHFORM;
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	/**
	 * 根据参数条件以及应用标识查询,返回表单(Form)的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的Form数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * @see ParamsTable#params
	 * 
	 * @param params
	 *            参数表
	 * @application 应用标识
	 * @return 表单的DataPackage
	 */

	public DataPackage<E> queryForm(ParamsTable params, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.type <> 0";
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer
				.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer
				.parseInt(_pagelines) : Integer.MAX_VALUE;

		if (application != null && application.length() > 0) {
			hql += " and vo.applicationid = '" + application + "' ";
		}
		return getDatapackage(hql, params, page, lines);
	}

	/**
	 * 生成动态表，如果表存在更新表结构. 此更新,只能表单Form的类型为普通(NORMAL)或子表单(SUBFORM).
	 * 动态表结构，表结构规则为表名为"TLK_"+表单名,动态表的字列为公共字列+动态字列。动态表动态字列的名为以"ITEM_"为前缀加上表单字段名.
	 * 公共字列为所有生成的动态表都共有的字列。 公共字列为：ID, PARENT, LASTMODIFIED , FORMNAME , OWNER ,
	 * STATE , AUDITDATE , AUTHOR , CREATED , ISSUBDOC , FORMID, ISTMP , FLOWID,
	 * VERSIONS , SORTID , APPLICATIONID , STATEINT , STATELABEL 。
	 * 首先判断表名是否存在,若不存在即按表结构规则为表名命名为"TLK_"+表单名,添加的动态列名为"ITEM_"+字段名生成动态表.
	 * 若对应的表单的表名存在时,判断表是否有对应名字列存在,如果存在时,再判断表单字段类型与相应的数据库动态表的字列类型是否一致,
	 * 若不一致即更新表结构为相一致;否则表没有字列存在时,为数据库表添加的列名为"ITEM_"+字段名的字列.
	 * 
	 * @param vo
	 *            Form 对象
	 * @param conn
	 *            连接数据库对象
	 * @param isOverlaid
	 *            是否叠加
	 * @throws Exception
	 */
	public void updateDynaTable(Form vo, Connection conn, boolean isOverlaid)
			throws Exception {

		String doc_stuc = "ID VARCHAR2(255 CHAR) NOT NULL PRIMARY KEY , PARENT VARCHAR2(255 CHAR), LASTMODIFIED TIMESTAMP (6), FORMNAME VARCHAR2(255 CHAR), OWNER VARCHAR2(255 CHAR), STATE VARCHAR2(255 CHAR), AUDITDATE TIMESTAMP (6), AUTHOR VARCHAR2(255 CHAR), CREATED TIMESTAMP (6), ISSUBDOC NUMBER(1,0), FORMID VARCHAR2(255 CHAR), ISTMP NUMBER(1,0), FLOWID VARCHAR2(255 CHAR), VERSIONS NUMBER(10,0), SORTID VARCHAR2(255 CHAR), APPLICATIONID VARCHAR2(255 CHAR), STATEINT NUMBER(10,0), STATELABEL VARCHAR2(255 CHAR),";
		// Debug.println("FORM TYPE:="+vo.getType());
		if (vo.getType() == Form.FORM_TYPE_NORMAL
				|| vo.getType() == Form.FORM_TYPE_SUBFORM) {
		} else {
			return;
		}

		String tablename = (DQLASTUtil.TBL_PREFIX + vo.getName()).toUpperCase();

		if (tablename.indexOf(".") > 0) {
			return;
		}

		Statement sm = null;
		// PreparedStatement psm = null;
		ResultSet rs = null;
		String sqlstr = "";
		StringBuffer sqlpara = new StringBuffer("");
		boolean tableexist = false;
		try {
			// conn = DAOUtils.getDBConnection();
			sm = conn.createStatement();
			// 1.check table exists or not
			DatabaseMetaData dbmeta = conn.getMetaData();

			String schema = null;
			rs = dbmeta.getTables(null, schema, tablename.toUpperCase(), null);
			if (rs.next()) {
				tableexist = true;
			}
			rs.close();
			// 2.overlaid old table
			if (tableexist && isOverlaid) {
				sm.executeUpdate("drop table " + tablename);
				tableexist = false;
			}
			// 3.create or alter table
			if (!tableexist) {
				// create table
				Iterator<FormField> fields = vo.getFields().iterator();
				while (fields.hasNext()) {
					FormField ff = fields.next();
					String fname = DQLASTUtil.ITEM_FIELD_PREFIX + ff.getName();
					String ftype = ff.getFieldtype();

					if (ftype == null)
						continue;

					if (ftype.equals(Item.VALUE_TYPE_DATE)
							|| ftype.equals(Item.VALUE_TYPE_NUMBER)
							|| ftype.equals(Item.VALUE_TYPE_VARCHAR)
							|| ftype.equals(Item.VALUE_TYPE_TEXT)) {
						sqlpara.append(fname);
						sqlpara.append(getDBFieldType(ftype) + ",");
					}
				}

				if (sqlpara.length() <= 0) {
					return;
				}

				sqlpara.delete(sqlpara.length() - 1, sqlpara.length());
				sqlstr = "CREATE TABLE " + tablename + "(" + doc_stuc
						+ sqlpara.toString() + ")";
				sm.executeUpdate(sqlstr);
			} else {
				// alter table
				// ArrayList FieldsV = new ArrayList();
				Iterator<FormField> fields = vo.getFields().iterator();
				while (fields.hasNext()) {
					FormField ff = fields.next();

					String fname = (DQLASTUtil.ITEM_FIELD_PREFIX + ff.getName()
							.trim()).toUpperCase();
					String ftype = ff.getFieldtype();

					// handle those fields that its type is not VALUE_TYPE_BLOB
					if (ftype != null
							&& (ftype.equals(Item.VALUE_TYPE_DATE)
									|| ftype.equals(Item.VALUE_TYPE_NUMBER)
									|| ftype.equals(Item.VALUE_TYPE_VARCHAR) || ftype
									.equals(Item.VALUE_TYPE_TEXT))) {
						// FieldsV.add(fname);
						rs = dbmeta.getColumns(null, null, tablename
								.toUpperCase(), fname);
						if (rs.next()) {
							// alter column
							String dbtype = rs.getString("DATA_TYPE");
							if (!isSameType(dbtype, ftype)) {
								// check if the table have record
								ResultSet rsCount = sm
										.executeQuery("select count(*) as count from "
												+ tablename
												+ " where  "
												+ fname + " is not null");
								rsCount.next();
								if (rsCount.getInt("count") == 0) {
									sqlpara = new StringBuffer("");
									sqlpara.append(fname + " "
											+ getDBFieldType(ftype) + " ");
									sqlstr = "ALTER TABLE " + tablename
											+ " MODIFY  (" + sqlpara.toString()
											+ ")";
									sm.executeUpdate(sqlstr);
								}
								rsCount.close();
							}
							rs.close();
						} else {
							// add column
							sqlstr = "ALTER TABLE  " + tablename + "  ADD "
									+ fname + getDBFieldType(ftype) + "";

							sm.executeUpdate(sqlstr);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(sm);
		}
	}

	/**
	 * 判断表单字列类型与对应数据库相应动态表的字列类型是否相应. 如果不相应即返回false,否则返回true.
	 * 
	 * @param dbtype
	 *            数据库字列类型
	 * @param selftype
	 *            表单对应字段类型
	 * @return true or false
	 */
	private boolean isSameType(String dbtype, String selftype) {
		boolean same = false;
		if ((dbtype.equalsIgnoreCase(String.valueOf(Types.VARCHAR)))
				&& (selftype.equalsIgnoreCase(Item.VALUE_TYPE_VARCHAR)))
			same = true;
		if ((dbtype.equalsIgnoreCase(String.valueOf(Types.DATE)))
				&& (selftype.equalsIgnoreCase(Item.VALUE_TYPE_DATE)))
			same = true;
		if ((dbtype.equalsIgnoreCase(String.valueOf(Types.CLOB)))
				&& (selftype.equalsIgnoreCase(Item.VALUE_TYPE_TEXT)))
			same = true;
		if ((dbtype.equalsIgnoreCase(String.valueOf(Types.DECIMAL)))
				&& (selftype.equalsIgnoreCase(Item.VALUE_TYPE_NUMBER)))
			same = true;
		return same;
	}

	/**
	 * 根据表单字段类型,返回设置对应数据库动态表的字段类型.
	 * 
	 * @param selftype
	 *            表单字列类型
	 * @return 数据库字列类型字符串
	 */
	private String getDBFieldType(String selftype) {
		String tp = " VARCHAR2 (1000)";
		if (selftype.equalsIgnoreCase(Item.VALUE_TYPE_NUMBER))
			tp = " NUMBER(22,3)";
		if (selftype.equalsIgnoreCase(Item.VALUE_TYPE_DATE))
			tp = " DATE";
		if (selftype.equalsIgnoreCase(Item.VALUE_TYPE_TEXT))
			tp = " CLOB";
		return tp;
	}

	public Form findFormByRelationName(String relationName, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.relationName = '"
				+ relationName + "' ";
		if (!StringUtil.isBlank(application)) {
			hql += " and vo.applicationid = '" + application + "'";
		}

		return (Form) getData(hql);
	}

	public Collection<E> queryTemplateFormsByModule(String moduleid,String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.type=" + Form.FORM_TYPE_TEMPLATEFORM;
		if(!StringUtil.isBlank(moduleid)){
			hql += " and vo.module.id='"+moduleid+"'";
		}
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	public Collection<E> queryNormalFormsByModule(String moduleid,
			String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE 1 =1 ";
		if(!StringUtil.isBlank(moduleid)){
			hql += " and vo.module.id='"+moduleid+"'";
		}
		hql += "and ( vo.type=" + Form.FORM_TYPE_NORMAL +" or vo.type="+Form.FORM_TYPE_NORMAL_MAPPING +") ";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

}
