package OLink.bpm.core.formula;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import OLink.bpm.core.dynaform.document.dql.DqlBaseLexer;
import OLink.bpm.core.dynaform.document.dql.DqlBaseParser;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import eWAP.core.Tools;


public class FormulaTree {

	private static final double SEMICIRCLE = 180;

	private static final double DESCENDING = 20;

	public FormulaTree() {

	}

	public Map<String, FormulaNode> parse(String text) { // 解析
		DqlBaseLexer lexer = new DqlBaseLexer(new StringReader(text));
		DqlBaseParser parser = new DqlBaseParser(lexer);
		Map<String, FormulaNode> nodeMap = new TreeMap<String, FormulaNode>();

		try {
			parser.exprList();
			AST parent = parser.getAST();
			if (parent != null) {
				FormulaNode root = new FormulaNode();
				root.setId(Tools.getSequence());
				root.setText(parent.getText());

				nodeMap.put(root.getId(), root);

				parse(nodeMap, parent, root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeMap;
	}

	private void parse(Map<String, FormulaNode> nodeMap, AST ast, FormulaNode parent)
			throws Exception {
		AST lchild = ast.getFirstChild(); // 左节点

		AST rchild = null;
		if (lchild != null) {
			rchild = lchild.getNextSibling(); // 右节点
		}

		if (lchild != null) {
			FormulaNode lnode = addToParent(nodeMap, parent, lchild);
			if (lchild.getFirstChild() != null) {
				parse(nodeMap, lchild, lnode);
			}
		}

		if (rchild != null) {
			FormulaNode rnode = addToParent(nodeMap, parent, rchild);
			if (rchild.getFirstChild() != null) {
				parse(nodeMap, rchild, rnode);
			}
		}
	}

	public String dparse(Map<String, FormulaNode> nodeMap) { // 反解析
		StringBuffer formula = new StringBuffer();

		/* search root node */
		FormulaNode root = getRoot(nodeMap);

		if (root != null) {
			Stack<FormulaNode> stack = new Stack<FormulaNode>();
			stack.push(root);
			formula = dparse(formula, root, stack);
		}

		return formula.toString();
	}

	private StringBuffer dparse(StringBuffer formula, FormulaNode parent,
			Stack<FormulaNode> stack) {
		if (parent.getChildren() != null) {
			Collection<FormulaNode> colls = parent.getChildren();
			boolean isLeft = true;
			for (Iterator<FormulaNode> iter = colls.iterator(); iter.hasNext();) {
				FormulaNode node = iter.next();
				stack.push(node);
				if (node.getChildren() != null && node.getChildren().size() > 0) {
					formula.append(" (");
					dparse(formula, node, stack);
					if (isLeft) {
						formula.append(") ");
						formula.append(stack.pop().getText() + " ");
						isLeft = false;
					} else {
						formula.append(") ");
					}

				} else {
					if (isLeft) {
						formula.append(stack.pop().getText() + " ");
						isLeft = false;
					}
					formula.append(stack.pop().getText() + " ");
				}
			}
		}
		return formula;
	}

	private FormulaNode addToParent(Map<String, FormulaNode> nodeMap, FormulaNode parent, AST child)
			throws Exception {// 添加到父节点
		FormulaNode node = new FormulaNode();
		node.setId(Tools.getSequence());
		node.setText(child.getText());
		node.setParent(parent);

		nodeMap.put(node.getId(), node);

		parent.addChild(node);

		return node;
	}

	public String toTreeHtml(Map<String, FormulaNode> nodeMap, double ovalWidth, double ovalHeight,
			double startLeft, double startTop, double lineLength, double space,
			double angle) throws Exception {
		StringBuffer treeHtml = new StringBuffer();
		treeHtml.append("");
		String text = "";

		/* search root node */
		FormulaNode root = getRoot(nodeMap);

		if (root != null) {
			treeHtml
					.append("<v:oval id='"
							+ root.getId()
							+ "'strokecolor='red' fillcolor='yellow' style='position:absolute;left:"
							+ startLeft + ";top:" + startTop + ";width:"
							+ ovalWidth + ";height:" + ovalHeight + "'");
			treeHtml
					.append(" oncontextmenu='showmenuie5(this);return false;'>");
			treeHtml
					.append("<v:textbox  style='bottom : -0px;valign:center;align:center;Z-INDEX:1;position:ralative;left:"
							+ startLeft + ";top:" + startTop + "'>");
			treeHtml.append("<div align='center'>" + root.getText() + "</div>");
			treeHtml.append("</v:textbox>");
			treeHtml.append("</v:oval>");

			if (root.getChildren() != null) {
				treeHtml.append(toTreeHtml(root, new StringBuffer(), ovalWidth,
						ovalHeight, startLeft, startTop, lineLength, space,
						angle));
			}

		}

		text = "document.getElementById('treeDiv').innerHTML=\"" + treeHtml.toString()
				+ "\";";

		return text;
	}

	private FormulaNode getRoot(Map<String, FormulaNode> nodeMap) { // 获取根节点
		if (nodeMap != null && nodeMap.size() > 0) {
			for (Iterator<FormulaNode> iter = nodeMap.values().iterator(); iter.hasNext();) {
				FormulaNode node = iter.next();
				if (node.getParent() == null) {
					return node;
				}
			}
		}
		return null;
	}

	private String toTreeHtml(FormulaNode parent, StringBuffer html,
			double ovalWidth, double ovalHeight, double startLeft,
			double startTop, double lineLength, double space, double angle)
			throws Exception {
		Collection<FormulaNode> colls = parent.getChildren();

		boolean isLeft = true; // 是否为左节点

		double radius = ovalWidth / 2; // 半径

		double halfSpace = space / 2; // 线段两起始之间的距离的一半

		for (Iterator<FormulaNode> iter = colls.iterator(); iter.hasNext();) {
			FormulaNode element = iter.next();
			/** 计算 * */
			double fromX;

			double fromY = startTop
					+ (ovalHeight - (radius - Math.sqrt(Math.pow(radius, 2)
							- Math.pow(halfSpace, 2))));

			double toX;

			double toY = fromY
					+ (Math.cos(Math.PI * (angle / SEMICIRCLE)) * lineLength);

			if (isLeft) { // 左节点
				fromX = startLeft + radius - halfSpace;

				toX = fromX
						- (Math.sin(Math.PI * (angle / SEMICIRCLE)) * lineLength);

				isLeft = false;
			} else { // 右节点
				fromX = startLeft + 2 * radius - (radius - halfSpace);

				toX = fromX
						+ (Math.sin(Math.PI * (angle / SEMICIRCLE)) * lineLength);
			}

			double tmpStartLeft = toX - ovalWidth / 2;
			double tmpStartTop = toY;

			html.append(toChildHtml(element, ovalWidth, ovalHeight,
					tmpStartLeft, tmpStartTop, fromX, fromY, toX, toY));

			if (element.getChildren() != null) {
				toTreeHtml(element, html, ovalWidth, ovalHeight, tmpStartLeft,
						tmpStartTop, lineLength - DESCENDING, space
								- DESCENDING / 2, angle - DESCENDING / 2);
			}
		}

		return html.toString();
	}

	private String toChildHtml(FormulaNode child, double ovalWidth,
			double ovalHeight, double startLeft, double startTop, double fromX,
			double fromY, double toX, double toY) throws Exception {
		StringBuffer html = new StringBuffer();

		html.append("<v:line style='position:absolute;' from='" + fromX + ","
				+ fromY + "' to='" + toX + "," + toY + "' />");

		html
				.append("<v:oval id='"
						+ child.getId()
						+ "'strokecolor='red' fillcolor='yellow' style='position:absolute;left:"
						+ startLeft + ";top:" + startTop + ";width:"
						+ ovalWidth + ";height:" + ovalHeight + "'");
		html.append(" oncontextmenu='showmenuie5(this);return false;'>");

		html
				.append("<v:textbox  style='bottom : -0px;valign:center;align:center;Z-INDEX:1;position:ralative;left:"
						+ startLeft + ";top:" + startTop + "'>");
		html.append("<div align='center'>" + child.getText() + "</div>");
		html.append("</v:textbox>");
		html.append("</v:oval>");

		return html.toString();
	}

	public Map<String, FormulaNode> delNode(Map<String, FormulaNode> nodeMap, String currId) {// 删除子节点
		FormulaNode currnode = nodeMap.get(currId);
		return delNode(nodeMap, currnode);
	}

	private Map<String, FormulaNode> delNode(Map<String, FormulaNode> nodeMap, FormulaNode currnode) {// 递归删除子节点
		if (currnode.getChildren() != null && currnode.getChildren().size() > 0) {
			Collection<FormulaNode> tmp = currnode.getChildren();
			Collection<FormulaNode> colls = new ArrayList<FormulaNode>();
			for (Iterator<FormulaNode> iter = tmp.iterator(); iter.hasNext();) {
				colls.add(iter.next());
			}

			for (Iterator<FormulaNode> iter = colls.iterator(); iter.hasNext();) {
				FormulaNode node = iter.next();
				delNode(nodeMap, node);
			}
		}

		if (currnode.getParent() != null) {
			currnode.getParent().delChild(currnode);
		}

		nodeMap.remove(currnode.getId());

		return nodeMap;
	}

	public Map<String, FormulaNode> addNode(Map<String, FormulaNode> nodeMap, String parentId, String text,
			String valuetype) throws Exception {// 添加节点
		FormulaNode parent = null;
		if (parentId != null && parentId.trim().length() > 0) {
			parent = nodeMap.get(parentId);
		}

		if (parent != null) {// add
			if (parent.getChildren().size() < 2) {
				FormulaNode child = new FormulaNode();
				child.setId(Tools.getSequence());
				child.setParent(parent);
				child.setText(text);
				child.setValuetype(valuetype);

				parent.addChild(child);
				nodeMap.put(child.getId(), child);
			}
		} else { // create
			nodeMap = new TreeMap<String, FormulaNode>();
			FormulaNode node = new FormulaNode();
			node.setId(Tools.getSequence());
			node.setText(text);

			nodeMap.put(node.getId(), node);
		}

		return nodeMap;
	}

	public String[] getNodeInfo(Map<String, FormulaNode> nodeMap, String parentId) {
		String[] info = new String[2];
		FormulaNode parent = nodeMap.get(parentId);
		Object[] objs = parent.getChildren().toArray();
		info[0] = ((FormulaNode) objs[0]).getValuetype();
		info[1] = ((FormulaNode) objs[0]).getText();
		
		return info;
	}

	public String refreshMenu(Map<String, FormulaNode> nodeMap, String id) {
		StringBuffer fun = new StringBuffer();

		FormulaNode node = nodeMap.get(id);
		int symbolType = MenubarType.MENUBAR_TYPE_NOTSYMBOL;

		int[] disableType = new int[1];

		symbolType = MenubarType.getType(node.getText());

		if (symbolType == MenubarType.MENUBAR_TYPE_RELATION) { // 根据不同的类型disable
			disableType = new int[] { 3, 4 };
		} else if (symbolType == MenubarType.MENUBAR_TYPE_OPERATOR
				|| symbolType == MenubarType.MENUBAR_TYPE_COMPARE) {
			if (node.getChildren().size() == 0) {
				disableType = new int[] { 0, 1, 2, 4 };
			} else {
				disableType = new int[] { 0, 1, 2, 3 };
			}
		}

		if (node.getChildren().size() == 2 || symbolType == Integer.MAX_VALUE) {
			disableType = MenubarType.TYPES;
		}

		StringBuffer innerHtml = new StringBuffer();

		for (int i = 0; i < MenubarType.NAMES.length; i++) {
			innerHtml
					.append("<div class='link' onMouseOver=this.className='overlink' onMouseOut=this.className='link'");
			innerHtml
					.append(" style='padding-top:2;padding-bottom:2;text-align: left'");

			boolean isDisable = false;

			for (int j = 0; j < disableType.length; j++) {
				if (MenubarType.TYPES[i] == disableType[j]) {
					innerHtml.append(" disabled");
					isDisable = true;
				}
			}

			if (!isDisable) {
				innerHtml.append(" onclick='addTreeElement(this,"
						+ MenubarType.TYPES[i] + ");'");
			}

			innerHtml.append(">");
			innerHtml.append("{*[New " + MenubarType.NAMES[i] + "]*}");
			innerHtml.append("</div>");
		}

		innerHtml.append("<table width=100%>");
		innerHtml
				.append("<tr height=4><td width=100% BACKGROUND='/images/line.png'></td></tr>");
		innerHtml.append("</table>");

		innerHtml
				.append("<div class='link' onMouseOver=this.className='overlink' onMouseOut=this.className='link'");
		innerHtml
				.append(" style='padding-top:2;padding-bottom:2;text-align: left'");
		innerHtml.append(" onclick='deleteSel();'");
		innerHtml.append(">");
		innerHtml.append("&nbsp;&nbsp;{*[Delete]*}&nbsp;&nbsp;");
		innerHtml.append("</div>");

		fun.append("document.getElementById('ie5menu').innerHTML=\""
				+ innerHtml.toString() + "\";");

		return fun.toString();
	}

	public static void main(String[] args) throws RecognitionException,
			TokenStreamException {
		String text = "1+(2*3+5)*10+6";
		FormulaTree tree = new FormulaTree();
		Map<String, FormulaNode> map = tree.parse(text);
		String formula = tree.dparse(map);
		{
			DqlBaseLexer lexer = new DqlBaseLexer(new StringReader(formula));
			DqlBaseParser parser = new DqlBaseParser(lexer);
			parser.exprList();
		}
		{
			DqlBaseLexer lexer = new DqlBaseLexer(new StringReader(text));
			DqlBaseParser parser = new DqlBaseParser(lexer);
			parser.exprList();
		}

	}

}
