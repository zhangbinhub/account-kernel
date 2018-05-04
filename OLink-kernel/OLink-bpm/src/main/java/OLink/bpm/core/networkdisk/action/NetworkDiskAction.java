package OLink.bpm.core.networkdisk.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.networkdisk.ejb.NetDisk;
import OLink.bpm.core.networkdisk.ejb.NetDiskProcess;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;

/**
 * action泛型参数为不定类型
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class NetworkDiskAction extends BaseAction {


	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */

	public NetworkDiskAction() throws Exception {
		super(ProcessFactory.createProcess(NetDiskProcess.class), new NetDisk());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public String doEdit() {
		try {
			Map<?, ?> params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			NetDisk contentVO = (NetDisk)process.doView(id);
			if(contentVO ==null){
				contentVO = new NetDisk();
				contentVO.setId(id);
				contentVO.setPemission("true");
			}else{
				if(contentVO.getTotalSize()<1024){
					contentVO.setTotalSize(contentVO.getTotalSize());
				}else if(contentVO.getTotalSize()<(1024*1024) && contentVO.getTotalSize()>=1024){
					contentVO.setTotalSize(contentVO.getTotalSize()/1024);
				}else{
					contentVO.setTotalSize(contentVO.getTotalSize()/(1024*1024));
				}
				
				if(contentVO.getUploadSize()<1024){
					contentVO.setUploadSize(contentVO.getUploadSize());
				}else if(contentVO.getUploadSize()<(1024*1024) && contentVO.getUploadSize()>=1024){
					contentVO.setUploadSize(contentVO.getUploadSize()/1024);
				}else{
					contentVO.setUploadSize(contentVO.getUploadSize()/(1024*1024));
				}
			}
			setContent(contentVO);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}
	
	public String doSave(){
		try {
		NetDisk contentVO = (NetDisk)getContent();
		long temp1 = contentVO.getTotalSize();
		long temp2 = contentVO.getUploadSize();
		
		NetDisk contentVO1 = (NetDisk)process.doView(contentVO.getId());
		if(contentVO1!=null){
			contentVO1.setTotalSize(contentVO.getTotalSize()*1024*1024);
			contentVO1.setUploadSize(contentVO.getUploadSize()*1024);
			contentVO1.setPemission(contentVO.getPemission());
			process.doUpdate(contentVO1);
			contentVO1.setTotalSize(temp1);
			contentVO1.setUploadSize(temp2);
			contentVO1.setPemission(contentVO.getPemission());
			setContent(contentVO1);
			addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
		}else{
			contentVO.setTotalSize(contentVO.getTotalSize()*1024*1024);
			contentVO.setUploadSize(contentVO.getUploadSize()*1024);
			process.doCreate(contentVO);
			contentVO.setTotalSize(temp1);
			contentVO.setUploadSize(temp2);
			setContent(contentVO);
			addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
		}
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	
	public String doSaveAll(){
		try {
			NetDisk contentVO = (NetDisk)getContent();
			String _selects = this.getParams().getParameterAsString("_selects");
			String[] _selectsArray = _selects.split(";");
			for(int i=0;i<_selectsArray.length;i++){
				NetDisk contentVO1 = (NetDisk)process.doView(_selectsArray[i]);
				if(contentVO1!=null){
					contentVO1.setTotalSize(contentVO.getTotalSize()*1024*1024);
					contentVO1.setUploadSize(contentVO.getUploadSize()*1024);
					contentVO1.setPemission(contentVO.getPemission());
					process.doUpdate(contentVO1);
				}else{
					contentVO1 = new NetDisk();
					contentVO1.setId(_selectsArray[i]);
					contentVO1.setTotalSize(contentVO.getTotalSize()*1024*1024);
					contentVO1.setUploadSize(contentVO.getUploadSize()*1024);
					contentVO1.setPemission(contentVO.getPemission());
					process.doCreate(contentVO1);
				}
			}
			setContent(contentVO);
			} catch (Exception e) {
				e.printStackTrace();
				this.addFieldError("1", e.getMessage());
				return INPUT;
			}
			addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
	}
	
	
	
	public String networkList(){
		try {
			this.validateQueryParams();
			UserProcess userProcess = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
			DataPackage<UserVO> datas = userProcess.doQuery(this.getParams());
			Collection<UserVO>  users = datas.getDatas();
			for (Iterator<UserVO> iterator = users.iterator(); iterator
				.hasNext();) {
					UserVO user = iterator.next();
					NetDisk contentVO = (NetDisk)process.doView(user.getId());
					if(contentVO == null){
						contentVO = new NetDisk();
						contentVO.setId(user.getId());
						contentVO.setTotalSize(10*1024*1024);
						contentVO.setUploadSize(100*1024);
						contentVO.setPemission("true");
						process.doCreate(contentVO);
					}
			}	 
			datas.setDatas(users);
			this.setDatas(datas);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
}
