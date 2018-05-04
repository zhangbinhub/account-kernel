//Source file: E:\\CarAudit\\src\\caraudit\\utility\\Sequence.java

package OLink.bpm.core.dynaform.dts.excelimport.utility;

/**
 * <p>Title: </p>
 * <p>Description: This is a software porject. Guangdong province govement
 * caraudit office will use it.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Cyberway</p>
 *
 * @author James Zhou
 * @version 1.0
 */
public class Sequence {
  private static long _counter = 0;
  
  private static Object obj =  new Object();

  /**
   * @roseuid 3D86A1F00037
   */
  public Sequence() {

  }

  /**
   * @return long
   * @roseuid 3D86A1F00041
   */
  public static synchronized long getSequence() {
    try {
      long rtn = System.currentTimeMillis();
      while (rtn == _counter) {
    	obj.wait(1);
        rtn = System.currentTimeMillis();
      }
      _counter = rtn;
      return rtn;
    }
    catch (Exception ie) {
      return System.currentTimeMillis();
    }
  }

}
