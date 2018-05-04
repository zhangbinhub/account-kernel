package OLink.bpm.util.web;

public class StrutsUtil {
  public static String getCommandFromPath(String path) {
    if (path == null) {
      return path;
    }

    int i = path.lastIndexOf("/");
    if (i < 0) {
      return path;
    }
    else {
      return path.substring(i + 1);
    }
  }
}
