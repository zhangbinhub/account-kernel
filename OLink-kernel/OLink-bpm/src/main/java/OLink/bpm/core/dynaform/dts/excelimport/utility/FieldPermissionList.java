/*
 * Created on 2005-4-4
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.dts.excelimport.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.form.ejb.FormField;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldPermissionList {
    private Collection<FieldPermission> data = new ArrayList<FieldPermission>();
    private static final Logger log = Logger.getLogger(FieldPermissionList.class); 
    public FieldPermissionList(){
        
    }
    
		public void add(FieldPermission fp){
				if (fp != null) {
					data.add(fp);
				}
		}
		
		public static FieldPermissionList parser(String permissionListStr) {
		    FieldPermissionList permissionList = new FieldPermissionList();
				if (permissionListStr==null || permissionListStr.trim().length()<=0) {
					return permissionList;
				}
				FieldPermission tmp = null;
				String[] permissionArray = CommonUtil.split(permissionListStr, ";");
				for(int i=0; i<permissionArray.length; i++){
				        tmp = new FieldPermission(permissionArray[i]);
				        permissionList.add(tmp);
				}
				
				return permissionList;
		}
		
		public void remove(FieldPermission fieldPerm){
		   data.remove(fieldPerm);    
		}
		
		public void clear(){
		    data.clear();
		}
		
		public int checkPermission(FormField formField){
		    return this.checkPermission(formField.getName());
		}
		
		public int checkPermission(String fieldName){
		    if(data == null || data.size() <= 0){
		      return PermissionType.MODIFY;
		    }
		    Iterator<FieldPermission> iters = data.iterator();
		    while(iters.hasNext()){
		        //FieldPermission fieldPerm = (FieldPermission)iters.next();
		        FieldPermission fieldPerm = iters.next();
		        if(fieldName != null && fieldName.equals(fieldPerm.getFieldName())){
		            return fieldPerm.getPermisstionType();
		        }
		    }
		    return PermissionType.MODIFY;
		}
		
		public String toString(){
		    StringBuffer sb = new StringBuffer();
		    Iterator<FieldPermission> iter = data.iterator();
				while(iter.hasNext()) {
					//FieldPermission fieldPerm = (FieldPermission)iter.next();
					FieldPermission fieldPerm = iter.next();
					sb.append(fieldPerm.toString());
					sb.append(";");
				}
				return sb.toString();
		}
		
    public static void main(String[] args) {
        String str = "@a;#b;$c;@d;$e;#f";
        FieldPermissionList fieldPermList = FieldPermissionList.parser(str);
        log.info("fieldPermList->" + fieldPermList.toString());
        log.info("checkPermission-->"+fieldPermList.checkPermission("d"));
        FieldPermission fieldPerm = new FieldPermission("#b");
        log.info("fieldPerm->"+fieldPerm.toString());
        fieldPermList.remove(fieldPerm);
        log.info("fieldPermList3->" + fieldPermList.toString());
//        fieldPermList.clear();
//        log.info("fieldPermList4->" + fieldPermList.toString());
        
    }
}
