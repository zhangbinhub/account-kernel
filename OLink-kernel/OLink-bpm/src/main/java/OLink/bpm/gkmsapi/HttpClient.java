package OLink.bpm.gkmsapi;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpClient
{
	private static String INTERNAL_TRANS_CHARSET="ISO8859-1";
	private int m_port;
	private String m_host;
	private String m_charset="UTF-8";
	private String m_authorization=null;  	 //保存base64(user:md5(pwd))结果

	/**
	 * 构造函数
	 * @param h 服务器地址
	 * @param p 端口
	 */
	public HttpClient(String h, int p)
	{
		m_host= h;
		m_port = p;
	}

	/**
	 * 构造函数
	 * @param h 服务器地址
	 * @param p 端口
	 * @param auth base64 的 认证编码
	 */
	public HttpClient(String h, int p, String auth)
	{
		m_host= h;
		m_port = p;
		m_authorization = auth;
	}

	/**
	 * 向服务器发送XML请求
	 * @param contents : 请求的消息包(UTF-8编码)
	 * @return 服务器回复请求
	 */
	public String request(String method, String uri, String contents)throws Exception{
		String res = null;
		StringBuffer recv_buf=new StringBuffer(64*1024);   //保存服务器返回的结果
		//把XML转成UTF-8的编码
		byte[] cs=contents.getBytes(m_charset);

		//生成HTTP 请求包的头
		StringBuffer httpHeader=new StringBuffer(1024);
		httpHeader.append(method+" "+uri+" HTTP/1.1\r\n");
		httpHeader.append("Accept:*/*\r\n");
		httpHeader.append("Content-type:application/binary;charset="+m_charset+"\r\n");
		httpHeader.append("Host: "+m_host+":"+m_port+"\r\n");
		if(m_authorization!=null){
			httpHeader.append("Authorization: Basic "+m_authorization+"\r\n");
		}
		httpHeader.append("Content-Length: " +cs.length+ "\r\n");
		httpHeader.append("Cache-Control: no-cache\r\n");
		httpHeader.append("Connection: Close\r\n\r\n");

		System.out.println(">> request >> \r\n"+httpHeader.toString()+contents+"\r\n");

		//建立socket
	    Socket socket=new Socket(m_host,m_port);
		OutputStream out = socket.getOutputStream();

	    //发送HTTP请求
		out.write(httpHeader.toString().getBytes(m_charset));
	    out.write(cs);
	    out.flush();

		// 接收响应
		// 需要指定输入的编码方式为UTF-8
		// 提取返回的XML包从<response开始部分
		int fpos0 =0; // the pos of \r\n\r\n
		int fpos1 =0; // the pos of Content-Length: or Transfer-Encoding: chunked
		int fpos2 =0;
		int content_length = 0;
		boolean chunked = false;
		byte buf[] = new byte[1024*16];
	    DataInputStream input = new DataInputStream(socket.getInputStream());
	    while (true)
	    {
	    	int reclen = input.read(buf);
			if (reclen <=0 )
			{
				// error or the end
				break;
			}
			String s = new String(buf, 0, reclen, INTERNAL_TRANS_CHARSET);
			recv_buf.append( s );

			// check the end of the http header
			if (fpos0<=0)
        		fpos0 = recv_buf.indexOf("\r\n\r\n");

        	if(fpos0 == -1)
        	{
        		// not found the end of the header , co continue reading
        		continue;
        	}

        	// check transfer encoding type
			if ( fpos0>0 && fpos1 <= 0 )
			{
	        	fpos1 = recv_buf.indexOf("Content-Length:");

	        	// if has content-length, get the conten len
	        	if (fpos1 != -1 )
	        	{
		        	int p1 = recv_buf.indexOf(":", fpos1);
		        	int p2 = recv_buf.indexOf("\r\n", fpos1);
		        	String temp = recv_buf.substring(p1+1, p2).trim();
	    			content_length = Integer.parseInt(temp);
	        	}else
	    		if (fpos1 == -1 )
	    		{
	    			fpos1=recv_buf.indexOf("Transfer-Encoding: chunked");
	    			if (fpos1 >0 ) chunked = true;
	    		}
		    	if (fpos1 <=0 ) continue;

				// remove the http header !!!
				recv_buf.delete(0,fpos0+4);
			}

			// has content length
			if ( ! chunked )
			{
				// if has content length
				int recv_len = recv_buf.length();
				if ( recv_len == content_length) break;
				continue;
			}
			// if unknow content length
			int n = -1;
			while (true)
			{
				// the http protocol :
				// ---------------------------------------
				//
				// HTTP 200 OK
				// ....
				// Transfer-Encoding: chunked
				// ...
				// \r\n
				// \r\n
				// 123\r\n
				// the conents 123 bytes
				// \r\n
				// 456\r\n
				// the conents 123 bytes
				// 0\r\n
				//
				// find the \r\n after 123 from the fpos2( the end of the \r\n\r\n )
				int fpos3 = recv_buf.indexOf("\r\n", fpos2);
    			if ( fpos3 == -1)
    			{
    				break;
				}
    			String temp = recv_buf.substring(fpos2, fpos3);
				n = Integer.parseInt(temp, 16); // hex

				int rlen = recv_buf.length() - fpos2 - n ;
				if (rlen >= 2 )
				{
					// remove the line: 123\r\n
					recv_buf.delete( fpos2 , fpos2 + temp.length() + 2 );

					// remove the end of the line: \r\n
					recv_buf.delete( fpos2+n , fpos2 + n + 2 );
					fpos2 = fpos2+n;
					rlen -= 2;
				}
				if (n==0 || rlen <= 0 ) break;
			}
			// means 0\r\n, 表示已经接收完所有内容了。
			if (n==0) break;
		} // end of while
	    out.close();
	    input.close();

	    buf = recv_buf.toString().getBytes(INTERNAL_TRANS_CHARSET);
		res = new String(buf, m_charset);
		return res;
	}

}
