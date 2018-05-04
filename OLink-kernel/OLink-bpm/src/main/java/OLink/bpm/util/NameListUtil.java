package OLink.bpm.util;


public class NameListUtil {
  //获取所有ID通过指定连接符
public static String getAllIdBySep(String str, String separator){
  if (str == null || str.length() <= 0) {
    return "";
  }

  String[] pers = StringUtil.split(str, ';');

  StringBuffer allID = new StringBuffer();
  for(int i=0; i<pers.length; i++){
//      String tmp = getId(pers[i]);
//      if(tmp != null){
      allID.append(getId(pers[i]) + separator);
//      }
  }
  return allID.toString();
}

  //获取所有ID
  public static String getAllId(String str){
    if (str == null || str.length() <= 0) {
      return "";
    }

    String[] pers = StringUtil.split(str, ';');

    StringBuffer allID = new StringBuffer();
    for(int i=0; i<pers.length; i++){
//      String tmp = getId(pers[i]);
//      if(tmp != null){
        allID.append(getId(pers[i]) + ";");
//      }
    }
    return allID.toString();
  }
  //获取ID（角色或人员）
  public static String getId(String str) {
    if (str == null || str.trim().equals("")) {
      return null;
    }
    String[] tmp = StringUtil.split(str, '|');
    if (tmp != null && tmp.length >= 2) {
      return tmp[0];
    }
    else {
      return null;
    }
  }

  //获取部门字串
  public static String getDept(String str) {
    if (str == null || str.trim().equals("")) {
      return null;
    }
    String[] tmp = StringUtil.split(str, '|');
    if (tmp != null && tmp.length >= 2) {
      String part2 = tmp[1];
      int pos2 = part2.lastIndexOf("/");
      if (pos2 > 0) {
        return part2.substring(0, pos2);
      }
    }
    return null;
  }

  //获取角色字串（角色或人员）
  public static String getShortName(String str) {
    if (str == null || str.trim().equals("")) {
      return null;
    }
    String[] tmp = StringUtil.split(str, '|');
    if (tmp != null && tmp.length >= 2) {
      String part = tmp[1];
      int lastpos = part.lastIndexOf("/");
      if (lastpos > 0 && lastpos + 1 <= part.length()) {
        return part.substring(lastpos + 1, part.length());
      }
      else {
        return part;
      }
    }
    return null;
  }

  public static void main(String[] args) {
//    String test = "12345|赛百威公司/开发部/部门经理";
   // String test = "";
  }

}
