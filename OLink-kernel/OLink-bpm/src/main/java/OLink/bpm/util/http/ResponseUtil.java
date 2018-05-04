package OLink.bpm.util.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtil {
	public static void setTextToResponse(HttpServletResponse response, String text) {
		PrintWriter out = null;
		try {
			response.setHeader("Content-Type", "text/html; charset=UTF-8");
			out = response.getWriter();
			out.print(text);
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			if(out != null)
				out.close();
		}
	}

	public static void setJsonToResponse(HttpServletResponse response, String JSON) {
		PrintWriter out = null;
		try {
			response.setHeader("Content-Type", "application/x-json; charset=UTF-8");
			response.setCharacterEncoding("utf-8"); //解决Json数据传递中文乱码
			out = response.getWriter();
			out.print(JSON);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				out.close();
		}
	}

	public static void setXmlToResponse(HttpServletResponse response, String XML) {
		PrintWriter out = null;
		try {
			response.setHeader("Content-Type", "application/xml; charset=UTF-8");
			out = response.getWriter();
			out.print(XML);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				out.close();
		}
	}
}
