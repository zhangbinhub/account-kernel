package OLink.bpm.core.dynaform.signature.ejb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.DateField;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.InputField;
import OLink.bpm.core.dynaform.signature.dao.HtmlsignatureDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.cache.MemoryCacheUtil;
import org.apache.log4j.Logger;

import eWAP.core.Tools;

import com.opensymphony.webwork.ServletActionContext;

/**
 * 
 * @author Alex
 * 
 */
public class HtmlsignatureProcessBean extends AbstractDesignTimeProcessBean<Htmlsignature>
		implements HtmlsignatureProcess<Htmlsignature> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2467914335935009878L;
	private static final Logger log = Logger.getLogger(HtmlsignatureProcessBean.class);
	private static final int GETBATCHDOCUMENTTYPE = 0;

	private static final int GETDOCUMENTTYPE = 1;

	private static final int SAVESIGNATURETYPE = 2;

	private static final int GETNOWTIMETYPE = 3;

	private static final int DELESIGNATURETYPE = 4;

	private static final int LOADSIGNATURETYPE = 5;

	private static final int SHOWSIGNATURETYPE = 6;

	private static final int GETSIGNATUREDATATYPE = 7;

	private static final int PUTSIGNATUREDATATYPE = 8;

	private static final int SAVEHISTORYTYPE = 9;
	private String mFormID;
	private String mDocumentID = "";
	private String mSignatureID = "";
	private String mSignature = "";
	private String mSignatures;
	public String mUserName;
	/**
	 * 文件名
	 */
	public String keyName;
	/**
	 * 文件对象
	 */
	public java.io.File objFile;
	/**
	 * 缓冲
	 */
	public char[] chrBuffer;
	/**
	 * 实际读出的字符数
	 */
	public int intLength;
	/**
	 * 印章名称
	 */
	public String mSignatureName;
	/**
	 * 签章单位
	 */
	public String mSignatureUnit;
	/**
	 * 持章人
	 */
	public String mSignatureUser;
	/**
	 * 签章SN
	 */
	public String mSignatureSN;
	/**
	 * 全球唯一标识符
	 */
	public String mSignatureGUID;
	/**
	 * 机器IP
	 */
	public String mMACHIP;
	/**
	 * 操作标志
	 */
	public String OPType;
	/**
	 * KEY序列号
	 */
	public String mKeySn;

	protected IDesignTimeDAO<Htmlsignature> getDAO() throws Exception {

		return (HtmlsignatureDAO) DAOFactory.getDefaultDAO(Htmlsignature.class.getName());
	}

	public List<Htmlsignature> queryAll() throws Exception {

		return ((HtmlsignatureDAO) getDAO()).queryAll();
	}

	public List<Htmlsignature> queryById(String SignatureID, String DocumentID, String FormID)
			throws Exception {
		return ((HtmlsignatureDAO) getDAO()).queryById(SignatureID, DocumentID,
				FormID);
	}

	public void updateHtmlsignature(Htmlsignature htmlsignature)
			throws Exception {

		((HtmlsignatureDAO) getDAO()).updateHtmlsignature(htmlsignature);
	}

	public void createHtmlsignature(String mDocumentID, String mSignatureID,
			String mSignature, String FormID) throws Exception {

		((HtmlsignatureDAO) getDAO()).createHtmlsignature(mDocumentID,
				mSignatureID, mSignature, FormID);
	}

	public List<Htmlsignature> queryByDocumentID(String DocumentID, String FormID)
			throws Exception {

		return ((HtmlsignatureDAO) getDAO()).queryByDocumentID(DocumentID,
				FormID);
	}

	public void saveSignature(ParamsTable params) throws Exception {
		mFormID = params.getParameterAsString("FormID");
		mDocumentID = params.getParameterAsString("DOCUMENTID");
		mSignatureID = params.getParameterAsString("SIGNATUREID");
		mSignature = params.getParameterAsString("SIGNATURE");
		List<Htmlsignature> list = this.queryById(mSignatureID, mDocumentID, mFormID);
		Iterator<Htmlsignature> ite = list.iterator();
		if (ite.hasNext()) {
			//Htmlsignature htmlSignature = (Htmlsignature) list.get(0);
			Htmlsignature htmlSignature = list.get(0);
			htmlSignature.setDocumentID(mDocumentID);
			htmlSignature.setSignatureID(mSignatureID);
			htmlSignature.setFormID(mFormID);
			htmlSignature.setSignature(mSignature);
			this.updateHtmlsignature(htmlSignature);
		} else {
			// 取得唯一值(mSignature)
			Date dt = new Date();
			long lg = dt.getTime();
			Long ld = Long.valueOf(lg);
			mSignatureID = ld.toString();
			this.createHtmlsignature(mDocumentID, mSignatureID, mSignature,
					mFormID);
		}
		printWriter(HtmlsignatureProcessBean.SAVESIGNATURETYPE, mSignatureID);
	}

	public void getNowTime() throws Exception {
		//java.sql.Date mDate;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String mDateTime = formatter.format(cal.getTime());
		printWriter(HtmlsignatureProcessBean.GETNOWTIMETYPE, mDateTime);
	}

	public void deleSignature(ParamsTable params) throws Exception {
		mFormID = params.getParameterAsString("FormID");
		mDocumentID = params.getParameterAsString("DOCUMENTID");
		mSignatureID = params.getParameterAsString("SIGNATUREID");
		List<Htmlsignature> list = this.queryById(mSignatureID, mDocumentID, mFormID);
		Iterator<Htmlsignature> ite = list.iterator();
		if (ite.hasNext()) {
			//Htmlsignature htmlSignature = list.get(0);
			Htmlsignature htmlSignature = list.get(0);
			this.doRemove(htmlSignature);
		}
		printWriter(HtmlsignatureProcessBean.DELESIGNATURETYPE, null);
	}

	public void loadSignature(ParamsTable params) throws Exception {
		mDocumentID = params.getParameterAsString("DOCUMENTID");
		mFormID = params.getParameterAsString("FormID");
		mSignatureID = params.getParameterAsString("SIGNATUREID");
		List<Htmlsignature> list = this.queryById(mSignatureID, mDocumentID, mFormID);
		Iterator<Htmlsignature> ite = list.iterator();
		if (ite.hasNext()) {
			//Htmlsignature htmlSignature = (Htmlsignature) list.get(0);
			Htmlsignature htmlSignature = list.get(0);
			mSignature = htmlSignature.getSignature();
		}
		printWriter(HtmlsignatureProcessBean.LOADSIGNATURETYPE, mSignature);
	}

	public void showSignature(ParamsTable params) throws Exception {
		mFormID = params.getParameterAsString("FormID");
		mDocumentID = params.getParameterAsString("DOCUMENTID");
		mSignatures = "";
		List<Htmlsignature> list = this.queryByDocumentID(mDocumentID, mFormID);
		for (Iterator<Htmlsignature> ite = list.iterator(); ite.hasNext();) {
			//Htmlsignature htmlSignature = (Htmlsignature) ite.next();
			Htmlsignature htmlSignature = ite.next();
			mSignatures = mSignatures + htmlSignature.getSignatureID() + ";";
		}
		printWriter(HtmlsignatureProcessBean.SHOWSIGNATURETYPE, mSignatures);
	}

	@SuppressWarnings("deprecation")
	public void getSignatureData(ParamsTable params) throws Exception {
		String mSignatureData = "";
		mFormID = params.getParameterAsString("FormID");
		mDocumentID = params.getParameterAsString("DOCUMENTID");
		FormProcess formPross = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) formPross.doView(mFormID);
		DocumentProcess proxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,form
				.getApplicationid());
		Document doc2 = (Document) proxy.doView(mDocumentID);

		Collection<String> formcollection = form.getAllFieldNames();
		Collection<Item> itemcollection = doc2.getItems();
		String days = "";
		for (Object objcect : formcollection) {
			for (Item item : itemcollection) {
				//Item item = (Item) objcect2;
				Object value = item.getValue();
				if (item.getName().length()>0
						&& item.getName().equals(objcect.toString())) {
					if (value != null) {
						if (value instanceof Date) {
							Date d = (Date) value;
							DateField dateField = (DateField) form
									.findFieldByName(objcect.toString());
							SimpleDateFormat format = new SimpleDateFormat(
									dateField.getDatePatternValue());
							days = format.format(d);
							mSignatureData = mSignatureData + item.getName()
									+ "=" + days + "\r\n";
						} else if (value instanceof Number) {
							InputField inputField = (InputField) form
									.findFieldByName(objcect.toString());
							DecimalFormat format = new DecimalFormat(inputField
									.getNumberPattern());
							mSignatureData = mSignatureData + item.getName()
									+ "=" + format.format(value)
									+ "\r\n";
						} else {
							mSignatureData = mSignatureData + item.getName()
									+ "=" + item.getValue().toString() + "\r\n";
						}

					} else {
						mSignatureData = mSignatureData + item.getName()
								+ "=\r\n";
					}

				}
			}
		}
		mSignatureData = java.net.URLEncoder.encode(mSignatureData);
		printWriter(HtmlsignatureProcessBean.GETSIGNATUREDATATYPE, mSignatureData);
	}

	public void putSignatureData(ParamsTable params) throws Exception {
		mFormID = params.getParameterAsString("FormID");
		mDocumentID = params.getParameterAsString("DOCUMENTID");
		mSignature = params.getParameterAsString("SIGNATURE");
		Date dt = new Date();
		long lg = dt.getTime();
		Long ld = Long.valueOf(lg);
		mSignatureID = ld.toString();
		this
				.createHtmlsignature(mDocumentID, mSignatureID, mSignature,
						mFormID);
		printWriter(HtmlsignatureProcessBean.PUTSIGNATUREDATATYPE, mSignatureID);
	}

	public void saveHistory(ParamsTable params) throws Exception {
		mSignatureName = params
				.getParameterAsString("SIGNATURENAME");// 印章名称
		mSignatureUnit = params
				.getParameterAsString("SIGNATUREUNIT");// 印章单位
		mSignatureUser = params
				.getParameterAsString("SIGNATUREUSER");// 印章用户名
		mSignatureSN = params.getParameterAsString("SIGNATURESN");// 印章序列号
		mSignatureGUID = params
				.getParameterAsString("SIGNATUREGUID");// 全球唯一标识
		mDocumentID = params.getParameterAsString("DOCUMENTID");// 页面ID
		mSignatureID = params.getParameterAsString("SIGNATUREID");// 签章序列号
		mMACHIP = params.getParameterAsString("MACHIP");// 签章机器IP
		OPType = params.getParameterAsString("LOGTYPE");// 日志标志
		mKeySn = params.getParameterAsString("KEYSN");// KEY序列号
		Htmlhistory htmlhistory = new Htmlhistory();
		htmlhistory.setId(Tools.getSequence());
		htmlhistory.setSignatureName(mSignatureName);
		htmlhistory.setSignatureUnit(mSignatureUnit);
		htmlhistory.setSignatureUser(mSignatureUser);
		htmlhistory.setSignatureSN(mSignatureSN);
		htmlhistory.setSignatureGUID(mSignatureGUID);
		htmlhistory.setDocumentID(mDocumentID);
		htmlhistory.setSignatureID(mSignatureID);
		htmlhistory.setIP(mMACHIP);
		htmlhistory.setLogType(OPType);
		htmlhistory.setKeySN(mKeySn);
		HtmlhistoryProcess hh = (HtmlhistoryProcess) ProcessFactory
				.createProcess(HtmlhistoryProcess.class);
		hh.createHtmlhistory(htmlhistory);
		printWriter(HtmlsignatureProcessBean.SAVEHISTORYTYPE, mSignatureID);
	}

	public void signatureKey(ParamsTable params) throws Exception {
		mUserName = params.getParameterAsString("USERNAME");
		String RealPath = "\\portal\\share\\dynaform\\form\\signature\\" + mUserName + "\\" + mUserName
				+ ".key";
		String KeyName = ServletActionContext.getServletContext().getRealPath(
				RealPath);
		objFile = new java.io.File(KeyName); // 创建文件对象
		chrBuffer = new char[10];
		PrintWriter out;
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			out = response.getWriter();
			if (objFile.exists()) {// 文件存在
				InputStreamReader isr = new InputStreamReader(
						new FileInputStream(KeyName));
				while ((intLength = isr.read(chrBuffer)) != -1) { // 读文件内容
					out.write(chrBuffer, 0, intLength);
				}

				out.write("\r\n");
				out.write("RESULT=OK");
				out.flush();
				out.close();
				isr.close(); // 关闭读文件对象
			} else {
				out.println("File Not Found" + KeyName); // 文件不存在
			}
		} catch (Exception e) {

			log.error(e.toString());
		}
	}

	public void getBatchDocument(ParamsTable params) throws Exception {

		String mDocumentID = params.getParameterAsString("DocumentID");
		String mformID = params.getParameterAsString("FormID2");
		DocumentProcess proxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
				params.getParameterAsString("ApplicationID2"));
		Document doc2 = (Document) proxy.doView(mDocumentID);
		FormProcess formPross = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) formPross.doView(mformID);
		Collection<String> formcollection = form.getAllFieldNames();
		Collection<Item> itemcollection = doc2.getItems();
		String documentName = "";
		StringBuffer temp = new StringBuffer();
		for (Object objcect : formcollection) {
			for (Item item : itemcollection) {
				//Item item = (Item) objcect2;
				if (item.getName().length()>0
						&& item.getName().equals(objcect.toString())) {
					temp.append(item.getName()).append(",");
				}
			}
		}
		documentName = temp.substring(0, temp.lastIndexOf(","));
		printWriter(HtmlsignatureProcessBean.GETBATCHDOCUMENTTYPE, documentName);
	}

	public void getDocument(ParamsTable params, WebUser user) throws Exception {
		String _docid = params.getParameterAsString("_docid");
		String _formid = params.getParameterAsString("_formid");
		Document doc = (Document) MemoryCacheUtil.getFromPrivateSpace(_docid,
				user);
		FormProcess formPross = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) formPross.doView(_formid);
		Collection<String> formcollection = form.getAllFieldNames();
		Collection<Item> itemcollection = doc.getItems();
		String documentName = "";
		StringBuffer temp = new StringBuffer();
		for (Object objcect : formcollection) {
			for (Item item : itemcollection) {
				//Item item = (Item) objcect2;
				if (item.getName().length()>0
						&& item.getName().equals(objcect.toString())) {
					temp.append(item.getName()).append(",");
				}
			}
		}
		documentName = temp.substring(0, temp.lastIndexOf(","));
		printWriter(HtmlsignatureProcessBean.GETDOCUMENTTYPE, documentName);
	}

	public void printWriter(int printtype, String value) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			if (printtype == HtmlsignatureProcessBean.GETBATCHDOCUMENTTYPE
					|| printtype == HtmlsignatureProcessBean.GETDOCUMENTTYPE) {
				out.print(value.toString());
			} else if (printtype == HtmlsignatureProcessBean.SAVESIGNATURETYPE) {
				out.print("SIGNATUREID=" + value + "\r\n");
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.GETNOWTIMETYPE) {
				out.print("NOWTIME=" + value + "\r\n");
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.DELESIGNATURETYPE) {
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.LOADSIGNATURETYPE) {
				out.print(value + "\r\n");
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.SHOWSIGNATURETYPE) {
				out.print("SIGNATURES=" + value + "\r\n");
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.GETSIGNATUREDATATYPE) {
				out.print("SIGNATUREDATA=" + value + "\r\n");
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.PUTSIGNATUREDATATYPE) {
				out.print("SIGNATUREID=" + value + "\r\n");
				out.print("RESULT=OK");
			} else if (printtype == HtmlsignatureProcessBean.SAVEHISTORYTYPE) {
				out.print("SIGNATUREID=" + value + "\r\n");
				out.print("RESULT=OK");
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
