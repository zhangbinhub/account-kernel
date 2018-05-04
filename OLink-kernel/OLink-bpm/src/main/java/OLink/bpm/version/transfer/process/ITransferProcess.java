package OLink.bpm.version.transfer.process;

public interface ITransferProcess {

	void processAllTransfer(String version);
	void processTransfer(String className, String version);
}
