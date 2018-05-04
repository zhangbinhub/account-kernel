package OLink.bpm.util.pdf;

import eWAP.itext.text.Document;
import eWAP.itext.text.Element;
import eWAP.itext.text.pdf.BaseFont;
import eWAP.itext.text.pdf.PdfContentByte;
import eWAP.itext.text.pdf.PdfGState;
import eWAP.itext.text.pdf.PdfPageEventHelper;
import eWAP.itext.text.pdf.PdfWriter;

public class PageNumbersWatermark extends PdfPageEventHelper {
	protected BaseFont bfChinese;
	protected String watermark;
	protected PdfGState gstate;

	public PageNumbersWatermark(BaseFont bfChinese, String watermark) {
		this.bfChinese = bfChinese;
		this.watermark = watermark;
	}

	public void onOpenDocument(PdfWriter writer, Document document) {
		super.onOpenDocument(writer, document);

		gstate = new PdfGState();
		gstate.setFillOpacity(0.3f);
		gstate.setStrokeOpacity(0.3f);
	}

	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.setGState(gstate);
		cb.beginText();
		cb.setFontAndSize(bfChinese, 48);
		cb.showTextAligned(Element.ALIGN_CENTER, watermark, document.getPageSize().getWidth() / 2, document
				.getPageSize().getHeight() / 2, 45);
		cb.endText();
		cb.restoreState();

	}
}
