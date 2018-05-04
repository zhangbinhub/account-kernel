package OLink.bpm.version.transfer;

import OLink.bpm.version.transfer.process.TransferProcessFactory;
import org.apache.log4j.Logger;

public class AllTransfer {

	private final static Logger LOG = Logger.getLogger(AllTransfer.class);

	public void to2_4() {
		LOG.info("---->begin to transfer all data needed!");
		TransferProcessFactory.createDefaultTransferProcess().processAllTransfer("2.4");
		LOG.info("---->transfer all data successfully!");
	}

	public void to2_5() {
		LOG.info("---->begin to transfer all data needed!");
		TransferProcessFactory.createDefaultTransferProcess().processAllTransfer("2.5");
		LOG.info("---->transfer all data successfully!");
	}
	
	public void to2_5SP4() {
		LOG.info("---->begin to transfer all data needed!");
		TransferProcessFactory.createDefaultTransferProcess().processAllTransfer("2.5sp4");
		LOG.info("---->transfer all data successfully!");
	}
	
	public void to2_6() {
		LOG.info("---->begin to transfer all data needed!");
		TransferProcessFactory.createDefaultTransferProcess().processAllTransfer("2.6");
		LOG.info("---->transfer all data successfully!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// new AllTransfer().to2_5SP4();
		System.out.println("============================迁移开始========================");
		String version = "";
		String transferElement = "";
		for (int i = 0; i < args.length; i++) {
			System.out.println("参数" + i + "：" + args[i]);

			if (args[i].startsWith("version")) {
				String[] array = args[i].split("=");
				if (array.length >= 2) {
					version = array[1];
					System.out.println("迁移至版本: " + array[1]);
				}
			} else if (args[i].startsWith("transfer")) {
				String[] array = args[i].split("=");
				if (array.length >= 2) {
					transferElement = array[1];
					char oldChar = transferElement.charAt(0);
					char newChar = (char) (oldChar - 32);
					// 首字母转大写
					transferElement = transferElement.replace(oldChar, newChar);
					System.out.println("迁移的元素: " + transferElement);
				}
			}
		}

		if ("all".equals(transferElement.toLowerCase())) {
			TransferProcessFactory.createDefaultTransferProcess().processAllTransfer(version);
		} else {
			try {
				String packageName = "OLink.bpm.version.transfer.";
				String className = packageName + transferElement + "Transfer";
				TransferProcessFactory.createDefaultTransferProcess().processTransfer(className, version);
			} catch (Exception e) {
				System.out.println("元素不存在: " + transferElement);
			}
		}
		System.out.println("============================迁移完成========================");
	}

}
