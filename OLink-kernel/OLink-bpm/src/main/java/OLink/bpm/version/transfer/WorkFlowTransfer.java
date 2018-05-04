package OLink.bpm.version.transfer;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHIS;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcessBean;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import org.apache.log4j.Logger;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;

public class WorkFlowTransfer extends BaseTransfer {
	
	private final static Logger LOG = Logger.getLogger(ApplicationTransfer.class);

	public void to2_4() {

	}
	
	public void to2_5SP4() {
		Connection conn = null;
		Statement stmt = null;
		String sql = "UPDATE T_FLOWSTATERT SET SUB_POSITION = POSITION WHERE SUB_POSITION IS NULL";
		try{
		ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		Collection<ApplicationVO> appList = applicationProcess.doSimpleQuery(new ParamsTable());
		for(Iterator<ApplicationVO> it = appList.iterator();it.hasNext();){
			ApplicationVO app = it.next();
			conn = app.getConnection();
			stmt = conn.createStatement();
			LOG.info(sql);
			int i = stmt.executeUpdate(sql);
			LOG.info(i+"row effect");
			stmt.close();// Add By XGY 20130228
			conn.close();
			stmt = null;
		}
		}catch(Exception e){
			LOG.info("execute failed ："+e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn !=null) conn = null;
			if(stmt !=null) stmt = null;
		}
	}
	

	public void to2_6() throws Exception {
		String sql ="SELECT * FROM T_RELATIONHIS vo WHERE vo.FLOWSTATERT_ID IS NULL";
		ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		Collection<ApplicationVO> appList = applicationProcess.doSimpleQuery(new ParamsTable());
		for(Iterator<ApplicationVO> it = appList.iterator();it.hasNext();){
			ApplicationVO app = it.next();
			
			RelationHISProcess hisProcess = new RelationHISProcessBean(app.getId());
			DocumentProcess docProcess = new DocumentProcessBean(app.getId());
			Collection<RelationHIS> list = hisProcess.doQueryBySQL(sql);
			for(Iterator<RelationHIS> iter = list.iterator();iter.hasNext();){
				RelationHIS his = iter.next();
				Document doc = (Document) docProcess.doView(his.getDocid());
				if(doc !=null && !StringUtil.isBlank(doc.getStateid())){
					his.setFlowStateId(doc.getStateid());
					hisProcess.doUpdate(his);
				}
			}
		
		}
		
	}
	
	
	public static void main(String[] args) {
		try {
			new WorkFlowTransfer().to2_5SP4();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
