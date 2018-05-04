package OLink.bpm.core.email.runtime.mail;

//import com.sun.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class OBPMTrustManager implements X509TrustManager {
	/*
	public boolean isClientTrusted(X509Certificate[] cert) {
		return true;
	}

	public boolean isServerTrusted(X509Certificate[] cert) {
		return true;
	}
	*/

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}
