package OLink.bpm.core.sso.cas.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.CleanUpTimerTask;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.ProxyListEditor;
import org.jasig.cas.client.validation.TicketValidator;

import OLink.bpm.util.http.UrlUtil;
import OLink.bpm.util.property.PropertyUtil;

public class OBPMCas20ProxyReceivingTicketValidationFilter extends AbstractTicketValidationFilter{
	private static final String[] RESERVED_INIT_PARAMS = new String[] {"proxyReceptorUrl", "acceptAnyProxy", "allowedProxyChains", "casServerUrlPrefix", "proxyCallbackUrl", "renew", "exceptionOnValidationFailure", "redirectAfterValidation", "useSession", "serverName", "service", "artifactParameterName", "serviceParameterName", "encodeServiceUrl", "millisBetweenCleanUps"};

    private static final int DEFAULT_MILLIS_BETWEEN_CLEANUPS = 60 * 1000;

    /**
     * The URL to send to the CAS server as the URL that will process proxying requests on the CAS client. 
     */
    private String proxyReceptorUrl;

    private Timer timer;

    private TimerTask timerTask;

    private int millisBetweenCleanUps;
    
    /**
     * Storage location of ProxyGrantingTickets and Proxy Ticket IOUs.
     */
    private ProxyGrantingTicketStorage proxyGrantingTicketStorage = new ProxyGrantingTicketStorageImpl();

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
    	setExceptionOnValidationFailure(true);
        log.trace("Setting exceptionOnValidationFailure parameter: " + true);
        
        String loginUrl = PropertyUtil.get("cas.server.login.url");
        // 从sso配置文件中获取参数
        setServerName(UrlUtil.getUrlPrefix(loginUrl));
        log.trace("Loading serverName property: " + UrlUtil.getUrlPrefix(loginUrl));
        setRedirectAfterValidation(parseBoolean(PropertyUtil.get("cas.redirect.after.validation", "true")));
        log.trace("Setting redirectAfterValidation parameter: " + PropertyUtil.get("cas.redirect.after.validation", "true"));
        setUseSession(parseBoolean(PropertyUtil.get("cas.use.session", "true")));
        log.trace("Setting useSession parameter: " + PropertyUtil.get("cas.use.session", "true"));
        setTicketValidator(getTicketValidator(filterConfig));
        
        setProxyReceptorUrl(getPropertyFromInitParams(filterConfig, "proxyReceptorUrl", null));

        final String proxyGrantingTicketStorageClass = getPropertyFromInitParams(filterConfig, "proxyGrantingTicketStorageClass", null);

        if (proxyGrantingTicketStorageClass != null) {
            try {
                final Class<?> storageClass = Class.forName(proxyGrantingTicketStorageClass);
                this.proxyGrantingTicketStorage = (ProxyGrantingTicketStorage) storageClass.newInstance();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.trace("Setting proxyReceptorUrl parameter: " + this.proxyReceptorUrl);
        this.millisBetweenCleanUps = Integer.parseInt(getPropertyFromInitParams(filterConfig, "millisBetweenCleanUps", Integer.toString(DEFAULT_MILLIS_BETWEEN_CLEANUPS)));
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.proxyGrantingTicketStorage, "proxyGrantingTicketStorage cannot be null.");

        if (this.timer == null) {
            this.timer = new Timer(true);
        }

        if (this.timerTask == null) {
            this.timerTask = new CleanUpTimerTask(this.proxyGrantingTicketStorage);
        }
        this.timer.schedule(this.timerTask, this.millisBetweenCleanUps, this.millisBetweenCleanUps);
    }

    /**
     * Constructs a Cas20ServiceTicketValidator or a Cas20ProxyTicketValidator based on supplied parameters.
     *
     * @param filterConfig the Filter Configuration object.
     * @return a fully constructed TicketValidator.
     */
    @SuppressWarnings("unchecked")
	protected final TicketValidator getTicketValidator(final FilterConfig filterConfig) {
    	// 从sso配置文件中获取参数
        final String allowAnyProxy = getPropertyFromInitParams(filterConfig, "acceptAnyProxy", null);
        final String allowedProxyChains = getPropertyFromInitParams(filterConfig, "allowedProxyChains", null);
        final String casServerUrlPrefix =  PropertyUtil.get("cas.server.url.prefix", null);
       
        
        final Cas20ServiceTicketValidator validator;

        if (CommonUtils.isNotBlank(allowAnyProxy) || CommonUtils.isNotBlank(allowedProxyChains)) {
            final Cas20ProxyTicketValidator v = new Cas20ProxyTicketValidator(casServerUrlPrefix);
            v.setAcceptAnyProxy(parseBoolean(allowAnyProxy));
            v.setAllowedProxyChains(createProxyList(allowedProxyChains));
            validator = v;
        } else {
            validator = new Cas20ServiceTicketValidator(casServerUrlPrefix);
        }
        validator.setProxyCallbackUrl(getPropertyFromInitParams(filterConfig, "proxyCallbackUrl", null));
        validator.setProxyGrantingTicketStorage(proxyGrantingTicketStorage);
        validator.setProxyRetriever(new Cas20ProxyRetriever(casServerUrlPrefix, "UTF-8"));
        validator.setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));

        final Map<String, String> additionalParameters = new HashMap<String, String>();
        final List<String> params = Arrays.asList(RESERVED_INIT_PARAMS);

        for (final Enumeration<String> e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
            final String s = e.nextElement();

            if (!params.contains(s)) {
                additionalParameters.put(s, filterConfig.getInitParameter(s));
            }
        }

        validator.setCustomParameters(additionalParameters);

        return validator;
    }

    protected final ProxyList createProxyList(final String proxies) {
        if (CommonUtils.isBlank(proxies)) {
            return new ProxyList();
        }

        final ProxyListEditor editor = new ProxyListEditor();
        editor.setAsText(proxies);
        return (ProxyList) editor.getValue();
     }

    public void destroy() {
        super.destroy();
        this.timer.cancel();
    }

    /**
     * This processes the ProxyReceptor request before the ticket validation code executes.
     */
    protected final boolean preFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String requestUri = request.getRequestURI();

        if (CommonUtils.isEmpty(this.proxyReceptorUrl) || !requestUri.endsWith(this.proxyReceptorUrl)) {
            return true;
        }

        CommonUtils.readAndRespondToProxyReceptorRequest(request, response, proxyGrantingTicketStorage);

        return false;
    }

    public final void setProxyReceptorUrl(final String proxyReceptorUrl) {
        this.proxyReceptorUrl = proxyReceptorUrl;
    }

    public void setProxyGrantingTicketStorage(final ProxyGrantingTicketStorage storage) {
        proxyGrantingTicketStorage = storage;
    }

    public void setTimer(final Timer timer) {
        this.timer = timer;
    }

    public void setTimerTask(final TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public void setMillisBetweenCleanUps(final int millisBetweenCleanUps) {
        this.millisBetweenCleanUps = millisBetweenCleanUps;
    }
}
