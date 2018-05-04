package OLink.bpm.version.transfer.process;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.version.transfer.ITransfer;
import OLink.bpm.version.transfer.transferype.TransferType;

public class TransferProcessImpl implements ITransferProcess {

	public void processAllTransfer(String version) {
		Collection<?> transferTypes = TransferType.getAllTransferTypes();
		if (transferTypes != null) {
			for (Iterator<?> iterator = transferTypes.iterator(); iterator.hasNext();) {
				String className = (String) iterator.next();
				processTransfer(className, version);
			}
		}
	}

	public void processTransfer(String className, String version) {
		try {
			Class<?> c = Class.forName(className);
			ITransfer transfer = (ITransfer) c.newInstance();
			if ("2.4".equals(version)) {
				transfer.to2_4();
			}
			if ("2.5".equals(version)) {
				transfer.to2_5();
			}
			if ("2.5sp4".equals(version)) {
				transfer.to2_5SP4();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
