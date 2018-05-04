package OLink.bpm.util.hibernate.vo2hbm;

import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

public class CreateHbm {

	public CreateHbm() {
		super();
	}

	private String createHead(Class<?> voClazz) {
		String tbName = voClazz.getName().substring(
				voClazz.getName().lastIndexOf(".") + 1);
		tbName = "t_" + tbName.toLowerCase();
		tbName = tbName.substring(0, tbName.length() - 2);

		String rtn = "";
		rtn += "<?xml version=\"1.0\"?>\n";
		rtn += "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n";
		rtn += "\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n";
		rtn += "<!-- Generated " + new Date() + " by CreateHbm -->\n";
		rtn += "<hibernate-mapping>\n";
		rtn += "    <class name=\"" + voClazz.getName() + "\" table=\""
				+ tbName + "\">\n";

		return rtn;
	}

	private String createFoo() {
		String rtn = "";
		rtn += "    </class>\n";
		rtn += "</hibernate-mapping>\n";
		return rtn;
	}

	private String createBody(Class<?> voClazz) {
		StringBuffer rtn = new StringBuffer();

		rtn.append(createPkElement());

		PropertyDescriptor[] encf = PropertyUtils
				.getPropertyDescriptors(voClazz);

		for (int i = 0; i < encf.length; i++) {
			rtn.append(createElement(encf[i]));
		}
		return rtn.toString();
	}

	private String createPkElement() {
		String rtn = "";
		rtn += "        <id name=\"id\">\n";
		rtn += "        <column name=\"id\"/>\n";
		rtn += "        <generator class=\"assigned\" />\n";
		rtn += "        </id>\n";
		return rtn;
	}

	private String createElement(PropertyDescriptor fd) {
		String rtn = "";
		try {
			Class<?> fieldType = fd.getPropertyType();
			String fieldName = fd.getName();

			if (fieldName.indexOf("_") >= 0) {
				return rtn;
			}

			if (fieldName.equals("class")) {
				return rtn;
			}

			if (fieldName.equals("id")) {
				return rtn;
			}

			if (fieldType.equals(Long.TYPE)) { // Long
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(Integer.TYPE)) { // Int
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(Double.TYPE)) { // Double
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(String.class)) { // String
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName
						+ "\" length=\"200\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (java.util.Collection.class.isAssignableFrom(fieldType)) { // Collection
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(Float.TYPE)) { // Float
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(java.sql.Date.class)) { // Date
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(java.sql.Timestamp.class)) { // Timestamp
				rtn += "        <property name=\"" + fieldName + "\" >\n";
				rtn += "            <column name=\"" + fieldName + "\" />\n";
				rtn += "        </property>\n";
				return rtn;
			}

			if (fieldType.equals(Boolean.TYPE)) { // Boolean
				rtn += "        <property name=\"" + fieldName + "\">\n";
				rtn += "            <column name=\"" + fieldName + "\"/>\n";
				rtn += "        </property>\n";
				return rtn;
			}
			if (fieldType.equals(byte[].class)) { // Byte[]
				rtn += "        <property name=\"" + fieldName + "\">\n";
				rtn += "            <column name=\"" + fieldName + "\"/>\n";
				rtn += "        </property>\n";
				return rtn;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtn;

	}

	public String fabricateXml(Class<?> voClazz) {
		String rtn = "";
		rtn += createHead(voClazz);
		rtn += createBody(voClazz);
		rtn += createFoo();

		return rtn;

	}

	public void buildHbmXml(String className, String targetPath) {
		try {
			Class<?> clazz = Class.forName(className);
			String cn = className.substring(className.lastIndexOf(".") + 1);

			String targetFn = targetPath + "\\" + cn + ".hbm.xml";
			String xml = fabricateXml(clazz);
			File xFile = new File(targetFn);
			FileWriter fw = new FileWriter(xFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(xml);

			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//int s = 0;

	public void batchBuildHbmXml(File d, String sourcePath) {
		if (d.isDirectory()) {
			File[] fs = d.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isDirectory()) {
					batchBuildHbmXml(fs[i], sourcePath);
				} else {
					if (fs[i].getName().endsWith("VO.java")) {
						String fn = fs[i].getAbsolutePath();

						String pattern = sourcePath.replaceAll("\\\\", ".");
						String className = fn.replaceAll("\\\\", ".");
						className = className.replaceFirst(pattern, "");
						className = className.substring(1,
								className.length() - 5);

						String targetPath = fs[i].getParent();
						buildHbmXml(className, targetPath);

					}
				}
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		CreateHbm hbm = new CreateHbm();
		// String xml = hbm.fabricateXml(UserVO.class);
		hbm.batchBuildHbmXml(new File(sourcePath), sourcePath);
		*/
	}

}
