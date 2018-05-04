package OLink.bpm.core.report.dataprepare;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collection;

import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepareProcess;
import OLink.bpm.core.report.dataprepare.ejb.SqlSentence;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;
import OLink.bpm.util.ProcessFactory;

public class ExecuteDataPrepare {

	public static String  execute(DataPrepare vo) throws Exception{
		
		if(vo==null) return "";
		
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=getConnection(vo.getDataSource());
			stmt=conn.createStatement();
			
			Collection<SqlSentence> sqlSentences=vo.getSqlSentences();
			
	        Object sentences[]=sqlSentences.toArray();
			Object temp = null;
			int k = 0;
			for (int i = 0; i < sentences.length; i++) {
				k = i;
				SqlSentence em=(SqlSentence)sentences[i];
				for (int j = i + 1; j < sentences.length; j++) {
					SqlSentence cm = (SqlSentence)sentences[j];
					if (Integer.parseInt(em.getExecuteOrder()) >Integer.parseInt(cm.getExecuteOrder())) {
						em = cm;
						k = j;
					}
				}
				if (k != i) {
					temp = sentences[i];
					sentences[i] = sentences[k];
					sentences[k] = temp;
				}
			}

		for (int i = 0; i < sentences.length; i++) {
			SqlSentence em=(SqlSentence)sentences[i];
			if(em.getSentence()!=null&&em.getSentence().trim().length()>0)
			stmt.executeUpdate(em.getSentence());
		}	
			
		} catch (Exception e) {
          e.printStackTrace();
          return "数据准备失败:"+e.getMessage();
		}finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
		}
		
	 return "数据准备完毕";	
	}
	
	public static String  clearTempData(DataPrepare vo)throws Exception{
		if(vo==null) return "";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=getConnection(vo.getDataSource());
			stmt=conn.createStatement();
			
			String sql=vo.getClearDataSql();
			
			if(sql!=null&&sql.trim().length()>0)
				stmt.executeUpdate(sql);
			
		} catch (Exception e) {
          e.printStackTrace();
          return "数据清除失败:"+e.getMessage();
		}finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
		}
		 return "数据清除成功";	
	}
	
	
	private static  Connection getConnection(DataSource dts) throws Exception {
		DriverManager.registerDriver((Driver) Class.forName(
				dts.getDriverClass()).newInstance());
		Connection conn = DriverManager.getConnection(dts.getUrl(), dts
				.getUsername(), dts.getPassword());
		return conn;
	}

  	public static void main(String args[]) throws Exception{
  		
  		DataPrepareProcess dp = (DataPrepareProcess) (ProcessFactory
				.createProcess(DataPrepareProcess.class));
		DataPrepare dt=(DataPrepare)dp.doView("5cefed6d-5517-420f-aa91-9d1f4f061193");
		
		//ExecuteDataPrepare ep=new ExecuteDataPrepare();
		
		ExecuteDataPrepare.execute(dt);
		
  	}
	
}
