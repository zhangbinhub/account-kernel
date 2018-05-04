package OLink.bpm.util.text;

public class ParameterPart extends AbstractPart {
	private String name;

	public ParameterPart(String name) {
		this.name = name;
	}

	public String toText(TemplateContext context) {
		String text = context.getParams(name);
		if (text != null && text.trim().length() > 0) {
			return text;
		} else {
			return name;
		}

	}

}
