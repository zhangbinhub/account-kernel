package OLink.bpm.gkmsapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.util.StringUtil;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * xml抽象解析类型
 * @author Tom
 *
 */
public class XMLParser{
	
	private User user;
	private Ug ug;
	private Role role;
	private Collection<User> users;
	private Map<String,String> userids;
	private Collection<Ug> ugs;
	private Map<String,String> ugids;
	private Collection<Role> roles;
	private Collection<String> privs;
	private String priv;
	private String tag="";
	private String type = "";
	private String subtype = "";
	private String msid = "";
	private int code;
	private String message="";
	
	public XMLParser(){
		
	}
	
	/**
	 * 解析xml
	 * @param xml
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parseXML(String xml) throws IOException, XmlPullParserException {
		if (StringUtil.isBlank(xml))return;
		parseXML(xml.getBytes("UTF-8"));
	}
	
	/**
	 * 解析xml
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parseXML(byte[] bytes) throws IOException, XmlPullParserException {
		parseXML(bytes, "UTF-8");
	}
	
	/**
	 * 解析xml
	 * @param bytes
	 * @param charset
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parseXML(byte[] bytes, String charset) throws IOException, XmlPullParserException {
		KXmlParser parser = new KXmlParser();
		try {
			parser.setInput(new ByteArrayInputStream(bytes), charset);
			processDocument(parser);
		} catch (XmlPullParserException e) {
			throw new XmlPullParserException("xml.parse.error");
		} catch (IOException e) {
			throw new IOException("date.stream.error");
		}
	}
	

	/**
	 * 解析xml文档
	 * @param xmlParser
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	protected void processDocument(KXmlParser xpp) throws IOException, XmlPullParserException{
		// 解析XML
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
				
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				
			} else if (eventType == XmlPullParser.START_TAG) {
				processStartElement(xpp);
			} else if (eventType == XmlPullParser.END_TAG) {
				processEndElement(xpp);
			} else if (eventType == XmlPullParser.TEXT) {
				processText(xpp);
			}
			eventType = xpp.next();
		}
	}
	
	public void processStartElement(KXmlParser xpp) {
		String tagname = xpp.getName().toLowerCase();
		tag = tagname;
		if (tagname.equals("response")) {
			type = xpp.getAttributeValue("","type");
			subtype = xpp.getAttributeValue("","subtype");
			msid = xpp.getAttributeValue("","msid");
		}else if(tagname.equals("result")){
			code = Integer.parseInt(xpp.getAttributeValue("","code"));
		}else if(tagname.equals("message")){
			
		}else if(tagname.equals("users")){
			users = new ArrayList<User>();
			userids = new LinkedHashMap<String, String>();
		}else if(tagname.equals("user")){
			User user = new User();
			user.setAccount(xpp.getAttributeValue("","account"));
			user.setGid(xpp.getAttributeValue("","gid"));
			user.setZoneid(xpp.getAttributeValue("","zoneid"));
			user.setName(xpp.getAttributeValue("","name"));
			user.setDisplayName(xpp.getAttributeValue("","display_name"));
			user.setState((xpp.getAttributeValue("","state")!=null && xpp.getAttributeValue("","state").equals("1"))?true:false);
			user.setSex((xpp.getAttributeValue("","sex")!=null && !xpp.getAttributeValue("","sex").equals(""))?Integer.parseInt(xpp.getAttributeValue("","sex")):0);
			user.setBirthday(xpp.getAttributeValue("","birthday"));
			user.setEmail(xpp.getAttributeValue("","email"));
			user.setUg_name(xpp.getAttributeValue("","ug_name"));
			user.setMobile(xpp.getAttributeValue("","mobile"));
			user.setOfficeTel(xpp.getAttributeValue("","office_tel"));
			user.setFax(xpp.getAttributeValue("","fax"));
			user.setWebAddress(xpp.getAttributeValue("","webaddress"));
			user.setPostcode(xpp.getAttributeValue("","postcode"));
			user.setAddress(xpp.getAttributeValue("","address"));
			user.setPosition(xpp.getAttributeValue("","position"));
			user.setRemark(xpp.getAttributeValue("","remark"));
			if(users!=null){
				users.add(user);
				userids.put(user.getAccount(), user.getGid());
			}else{
				this.user = user;
			}
		}else if(tagname.equals("ugs")){
			ugs = new ArrayList<Ug>();
			ugids = new LinkedHashMap<String, String>();
		}else if(tagname.equals("ug")){
			Ug ug = new Ug();
			ug.setCode(xpp.getAttributeValue("","code"));
			ug.setName(xpp.getAttributeValue("","name"));
			ug.setParentCode(xpp.getAttributeValue("","parent_code"));
			ug.setSign(xpp.getAttributeValue("","sign").equals("0")?false:true);
			ug.setLocation(xpp.getAttributeValue("","location"));
			ug.setEmail(xpp.getAttributeValue("","email"));
			ug.setRemark(xpp.getAttributeValue("","remark"));
			if(ugs!=null){
				ugs.add(ug);
				ugids.put(ug.getCode(), ug.getCode());
			}else{
				this.ug = ug;
			}
		}else if(tagname.equals("roles")){
			roles = new ArrayList<Role>();
		}else if(tagname.equals("role")){
			role = new Role();
			role.setId(xpp.getAttributeValue("","id"));
			role.setName(xpp.getAttributeValue("","name"));
			role.setRemark(xpp.getAttributeValue("","remark"));
			if(roles!=null){
				roles.add(role);
			}
		}else if(tagname.equals("privs")){
			privs = new ArrayList<String>();
			if(role!=null){
				role.setPrivs(privs);
			}
		}else if(tagname.equals("priv")){
			if(privs!=null){
				privs.add(xpp.getAttributeValue("","code"));
			}else{
				priv = xpp.getAttributeValue("","code");
			}
		}
	}
	
	public void processText(KXmlParser xpp) throws XmlPullParserException,IOException {
		String text = xpp.getText().trim();
		if(tag.equals("result")){
			message = text;
		}
		tag = "";
	}
	
	public void processEndElement(KXmlParser xpp) {
		
	}

	public User getUser() {
		return user;
	}

	public Ug getUg() {
		return ug;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public Collection<Ug> getUgs() {
		return ugs;
	}

	public String getTag() {
		return tag;
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getMsid() {
		return msid;
	}

	public int getCode() {
		return code;
	}
	
	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public Collection<String> getPrivs() {
		return privs;
	}

	public Role getRole() {
		return role;
	}

	public String getPriv() {
		return priv;
	}

	public Collection<Role> getRoles() {
		return roles;
	}
	
	

	public Map<String, String> getUserids() {
		return userids;
	}

	public Map<String, String> getUgids() {
		return ugids;
	}

	public static void main(String[] args){
		String res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response type=\"ug\" subtype=\"addug\" msid=\"c01d0b8aacca5eafaff3e0296cce5ecc\"><result code=\"0\">ok.</result></response>";
		XMLParser xml = new XMLParser();
		try {
			xml.parseXML(res);
			System.out.println(xml.getCode()+xml.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	
}
