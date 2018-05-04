package pers.acp.communications.server.soap.webservice.base;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface IWebService {

	@WebMethod(exclude = true)
	String getServiceName();

}
