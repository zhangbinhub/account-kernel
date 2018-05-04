package OLink.bpm.version.transfer.process;

public class TransferProcessFactory {

	public static ITransferProcess createDefaultTransferProcess(){
		return new TransferProcessImpl();
	}
}
