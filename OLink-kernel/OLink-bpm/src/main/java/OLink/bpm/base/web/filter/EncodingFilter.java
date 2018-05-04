package OLink.bpm.base.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import OLink.bpm.constans.Environment;

public class EncodingFilter extends HttpServlet implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -161915424294952305L;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain arg2) throws IOException, ServletException {
		
		// 获取参数前需设置编码
		request.setCharacterEncoding(Environment.getInstance().getEncoding());
		arg2.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}
}
