/*
 * Created on 2005-10-27
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.macro.util;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FactoryJsUtil {
  
  public Object createObject(String className) throws Exception {
    Class<?> cls = Class.forName(className);
    Object obj = cls.newInstance();
    return obj;
  }
  

}
