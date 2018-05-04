package OLink.bpm.core.sysconfig.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import OLink.bpm.core.sysconfig.ejb.ImConfig;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import OLink.bpm.core.sysconfig.RootElementNotFoundException;
import OLink.bpm.core.sysconfig.ejb.AuthConfig;
import OLink.bpm.core.sysconfig.ejb.CheckoutConfig;
import OLink.bpm.core.sysconfig.ejb.EmailConfig;
import OLink.bpm.core.sysconfig.ejb.LdapConfig;
import OLink.bpm.core.sysconfig.ejb.SysConfigProcessBean;

public class ImportUtil {

	private static final int BUFFER_SIZE = 16 * 1024;

	public static void copy(File src, File dst) {
		try {
			InputStream is = null;
			OutputStream os = null;
			try {
				is = new BufferedInputStream(new FileInputStream(src),
						BUFFER_SIZE);
				os = new BufferedOutputStream(new FileOutputStream(dst),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				int len = 0;
				while ((len = is.read(buffer)) > 0)
					os.write(buffer, 0, len);
			} finally {
				if (null != is)
					is.close();
				if (null != os)
					os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void load(final File file)
			throws DocumentException, SecurityException,
			IllegalArgumentException, RootElementNotFoundException,
			IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException {

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		parseXmlAndHandle(document);
	}

	private static void parseXmlAndHandle(Document document)
			throws RootElementNotFoundException, IOException,
			SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException {
		AuthConfig authConfig = null;
		LdapConfig ldapConfig = null;
		EmailConfig emailConfig = null;
		ImConfig imConfig = null;
		CheckoutConfig checkoutConfig = null;
		Element root = document.getRootElement();
		if (root == null || !"sysConfig".equals(root.getName())) {
			throw new RootElementNotFoundException();
		} else {
			Element e_auth = root.element(AuthConfig.class.getName());
			Element e_ldap = root.element(LdapConfig.class.getName());
			Element e_email = root.element(EmailConfig.class.getName());
			Element e_im = root.element(ImConfig.class.getName());
			Element e_checkout = root.element(CheckoutConfig.class.getName());
			authConfig = (AuthConfig) initConfig(e_auth);
			ldapConfig = (LdapConfig) initConfig(e_ldap);
			emailConfig = (EmailConfig) initConfig(e_email);
			checkoutConfig = (CheckoutConfig) initConfig(e_checkout);
			imConfig = (ImConfig) initConfig(e_im);
		}
		new SysConfigProcessBean().save(authConfig, ldapConfig, emailConfig,imConfig,checkoutConfig);
	}

	private static Object initConfig(Element element)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SecurityException, NoSuchFieldException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		if (element != null) {
			Class<?> clazz = Class.forName(element.getName());
			Object config = clazz.newInstance();
			for (Iterator<?> elements = element.elementIterator(); elements != null
					&& elements.hasNext();) {
				Element e_field = (Element) elements.next();
				String fieldName = e_field.getName();
				String fieldValue = e_field.getText();
				if (fieldValue == null)
					fieldValue = e_field.attributeValue("value");
				StringBuffer setter = new StringBuffer("set");
				if (fieldName != null) {
					if (fieldName.length() > 0) {
						String first = fieldName.substring(0, 1).toUpperCase();
						setter.append(first);
					}
					if (fieldName.length() > 1) {
						setter.append(fieldName.substring(1));
					}
				}
				Field field = clazz.getDeclaredField(fieldName);
				if (field != null) {
					Method method_set = clazz.getMethod(setter.toString(),
							field.getType());
					if (method_set != null)
						method_set.invoke(config, fieldValue);
				}
			}
			return config;
		}
		return null;
	}

	public static void main(String[] args) throws SecurityException,
			IllegalArgumentException, DocumentException,
			RootElementNotFoundException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchFieldException, NoSuchMethodException,
			InvocationTargetException {
		File file = new File(ImportUtil.class.getClassLoader().getResource(
				"sysConfig.xml").getFile());
		load(file);
	}
}
