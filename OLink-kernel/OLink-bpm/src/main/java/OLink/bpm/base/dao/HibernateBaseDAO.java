package OLink.bpm.base.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * The base hibernate base dao.
 */
public class HibernateBaseDAO<E> implements IDesignTimeDAO<E> {

    private static Log log = LogFactory.getLog(HibernateBaseDAO.class);

    /**
     * @uml.property name="sessionFactory"
     */
    private static SessionFactory sessionFactory;

    public String _voClazzName;

    private static String defaultSchema;

    // public static String dialect;

    public HibernateBaseDAO(String valueObjectName) {
        this._voClazzName = valueObjectName;
    }

    public HibernateBaseDAO() {
    }

    /**
     * Return the session factory
     *
     * @return The session factory.
     * @throws Exception
     * @uml.property name="sessionFactory"
     */
    public static SessionFactory getSessionFactory() throws Exception {
        if (sessionFactory == null) {
            Configuration cfg = new Configuration().configure();
//			String[] jndis = ResourcePool.GetConfigInfoAttr(
//					ResourcePool.getRootpath() + "config/system.xml",
//					"RESOURCE", "DS", "jndi");
//			String[] dialects = ResourcePool.GetConfigInfoAttr(
//					ResourcePool.getRootpath() + "config/system.xml",
//					"RESOURCE", "DS", "hibernate.dialect");
//			cfg.setProperty("hibernate.connection.datasource", jndis[0]);
//			cfg.setProperty("hibernate.dialect", dialects[0]);
            sessionFactory = cfg.buildSessionFactory();
            defaultSchema = cfg.getProperty("hibernate.default_schema");
        }
        return sessionFactory;
    }

    /**
     * Gets object datas
     *
     * @param hql the HQL statement
     * @return object
     * @see IDesignTimeDAO#getData(String)
     */
    @SuppressWarnings("unchecked")
    public Object getData(String hql) throws Exception {
        Session session = currentSession();

        Query query = session.createQuery(hql);
        query.setFirstResult(0);
        query.setFetchSize(1);

        // session.setFlushMode(arg0)
        // query.setReadOnly(true);
        List<E> list = query.list();
        return (list.size() > 0) ? list.get(0) : null;
    }

    /**
     * Get datas collection .
     *
     * @param hql the HQL statement.
     * @return collection
     * @see IDesignTimeDAO#getDatas(String)
     */
    public Collection<E> getDatas(String hql) throws Exception {
        return getDatas(hql, 1, Integer.MAX_VALUE);
    }

    /**
     * Get datas collection .
     *
     * @param hql    the HQL statement.
     * @param params ParamsTable
     * @return Collection
     * @see IDesignTimeDAO#getDatas(String,
     * Object)
     */
    public Collection<E> getDatas(String hql, ParamsTable params) throws Exception {
        return getDatas(hql, params, 1, Integer.MAX_VALUE);
    }

    /**
     * Get datas collection.
     *
     * @param hql    the HQL statement
     * @param params ParamsTable
     * @param page   the page number
     * @param lines  Each page shows the number of the data
     * @return collection Get datas collection
     * @see IDesignTimeDAO#getDatas(String,
     * Object, int, int)
     */
    public Collection<E> getDatas(String hql, ParamsTable params, int page, int lines) throws Exception {
        // There is no detail syntax checking here, it may has some potential
        // issue here.
        HibernateSQLUtils sqlUtil = new HibernateSQLUtils();

        String whereClause = sqlUtil.createWhere(_voClazzName, params);
        if (whereClause != null && whereClause.trim().length() > 0) {
            int p = hql.toLowerCase().indexOf(" where ");

            hql = (p >= 0) ? hql.substring(0, p) + " where " + whereClause + " and " + hql.substring(p + 7) : hql
                    + " where " + whereClause;
        }

        if (params != null) {
            String application = params.getParameterAsString("application"); // 加入applicationid作为查询条件
            if (!StringUtil.isBlank(application)) {
                String appCondition = "applicationid='" + application + "'";
                hql = sqlUtil.appendCondition(hql, appCondition);
            }

            String domain = params.getParameterAsString("domain"); // 加入domainid作为查询条件
            if (!StringUtil.isBlank(domain)) {
                String domainCondition = "domainid='" + domain + "'";
                hql = sqlUtil.appendCondition(hql, domainCondition);
            }
        }

        String orderBy = sqlUtil.createOrderBy(_voClazzName, params);
        if (orderBy != null && orderBy.trim().length() > 0) {
            int p = hql.toLowerCase().indexOf(" order by ");

            hql = (p >= 0) ? hql.substring(0, p + 10) + orderBy + ", " + hql.substring(p + 10) : hql + " order by "
                    + orderBy;
        }

        return getDatas(hql, page, lines);
    }

    /**
     * Get TotalLines(Hibernate版本不支持泛型)
     *
     * @param hql the HQL statement
     * @return int
     * @see IDesignTimeDAO#getTotalLines(String)
     */
    @SuppressWarnings("unchecked")
    public int getTotalLines(String hql) throws Exception {
        Session session = currentSession();

        Long amount = Long.valueOf(0);
        int from = hql.toLowerCase().indexOf("from");
        int order = hql.toLowerCase().indexOf("order by");

        String newhql = (order > 0) ? "select count(*) as row_count " + hql.substring(from, order)
                : "select count(*) as row_count " + hql.substring(from);

        Query query = session.createQuery(newhql);

        List<E> rst = query.list();

        if (!rst.isEmpty())
            amount = (Long) rst.get(0);
        else
            return 0;

        return amount.intValue();
    }

    public int getTotalLinesBySQL(String sql) throws Exception {
        Session session = currentSession();

        int from = sql.toLowerCase().indexOf("from");
        int order = sql.toLowerCase().indexOf("order by");

        String newhql = (order > 0) ? "select count(*) " + sql.substring(from, order) : "select count(*) "
                + sql.substring(from);

        SQLQuery query = session.createSQLQuery(newhql);
        Number number = (Number) query.uniqueResult();

        return number.intValue();
    }

    /**
     * Get datas collection.(Hibernate版本不支持泛型)
     *
     * @param hql   the HQL statement
     * @param page  the page number
     * @param lines Each page shows the number of the data
     * @return Collection Get datas collection.
     * @see IDesignTimeDAO#getDatas(String, int,
     * int)
     */
    @SuppressWarnings("unchecked")
    public Collection<E> getDatas(String hql, int page, int lines) throws Exception {
        Session session = currentSession();
        // session.flush();

        Query query = session.createQuery(hql);

        query.setFirstResult((page - 1) * lines);
        query.setMaxResults(lines);

        Collection<E> rtn = query.list();

        return rtn;

    }

    public Collection<E> getDatasBySQL(String sql) throws Exception {
        return getDatasBySQL(sql, 1, Integer.MAX_VALUE);
    }

    /**
     * Hibernate版本不支持泛型
     */
    @SuppressWarnings("unchecked")
    public Collection<E> getDatasBySQL(String sql, int page, int lines) throws Exception {
        Session session = currentSession();
        // session.flush();

        SQLQuery query = session.createSQLQuery(sql);
        query.addEntity(Class.forName(this._voClazzName));

        query.setFirstResult((page - 1) * lines);
        query.setMaxResults(lines);

        Collection<E> rtn = query.list();

        return rtn;

    }

    /**
     * Get datas Package.
     *
     * @param hql the HQL statement
     * @return DataPackage Get the datapackage.
     * @see IDesignTimeDAO#getDatapackage(String)
     */
    public DataPackage<E> getDatapackage(String hql) throws Exception {
        return getDatapackage(hql, 1, Integer.MAX_VALUE);
    }

    /**
     * Get the datapackage
     *
     * @param hql
     * @param page
     * @param lines
     * @return dataPackape
     * @see IDesignTimeDAO#getDatapackage(String,
     * int, int)
     */
    public DataPackage<E> getDatapackage(String hql, int page, int lines) throws Exception {
        DataPackage<E> result = new DataPackage<E>();

        result.rowCount = getTotalLines(hql);
        result.pageNo = page;
        result.linesPerPage = lines;
        if (result.pageNo > result.getPageCount()) {
            result.pageNo = 1;
            page = 1;
        }
        result.datas = getDatas(hql, page, lines);

        return result;
    }

    /**
     * @see IDesignTimeDAO#getDatapackage(String,
     * Object)
     */
    public DataPackage<E> getDatapackage(String hql, ParamsTable params) throws Exception {
        // return getDatapackage(hql, params);
        return getDatapackage(hql, params, 1, Integer.MAX_VALUE);
    }

    /**
     * Get the datapackage
     *
     * @param hql
     * @param params Object
     * @param page   int
     * @param lines  int
     * @return datapackage
     * @see IDesignTimeDAO#getDatapackage(String,
     * Object, int, int)
     */
    public DataPackage<E> getDatapackage(String hql, ParamsTable params, int page, int lines) throws Exception {
        // There is no detail syntax checking here, it may has some potential
        // issue here.
        HibernateSQLUtils sqlUtil = new HibernateSQLUtils();

        String whereClause = sqlUtil.createWhere(_voClazzName, params);
        if (whereClause != null && whereClause.trim().length() > 0) {
            int p = hql.toLowerCase().indexOf(" where ");

            hql = (p >= 0) ? hql = hql.substring(0, p) + " where " + whereClause + " and " + hql.substring(p + 7) : hql
                    + " where " + whereClause;
        }

        if (params != null) {
            String application = (String) params // 根据instace
                    // 查询
                    .getParameter("application");
            if (application != null && application.trim().length() > 0) {
                if (hql.toLowerCase().indexOf(" where ") != -1) {
                    hql += " and applicationid='" + application + "'";
                } else {
                    hql += " where applicationid='" + application + "'";
                }
            }
        }

        String orderBy = sqlUtil.createOrderBy(_voClazzName, params);
        if (orderBy != null && orderBy.trim().length() > 0) {
            int p = hql.toLowerCase().indexOf(" order by ");

            hql = (p >= 0) ? hql.substring(0, p + 10) + orderBy + ", " + hql.substring(p + 10) : hql + " order by "
                    + orderBy;
        }

        DataPackage<E> result = new DataPackage<E>();
        result.rowCount = getTotalLines(hql);
        result.pageNo = page;
        result.linesPerPage = lines;

        if (result.pageNo > result.getPageCount()) {
            result.pageNo = 1;
            page = 1;
        }

        result.datas = getDatas(hql, page, lines);
        return result;
    }

    /**
     * 根据SQL查询
     *
     * @param sql
     * @param params
     * @param page
     * @param lines
     * @return
     * @throws Exception
     */
    public DataPackage<E> getDatapackageBySQL(String sql, ParamsTable params, int page, int lines) throws Exception {
        // There is no detail syntax checking here, it may has some potential
        // issue here.
        HibernateSQLUtils sqlUtil = new HibernateSQLUtils();

        String whereClause = sqlUtil.createWhere(params);
        if (whereClause != null && whereClause.trim().length() > 0) {
            int p = sql.toLowerCase().indexOf(" where ");

            sql = (p >= 0) ? sql = sql.substring(0, p) + " where " + whereClause + " and " + sql.substring(p + 7) : sql
                    + " where " + whereClause;
        }

        if (params != null) {
            String application = (String) params // 根据instace
                    // 查询
                    .getParameter("application");
            if (application != null && application.trim().length() > 0) {
                if (sql.toLowerCase().indexOf(" where ") != -1) {
                    sql += " and applicationid='" + application + "'";
                } else {
                    sql += " where applicationid='" + application + "'";
                }
            }
        }

        String orderBy = sqlUtil.createOrderBy(params);
        if (orderBy != null && orderBy.trim().length() > 0) {
            int p = sql.toLowerCase().indexOf(" order by ");

            sql = (p >= 0) ? sql.substring(0, p + 10) + orderBy + ", " + sql.substring(p + 10) : sql + " order by "
                    + orderBy;
        }

        DataPackage<E> result = new DataPackage<E>();
        result.rowCount = getTotalLinesBySQL(sql);
        result.pageNo = page;
        result.linesPerPage = lines;

        if (result.pageNo > result.getPageCount()) {
            result.pageNo = 1;
            page = 1;
        }

        result.datas = getDatasBySQL(sql, page, lines);
        return result;
    }

    /**
     * Execute the sql statement.
     *
     * @param hql The sql statement.
     * @throws Exception
     */
    protected void execHQL(String hql) throws Exception {
        Session session = currentSession();
        session.createQuery(hql).executeUpdate();
    }

    /**
     * @see IDesignTimeDAO#create(ValueObject,
     * WebUser)
     */
    public void create(ValueObject vo, WebUser user) throws Exception {
        Session session = currentSession();
        session.save(vo);
    }

    /**
     * create the value object.
     *
     * @see IDesignTimeDAO#create(ValueObject)
     */
    public void create(ValueObject vo) throws Exception {
        try {
            Session session = currentSession();
            session.save(vo);
        } catch (Exception ex) {
            throw ex;
            // ex.printStackTrace();
        }
    }

    /**
     * create the value object.
     *
     * @see IDesignTimeDAO#create(Object)
     */
    public void create(Object po) throws Exception {
        Session session = currentSession();
        session.save(po);
    }

    /**
     * Remove the value object by primary key
     *
     * @throws Exception
     * @see IDesignTimeDAO#remove(String)
     */
    public void remove(String id) throws Exception {
        Session session = currentSession();
        ValueObject vo = find(id);

        if (vo != null)
            session.delete(vo);
    }

    public void remove(String ids[]) throws Exception {
        String hql = "Delete " + _voClazzName;
        StringBuffer idsbuffer = new StringBuffer();
        if (ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                idsbuffer.append("'").append(ids[i]).append("',");
            }
            idsbuffer.deleteCharAt(idsbuffer.lastIndexOf(","));
        }
        if (ids.length > 1) {
            hql += " where id in (" + idsbuffer + ")";
        } else {
            hql += " where id = " + idsbuffer;
        }
        executeUpdate(hql);
    }

    public void remove(Collection<E> vos) throws Exception {
        String hql = "Delete " + _voClazzName;
        StringBuffer idsbuffer = new StringBuffer();
        if (vos.size() > 0) {
            for (Iterator<E> iterator = vos.iterator(); iterator.hasNext(); ) {
                ValueObject vo = (ValueObject) iterator.next();
                idsbuffer.append("'").append(vo.getId()).append("',");
            }
            idsbuffer.deleteCharAt(idsbuffer.lastIndexOf(","));
        }

        if (vos.size() > 1) {
            hql += " where id in (" + idsbuffer + ")";
        } else {
            hql += " where id = " + idsbuffer;
        }
        executeUpdate(hql);
    }

    /**
     * Update the value object by web user
     *
     * @param vo   the value object
     * @param user the webuser
     * @see IDesignTimeDAO#update(ValueObject,
     * WebUser)
     */
    public void update(ValueObject vo, WebUser user) throws Exception {
        Session session = currentSession();
        session.merge(vo);
    }

    /**
     * Update the value object
     *
     * @param vo the value object
     * @see IDesignTimeDAO#update(ValueObject)
     */
    public void update(ValueObject vo) throws Exception {
        Session session = currentSession();
        session.merge(vo);
        // session.saveOrUpdate(vo);
    }

    /**
     * @see IDesignTimeDAO#update(Object)
     */
    public void update(Object po) throws Exception {
        Session session = currentSession();
        session.merge(po);
    }

    /**
     * find the value object by primary key
     *
     * @param id primary key
     * @see IDesignTimeDAO#find(String)
     */
    @SuppressWarnings("unchecked")
    public ValueObject find(String id) throws Exception {
        Session session = currentSession();
        ValueObject rtn = null;
        if (id != null && id.length() > 0) {
            rtn = (ValueObject) session.get(Class.forName(_voClazzName), id);

            if (rtn == null || rtn.getId() == null) {
                String hql = "FROM " + _voClazzName + " WHERE id='" + id + "'";

                Query query = session.createQuery(hql);

                query.setFirstResult(0);
                query.setMaxResults(1);

                List<E> result = query.list();

                if (!result.isEmpty()) {
                    rtn = (ValueObject) result.get(0);
                    session.load(rtn, rtn.getId());
                }

            }

        }
        return rtn;

    }

    @SuppressWarnings("unchecked")
    public ValueObject findByName(String name, String application) throws Exception {
        Session session = currentSession();
        ValueObject rtn = null;
        if (name != null && name.length() > 0) {
            rtn = (ValueObject) session.get(Class.forName(_voClazzName), name);
            String hql = "FROM " + _voClazzName + " WHERE name='" + name + "' and applicationid='" + application + "'";
            Query query = session.createQuery(hql);
            query.setFirstResult(0);
            query.setMaxResults(1);

            List<E> result = query.list();

            if (!result.isEmpty()) {
                rtn = (ValueObject) result.get(0);
            }
        }
        return rtn;
    }

    /**
     * Get the DataPackage
     *
     * @param params ParamsTable
     * @see IDesignTimeDAO#query(ParamsTable)
     */
    public DataPackage<E> query(ParamsTable params) throws Exception {
        String hql = "from " + _voClazzName;
        String _currpage = params.getParameterAsString("_currpage");
        String _pagelines = params.getParameterAsString("_pagelines");

        int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
        int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

        return getDatapackage(hql, params, page, lines);
    }

    /**
     * query datapackage
     *
     * @param params ParamsTable
     * @param user   WebUser
     * @return datapackage
     */
    public DataPackage<E> query(ParamsTable params, WebUser user) throws Exception {
        String hql = "from " + _voClazzName;
        String _currpage = params.getParameterAsString("_currpage");
        String _pagelines = params.getParameterAsString("_pagelines");

        int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
        int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

        return getDatapackage(hql, params, page, lines);
    }

    public SessionFactory buildSessionFactory() throws Exception {
        Configuration cfg = new Configuration().configure();
        SessionFactory factory = cfg.buildSessionFactory();
        return factory;
    }

    /**
     * @see IDesignTimeDAO#simpleQuery(ParamsTable)
     */
    public Collection<E> simpleQuery(ParamsTable params) throws Exception {
        String hql = "from " + _voClazzName;
        return getDatas(hql, params);
    }

    /**
     * Get the current session.
     *
     * @return The current session.
     * @throws Exception
     */
    protected static Session currentSession() throws Exception {
        SessionSignal signal = PersistenceUtils.getSessionSignal();

        Session s = signal.currentSession;

        if (s == null || !s.isOpen()) {
            s = getSessionFactory().openSession();
            log.debug("Opening new Session for this thread:" + s);
            signal.currentSession = s;
        } else {
            log.debug("Session was existed:" + s);
        }

        return s;
    }

    /**
     * Close the session.
     *
     * @throws Exception
     */
    static void closeSession() throws Exception {
        SessionSignal signal = PersistenceUtils.getSessionSignal();

        Session s = signal.currentSession;

        if (s != null && s.isOpen()) {
            s.close();
            signal.currentSession = null;
            signal.currentTransaction = null;
            // signal.sessionSignal = 0;
            signal = null;
        }
    }

    /**
     * Open and begin the transcation
     *
     * @throws Exception
     */
    static void beginTransaction() throws Exception {
        SessionSignal signal = PersistenceUtils.getSessionSignal();

        Transaction tx = signal.currentTransaction;
        signal.transactionSignal++;

        if (tx == null) {
            tx = currentSession().beginTransaction();
            log.debug("Starting new database transaction in this thread:" + tx);
            signal.currentTransaction = tx;
        } else {
            log.debug("transaction was existed:" + tx);
        }
    }

    /**
     * Commit the transaction
     *
     * @throws Exception
     */
    static void commitTransaction() throws Exception {
        SessionSignal signal = PersistenceUtils.getSessionSignal();
        Transaction tx = signal.currentTransaction;
        signal.transactionSignal--;
        try {
            if (signal.transactionSignal <= 0) {
                if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                    log.debug("Commit database transaction of this thread.");
                    tx.commit();
                    signal.currentTransaction = null;
                    signal.transactionSignal = 0;
                }
            }
        } catch (Exception ex) {
            rollbackTransaction();
            throw ex;
        }
    }

    /**
     * Roll back the transaction.
     *
     * @throws Exception
     */
    static void rollbackTransaction() throws Exception {
        SessionSignal signal = PersistenceUtils.getSessionSignal();
        Transaction tx = signal.currentTransaction;

        signal.currentTransaction = null;
        signal.transactionSignal = 0;

        if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
            log.debug("Try to rollback database transaction of this thread.");
            tx.rollback();
        }
    }

    /**
     * Remove the value object
     *
     * @param obj the value object
     */
    public void remove(ValueObject obj) throws Exception {
        Session session = currentSession();
        session.delete(obj);
    }

    /**
     * @param hql
     * @return
     * @throws Exception
     */
    public int executeUpdate(String hql) throws Exception {
        Session session = currentSession();
        return session.createQuery(hql).executeUpdate();
    }

    public String getSchema() {
        return StringUtil.isBlank(defaultSchema) ? "" : defaultSchema + ".";
    }

    public Collection<E> queryByHQL(String hql, int pageNo, int pageSize) throws Exception {
        return this.getDatas(hql, pageNo, pageSize);
    }

    public void checkout(String id, WebUser user) throws Exception {
        if (!StringUtil.isBlank(id)) {
            String hql = "update " + _voClazzName + " set checkout = true , checkoutHandler = '" + user.getId() + "' where id = '" + id + "'";
            executeUpdate(hql);
        }
    }

    public void checkin(String id, WebUser user) throws Exception {
        if (!StringUtil.isBlank(id)) {
            String hql = "update " + _voClazzName + " set checkout = false , checkoutHandler = '' where id = '" + id + "'";
            executeUpdate(hql);
        }
    }

}
