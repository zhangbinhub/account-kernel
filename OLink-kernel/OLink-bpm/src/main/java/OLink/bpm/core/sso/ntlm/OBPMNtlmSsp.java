package OLink.bpm.core.sso.ntlm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.http.NtlmSsp;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.util.Base64;

public class OBPMNtlmSsp extends NtlmSsp {

	/**
	 * Calls the static
	 * {@link #authenticate(HttpServletRequest, byte[])}
	 * method to perform NTLM authentication for the specified servlet request.
	 * 
	 * @param req
	 * @param challenge
	 * @return NtlmPasswordAuthentication instance or null
	 * @throws IOException
	 * @throws ServletException
	 */
	public NtlmPasswordAuthentication doAuthentication(HttpServletRequest req,
			byte[] challenge) throws IOException, ServletException {
		return authenticate(req, challenge);
	}

	/**
	 * Calls the static
	 * {@link #authenticate(HttpServletRequest, HttpServletResponse, byte[])}
	 * method to perform NTLM authentication for the specified servlet request.
	 * 
	 * @param req
	 * @param challenge
	 * @return NtlmPasswordAuthentication instance or null
	 * @throws IOException
	 * @throws ServletException
	 */
	public NtlmPasswordAuthentication doAuthentication(HttpServletRequest req,
			HttpServletResponse resp, byte[] challenge) throws IOException,
			ServletException {
		return authenticate(req, resp, challenge);
	}

	/**
	 * Calls the static
	 * {@link #authenticate(HttpServletRequest, HttpServletResponse, byte[])}
	 * method to perform NTLM authentication for the specified servlet request.
	 * 
	 * @param req
	 * @param challenge
	 * @return NtlmPasswordAuthentication instance or null
	 * @throws IOException
	 * @throws ServletException
	 */
	public static NtlmPasswordAuthentication authenticate(
			HttpServletRequest req, byte[] challenge) throws IOException,
			ServletException {
		return authenticate(req, null, challenge);
	}

	/**
	 * Performs NTLM authentication for the servlet request.
	 * 
	 * @param req
	 * @param resp
	 * @param challenge
	 * @return NtlmPasswordAuthentication instance or null
	 * @throws IOException
	 * @throws ServletException
	 */
	public static NtlmPasswordAuthentication authenticate(
			HttpServletRequest req, HttpServletResponse resp, byte[] challenge)
			throws IOException, ServletException {
		String msg = req.getHeader("Authorization");
		if (msg != null && msg.startsWith("NTLM ")) {
			byte[] src = Base64.decode(msg.substring(5));
			if (src[8] == 1) {
				Type1Message type1 = new Type1Message(src);
				Type2Message type2 = new Type2Message(type1, challenge, null);
				msg = Base64.encode(type2.toByteArray());
				if (resp != null) {
					resp.setHeader("WWW-Authenticate", "NTLM " + msg);
				}
			} else if (src[8] == 3) {
				Type3Message type3 = new Type3Message(src);
				byte[] lmResponse = type3.getLMResponse();
				if (lmResponse == null)
					lmResponse = new byte[0];
				byte[] ntResponse = type3.getNTResponse();
				if (ntResponse == null)
					ntResponse = new byte[0];
				return new NtlmPasswordAuthentication(type3.getDomain(), type3
						.getUser(), challenge, lmResponse, ntResponse);
			}
		} else {
			if (resp != null) {
				resp.setHeader("WWW-Authenticate", "NTLM");
			}
		}
		if (resp != null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.setContentLength(0);
			resp.flushBuffer();
		}
		return null;
	}

}
