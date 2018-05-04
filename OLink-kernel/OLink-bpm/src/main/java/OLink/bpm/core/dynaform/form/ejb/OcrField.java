//Source file: C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\OcrField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

public class OcrField extends FormField 
{
   
   /**
	 * 
	 */
	private static final long serialVersionUID = 7346290720046520313L;

/**
   @roseuid 41ECB66C0362
    */
   public OcrField() throws Exception
   {
    throw new Exception("This type field does not support yeat!");
   }
   
   /**
   @return boolean
   @roseuid 41ECB66C0380
    */
   public ValidateMessage validate(IRunner runner, Document doc) throws Exception
   {
    return null;
   }
   
   /**
   @roseuid 41ECB66C038A
    */
   public void store() 
   {
    
   }
   
   /**
   @param tmpltStr
   @return FormField
   @roseuid 41ECB66D001A
    */
   public FormField init(String tmpltStr) 
   {
    return null;
   }
	public String toTemplate(){
		return null;
	}

	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}


}
