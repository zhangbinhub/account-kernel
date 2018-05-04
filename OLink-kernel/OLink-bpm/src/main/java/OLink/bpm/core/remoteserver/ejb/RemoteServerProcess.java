package OLink.bpm.core.remoteserver.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;
import org.apache.axis.client.Call;

public interface RemoteServerProcess extends IDesignTimeProcess<RemoteServerVO> {

	/**
	 * 终端地址是否有效
	 * 
	 * @param module
	 * @return true|false
	 * @throws Exception
	 */
	boolean isAvailableEndpointAddress(String url) throws Exception;

	/**
	 * 获取终端服务器的URL,如->http://localhost:8080/webapp
	 * 
	 * @param remoteServerId
	 * @return url
	 * @throws Exception
	 */
	String getImpOperatorServiceUrl(String remoteServerId) throws Exception;

	/**
	 * 获取终端服务器的访问
	 * 
	 * @param url
	 * @return Call(访问)对象
	 * @throws Exception
	 */
	Call getEndpointCall(String url) throws Exception;

	/**
	 * 终端服务器是否有效果
	 * 
	 * @param remoteServerId
	 * @return
	 * @throws Exception
	 */
	boolean isAvailableEndpointServer(String remoteServerId) throws Exception;

	/**
	 * 把导出的文件导入到远程服务器中
	 * 
	 * @param expFile
	 *            导出的文件
	 * @param module
	 *            模块
	 * @throws Exception
	 */
	Collection<?> remoteImport(String xmlStr, String remoteServerId, String applicationId) throws Exception;

}
