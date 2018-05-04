package OLink.core.protection;

import OLink.bpm.core.dynaform.document.dao.*;
import OLink.bpm.util.*;
import OLink.bpm.util.cache.*;
import com.jamonapi.proxy.MonProxyFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.deploy.application.dao.HibernateApplicationDAO;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcessBean;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.dao.HibernateDomainDAO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainProcessBean;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class WarpProcessFactory
        implements iProcessFactory {
    private transient Logger log = Logger.getLogger(WarpProcessFactory.class);

    private static HashMap<Class<?>, IDesignTimeProcess<?>> _processPool = new HashMap(
            100);

    private static HashMap<Class<?>, IRunTimeProcess<?>> _runtimeProcessPool = new HashMap(
            100);
    private static LicenseKey licenseKey;
    private Enhancer enhancer = new Enhancer();

    static LicenseKey getLicenseKey() {
        if (licenseKey == null) {
            try {
                licenseKey = loadLicense();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return licenseKey;
    }

    public static LicenseKey loadLicense()
            throws Exception {
        return new LicenseKey();
    }

    public IRunTimeProcess createRuntimeProcess(Class<?> iProcessClazz, String applicationid)
            throws CreateProcessException {
        IRunTimeProcess process = null;
        try {
            process = (IRunTimeProcess) _runtimeProcessPool.get(iProcessClazz);
            if (process != null) {
                return process;
            }
            String cn = iProcessClazz.getName();
            cn = cn + "Bean";
            Class clz = Class.forName(cn);

            Constructor constructor = clz.getConstructor(new Class[]{String.class});

            process = (IRunTimeProcess) constructor.newInstance(new Object[]{applicationid});
            if (!(process instanceof DocumentProcess)) return process;
            process = new WarpDocumentProcessBean(applicationid);
        } catch (Exception e) {
            throw new CreateProcessException(e.getMessage());
        }

        return process;
    }

    public IDesignTimeProcess createProcess(Class<?> iProcessClazz)
            throws ClassNotFoundException {
        IDesignTimeProcess process =
                (IDesignTimeProcess) _processPool.get(
                        iProcessClazz);

        if (process != null) {
            return process;
        }

        Class[] iclzs = {iProcessClazz};
        String cn = iProcessClazz.getName();
        cn = cn + "Bean";
        Class clz = Class.forName(cn);
        try {
            WarpProcessFactory factory = new WarpProcessFactory();
            process = (IDesignTimeProcess) MonProxyFactory.monitor(factory.getInstrumentedClass(iclzs, clz));
            if ((process instanceof ApplicationProcess)) {
                process = new WarpApplicationProcessBean();
                _processPool.put(iProcessClazz, process);

                return process;
            }
            if ((process instanceof DomainProcess)) {
                process = new WarpDomainProcessBean();
                _processPool.put(iProcessClazz, process);
                return process;
            }
            if (!(process instanceof DataSourceProcess)) {
                _processPool.put(iProcessClazz, process);
                return process;
            }
            process = new WarpDataSourceProcessBean();
        } catch (Exception e) {
            e.printStackTrace();
        }

        _processPool.put(iProcessClazz, process);

        return process;
    }

    public IDesignTimeProcess<?> createProcess(String clazzName)
            throws ClassNotFoundException {
        Class iProcessClazz = Class.forName(clazzName);

        return createProcess(iProcessClazz);
    }

    public Object getInstrumentedClass(Class<?>[] iclzs, Class<?> clz) {
        this.enhancer.setInterfaces(iclzs);
        this.enhancer.setSuperclass(clz);
        this.enhancer.setCallback(this);
        return this.enhancer.create();
    }

    public Object intercept(Object o, Method method, Object[] methodParameters, MethodProxy methodProxy)
            throws Throwable {
        if (method.getName().equals("finalize")) {
            return methodProxy.invokeSuper(o, methodParameters);
        }

        boolean chechedMethod = false;
        Object result = null;
        try {
            ICacheProvider provider = MyCacheManager.getProviderInstance();
            String cacheName = MyCacheManager.buildCacheKeyString(o.getClass().getSuperclass(), method);

            Class methodClazz = method.getClass();
            if ((!methodClazz.equals(Object.class)) &&
                    (provider != null)) {
                IMyCache cache = provider.getCache(cacheName);
                if (cache != null) {
                    chechedMethod = true;
                    CacheKey cacheKey = new CacheKey(o, method, methodParameters);

                    IMyElement cachedElement = cache.get(cacheKey);

                    if ((cachedElement != null) && (cachedElement.getValue() != null)) {
                        result = cachedElement.getValue();
                    } else {
                        Object[] invokeParameters = (Object[]) ObjectUtil.clone(methodParameters);
                        result = methodProxy.invokeSuper(o, invokeParameters);

                        if ((result instanceof Form)) {
                            result = new WarpForm((Form) result);
                        }

                        cache.put(cacheKey, result);
                    }
                }

            }

            if (!chechedMethod) {
                result = methodProxy.invokeSuper(o, methodParameters);
            }

            if ((provider != null) && (provider.clearByCacheName(cacheName))) {
                PersistenceUtils.currentSession().clear();
                PersistenceUtils.getTableStateMap().clear();
                JavaScriptFactory.clear();
                this.log.info("##CLEAN-CACHE-->>" + cacheName);
            }

            return result;
        } catch (Throwable t) {
            throw t;
        }
    }

    static class WarpApplicationProcessBean extends ApplicationProcessBean {
        int amount = 0;

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        WarpApplicationProcessBean() {
            setAmount(100);
        }

        protected IDesignTimeDAO<ApplicationVO> getDAO()
                throws Exception {
            return super.getDAO();
        }

        public void doCreateOrUpdate(ValueObject vo) throws Exception {
            try {
                if (isLegitimate((ApplicationVO) vo)) {
                    super.doCreateOrUpdate(vo);
                    return;
                }
                throw new WarpException("未找到定义软件");
            } catch (Exception e) {
                throw e;
            }
        }

        private boolean isLegitimate(ApplicationVO vo)
                throws Exception {
            if (vo == null)
                return false;
            if (!vo.isActivated())
                return true;
            String hql = "FROM " + ApplicationVO.class.getName() + " vo WHERE vo.activated = true";
            if (!StringUtil.isBlank(vo.getId())) {
                hql = hql + " and vo.id !='" + vo.getId() + "'";
            }
            int total = ((HibernateApplicationDAO) getDAO()).getTotalLines(hql);

            return total < getAmount();
        }

        public void doCreate(ValueObject vo)
                throws Exception {
            try {
                if (isLegitimate((ApplicationVO) vo)) {
                    super.doCreate(vo);
                    return;
                }
                throw new WarpException("未找到定义软件");
            } catch (Exception e) {
                throw e;
            }
        }

        public void doUpdate(ValueObject vo)
                throws Exception {
            try {
                if (isLegitimate((ApplicationVO) vo)) {
                    super.doUpdate(vo);
                    return;
                }
                throw new WarpException("未找到定义软件");
            } catch (Exception e) {
                throw e;
            }
        }

        public Collection<ApplicationVO> queryByDomain(String domainId)
                throws Exception {
            Collection rtn = super.queryByDomain(domainId);
            if ((rtn != null) && (rtn.size() > getAmount())) {
                Collection temp = new ArrayList();
                int index = 0;
                for (Iterator iter = rtn.iterator(); iter.hasNext(); index++) {
                    if (index >= getAmount()) break;
                    temp.add((ApplicationVO) iter.next());
                }
                return temp;
            }
            return rtn;
        }
    }

    static class WarpDataSourceProcessBean extends DataSourceProcessBean {
        Collection<String> allowDbType = null;

        WarpDataSourceProcessBean() {
            this.allowDbType = getDBType(WarpProcessFactory.getLicenseKey().getType().charAt(0));
        }

        public static ArrayList<String> getDBType(char key) {
            ArrayList allowDbType = new ArrayList();
            allowDbType.add("ORACLE");
            allowDbType.add("MSSQL");
            allowDbType.add("DB2");
            allowDbType.add("MYSQL");
            allowDbType.add("HSQLDB");
            return allowDbType;
        }

        Collection<String> getAllowDbType() {
            return this.allowDbType;
        }

        public void doCreate(ValueObject vo) throws Exception {
            try {
                if (isLegitimate((DataSource) vo)) {
                    super.doCreate(vo);
                    return;
                }
                throw new WarpException("未找到定义数据源");
            } catch (Exception e) {
                throw e;
            }
        }

        public void doCreateOrUpdate(ValueObject vo)
                throws Exception {
            try {
                if (isLegitimate((DataSource) vo)) {
                    super.doCreateOrUpdate(vo);
                    return;
                }
                throw new WarpException("未找到定义数据源");
            } catch (Exception e) {
                throw e;
            }
        }

        public void doUpdate(ValueObject vo, WebUser user) throws Exception {
            try {
                if (isLegitimate((DataSource) vo)) {
                    super.doUpdate(vo, user);
                    return;
                }
                throw new WarpException("未找到定义数据源");
            } catch (Exception e) {
                throw e;
            }
        }

        public void doUpdate(ValueObject vo) throws Exception {
            try {
                if (isLegitimate((DataSource) vo)) {
                    super.doUpdate(vo);
                    return;
                }
                throw new WarpException("未找到定义数据源");
            } catch (Exception e) {
                throw e;
            }
        }

        private boolean isLegitimate(DataSource vo)
                throws Exception {
            if (vo == null) {
                return false;
            }
            return getAllowDbType().contains(DataSource.dbType2NameMap.get(Integer.valueOf(vo.getDbType())));
        }
    }

    static class WarpDocumentProcessBean extends DocumentProcessBean {
        Collection<String> allowDbType = null;

        public WarpDocumentProcessBean(String applicationId) {
            super(applicationId);
            this.allowDbType = getDBTypeName(WarpProcessFactory.getLicenseKey().getType().charAt(0));
        }

        Collection<String> getAllowDbType() {
            return this.allowDbType;
        }

        protected IRuntimeDAO getDAO() throws Exception {
            IRuntimeDAO dao = super.getDAO();
            try {
                if (isLegitimate(dao)) return dao;
                throw new WarpException("此版本不支持定义数据库类型");
            } catch (Exception e) {
                throw e;
            }
        }

        private boolean isLegitimate(IRuntimeDAO dao) throws Exception {
            if (dao == null) {
                return false;
            }
            return getAllowDbType().contains(dao.getClass().getName());
        }

        public static ArrayList<String> getDBTypeName(char key) {
            ArrayList allowDbType = new ArrayList();
            allowDbType = new ArrayList();
            allowDbType.add(OracleDocStaticTblDAO.class.getName());
            allowDbType.add(MssqlDocStaticTblDAO.class.getName());
            allowDbType.add(MysqlDocStaticTblDAO.class.getName());
            allowDbType.add(HsqldbDocStaticTblDAO.class.getName());
            allowDbType.add(DB2DocStaticTblDAO.class.getName());
            return allowDbType;
        }
    }

    static class WarpDomainProcessBean extends DomainProcessBean {
        int amount = 0;

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        WarpDomainProcessBean() {
            setAmount(100);
        }

        public void doCreate(ValueObject vo)
                throws Exception {
            try {
                if (isLegitimate((DomainVO) vo)) {
                    super.doCreate(vo);
                    return;
                }
                throw new WarpException("输入的企业域不正确");
            } catch (Exception e) {
                throw e;
            }
        }

        public void doUpdate(ValueObject vo)
                throws Exception {
            try {
                if (isLegitimate((DomainVO) vo)) {
                    super.doUpdate(vo);
                    return;
                }
                throw new WarpException("输入的企业域不正确");
            } catch (Exception e) {
                throw e;
            }
        }

        private boolean isLegitimate(DomainVO vo)
                throws Exception {
            if (vo == null)
                return false;
            if (vo.getStatus() == 0)
                return true;
            String hql = "FROM " + DomainVO.class.getName() + " vo WHERE vo.status = 1";
            if (!StringUtil.isBlank(vo.getId())) {
                hql = hql + " and vo.id !='" + vo.getId() + "'";
            }
            int total = ((HibernateDomainDAO) getDAO()).getTotalLines(hql);

            return total < getAmount();
        }

        public DomainVO getDomainByDomainName(String name)
                throws Exception {
            DomainVO vo = super.getDomainByDomainName(name);
            if (!isLegitimate(vo)) {
                throw new WarpException("输入的企业域不正确");
            }
            return vo;
        }
    }

    static class WarpForm extends Form {
        public WarpForm(Form source) {
            try {
                super.setId(source.getId());
                super.setName(source.getName());
                super.setApplicationid(source.getApplicationid());
                super.setSortId(source.getSortId());
                super.setSubFormMap(source.getSubFormMap());
                super.setDiscription(source.getDiscription());
                super.setType(source.getType());
                super.setModule(source.getModule());
                super.setShowLog(source.isShowLog());

                super.setLastmodifier(source.getLastmodifier());
                super.setLastmodifytime(source.getLastmodifytime());
                super.setStyle(source.getStyle());

                super.setActivityXML(source.getActivityXML());

                super.setVersion(source.getVersion());
                super.setIsopenablescript(source.getIsopenablescript());
                super.setIseditablescript(source.getIseditablescript());

                super.setRelationName(source.getRelationName());
                super.setRelationText(source.getRelationText());

                super.setOnSaveStartFlow(source.isOnSaveStartFlow());

                super.setMappingStr(source.getMappingStr());

                super.setDocumentSummaryXML(source.getDocumentSummaryXML());
                super.setSummaryCfg(source.getSummaryCfg());
                super.setCheckout(source.isCheckout());
                super.setCheckoutHandler(source.getCheckoutHandler());
                super.setTemplatecontext(source.getTemplatecontext());
            } catch (Exception e) {
                System.out.println("@@@@" + e);
            }
        }

        public String toHtml(Document doc, ParamsTable params, WebUser user, Collection<ValidateMessage> errors) throws Exception {
            String html = super.toHtml(doc, params, user, errors);
            html = html + WarpProcessFactory.getLicenseKey().toCopyright();
            return html;
        }
    }

    static class WarpFormBeanInfo {
    }
}