package OLink.bpm.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;

public class CheckCodeServlet extends HttpServlet {
	
	private static final long serialVersionUID = -3765376273799867603L;

	private static int WIDTH = 60;

	private static int HEIGHT = 20;

	private String CheckCode_Session = Web.SESSION_ATTRIBUTE_CHECKCODE;

	/**
	 * HttpServlet方法实现，验证码图片升成逻辑 1.设置浏览器不要缓存此图片 2.创建内存图象并获得其图形上下文 3.产生随机的认证码
	 * 4.产生图像 5.结束图像的绘制过程，完成图像 6.将图像输出到客户端 7.将当前验证码存入到Session
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		// 设置浏览器不要缓存此图片
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		ServletOutputStream sos = response.getOutputStream();

		// 创建内存图象并获得其图形上下文
		BufferedImage image = new BufferedImage(WIDTH+6, HEIGHT+4,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		// 产生随机的认证码
		char[] rands = generateCheckCode();

		// 产生图像
		drawBackground(g);
		drawRands(g, rands);

		// 结束图像的绘制过程，完成图像
		g.dispose();

		// 将图像输出到客户端
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ImageIO.write(image, "JPEG", bos);

		byte[] buf = bos.toByteArray();
		response.setContentLength(buf.length);

		// 下面的语句也可写成：bos.writeTo(sos);
		sos.write(buf);
		bos.close();
		sos.close();

		// 将当前验证码存入到Session中
		session.setAttribute(CheckCode_Session, String.valueOf(rands));
	}

	/**
	 * 生成验证码
	 * 
	 * @return
	 */
	private char[] generateCheckCode() {
		// 定义验证码的字符表
		// 考虑到1和l难以区分,将l大写为L
		String chars = "0123456789abcdefghijkLmnopqrstuvwxyz";

		char[] rands = new char[4];
		for (int i = 0; i < 4; i++) {
			//int rand = Double.valueOf(Math.random() * 36).intValue();
			int rand = new Random().nextInt(36);
			rands[i] = chars.charAt(rand);
		}

		return rands;
	}

	/**
	 * 生成图片
	 * 
	 * @param g
	 * @param rands
	 */
	private void drawRands(Graphics g, char[] rands) {
		g.setColor(new Color(0x1f1f1f));
		// 创建字体，字体的大小应该根据图片的高度来定。
		Font font = new Font("Fixedsys", Font.PLAIN, HEIGHT);
		// 设置字体。
		g.setFont(font);
		int xx = WIDTH / (rands.length + 1);
		int codeY = HEIGHT;
		for (int i = 0; i < rands.length; i++) {
			g.drawString("" + rands[i], (i + 1) * xx, codeY);
		}
	}

	/**
	 * 画干扰背景
	 * 
	 * @param g
	 */
	private void drawBackground(Graphics gd) {
		// 创建一个随机数生成器类
		Random r = new Random();
		gd.setColor(Color.black);
		gd.clipRect(0, 0, WIDTH+6, HEIGHT+4);
		// 画背景
		gd.setColor(Color.white);
		gd.fillRect(1, 1, WIDTH+4, HEIGHT+2);
		// 随机干扰点
//		for (int x = 3; x < WIDTH + 4; x += 4) {
//			for (int y = 2; y < HEIGHT + 3; y += 3) {
//				gd.setColor(Color.black);
//				gd.drawOval(x, y, 1, 0);
//			}
//		}
		// 随机产生160条干扰线，使图象中的认证码不易被其它程序探测到。
		for (int i = 0; i < 30; i++) {
			int x = r.nextInt(WIDTH) + 2;
			int y = r.nextInt(HEIGHT) + 3;
			int xl = r.nextInt(12);
			int yl = r.nextInt(12);
			// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
			int red = r.nextInt(255);
			int green = r.nextInt(255);
			int blue = r.nextInt(255);

			// 用随机产生的颜色将验证码绘制到图像中。
			gd.setColor(new Color(red, green, blue));
			gd.drawLine(x, y, x + xl, y + yl);
		}
	}

	// private void drawRands(Graphics g, char[] rands) {
	//
	// g.setColor(Color.black);
	// // 创建字体，字体的大小应该根据图片的高度来定。
	// Font font = new Font("Fixedsys", Font.PLAIN, HEIGHT - 2);
	// // 设置字体。
	// g.setFont(font);
	//
	// // 在不同的高度上输出验证码的每个字符
	// // 1,10,20,30 左右位置，15高度
	// g.drawString("" + rands[0], 3, 13);
	// g.drawString("" + rands[1], 16, 13);
	// g.drawString("" + rands[2], 29, 13);
	// g.drawString("" + rands[3], 42, 13);
	// }
	// private void drawBackground(Graphics g) {
	// Random r = new Random();
	// // 画背景
	// g.setColor(new Color(0xffffff));
	// g.fillRect(0, 0, WIDTH, HEIGHT);
	// // 随机干扰点
	// for (int x = 0; x < WIDTH; x++) {
	// for (int y = 0; y < HEIGHT; y++) {
	// int t = r.nextInt(3);
	// if (t == 0) {
	// // 蓝绿
	// g.setColor(new Color(200, 248, 208));
	// g.drawOval(x, y, 1, 0);
	// }
	// if (t == 1) {
	// // 白
	// g.setColor(new Color(255, 255, 255));
	// g.drawOval(x, y, 1, 0);
	// }
	// if (t == 2) {
	// // 淡紫
	// g.setColor(new Color(200, 200, 248));
	// g.drawOval(x, y, 1, 0);
	// }
	// }
	// }
	// }

	/**
	 * public void CreateImage(string Code) {
	 * System.Drawing.Text.PrivateFontCollection pfc = new
	 * System.Drawing.Text.PrivateFontCollection(); pfc.AddFontFile(
	 * HttpContext.Current.Server.MapPath( "~/File/Font/arial.ttf")); //添加字体的集合
	 * 
	 * int iwidth = (int)(Code.Length * 15); System.Drawing.Bitmap image = new
	 * System.Drawing.Bitmap(iwidth, 24); Graphics g =
	 * Graphics.FromImage(image);
	 * 
	 * Font f = new System.Drawing.Font(pfc.Families[0], 15,FontStyle.Bold,
	 * GraphicsUnit.Pixel);//创建font
	 * 
	 * Brush b = new System.Drawing.SolidBrush(Color.White);
	 * 
	 * g.Clear(Color.Blue);
	 * 
	 * //画字 g.DrawString(Code, f, b, 3, 3);
	 * 
	 * System.IO.MemoryStream ms = new System.IO.MemoryStream();
	 * image.Save(ms,System.Drawing.Imaging.ImageFormat.Jpeg);
	 * 
	 * Response.ContentType = "image/Jpeg"; Response.BinaryWrite(ms.ToArray());
	 * g.Dispose(); image.Dispose(); }
	 */

}
