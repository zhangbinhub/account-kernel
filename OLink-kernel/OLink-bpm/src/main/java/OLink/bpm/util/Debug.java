package OLink.bpm.util;

import org.apache.log4j.Logger;

/**
 * The system debug out.
 */
public class Debug {

    public static final boolean IS_DEBUG = true;
    private static final Logger log = Logger.getLogger(Debug.class);
    /**
     * Disable the default contrustor
     */
    private Debug(){
        super();
    }
    
    /**
     * print out the message.
     * @param o The message.
     */
    public static void println(Object o) {
        if (IS_DEBUG) {
           log.info(o);
        }
    }

    /**
     * Print out the message.
     * @param i The message.
     */
    public static void println(int i) {
        if (IS_DEBUG) {
        	 log.info(i);
        }
    }

    /**
     * Print out the message.
     * @param l The message 
     */
    public static void println(long l) {
        if (IS_DEBUG) {
        	 log.info(l);
        }
    }

    /**
     * Print out the message.
     * @param d The message
     */
    public static void println(double d) {
        if (IS_DEBUG) {
        	 log.info(d);
        }
    }
    /**
     * Print out the message.
     * @param b The message
     */
    public static void println(boolean b) {
        if (IS_DEBUG) {
        	 log.info(b);
        }
    }    
}
