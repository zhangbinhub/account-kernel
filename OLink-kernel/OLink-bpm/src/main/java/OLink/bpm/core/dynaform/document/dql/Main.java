package OLink.bpm.core.dynaform.document.dql;

import java.io.StringReader;

import OLink.bpm.base.action.ParamsTable;
import antlr.collections.AST;

public class Main {
	public static void main(String[] args) throws Exception {
		// String text = "$user like #abc and (a > 0 and (b<0 or c>=1) AND D IN
		// ('1','2','3')) OR E NOT IN (4,5,6) and f = 'and' or g.h > a.b and
		// $i>'j' and i is null and j is not null";

		// String text = "$formname='WPI-GB/Trade Expense/PWP Form/fm_wpigb_pwp'
		// and number like #_number and region like #_region and district like
		// #_district and district in
		// ('1168412207571000','1168506404566000','1168510939191000','1168510149160000','1168506437519000','1168510732800000','1168510706347000','1168510132785000','1168510563191000','1168510057425000','1168504762722000','1168503517378000','1168503439738000','1168510630300000','1168510610722000','1168510659910000','1168510239800000','1168510030128000','1168412275742000','1165658347310000','1168510546253000','1168509988550000','1168510955941000','1168510010238000','1165460750500000','1168506377675000','1166263728507000','1168510259035000','1166263742908000','1168510078582000','1168501849082000','1168510113769000','1168510291285000','1165461107814000','1168412243836000','1168510682863000','1168510098207000','1168509968519000','1168412182399000','1165461068958000','1165658369312000','1168412317211000','1168506356832000','1168503452441000','1165461097109000','1165461077741000','1165461090359000','1168509899082000','1168510521066000')
		// ";
		// String text = "1+2-3*4/5";

		// String text = "#b=1 and #c>0 and #d<100";
		// String text = "$parent.formname.c = 3";
		String text = " 员工代码='0245' AND 费用起始日期 LIKE '2007-11%' AND 科目 IN ('382270','382500-02') AND $parent.$id != 'bb7fd302-14f1-4826-aea5-4654a5b07edd' AND $parent.PAYMENT.付款类型='借支' AND $parent.PAYMENT.$stateint IN (256,4096,1048576)";
		// String text = "#b='1997-11-12' and c>0 and d<100";
		DqlBaseLexer lexer = new DqlBaseLexer(new StringReader(text));
		DqlBaseParser parser = new DqlBaseParser(lexer);
		parser.exprList();
		// parser.whenClause();
		// AST t = parser.getAST();
		// ExprTreeParser treeParser = new ExprTreeParser();
		ParamsTable params = new ParamsTable();
		params.setParameter("abc", "helloworld!");
		// String x = treeParser.expr(t, null,params, 0, 0,new
		// OracleSQLFunction());
		// String type = t.getType();
		// t.getNextSibling();
	}

	public static String getAstText(AST ast) {
		StringBuffer text = new StringBuffer();
		String type = ast.getText();
		if (ast.getNumberOfChildren() > 0) {
			text.append("(");
			AST tmp = ast.getFirstChild();
			text.append("(1)").append(getAstText(tmp));
			for (;;) {
				tmp = tmp.getNextSibling();
				if (tmp != null) {
					text.append("(2)").append(type).append(getAstText(tmp));
				} else {
					break;
				}
			}
			text.append(")");

		} else {
			text.append("(3)").append(type);
		}

		return text.toString();
	}
}
