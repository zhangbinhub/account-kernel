package OLink.bpm.core.overview;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.workflow.element.*;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.element.AutoNode;
import OLink.bpm.core.workflow.element.CompleteNode;
import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.EndNode;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.element.TerminateNode;

import eWAP.itext.text.Cell;
import eWAP.itext.text.Font;
import eWAP.itext.text.Paragraph;
import eWAP.itext.text.Table;
import eWAP.itext.text.pdf.BaseFont;

/**
 * 工作流的pdf表格生成
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public class WorkFlowOverview implements IOverview {

	public Table buildOverview(String applicationId) throws Exception {
		Table table = new Table(1);
		table.setPadding(2);
		table.setSpacing(0);
		table.setBorderWidth(1);
		table.setWidth(100);
		if (!StringUtil.isBlank(applicationId)) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			Cell wc = new Cell();
			wc.setBackgroundColor(Color.gray);
			wc.addElement(new Paragraph("流程：", fontChinese));
			table.addCell(wc);

			BillDefiProcess bp = (BillDefiProcess) ProcessFactory
					.createProcess(BillDefiProcess.class);
			Collection<BillDefiVO> billdefis = bp.doSimpleQuery(
					new ParamsTable(), applicationId);
			if (billdefis != null) {
				wc = new Cell();
				Iterator<BillDefiVO> it = billdefis.iterator();
				Table bTable = new Table(1);
				bTable.setWidth(99);
				bTable.setPadding(0);
				bTable.setSpacing(0);
				bTable.setBorderWidth(1);
				Cell b_cell = null;
				while (it.hasNext()) {
					BillDefiVO bd = it.next();
					if (bd != null) {
						b_cell = new Cell();
						Table bt = new Table(1);
						bt.setWidth(99);
						bt.setPadding(0);
						bt.setSpacing(0);
						bt.setBorderWidth(1);

						Cell cell = new Cell();
						cell.setBorderWidth(0);
						String bName = bd.getSubject();
						cell.addElement(new Paragraph("流程名称："
								+ (bName != null ? bName : ""), fontChinese));
						String createTime = "";
						cell.addElement(new Paragraph("创建时间："
								+ (createTime != null ? createTime : ""),
								fontChinese));
						String description = "";
						cell.addElement(new Paragraph("描述："
								+ (description != null ? description : ""),
								fontChinese));
						bt.addCell(cell);

						cell = new Cell();
						cell.addElement(new Paragraph("节点信息：", fontChinese));
						Table nTable = createNode(bd);
						if (nTable != null) {
							cell.addElement(nTable);
						}
						bt.addCell(cell);

						cell = new Cell();
						cell.addElement(new Paragraph("路径信息：", fontChinese));
						Table rTable = createRelation(bd);
						if (rTable != null) {
							cell.addElement(rTable);
						}
						bt.addCell(cell);

						b_cell.addElement(bt);
						bTable.addCell(b_cell);
					}
				}
				wc.addElement(bTable);
				table.addCell(wc);
			}
		}
		return table;
	}

	// 流程节点信息
	private Table createNode(BillDefiVO bd) throws Exception {
		if (bd != null) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			FlowDiagram fd = bd.toFlowDiagram();
			Collection<Element> elements = fd.getAllElements();

			Table nt = new Table(6);
			nt.setWidth(99);
			nt.setPadding(0);
			nt.setSpacing(0);
			nt.setBorderWidth(1);
			if (elements != null) {
				Iterator<Element> it = elements.iterator();
				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("节点名称", fontChinese));
				nt.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("类型", fontChinese));
				nt.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("状态标签", fontChinese));
				nt.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("描述", fontChinese));
				nt.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("是否分散", fontChinese));
				nt.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("是否聚合", fontChinese));
				nt.addCell(cell);

				while (it.hasNext()) {
					Element element = it.next();
					if (element instanceof Node) {
						Node node = (Node) element;

						// 名称
						cell = new Cell();
						String nName = node.name;
						cell.addElement(new Paragraph(nName != null ? nName
								: "", fontChinese));
						nt.addCell(cell);
						// 类型
						cell = new Cell();
						String nType = "";
						if (node instanceof AbortNode) {
							nType = "取消节点";
						} else if (node instanceof AutoNode) {
							nType = "自动节电";
						} else if (node instanceof CompleteNode) {
							nType = "完成节点";
						} else if (node instanceof ManualNode) {
							nType = "人工节点";
						} else if (node instanceof StartNode) {
							nType = "开始节点";
						} else if (node instanceof SubFlow) {
							nType = "子流程节点";
						} else if (node instanceof SuspendNode) {
							nType = "挂起节点";
						} else if (node instanceof TerminateNode) {
							nType = "终止节点";
						}
						cell.addElement(new Paragraph(nType != null ? nType
								: "", fontChinese));
						nt.addCell(cell);

						// 状态标签
						cell = new Cell();
						String statelabel = node.statelabel;
						cell.addElement(new Paragraph(
								statelabel != null ? statelabel : "",
								fontChinese));
						nt.addCell(cell);

						// 描述
						cell = new Cell();
						String description = "";
						cell.addElement(new Paragraph(
								description != null ? description : "",
								fontChinese));
						nt.addCell(cell);

						// 是否分散
						cell = new Cell();
						Collection<Relation> rns = fd.getNodeNextRelation(node);
						String dispersion = "否";
						if (rns != null && rns.size() > 1
								&& !(node instanceof StartNode))
							dispersion = "是";
						cell.addElement(new Paragraph(
								dispersion != null ? dispersion : "",
								fontChinese));
						nt.addCell(cell);

						// 是否聚合
						cell = new Cell();
						Collection<Element> rbs = fd
								.getNodeBeforeRelation(node, false);
						String poly = "否";
						if (rbs != null
								&& rbs.size() > 1
								&& !(node instanceof TerminateNode
										|| node instanceof EndNode || node instanceof CompleteNode)) {
							poly = "是";
						}
						cell.addElement(new Paragraph(poly != null ? poly : "",
								fontChinese));
						nt.addCell(cell);

						// 审批人
						if (node instanceof ManualNode) {
							cell = new Cell();
							cell.setColspan(6);
							ManualNode mNode = (ManualNode) node;
							String namelist = mNode.namelist;
							cell.addElement(new Paragraph("审批人：\n"
									+ (namelist != null ? namelist : ""),
									fontChinese));
							nt.addCell(cell);
						}

						if (node instanceof ManualNode) {
							ManualNode mNode = (ManualNode) node;
							String actorListScript = mNode.actorListScript;
							if (!StringUtil.isBlank(actorListScript)) {
								cell = new Cell();
								cell.setColspan(6);
								cell.addElement(new Paragraph("审批脚本：\n"
										+ StringUtil
												.dencodeHTML(actorListScript),
										fontChinese));
								nt.addCell(cell);
							}
						}

					}
				}
			}
			return nt;
		}
		return null;
	}

	// 流程路径信息
	private Table createRelation(BillDefiVO bd) throws Exception {
		if (bd != null) {
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
			FlowDiagram fd = bd.toFlowDiagram();
			Collection<Element> elements = fd.getAllElements();

			if (elements != null && elements.size() > 0) {
				Table rTable = new Table(3);
				rTable.setWidth(99);
				rTable.setPadding(0);
				rTable.setSpacing(0);
				rTable.setBorderWidth(1);
				// 表头
				Cell cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("路径名称", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("模式", fontChinese));
				rTable.addCell(cell);

				cell = new Cell();
				cell.setBackgroundColor(Color.gray);
				cell.addElement(new Paragraph("描述", fontChinese));
				rTable.addCell(cell);

				Iterator<Element> it = elements.iterator();
				while (it.hasNext()) {
					Element element = it.next();
					if (element instanceof Relation) {
						Relation relation = (Relation) element;
						if (!StringUtil.isBlank(relation.name)
								|| !StringUtil.isBlank(relation.editMode)) {
							// 路径名称
							cell = new Cell();
							String rName = relation.name;
							cell.addElement(new Paragraph(rName != null ? rName
									: "", fontChinese));
							rTable.addCell(cell);

							// 模式
							cell = new Cell();
							String editMode = "";
							if ("00".equals(relation.editMode)) {
								editMode = "设计模式";
							} else if ("01".equals(relation.editMode)) {
								editMode = "代码模式";
							}
							cell.addElement(new Paragraph(
									editMode != null ? editMode : "",
									fontChinese));
							rTable.addCell(cell);

							// 描述
							cell = new Cell();
							String description = "";
							cell.addElement(new Paragraph(
									description != null ? description : "",
									fontChinese));
							rTable.addCell(cell);
						}

						// 路径进入条件
						if ("00".equals(relation.editMode)) {
							String processDescription = relation.processDescription;
							if (!StringUtil.isBlank(processDescription)) {
								cell = new Cell();
								cell.setColspan(3);
								cell
										.addElement(new Paragraph(
												"路径进入条件（设计模式）：\n"
														+ StringUtil
																.dencodeHTML(processDescription),
												fontChinese));
								rTable.addCell(cell);
							}
						} else if ("01".equals(relation.editMode)) {
							String condition = relation.condition;
							if (!StringUtil.isBlank(condition)) {
								cell = new Cell();
								cell.setColspan(3);
								cell.addElement(new Paragraph("路径进入条件（代码模式）：\n"
										+ StringUtil.dencodeHTML(condition),
										fontChinese));
								rTable.addCell(cell);
							}

						}

						// 路径执行脚本
						String action = relation.action;
						if (!StringUtil.isBlank(action)) {
							cell = new Cell();
							cell.setColspan(3);
							cell.addElement(new Paragraph("路径执行脚本：\n"
									+ StringUtil.dencodeHTML(action),
									fontChinese));
							rTable.addCell(cell);
						}

						// 路径送出校验脚本

						String validateScript = relation.validateScript;
						if (!StringUtil.isBlank(validateScript)) {
							cell = new Cell();
							cell.setColspan(3);
							cell.addElement(new Paragraph("路径送出校验脚本：\n"
									+ StringUtil.dencodeHTML(validateScript),
									fontChinese));
							rTable.addCell(cell);
						}

					}
				}
				return rTable;
			}
		}
		return null;
	}

}
