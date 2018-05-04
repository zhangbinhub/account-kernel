package OLink.bpm.core.remoteserver.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.xml.rpc.ParameterMode;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.remoteserver.dao.RemoteServerDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;

public class RemoteServerProcessBean extends AbstractDesignTimeProcessBean<RemoteServerVO> implements RemoteServerProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5635907488770248867L;
	private final static Logger log = Logger.getLogger(RemoteServerProcessBean.class);

	//@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<RemoteServerVO> getDAO() throws Exception {
		return (RemoteServerDAO) DAOFactory.getDefaultDAO(RemoteServerVO.class.getName());
	}

	/**
	 * 终端服务器是否有效
	 * 
	 * @param remoteServerId
	 * @return
	 * @throws Exception
	 */
	public boolean isAvailableEndpointServer(String remoteServerId) throws Exception {
		RemoteServerVO remoteServer = (RemoteServerVO) doView(remoteServerId);
		if (remoteServer != null) {
			String remoteServerUrl = remoteServer.getUrl();
			isAvailableEndpointAddress(remoteServerUrl);
			return true;
		}

		return false;
	}

	/**
	 * 终端地址是否有效
	 * 
	 * @param module
	 * @return true|false
	 * @throws Exception
	 */
	public boolean isAvailableEndpointAddress(String url) throws Exception {
		try {
			Call call = getEndpointCall(url);
			call.getService();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 获取终端服务器的URL,如->http://localhost:8080/webapp
	 * 
	 * @param remoteServerId
	 * @return url
	 * @throws Exception
	 */
	public String getImpOperatorServiceUrl(String remoteServerId) throws Exception {
		String url = "";
		RemoteServerVO remoteServer = (RemoteServerVO) doView(remoteServerId);
		if (remoteServer != null) {
			String remoteServerUrl = remoteServer.getUrl();
			url = remoteServerUrl + "/services/ImpOperator";
		}
		log.info("Server Url-->" + url);
		return url;
	}

	/**
	 * 获取终端服务器的访问
	 * 
	 * @param url
	 * @return Call(访问)对象
	 * @throws Exception
	 */
	public Call getEndpointCall(String url) throws Exception {
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(new java.net.URL(url));

		return call;
	}

	/**
	 * 把导出的文件导入到远程服务器中
	 * 
	 * @param xmlStr
	 *            导入文件的内容
	 * @param remoteServerId
	 *            远程服务器ID
	 * @param applicationId
	 *            应用ID
	 * @throws Exception
	 */
	public Collection<?> remoteImport(String xmlStr, String remoteServerId, String applicationId) throws Exception {
		Collection<?> messages = null;

		try {
			String url = getImpOperatorServiceUrl(remoteServerId);
			Call call = getEndpointCall(url);

			call.setOperationName("writeXmlToDB");
			call.addParameter("xmlStr", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("applicationId", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnClass(Collection.class);
			messages = (Collection<?>) call.invoke(new Object[] { xmlStr, applicationId });
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return messages;
	}
}
