package OLink.bpm.util.pdf;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import OLink.bpm.constans.Environment;
import OLink.bpm.util.StringUtil;

import eWAP.itext.text.BadElementException;
import eWAP.itext.text.Document;
import eWAP.itext.text.DocumentException;
import eWAP.itext.text.Element;
import eWAP.itext.text.Font;
import eWAP.itext.text.HeaderFooter;
import eWAP.itext.text.Image;
import eWAP.itext.text.PageSize;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Phrase;
import eWAP.itext.text.pdf.BaseFont;
import eWAP.itext.text.pdf.PdfContentByte;
import eWAP.itext.text.pdf.PdfImportedPage;
import eWAP.itext.text.pdf.PdfPCell;
import eWAP.itext.text.pdf.PdfPTable;
import eWAP.itext.text.pdf.PdfReader;
import eWAP.itext.text.pdf.PdfWriter;

public class ObpmPdfDocument {
	BaseFont bfChinese;
	Document document;
	PdfWriter writer;
	PdfPTable table;
	String watermark;
	Font font;


	public ObpmPdfDocument(String webFileName) {
		this(webFileName, "");
	}
	
	public ObpmPdfDocument(String webFileName, String watermark) {
		try {
			bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			font = new Font(bfChinese, 9f);
			document = new Document(PageSize.A4);
			writer = PdfWriter.getInstance(document, new FileOutputStream(getFileRealPath(webFileName)));
            writer.setViewerPreferences(PdfWriter.HideMenubar | PdfWriter.HideToolbar); 
            //writer.setEncryption(PdfWriter.STRENGTH128BITS, null, null, PdfWriter.ALLOW_PRINTING);
            writer.setEncryption(new byte[]{}, new byte[]{}, PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
           // writer.setViewerPreferences(PdfWriter.HideWindowUI);
			this.watermark = watermark;
			if (!StringUtil.isBlank(this.watermark)) {
				// 设置水印事件
				writer.setPageEvent(new PageNumbersWatermark(this.bfChinese, this.watermark));
			}
			
			HeaderFooter footer = new HeaderFooter(new Phrase(" "), true); 
			footer.setBorder(eWAP.itext.text.Rectangle.NO_BORDER); 
			footer.setAlignment(Element.ALIGN_CENTER); 
			document.setFooter(footer); 
			document.open();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		document.close();
	}

	/**
	 * 插入新一页
	 * 
	 */
	public void addPage() {
			document.newPage();
	}

	/**
	 * 插入标题
	 * 
	 * @param title
	 */
	public void addTitle(String title) {
		try {
			Font titleFont = new Font(bfChinese, 10f, Font.BOLD);
			document.add(new Paragraph(title, titleFont));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入表格开始
	 */
	public void addTableStart(int numColumns) {
		table = new PdfPTable(numColumns);
		table.setWidthPercentage(100f);
	}

	/**
	 * 插入数组行
	 * 
	 * @param array
	 */
	public void addArrayRow(String[] array) {
		for (int i = 0; i < array.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(array[i], font));
			cell.setBorder(0);
			table.addCell(cell);
		}
	}

	/**
	 * 插入列
	 * 
	 * @param text
	 */
	public void addCell(String text) {
		addCell(text, 0);
	}

	public void addCell(String text, int border) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		if (border > 0) {
			cell.setMinimumHeight(30f);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中 
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //垂直居中

			cell.setBorder(PdfPCell.BOX);
		} else {
			cell.setMinimumHeight(30f);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中 
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //垂直居中

			cell.setBorder(PdfPCell.NO_BORDER);
		}
		table.addCell(cell);
	}
	
	

	
	public void addCell(String text, int border,int spans,int color) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		if (border > 0 && spans>0) {
			cell.setMinimumHeight(30f);
			cell.setColspan(spans);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中 
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //垂直居中

			cell.setBorder(PdfPCell.BOX);
		} else if(spans>0){
			cell.setMinimumHeight(30f);
			cell.setColspan(spans);
			cell.setBorder(PdfPCell.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中 
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //垂直居中

			cell.setFixedHeight(30f);
		}else if(color >0 && spans>0 && border>0){
			cell.setMinimumHeight(30f);
			cell.setBackgroundColor(new Color(238,238,238));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中 
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //垂直居中

			cell.setBorder(PdfPCell.BOX);
		}
		table.addCell(cell);
	}
	
	public void addCell(String text, int border,int spans,int color,Float row) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		if(color >0 && spans>0 && border>0 && row.floatValue()>0){
			
			cell.setMinimumHeight(row.floatValue());
			
			cell.setColspan(spans);
			cell.setBorder(PdfPCell.BOX);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT); //水平居中 
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //垂直居中

			//cell.setFixedHeight(200f);
			table.addCell(cell);
		}
	}
	

	/**
	 * 插入表格结束
	 */
	public void addTableEnd() {
		try {
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 多个PDF合并
	 * @throws IOException 
	 * @throws DocumentException 
	 * */

	public void addPDF(String source,String title) throws IOException, DocumentException{
		//地址处理
		int lastString = source.indexOf("_");
		String temPath = null;
		if(lastString != -1){
			temPath = source.substring(1,lastString);
		}
		
		String  sourcePath = getFileRealPath(temPath).replaceAll("\\\\", "/");
		PdfReader reader = new PdfReader(sourcePath);
		PdfContentByte cb = writer.getDirectContent();
		int pageOfCurrentReaderPDF = 0;
		int pages = reader.getNumberOfPages();
		for(int i = 0;i<pages;i++){
	    document.newPage();
	    //document.addTitle(title);
	    pageOfCurrentReaderPDF++;
		PdfImportedPage page = writer.getImportedPage(reader,pageOfCurrentReaderPDF);
		cb.addTemplate(page, 0, 0);

		}
	}
	/**
	 * 插入图片
	 */
	public void addImage(String webFileName) {
		try {
			if (StringUtil.isBlank(webFileName)) {
				return;
			}

			// "/uploads/item/11de-b730-841a1ad0-aaec-e539445ba127.bmp_untitled.bmp";
			int lastIndex = webFileName.indexOf("_");
			if (lastIndex != -1) {
				webFileName = webFileName.substring(0, lastIndex);
			}
			Image image = Image.getInstance(getFileRealPath(webFileName));
			

			document.add(toFitPage(image));
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Image toFitPage(Image image) {
		float pageWidth = document.getPageSize().getWidth() - 90;
		float pageHight = document.getPageSize().getHeight() - 160;

		if (image.getWidth() > pageWidth || image.getHeight() > pageHight) {
			image.scaleToFit(pageWidth, pageHight);
		}
		return image;
	}

	/**
	 * 插入图片
	 */
	public void addImageRow(String label, String webFileName) {
		try {
			if (StringUtil.isBlank(webFileName)) {
				return;
			}

			int lastIndex = webFileName.indexOf("_");
			if (lastIndex != -1) {
				webFileName = webFileName.substring(0, lastIndex);
			}

			Image image = Image.getInstance(webFileName);
			table.addCell(new Phrase(label, new Font(bfChinese)));

			toFitPage(image);
			table.addCell(image);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件真实路径名称
	 * 
	 * @param webFileName
	 * @return
	 */
	public String getFileRealPath(String webFileName) {
		Environment env = Environment.getInstance();
		String realfilename = env.getRealPath(webFileName);

		return realfilename;
	}

	/**
	 * 插入文本行
	 * 
	 * @param label
	 * @param value
	 */
	public void addTextRow(String label, String value) {
		PdfPCell cell0 = new PdfPCell(new Phrase(label, font));
		PdfPCell cell1 = new PdfPCell(new Phrase(value, font));

		cell0.setBorder(0);
		cell1.setBorder(0);

		table.addCell(cell0);
		table.addCell(cell1);
	}
}
