package OLink.bpm.core.workflow.element;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtil {

	public final static String TYPE_JPEG = "jpg";

	public final static String TYPE_GIF = "gif";

	public final static String TYPE_PNG = "png";

	private FlowDiagram _flowDiagram;

	public ImageUtil(FlowDiagram fd) {
		_flowDiagram = fd;
	}

	public void createImage(String imageType, String filepath) throws Exception {

		Rectangle r = _flowDiagram.getMaxRect();

		BufferedImage image = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_RGB);

		// Graphics g = image.getGraphics();
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		OGraphics og = new OGraphics(g);
		og.setCompressRate(1.0);
		og.setFont(PaintElement.DEF_FONT);
		_flowDiagram.paintTo(og, false);

		// happy modify---------------------start
		g.dispose();
		ImageWriter writer = null;
		Iterator<ImageWriter> iter = ImageIO
				.getImageWritersByFormatName("JPEG");
		if (iter.hasNext()) {
			writer = iter.next();
		}
		if (writer == null) {
			return;
		}

		IIOImage iioImage = new IIOImage(image, null, null);
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality((float) 0.8);
		ImageOutputStream outputStream = ImageIO
				.createImageOutputStream(new File(filepath));
		writer.setOutput(outputStream);
		writer.write(null, iioImage, param);

		outputStream.close();
		writer.dispose();
		// happy modify---------------------end

		// ImageIO.write(image, imageType, out);
		image = null;
		// out.close();
		g = null;

	}

	public void createMobileFlowImage(String imageType, OutputStream out)
			throws Exception {

		Rectangle r = _flowDiagram.getMaxRect();

		BufferedImage image = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		OGraphics og = new OGraphics(g);

		og.setCompressRate(0.5);
		og.setFont(PaintElement.DEF_FONT);

		_flowDiagram.paintMobile(og);

		ImageIO.write(image, imageType, out);

		out.close();
		image = null;
		g = null;
	}

	public void toImage(String filepath) throws Exception {
		// FileOutputStream out = new FileOutputStream(file);
		createImage(TYPE_JPEG, filepath);
		// out.close();
	}

	public void toMobileImage(File file) throws Exception {
		FileOutputStream out = new FileOutputStream(file);
		createMobileFlowImage(TYPE_PNG, out);
		out.close();
	}

}
