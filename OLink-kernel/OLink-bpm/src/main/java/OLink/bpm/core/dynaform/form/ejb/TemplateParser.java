package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.text.TemplateContext;
import org.htmlparser.Parser;

public class TemplateParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ComponentProcess cp = (ComponentProcess) ProcessFactory
					.createProcess(ComponentProcess.class);
			Component component = (Component) cp.doView("1175751225109000");

			TemplateContext context = TemplateContext.parse(component.getTemplatecontext());
			context.putParams("region", "r1");
			context.putParams("district", "d1");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Form parseTemplate(Form form, String template)
			throws Exception {
		Parser parser = new Parser();

		if (template == null) {
			template = "";
		}

		template = template.replaceAll("\\[计算插入模板\\]</MARQUEE>", "");

		parser.setInputHTML(template);
		TemplateProcessVisitor visitor = new TemplateProcessVisitor(form);
		parser.visitAllNodesWith(visitor);

		Form form2 = visitor.getResult();
		return form2;
	}

}
