package OLink.bpm.util.text;

public class TextPart extends AbstractPart {
	private String text;
	public TextPart(String text) {
		this.text = text;
	}

	public String toText(TemplateContext context) {
		return text;
	}

}
