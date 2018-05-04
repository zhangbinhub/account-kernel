package OLink.bpm.util.picture;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * The picture utility.
 */
public class PictureUtil {	
	/**
	 * Convert to image
	 * @param sf The source file name.
	 * @param df The target file name.
	 * @param width The target image width
	 * @param height The target image height
	 * @param color The background color.
	 * @throws Exception
	 */
	public static void convertImage(String sf, String df, int width,
            int height, Color color) throws Exception {
        
	    if (sf == null || sf.equals("")) 
            throw new Exception("Unvalid file parameters.");
        
	    if (df == null || df.equals(""))
            df = sf;
        
	    File _infile = new File(sf);
        Image src = javax.imageio.ImageIO.read(_infile);

        int x = 0, y = 0;
        int w = src.getWidth(null);
        int h = src.getHeight(null);

        if ((double) w / (double) h <= (double) width / (double) height) {
            w = w * height / h;
            h = height;
            x = (width - w) / 2;
        } else {
            h = h * width / w;
            w = width;
            y = (height - h) / 2;
        }
        
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.drawImage(src, x, y, w, h, color, null);
        FileOutputStream out = new FileOutputStream(df); //输出到文件流
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image);
        out.close();
    }
	
	/**
     * Convert to jpeg image
     * @param in The source file input stream.
     * @param out The target file ouput stream.
     * @param width The target image width
     * @param height The target image height
     * @param bgcolor  The background color.
     * @throws Exception
     */
    public static void toJpegImage(InputStream in, OutputStream out, int width,
            int height, Color bgcolor) throws Exception {
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
        BufferedImage src = decoder.decodeAsBufferedImage();

        int x = 0, y = 0;
        int w = src.getWidth();
        int h = src.getHeight();

        if ((double) w / (double) h <= (double) width / (double) height) {
            w = w * height / h;
            h = height;
            x = (width - w) / 2;
        } else {
            h = h * width / w;
            w = width;
            y = (height - h) / 2;
        }

        Graphics g = image.getGraphics();
        g.drawImage(src, x, y, w, h, bgcolor, null);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image);

        in.close();
        out.close();
    }
}
