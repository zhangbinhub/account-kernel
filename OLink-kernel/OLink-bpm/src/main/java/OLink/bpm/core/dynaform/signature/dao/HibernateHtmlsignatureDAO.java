package OLink.bpm.core.dynaform.signature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.signature.ejb.Htmlsignature;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateHtmlsignatureDAO extends HibernateBaseDAO<Htmlsignature> implements HtmlsignatureDAO {

	public HibernateHtmlsignatureDAO(String voClassName){
		super(voClassName);
	}
	public List<Htmlsignature> queryAll() throws Exception {
		
		String hql = "from Htmlsignature";
		return (List<Htmlsignature>)getDatas(hql);
	}

	public List<Htmlsignature> queryById(String SignatureID, String DocumentID, String FormID)
			throws Exception {
        String hql="from Htmlsignature as a where a.SignatureID='"+SignatureID+"' and a.DocumentID='"+DocumentID+"' and a.FormID='"+FormID+"'";
		return (List<Htmlsignature>)getDatas(hql);
	}
	public void createHtmlsignature(String mDocumentID,String mSignatureID,String mSignature, String FormID) throws Exception {
		Session session = HibernateBaseDAO.currentSession();
		Transaction ts = null;
		PreparedStatement prestmt=null;
		try {
			Connection conn = session.connection();
			ts = session.beginTransaction();
			String Sql="insert into T_HTMLSIGNATURE (DocumentID,SignatureID,Signature,FormID) values (?,?,?,?) ";
			prestmt = conn.prepareStatement(Sql);
			prestmt.setString(1, mDocumentID);
	        prestmt.setString(2, mSignatureID);
    		prestmt.setString(3, mSignature);
    		prestmt.setString(4, FormID);
    		prestmt.execute();
    		prestmt.close();
    		ts.commit();
		} catch (Exception e) {
			if(ts!=null)ts.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		
	}
	public void updateHtmlsignature(Htmlsignature htmlsignature) throws Exception {
		Session session = HibernateBaseDAO.currentSession();
		Transaction ts =null;
		try{
			ts = session.beginTransaction();
			session.update(htmlsignature);
			ts.commit();
		}catch(Exception e){
			if(ts!=null)ts.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		
	}
	public List<Htmlsignature> queryByDocumentID(String DocumentID , String FormID) throws Exception {
		 String hql="from Htmlsignature as a where a.DocumentID='"+DocumentID+"' and a.FormID='"+FormID+"'";
		return (List<Htmlsignature>)getDatas(hql);
	}

}
