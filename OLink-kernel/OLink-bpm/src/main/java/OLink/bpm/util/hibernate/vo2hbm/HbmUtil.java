package OLink.bpm.util.hibernate.vo2hbm;

import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

public class HbmUtil {

	/**
	 * Retrieve the hbl header string.
	 * @param voClazz The value object class name
	 * @return The hbl header string.
	 */
	private String getHead(Class<?> voClazz) {
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
	
	/**
	 * Retrieve the hbl body string.
	 * @param voClazz The value object class name
	 * @return The hbl header string.
	 */
	private String getBody(Class<?> voClazz) {
		StringBuffer rtn = new StringBuffer(getPkElement());

		PropertyDescriptor[] encf = PropertyUtils
                .getPropertyDescriptors(voClazz);
        for (int i = 0; i < encf.length; i++)
            rtn.append(getElement(encf[i]));
        
        return rtn.toString();
	}
	
	/**
	 * Retrieve the hbl tail string.
	 * @return The hbl tail string.
	 */
	private String getTail() {
		String rtn = "";
	    rtn += "    </class>\n";
	    rtn += "</hibernate-mapping>\n";
		return rtn;
	}
		
	/**
	 * Retrieve the primary key element string.  
	 * @return The primary key element string.
	 */
	private String getPkElement() {
        String rtn = "";
        rtn += "        <id name=\"id\">\n";
        rtn += "        <column name=\"id\"/>\n";
        rtn += "        <generator class=\"assigned\" />\n";
        rtn += "        </id>\n";
        return rtn;
    }
	
	/**
	 * Retrieve the field element string.  
	 * @param fd The field
	 * @return The field element string.  
	 */
	private String getElement(PropertyDescriptor fd) {        
		Class<?> fieldType = fd.getPropertyType();
        String fieldName = fd.getName();
		String rtn = "";

        if (fieldName.indexOf("_") >= 0) {
            return rtn;
        }

        if (fieldName.equals("class")) {
            return rtn;
        }

        if (fieldName.equals("id")) {
            return rtn;
        }

        if (fieldType.equals(Long.TYPE)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName + "\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(Integer.TYPE)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName + "\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(Double.TYPE)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName + "\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(String.class)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName
                    + "\" length=\"200\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(Float.TYPE)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName + "\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(java.sql.Date.class)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName + "\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(java.sql.Timestamp.class)) {
            rtn += "        <property name=\"" + fieldName + "\" >\n";
            rtn += "            <column name=\"" + fieldName + "\" />\n";
            rtn += "        </property>\n";
            return rtn;
        }

        if (fieldType.equals(Boolean.TYPE)) {
            rtn += "        <property name=\"" + fieldName + "\">\n";
            rtn += "            <column name=\"" + fieldName + "\"/>\n";
            rtn += "        </property>\n";
            return rtn;
        }
        if (fieldType.equals(byte[].class)) {
            rtn += "        <property name=\"" + fieldName + "\">\n";
            rtn += "            <column name=\"" + fieldName + "\"/>\n";
            rtn += "        </property>\n";
            return rtn;
        }
        
        return rtn;
	}
	
	/**
	 * Retrieve the hbl file string 
	 * @param voClazz The value object class.
	 * @return The  hbl file string.
	 */
	public String fabricateXml(Class<?> voClazz) {
		String rtn = "";
		rtn += getHead(voClazz);
		rtn += getBody(voClazz);
		rtn += getTail();
		
		return rtn;
		
	}
	
	/**
	 * Build the hbl xml file.
	 * @param className The target value objet class name.
	 * @param targetPath The target file path.
	 * @throws Exception
	 */
	public void buildHbmXml(String className, String targetPath) throws Exception {
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
	}
	
	/**
	 * Build the hbl xml file batchly.
	 * @param d The file path of the value object java source path. 
	 * @param sp The sourfile path.
	 * @throws Exception
	 */
	public void batchBuildHbmXml(File d, String sp) throws Exception {
        if (d.isDirectory()) {
            File[] fs = d.listFiles();
            for (int i = 0; i < fs.length; i++) {
                if (fs[i].isDirectory()) {
                    batchBuildHbmXml(fs[i], sp);
                } else {
                    if (fs[i].getName().endsWith("VO.java")) {
                        String fn = fs[i].getAbsolutePath();

                        String pattern = sp.replaceAll("\\\\", ".");
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
	
	public static void main(String[] args)  throws Exception{
		/*
	    HbmUtil hbm = new HbmUtil();
        hbm.batchBuildHbmXml(new File(sourcePath), sourcePath);
        */
	}
	
	

}
