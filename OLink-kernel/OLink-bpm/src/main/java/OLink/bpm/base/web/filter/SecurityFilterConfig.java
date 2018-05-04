package OLink.bpm.base.web.filter;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by zhang on 2016/4/8.
 */
@XStreamAlias("security-filter")
public class SecurityFilterConfig extends BaseConfig {

    public static SecurityFilterConfig getInstance() throws ConfigException {
        return Load(SecurityFilterConfig.class);
    }

    @XStreamAlias("errorPage")
    private String errorPage;

    @XStreamAlias("isForegroundURI")
    private ForegroundURI foregroundURI;

    @XStreamAlias("needcheckURI")
    private NeedcheckURI needcheckURI;

    @XStreamAlias("notcheckURI")
    private NotcheckURI notcheckURI;

    @XStreamAlias("foreground")
    private Foreground foreground;

    @XStreamAlias("background")
    private Background background;

    public NeedcheckURI getNeedcheckURI() {
        return needcheckURI;
    }

    public void setNeedcheckURI(NeedcheckURI needcheckURI) {
        this.needcheckURI = needcheckURI;
    }

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public ForegroundURI getForegroundURI() {
        return foregroundURI;
    }

    public void setForegroundURI(ForegroundURI foregroundURI) {
        this.foregroundURI = foregroundURI;
    }

    public NotcheckURI getNotcheckURI() {
        return notcheckURI;
    }

    public void setNotcheckURI(NotcheckURI notcheckURI) {
        this.notcheckURI = notcheckURI;
    }

    public Foreground getForeground() {
        return foreground;
    }

    public void setForeground(Foreground foreground) {
        this.foreground = foreground;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public class ForegroundURI {

        @XStreamImplicit(itemFieldName = "keyword")
        private List<String> keywords;

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

    }

    public class NeedcheckURI {

        @XStreamImplicit(itemFieldName = "keyword")
        private List<String> keywords;

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

    }

    public class NotcheckURI {

        @XStreamImplicit(itemFieldName = "keyword")
        private List<String> keywords;

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

    }

    public class Foreground {

        public String getTimeoutpage() {
            return timeoutpage;
        }

        public void setTimeoutpage(String timeoutpage) {
            this.timeoutpage = timeoutpage;
        }

        public String getLoginpage() {
            return loginpage;
        }

        public void setLoginpage(String loginpage) {
            this.loginpage = loginpage;
        }

        @XStreamAlias("timeoutpage")
        private String timeoutpage;

        @XStreamAlias("loginpage")
        private String loginpage;

    }

    public class Background {

        public String getTimeoutpage() {
            return timeoutpage;
        }

        public void setTimeoutpage(String timeoutpage) {
            this.timeoutpage = timeoutpage;
        }

        @XStreamAlias("timeoutpage")
        private String timeoutpage;

    }

}
