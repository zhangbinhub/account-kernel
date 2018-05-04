package OLink.bpm.core.dynaform.document.dql;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;

public class ExprTreeParser extends antlr.TreeParser implements DqlTokenTypes {
	public ExprTreeParser() {
		tokenNames = _tokenNames;
	}

	public final String expr(AST _t, TableMapping tableMapping, ParamsTable params, int side, int opt,
							 SQLFunction sqlFuction) throws RecognitionException {
		String r = "";

		//AST expr_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		AST i3 = null;
		AST i4 = null;
		AST i5 = null;
		AST i6 = null;
		AST i7 = null;
		AST i8 = null;
		AST i9 = null;
		AST i10 = null;
		AST i11 = null;
		AST i12 = null;
		AST i13 = null;
		String a, b;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case AND: {
				AST __t177 = _t;
				//AST tmp1_AST_in = (AST) _t;
				match(_t, AND);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, AND, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, AND, sqlFuction);
				_t = _retTree;
				_t = __t177;
				_t = _t.getNextSibling();
				r = "(" + a + " AND " + b + ")";
				break;
			}
			case OR: {
				AST __t178 = _t;
				//AST tmp2_AST_in = (AST) _t;
				match(_t, OR);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, OR, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, OR, sqlFuction);
				_t = _retTree;
				_t = __t178;
				_t = _t.getNextSibling();
				r = "(" + a + " OR " + b + ")";
				break;
			}
			case EQ: {
				AST __t179 = _t;
				//AST tmp3_AST_in = (AST) _t;
				match(_t, EQ);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, EQ, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, EQ, sqlFuction);
				_t = _retTree;
				_t = __t179;
				_t = _t.getNextSibling();
				r = "(" + a + " = " + b + ")";
				break;
			}
			case LT: {
				AST __t180 = _t;
				//AST tmp4_AST_in = (AST) _t;
				match(_t, LT);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, LT, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, LT, sqlFuction);
				_t = _retTree;
				_t = __t180;
				_t = _t.getNextSibling();
				r = "(" + a + " < " + b + ")";
				break;
			}
			case GT: {
				AST __t181 = _t;
				//AST tmp5_AST_in = (AST) _t;
				match(_t, GT);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, GT, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, GT, sqlFuction);
				_t = _retTree;
				_t = __t181;
				_t = _t.getNextSibling();
				r = "(" + a + " > " + b + ")";
				break;
			}
			case NE: {
				AST __t182 = _t;
				//AST tmp6_AST_in = (AST) _t;
				match(_t, NE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, NE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, NE, sqlFuction);
				_t = _retTree;
				_t = __t182;
				_t = _t.getNextSibling();
				r = "(" + a + " <> " + b + ")";
				break;
			}
			case LE: {
				AST __t183 = _t;
				//AST tmp7_AST_in = (AST) _t;
				match(_t, LE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, LE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, LE, sqlFuction);
				_t = _retTree;
				_t = __t183;
				_t = _t.getNextSibling();
				r = "(" + a + " <= " + b + ")";
				break;
			}
			case GE: {
				AST __t184 = _t;
				//AST tmp8_AST_in = (AST) _t;
				match(_t, GE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, GE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, GE, sqlFuction);
				_t = _retTree;
				_t = __t184;
				_t = _t.getNextSibling();
				r = "(" + a + " >= " + b + ")";
				break;
			}
			case CONCAT: {
				AST __t185 = _t;
				//AST tmp9_AST_in = (AST) _t;
				match(_t, CONCAT);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, CONCAT, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, CONCAT, sqlFuction);
				_t = _retTree;
				_t = __t185;
				_t = _t.getNextSibling();
				r = "(" + a + " || " + b + ")";
				break;
			}
			case PLUS: {
				AST __t186 = _t;
				//AST tmp10_AST_in = (AST) _t;
				match(_t, PLUS);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, PLUS, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, PLUS, sqlFuction);
				_t = _retTree;
				_t = __t186;
				_t = _t.getNextSibling();
				r = "(" + a + " + " + b + ")";
				break;
			}
			case MINUS: {
				AST __t187 = _t;
				//AST tmp11_AST_in = (AST) _t;
				match(_t, MINUS);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, MINUS, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, MINUS, sqlFuction);
				_t = _retTree;
				_t = __t187;
				_t = _t.getNextSibling();
				r = "(" + a + " - " + b + ")";
				break;
			}
			case STAR: {
				AST __t188 = _t;
				//AST tmp12_AST_in = (AST) _t;
				match(_t, STAR);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, STAR, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, STAR, sqlFuction);
				_t = _retTree;
				_t = __t188;
				_t = _t.getNextSibling();
				r = "(" + a + " * " + b + ")";
				break;
			}
			case DIV: {
				AST __t189 = _t;
				//AST tmp13_AST_in = (AST) _t;
				match(_t, DIV);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, DIV, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, DIV, sqlFuction);
				_t = _retTree;
				_t = __t189;
				_t = _t.getNextSibling();
				r = "(" + a + " / " + b + ")";
				break;
			}
			case LIKE: {
				AST __t190 = _t;
				//AST tmp14_AST_in = (AST) _t;
				match(_t, LIKE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, LIKE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, LIKE, sqlFuction);
				_t = _retTree;
				_t = __t190;
				_t = _t.getNextSibling();
				r = "(" + a + " LIKE " + b + ")";
				break;
			}
			case ILIKE: {
				AST __t191 = _t;
				//AST tmp15_AST_in = (AST) _t;
				match(_t, ILIKE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, ILIKE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, ILIKE, sqlFuction);
				_t = _retTree;
				_t = __t191;
				_t = _t.getNextSibling();
				r = "(" + a + " LIKE " + b + ")";
				break;
			}
			case NOT_LIKE: {
				AST __t192 = _t;
				//AST tmp16_AST_in = (AST) _t;
				match(_t, NOT_LIKE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, NOT_LIKE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, NOT_LIKE, sqlFuction);
				_t = _retTree;
				_t = __t192;
				_t = _t.getNextSibling();
				r = "(" + a + " NOT LIKE " + b + ")";
				break;
			}
			case NOT_ILIKE: {
				AST __t193 = _t;
				//AST tmp17_AST_in = (AST) _t;
				match(_t, NOT_ILIKE);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, NOT_ILIKE, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, NOT_ILIKE, sqlFuction);
				_t = _retTree;
				_t = __t193;
				_t = _t.getNextSibling();
				r = "(LOWER(" + a + ") NOT LIKE LOWER(" + b + "))";
				break;
			}
			case IN: {
				AST __t194 = _t;
				//AST tmp18_AST_in = (AST) _t;
				match(_t, IN);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, IN, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, IN, sqlFuction);
				_t = _retTree;
				_t = __t194;
				_t = _t.getNextSibling();
				r = "(" + a + " IN " + b + ")";
				break;
			}
			case NOT_IN: {
				AST __t195 = _t;
				//AST tmp19_AST_in = (AST) _t;
				match(_t, NOT_IN);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, NOT_IN, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, NOT_IN, sqlFuction);
				_t = _retTree;
				_t = __t195;
				_t = _t.getNextSibling();
				r = "(" + a + " NOT IN " + b + ")";
				break;
			}
			case IS: {
				AST __t196 = _t;
				//AST tmp20_AST_in = (AST) _t;
				match(_t, IS);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, IS, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, IS, sqlFuction);
				_t = _retTree;
				_t = __t196;
				_t = _t.getNextSibling();
				r = "(" + a + " IS " + b + ")";
				break;
			}
			case NOT_IS: {
				AST __t197 = _t;
				//AST tmp21_AST_in = (AST) _t;
				match(_t, NOT_IS);
				_t = _t.getFirstChild();
				a = expr(_t, tableMapping, params, 1, NOT_IS, sqlFuction);
				_t = _retTree;
				b = expr(_t, tableMapping, params, 2, NOT_IS, sqlFuction);
				_t = _retTree;
				_t = __t197;
				_t = _t.getNextSibling();
				r = "(" + a + " NOT_IS " + b + ")";
				break;
			}
			case IDENT: {
				i3 = _t;
				match(_t, IDENT);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i3.getText(), tableMapping, params, side, IDENT, opt, sqlFuction);
				break;
			}
			case NUM_INT: {
				i4 = _t;
				match(_t, NUM_INT);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i4.getText(), tableMapping, params, side, NUM_INT, opt, sqlFuction);
				break;
			}
			case NUM_FLOAT: {
				i5 = _t;
				match(_t, NUM_FLOAT);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i5.getText(), tableMapping, params, side, NUM_FLOAT, opt, sqlFuction);
				break;
			}
			case NUM_LONG: {
				i6 = _t;
				match(_t, NUM_LONG);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i6.getText(), tableMapping, params, side, NUM_LONG, opt, sqlFuction);
				break;
			}
			case NUM_DOUBLE: {
				i7 = _t;
				match(_t, NUM_DOUBLE);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i7.getText(), tableMapping, params, side, NUM_DOUBLE, opt, sqlFuction);
				break;
			}
			case QUOTED_STRING: {
				i8 = _t;
				match(_t, QUOTED_STRING);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i8.getText(), tableMapping, params, side, QUOTED_STRING, opt, sqlFuction);
				break;
			}
			case NULL: {
				i9 = _t;
				match(_t, NULL);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i9.getText(), tableMapping, params, side, NULL, opt, sqlFuction);
				break;
			}
			case TRUE: {
				i10 = _t;
				match(_t, TRUE);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i10.getText(), tableMapping, params, side, TRUE, opt, sqlFuction);
				break;
			}
			case FALSE: {
				i11 = _t;
				match(_t, FALSE);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i11.getText(), tableMapping, params, side, FALSE, opt, sqlFuction);
				break;
			}
			case EMPTY: {
				i12 = _t;
				match(_t, EMPTY);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(i12.getText(), tableMapping, params, side, EMPTY, opt, sqlFuction);
				break;
			}
			case IN_LIST: {
				i13 = _t;
				match(_t, IN_LIST);
				_t = _t.getNextSibling();
				r = DQLASTUtil.transTo(DQLASTUtil.inListToString(i13), tableMapping, params, side, IN_LIST, opt,
						sqlFuction);
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		_retTree = _t;
		return r;
	}

	public static final String[] _tokenNames = { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"all\"", "\"any\"",
			"\"and\"", "\"as\"", "\"asc\"", "\"avg\"", "\"between\"", "\"class\"", "\"count\"", "\"delete\"",
			"\"desc\"", "DOT", "\"distinct\"", "\"elements\"", "\"escape\"", "\"exists\"", "\"false\"", "\"fetch\"",
			"\"from\"", "\"full\"", "\"group\"", "\"having\"", "\"in\"", "\"indices\"", "\"inner\"", "\"insert\"",
			"\"into\"", "\"is\"", "\"join\"", "\"left\"", "\"like\"", "\"ilike\"", "\"max\"", "\"min\"", "\"new\"",
			"\"not\"", "\"null\"", "\"or\"", "\"order\"", "\"outer\"", "\"properties\"", "\"right\"", "\"select\"",
			"\"set\"", "\"some\"", "\"sum\"", "\"true\"", "\"union\"", "\"update\"", "\"versioned\"", "\"where\"",
			"\"case\"", "\"end\"", "\"else\"", "\"then\"", "\"when\"", "\"on\"", "\"with\"", "\"both\"", "\"empty\"",
			"\"leading\"", "\"member\"", "\"object\"", "\"of\"", "\"trailing\"", "AGGREGATE", "ALIAS", "CONSTRUCTOR",
			"CASE2", "EXPR_LIST", "FILTER_ENTITY", "IN_LIST", "INDEX_OP", "IS_NOT_NULL", "IS_NULL", "METHOD_CALL",
			"NOT_BETWEEN", "NOT_IN", "NOT_LIKE", "NOT_ILIKE", "ORDER_ELEMENT", "QUERY", "RANGE", "ROW_STAR",
			"SELECT_FROM", "UNARY_MINUS", "UNARY_PLUS", "VECTOR_EXPR", "WEIRD_IDENT", "CONSTANT", "NUM_DOUBLE",
			"NUM_FLOAT", "NUM_LONG", "JAVA_CONSTANT", "COMMA", "EQ", "NE", "SQL_NE", "LT", "GT", "LE", "GE", "CONCAT",
			"PLUS", "MINUS", "STAR", "DIV", "OPEN", "CLOSE", "OPEN_BRACKET", "CLOSE_BRACKET", "COLON", "PARAM",
			"NUM_INT", "QUOTED_STRING", "IDENT", "ID_START_LETTER", "ID_LETTER", "ESCqs", "WS", "HEX_DIGIT",
			"EXPONENT", "FLOAT_SUFFIX", "NOT_IS" };

}
