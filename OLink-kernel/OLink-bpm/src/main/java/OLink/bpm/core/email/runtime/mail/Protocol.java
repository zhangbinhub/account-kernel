package OLink.bpm.core.email.runtime.mail;

import java.util.List;

import javax.mail.Message;

import OLink.bpm.core.email.runtime.model.EmailHeader;


/**
 * 协议
 * @author Tom
 *
 */
public interface Protocol {
	
	/**
	 * 连接并打开给定的文件目录
	 * @param connectType 文件操作类型<br>类型：Folder.READ_ONLY, Folder.READ_WRITE
	 * @return
	 * @throws Exception
	 */
	ConnectionMetaHandler connect(int connectType) throws Exception;
	
	/**
	 * 连接并打开给定的文件目录
	 * @param connectType 文件操作类型<br>类型：Folder.READ_ONLY, Folder.READ_WRITE
	 * @param debug debug
	 * @return
	 * @throws Exception
	 */
	ConnectionMetaHandler connect(int connectType, boolean debug) throws Exception;
	
	/**
	 * 关闭连接
	 * @throws Exception
	 */
	void disconnect() throws Exception;
	
	ConnectionMetaHandler deleteMessages(int indexs[]) throws Exception;
	
	Message getMessage(int index) throws Exception;
	
	Message[] getMessages(int stratIndex, int endIndex) throws Exception;
	
	void emptyFolder() throws Exception;
	
	int getTotalMessageCount() throws Exception;
	
	int getUnreadMessageCount() throws Exception;
	
	void flagAsDeleted(int[] indexs) throws Exception;
	
	List<EmailHeader> fetchAllHeaders() throws Exception;
	
	List<Message> fetchAllHeadersAsMessages() throws Exception;
	
	List<EmailHeader> fetchHeaders(int[] indexs) throws Exception;
	
	List<EmailHeader> getHeadersSortList(String sortCriteriaRaw, String sortDirectionRaw) throws Exception;
	
}
