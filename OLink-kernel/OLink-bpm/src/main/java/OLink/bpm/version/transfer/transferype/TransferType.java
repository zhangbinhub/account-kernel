package OLink.bpm.version.transfer.transferype;

import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.version.transfer.ApplicationTransfer;
import OLink.bpm.version.transfer.PageTransfer;
import OLink.bpm.version.transfer.ViewTransfer;
import OLink.bpm.version.transfer.WorkFlowTransfer;
import OLink.bpm.version.transfer.BillDefiTransfer;
import OLink.bpm.version.transfer.DepartmentTransfer;
import OLink.bpm.version.transfer.FormTransfer;
import OLink.bpm.version.transfer.OperationTransfer;
import OLink.bpm.version.transfer.PermissionTransfer;
import OLink.bpm.version.transfer.ResourceTransfer;
import OLink.bpm.version.transfer.SummaryCfgTransfer;
import OLink.bpm.version.transfer.UserDefinedTransfer;

public class TransferType {

	public static String APPLICATIONTRANSFER = "ApplicationTransfer";
	public static String RESOURCETRANSFER = "ResourceTransfer";
	public static String VIEWTRANSFER = "ViewTransfer";
	public static String PERMISSIONTRANSFER = "PermissionTransfer";
	public static String PAGETRANSFER = "PageTransfer";
	public static String BILLDEFITRANSFER = "BillDefiTransfer";
	public static String SUMMARYCFGTRANSFER = "SummaryCfgTransfer";
	public static String USERDEFINEDTRANSFER = "UserDefinedTransfer";
	public static String WORKFLOWTRANSFER = "WorkFlowTransfer";
	public static String DEPARTMENTTRANSFER = "DepartmentTransfer";
	public static String FORMTRANSFER = "FormTransfer";
	
	

	private static Collection<String> transferTypes = new ArrayList<String>();

	static {
		transferTypes.add(ApplicationTransfer.class.getName());
		transferTypes.add(ResourceTransfer.class.getName());
		transferTypes.add(ViewTransfer.class.getName());
		transferTypes.add(PermissionTransfer.class.getName());
		transferTypes.add(PageTransfer.class.getName());
		transferTypes.add(BillDefiTransfer.class.getName());
		transferTypes.add(SummaryCfgTransfer.class.getName());
		transferTypes.add(OperationTransfer.class.getName());
		transferTypes.add(UserDefinedTransfer.class.getName());
		transferTypes.add(WorkFlowTransfer.class.getName());
		transferTypes.add(DepartmentTransfer.class.getName());
		transferTypes.add(FormTransfer.class.getName());
	}

	public static Collection<String> getAllTransferTypes() {
		return transferTypes;
	}

	public static void addTransferType(String transferType) {
		transferTypes.add(transferType);
	}
}
