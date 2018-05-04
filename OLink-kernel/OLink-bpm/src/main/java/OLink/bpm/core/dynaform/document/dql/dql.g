header
{
package OLink.bpm.core.dynaform.document.dql;
import org.hibernate.hql.ast.*;
import org.hibernate.hql.ast.util.*;
import OLink.bpm.base.action.ParamsTable;
}
class DqlBaseParser extends Parser;

options
{
	exportVocab=Dql;
	buildAST=true;
	k=3;    // For 'not like', 'not in', etc.
}

tokens
{
	// -- HQL Keyword tokens --
	ALL="all";
	ANY="any";
	AND="and";
	AS="as";
	ASCENDING="asc";
	AVG="avg";
	BETWEEN="between";
	CLASS="class";
	COUNT="count";
	DELETE="delete";
	DESCENDING="desc";
	DOT;
	DISTINCT="distinct";
	ELEMENTS="elements";
	ESCAPE="escape";
	EXISTS="exists";
	FALSE="false";
	FETCH="fetch";
	FROM="from";
	FULL="full";
	GROUP="group";
	HAVING="having";
	IN="in";
	INDICES="indices";
	INNER="inner";
	INSERT="insert";
	INTO="into";
	IS="is";
	JOIN="join";
	LEFT="left";
	LIKE="like";
	ILIKE="ilike";
	MAX="max";
	MIN="min";
	NEW="new";
	NOT="not";
	NULL="null";
	OR="or";
	ORDER="order";
	OUTER="outer";
	PROPERTIES="properties";
	RIGHT="right";
	SELECT="select";
	SET="set";
	SOME="some";
	SUM="sum";
	TRUE="true";
	UNION="union";
	UPDATE="update";
	VERSIONED="versioned";
	WHERE="where";

	// -- SQL tokens --
	// These aren't part of HQL, but the SQL fragment parser uses the HQL lexer, so they need to be declared here.
	CASE="case";
	END="end";
	ELSE="else";
	THEN="then";
	WHEN="when";
	ON="on";
	WITH="with";

	// -- EJBQL tokens --
	BOTH="both";
	EMPTY="empty";
	LEADING="leading";
	MEMBER="member";
	OBJECT="object";
	OF="of";
	TRAILING="trailing";

	// -- Synthetic token types --
	AGGREGATE;		// One of the aggregate functions (e.g. min, max, avg)
	ALIAS;
	CONSTRUCTOR;
	CASE2;
	EXPR_LIST;
	FILTER_ENTITY;		// FROM element injected because of a filter expression (happens during compilation phase 2)
	IN_LIST;
	INDEX_OP;
	IS_NOT_NULL;
	IS_NULL;			// Unary 'is null' operator.
	METHOD_CALL;
	NOT_BETWEEN;
	NOT_IN;
	NOT_LIKE;
	NOT_ILIKE;
	ORDER_ELEMENT;
	QUERY;
	RANGE;
	ROW_STAR;
	SELECT_FROM;
	UNARY_MINUS;
	UNARY_PLUS;
	VECTOR_EXPR;		// ( x, y, z )
	WEIRD_IDENT;		// Identifiers that were keywords when they came in.

	// Literal tokens.
	CONSTANT;
	NUM_DOUBLE;
	NUM_FLOAT;
	NUM_LONG;
	JAVA_CONSTANT;
}

{
    /** True if this is a filter query (allow no FROM clause). **/
	private boolean filter = false;

	/**
	 * Sets the filter flag.
	 * @param f True for a filter query, false for a normal query.
	 */
	public void setFilter(boolean f) {
		filter = f;
	}

	/**
	 * Returns true if this is a filter query, false if not.
	 * @return true if this is a filter query, false if not.
	 */
	public boolean isFilter() {
		return filter;
	}

	/**
	 * This method is overriden in the sub class in order to provide the
	 * 'keyword as identifier' hack.
	 * @param token The token to retry as an identifier.
	 * @param ex The exception to throw if it cannot be retried as an identifier.
	 */
	public AST handleIdentifierError(Token token,RecognitionException ex) throws RecognitionException, TokenStreamException {
		// Base implementation: Just re-throw the exception.
		throw ex;
	}

    /**
     * This method looks ahead and converts . <token> into . IDENT when
     * appropriate.
     */
    public void handleDotIdent() throws TokenStreamException {
    }

	/**
	 * Returns the negated equivalent of the expression.
	 * @param x The expression to negate.
	 */
	public AST negateNode(AST x) {
		// Just create a 'not' parent for the default behavior.
		return ASTUtil.createParent(astFactory, NOT, "not", x);
	}

	/**
	 * Returns the 'cleaned up' version of a comparison operator sub-tree.
	 * @param x The comparison operator to clean up.
	 */
	public AST processEqualityExpression(AST x) throws RecognitionException {
		return x;
	}

	public void weakKeywords() throws TokenStreamException { }

	public void processMemberOf(Token n,AST p,ASTPair currentAST) { }

}

setClause
	: (SET^ assignment (COMMA! assignment)*)
	;

assignment
	: stateField EQ^ newValue
	;

// "state_field" is the term used in the EJB3 sample grammar; used here for easy reference.
// it is basically a property ref
stateField
	: path
	;

// this still needs to be defined in the ejb3 spec; additiveExpression is currently just a best guess,
// although it is highly likely I would think that the spec may limit this even more tightly.
newValue
	: concatenation
	;

withClause
	: WITH^ logicalExpression
	;


// expressions
// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//       nextHigherPrecedenceExpression
//           (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
//
// Operator precedence in HQL
// lowest  --> ( 7)  OR
//             ( 6)  AND, NOT
//             ( 5)  equality: ==, <>, !=, is
//             ( 4)  relational: <, <=, >, >=,
//                   LIKE, NOT LIKE, BETWEEN, NOT BETWEEN, IN, NOT IN
//             ( 3)  addition and subtraction: +(binary) -(binary)
//             ( 2)  multiplication: * / %, concatenate: ||
// highest --> ( 1)  +(unary) -(unary)
//                   []   () (method call)  . (dot -- identifier qualification)
//                   aggregate function
//                   ()  (explicit parenthesis)
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
// is usually very straightfoward

logicalExpression
	: expression
	;

// Main expression rule
expression
	: logicalOrExpression
	;

// level 7 - OR
logicalOrExpression
	: logicalAndExpression ( OR^ logicalAndExpression )*
	;

// level 6 - AND, NOT
logicalAndExpression
	: negatedExpression ( AND^ negatedExpression )*
	;

// NOT nodes aren't generated.  Instead, the operator in the sub-tree will be
// negated, if possible.   Expressions without a NOT parent are passed through.
negatedExpression!
{ weakKeywords(); } // Weak keywords can appear in an expression, so look ahead.
	: NOT^ x:negatedExpression { #negatedExpression = negateNode(#x); }
	| y:equalityExpression { #negatedExpression = #y; }
	;

//## OP: EQ | LT | GT | LE | GE | NE | SQL_NE | LIKE;

// level 5 - EQ, NE
equalityExpression
	: x:relationalExpression (
		( EQ^
		| is:IS^	{ #is.setType(IS); } (NOT! { #is.setType(NE); } )?
		| NE^
		| ne:SQL_NE^	{ #ne.setType(NE); }
		) y:relationalExpression)* {
			// Post process the equality expression to clean up 'is null', etc.
			#equalityExpression = processEqualityExpression(#equalityExpression);
		}
	;

// level 4 - LT, GT, LE, GE, LIKE, NOT LIKE, BETWEEN, NOT BETWEEN
// NOTE: The NOT prefix for LIKE and BETWEEN will be represented in the
// token type.  When traversing the AST, use the token type, and not the
// token text to interpret the semantics of these nodes.
relationalExpression
	: concatenation (
		( ( ( LT^ | GT^ | LE^ | GE^ ) additiveExpression )* )
		// Disable node production for the optional 'not'.
		| (n:NOT!)? (
			// Represent the optional NOT prefix using the token type by
			// testing 'n' and setting the token type accordingly.
			(i:IN^ {
					#i.setType( (n == null) ? IN : NOT_IN);
					#i.setText( (n == null) ? "in" : "not in");
				}
				inList)
			| (b:BETWEEN^ {
					#b.setType( (n == null) ? BETWEEN : NOT_BETWEEN);
					#b.setText( (n == null) ? "between" : "not between");
				}
				betweenList )
			| (l:LIKE^ {
					#l.setType( (n == null) ? LIKE : NOT_LIKE);
					#l.setText( (n == null) ? "like" : "not like");
				}
				concatenation likeEscape)
			| (l2:ILIKE^ {
					#l2.setType( (n == null) ? ILIKE : NOT_ILIKE);
					#l2.setText( (n == null) ? "ilike" : "not ilike");
				}
				concatenation likeEscape)
			| (MEMBER! (OF!)? p:path! {
				processMemberOf(n,#p,currentAST);
			  } ) )
		)
	;

likeEscape
	: (ESCAPE^ concatenation)?
	;

inList
	: x:compoundExpr
	{ #inList = #([IN_LIST,"inList"], #inList); }
	;

betweenList
	: concatenation AND! concatenation
	;

//level 4 - string concatenation
concatenation
	: additiveExpression 
	( c:CONCAT^ { #c.setType(EXPR_LIST); #c.setText("concatList"); } 
	  additiveExpression
	  ( CONCAT! additiveExpression )* 
	  { #concatenation = #([METHOD_CALL, "||"], #([IDENT, "concat"]), #c ); } )?
	;

// level 3 - binary plus and minus
additiveExpression
	: multiplyExpression ( ( PLUS^ | MINUS^ ) multiplyExpression )*
	;

// level 2 - binary multiply and divide
multiplyExpression
	: unaryExpression ( ( STAR^ | DIV^ ) unaryExpression )*
	;
	
// level 1 - unary minus, unary plus, not
unaryExpression
	: MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
	| PLUS^ {#PLUS.setType(UNARY_PLUS);} unaryExpression
	| caseExpression
	| quantifiedExpression
	| atom
	;
	
caseExpression
	: CASE^ (whenClause)+ (elseClause)? END! 
	| CASE^ { #CASE.setType(CASE2); } unaryExpression (altWhenClause)+ (elseClause)? END!
	;
	
whenClause
	: (WHEN^ logicalExpression THEN! unaryExpression)
	;
	
altWhenClause
	: (WHEN^ unaryExpression THEN! unaryExpression)
	;
	
elseClause
	: (ELSE^ unaryExpression)
	;
	
quantifiedExpression
	: ( SOME^ | EXISTS^ | ALL^ | ANY^ ) 
	( identifier | collectionExpr)
	;

// level 0 - expression atom
// ident qualifier ('.' ident ), array index ( [ expr ] ),
// method call ( '.' ident '(' exprList ') )
atom
	 : primaryExpression
		(
			DOT^ identifier
				( options { greedy=true; } :
					( op:OPEN^ {#op.setType(METHOD_CALL);} exprList CLOSE! ) )?
		|	lb:OPEN_BRACKET^ {#lb.setType(INDEX_OP);} expression CLOSE_BRACKET!
		)*
	;

// level 0 - the basic element of an expression
primaryExpression
	:   identPrimary ( options {greedy=true;} : DOT^ "class" )?
	|   constant
	|   COLON^ identifier
	|   OPEN! (expressionOrVector) CLOSE!
	|   PARAM^ (NUM_INT)?
	;

// This parses normal expression and a list of expressions separated by commas.  If a comma is encountered
// a parent VECTOR_EXPR node will be created for the list.
expressionOrVector!
	: e:expression ( v:vectorExpr )? {
		// If this is a vector expression, create a parent node for it.
		if (#v != null)
			#expressionOrVector = #([VECTOR_EXPR,"{vector}"], #e, #v);
		else
			#expressionOrVector = #e;
	}
	;

vectorExpr
	: COMMA! expression (COMMA! expression)*
	;

// identifier, followed by member refs (dot ident), or method calls.
// NOTE: handleDotIdent() is called immediately after the first IDENT is recognized because
// the method looks a head to find keywords after DOT and turns them into identifiers.
identPrimary
	: identifier { handleDotIdent(); }
			( options { greedy=true; } : DOT^ ( identifier | ELEMENTS | o:OBJECT { #o.setType(IDENT); } ) )*
			( options { greedy=true; } :
				( op:OPEN^ { #op.setType(METHOD_CALL);} exprList CLOSE! )
			)?
	// Also allow special 'aggregate functions' such as count(), avg(), etc.
	| aggregate
	;

//## aggregate:
//##     ( aggregateFunction OPEN path CLOSE ) | ( COUNT OPEN STAR CLOSE ) | ( COUNT OPEN (DISTINCT | ALL) path CLOSE );

//## aggregateFunction:
//##     COUNT | 'sum' | 'avg' | 'max' | 'min';

aggregate
	: ( SUM^ | AVG^ | MAX^ | MIN^ ) OPEN! additiveExpression CLOSE! { #aggregate.setType(AGGREGATE); }
	// Special case for count - It's 'parameters' can be keywords.
	|  COUNT^ OPEN! ( STAR { #STAR.setType(ROW_STAR); } | ( ( DISTINCT | ALL )? ( path | collectionExpr ) ) ) CLOSE!
	|  collectionExpr
	;

//## collection: ( OPEN query CLOSE ) | ( 'elements'|'indices' OPEN path CLOSE );

collectionExpr
	: (ELEMENTS^ | INDICES^) OPEN! path CLOSE!
	;
                                           
// NOTE: compoundExpr can be a 'path' where the last token in the path is '.elements' or '.indicies'
compoundExpr
	: collectionExpr
	| path
	| (OPEN! ( (expression (COMMA! expression)*) ) CLOSE!)
	;

exprList
{
   AST trimSpec = null;
}
	: (t:TRAILING {#trimSpec = #t;} | l:LEADING {#trimSpec = #l;} | b:BOTH {#trimSpec = #b;})?
	  		{ if(#trimSpec != null) #trimSpec.setType(IDENT); }
	  ( 
	  		expression
	  )?
	;

constant
	: NUM_INT
	| NUM_FLOAT
	| NUM_LONG
	| NUM_DOUBLE
	| QUOTED_STRING
	| NULL
	| TRUE
	| FALSE
	| EMPTY
	;

//## quantifiedExpression: 'exists' | ( expression 'in' ) | ( expression OP 'any' | 'some' ) collection;

//## compoundPath: path ( OPEN_BRACKET expression CLOSE_BRACKET ( '.' path )? )*;

//## path: identifier ( '.' identifier )*;

path
	: identifier ( DOT^ { weakKeywords(); } identifier )*
	;

// Wraps the IDENT token from the lexer, in order to provide
// 'keyword as identifier' trickery.
identifier
	: IDENT
	exception
	catch [RecognitionException ex]
	{
		identifier_AST = handleIdentifierError(LT(1),ex);
	}
	;

// **** LEXER ******************************************************************

class DqlBaseLexer extends Lexer;

options {
	exportVocab=Dql;      // call the vocabulary "Hql"
	testLiterals = false;
	k=2; // needed for newline, and to distinguish '>' from '>='.
	// HHH-241 : Quoted strings don't allow unicode chars - This should fix it.
	charVocabulary='\u0000'..'\uFFFE';	// Allow any char but \uFFFF (16 bit -1, ANTLR's EOF character)
	caseSensitive = false;
	caseSensitiveLiterals = false;
}

// -- Declarations --
{
	// NOTE: The real implementations are in the subclass.
	protected void setPossibleID(boolean possibleID) {}
}

// -- Keywords --

EQ: '=';
LT: '<';
GT: '>';
SQL_NE: "<>";
NE: "!=" | "^=";
LE: "<=";
GE: ">=";

COMMA: ',';

OPEN: '(';
CLOSE: ')';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';

CONCAT: "||";
PLUS: '+';
MINUS: '-';
STAR: '*';
DIV: '/';
COLON: ':';
PARAM: '?';

IDENT options { testLiterals=true; }
	: ID_START_LETTER ( ID_LETTER|'.' )*
		{
    		// Setting this flag allows the grammar to use keywords as identifiers, if necessary.
			setPossibleID(true);
		}
	;

protected
ID_START_LETTER
    :    '_'
    |    '$'
    |	 '#'
    |    'a'..'z'
    |    '\u0080'..'\ufffe'       // HHH-558 : Allow unicode chars in identifiers
    ;

protected
ID_LETTER
    :    ID_START_LETTER
    |    '0'..'9'
    ;

QUOTED_STRING
	: '\'' ( (ESCqs)=> ESCqs | ~'\'' )* '\''
	;

protected
ESCqs
	:
		'\'' '\''
	;

WS  :   (   ' '
		|   '\t'
		|   '\r' '\n' { newline(); }
		|   '\n'      { newline(); }
		|   '\r'      { newline(); }
		)
		{$setType(Token.SKIP);} //ignore this token
	;

//--- From the Java example grammar ---
// a numeric literal
NUM_INT
	{boolean isDecimal=false; Token t=null;}
	:   '.' {_ttype = DOT;}
			(	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX {t=f1;})?
				{
					if (t != null && t.getText().toUpperCase().indexOf('F')>=0)
					{
						_ttype = NUM_FLOAT;
					}
					else
					{
						_ttype = NUM_DOUBLE; // assume double
					}
				}
			)?
	|	(	'0' {isDecimal = true;} // special case for just '0'
			(	('x')
				(											// hex
					// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefore ok to hush warning.
					options { warnWhenFollowAmbig=false; }
				:	HEX_DIGIT
				)+
			|	('0'..'7')+									// octal
			)?
		|	('1'..'9') ('0'..'9')*  {isDecimal=true;}		// non-zero decimal
		)
		(	('l') { _ttype = NUM_LONG; }

		// only check to see if it's a float if looks like decimal so far
		|	{isDecimal}?
			(   '.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
			|   EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
			|   f4:FLOAT_SUFFIX {t=f4;}
			)
			{
				if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0)
				{
					_ttype = NUM_FLOAT;
				}
				else
				{
					_ttype = NUM_DOUBLE; // assume double
				}
			}
		)?
	;

// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'a'..'f')
	;

// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e') ('+'|'-')? ('0'..'9')+
	;

protected
FLOAT_SUFFIX
	:	'f'|'d'
	;

//**********************************
class ExprTreeParser extends TreeParser;
/*
options {
    importVocab=DQL;
}
*/
expr[String moduleName, ParamsTable params, int side, int opt] returns [String r=""]
{ String a,b; }
    :   #(AND a=expr[moduleName,params,1,AND] b=expr[moduleName,params,2,AND])  {r = "("+a+" AND "+b+")";}
    |   #(OR a=expr[moduleName,params,1,OR] b=expr[moduleName,params,2,OR])  {r = "("+a+" OR "+b+")";}   
    |	#(EQ a=expr[moduleName,params,1,EQ] b=expr[moduleName,params,2,EQ])  {r = "("+a+" = "+b+")";} 
    |	#(LT a=expr[moduleName,params,1,LT] b=expr[moduleName,params,2,LT])  {r = "("+a+" < "+b+")";} 
    |	#(GT a=expr[moduleName,params,1,GT] b=expr[moduleName,params,2,GT])  {r = "("+a+" > "+b+")";} 
    |	#(NE a=expr[moduleName,params,1,NE] b=expr[moduleName,params,2,NE])  {r = "("+a+" <> "+b+")";} 
    |	#(LE a=expr[moduleName,params,1,LE] b=expr[moduleName,params,2,LE])  {r = "("+a+" <= "+b+")";} 
    |	#(GE a=expr[moduleName,params,1,GE] b=expr[moduleName,params,2,GE])  {r = "("+a+" >= "+b+")";} 
    |	#(CONCAT a=expr[moduleName,params,1,CONCAT] b=expr[moduleName,params,2,CONCAT])  {r = "("+a+" || "+b+")";} 
    |	#(PLUS a=expr[moduleName,params,1,PLUS] b=expr[moduleName,params,2,PLUS])  {r = "("+a+" + "+b+")";} 
    |	#(MINUS a=expr[moduleName,params,1,MINUS] b=expr[moduleName,params,2,MINUS])  {r = "("+a+" - "+b+")";} 
    |	#(STAR a=expr[moduleName,params,1,STAR] b=expr[moduleName,params,2,STAR])  {r = "("+a+" * "+b+")";} 
    |	#(DIV a=expr[moduleName,params,1,DIV] b=expr[moduleName,params,2,DIV])  {r = "("+a+" / "+b+")";} 
    |	#(LIKE a=expr[moduleName,params,1,LIKE] b=expr[moduleName,params,2,LIKE])  {r = "("+a+" LIKE "+b+")";} 
    |	#(ILIKE a=expr[moduleName,params,1,ILIKE] b=expr[moduleName,params,2,ILIKE])  {r = "("+a+" LIKE "+b+")";} 
    |	#(NOT_LIKE a=expr[moduleName,params,1,NOT_LIKE] b=expr[moduleName,params,2,NOT_LIKE])  {r = "("+a+" NOT LIKE "+b+")";} 
    |	#(NOT_ILIKE a=expr[moduleName,params,1,NOT_ILIKE] b=expr[moduleName,params,2,NOT_ILIKE])  {r = "(LOWER("+a+") NOT LIKE LOWER("+b+"))";} 
    |	#(IN a=expr[moduleName,params,1,IN] b=expr[moduleName,params,2,IN])  {r = "("+a+" IN "+b+")";} 
    |	#(NOT_IN a=expr[moduleName,params,1,NOT_IN] b=expr[moduleName,params,2,NOT_IN])  {r = "("+a+" NOT IN "+b+")";} 
    |	#(IS a=expr[moduleName,params,1,IS] b=expr[moduleName,params,2,IS])  {r = "("+a+" IS "+b+")";}
    |	#(NOT_IS a=expr[moduleName,params,1,NOT_IS] b=expr[moduleName,params,2,NOT_IS])  {r = "("+a+" NOT_IS "+b+")";}
    
    |	i3:IDENT{r = DQLASTUtil.transTo(i3.getText(),moduleName,params,side,IDENT,opt);}
    |	i4:NUM_INT {r = DQLASTUtil.transTo(i4.getText(),moduleName,params,side,NUM_INT,opt);}
    |	i5:NUM_FLOAT {r = DQLASTUtil.transTo(i5.getText(),moduleName,params,side,NUM_FLOAT,opt);}
    |	i6:NUM_LONG {r = DQLASTUtil.transTo(i6.getText(),moduleName,params,side,NUM_LONG,opt);}
    |	i7:NUM_DOUBLE {r = DQLASTUtil.transTo(i7.getText(),moduleName,params,side,NUM_DOUBLE,opt);}
    |	i8:QUOTED_STRING {r = DQLASTUtil.transTo(i8.getText(),moduleName,params,side,QUOTED_STRING,opt);}
    |	i9:NULL {r = DQLASTUtil.transTo(i9.getText(),moduleName,params,side,NULL,opt);}
    |	i10:TRUE {r = DQLASTUtil.transTo(i10.getText(),moduleName,params,side,TRUE,opt);}
    |	i11:FALSE {r = DQLASTUtil.transTo(i11.getText(),moduleName,params,side,FALSE,opt);}
    |	i12:EMPTY {r = DQLASTUtil.transTo(i12.getText(),moduleName,params,side,EMPTY,opt);}
    |	i13:IN_LIST {r = DQLASTUtil.transTo(DQLASTUtil.inListToString(i13),moduleName,params,side,IN_LIST,opt);}
    
    ;
    
