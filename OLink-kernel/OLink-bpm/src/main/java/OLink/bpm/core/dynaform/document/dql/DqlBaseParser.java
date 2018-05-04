
package OLink.bpm.core.dynaform.document.dql;
import org.hibernate.hql.ast.util.ASTUtil;

import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import antlr.collections.impl.BitSet;

/**
 * @author  nicholas
 */
public class DqlBaseParser extends antlr.LLkParser implements DqlTokenTypes {

    /**
	 * True if this is a filter query (allow no FROM clause). *
	 * @uml.property  name="filter"
	 */
	private boolean filter = false;

	/**
	 * Sets the filter flag.
	 * @param f  True for a filter query, false for a normal query.
	 * @uml.property  name="filter"
	 */
	public void setFilter(boolean f) {
		filter = f;
	}

	/**
	 * Returns true if this is a filter query, false if not.
	 * @return  true if this is a filter query, false if not.
	 * @uml.property  name="filter"
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


protected DqlBaseParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DqlBaseParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected DqlBaseParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DqlBaseParser(TokenStream lexer) {
  this(lexer,3);
}

public DqlBaseParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void setClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(SET);
			assignment();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop4:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					assignment();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop4;
				}
				
			} while (true);
			}
			}
			setClause_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = setClause_AST;
	}
	
	public final void assignment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignment_AST = null;
		
		try {      // for error handling
			stateField();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp24_AST = null;
			tmp24_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp24_AST);
			match(EQ);
			newValue();
			astFactory.addASTChild(currentAST, returnAST);
			assignment_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = assignment_AST;
	}
	
	public final void stateField() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST stateField_AST = null;
		
		try {      // for error handling
			path();
			astFactory.addASTChild(currentAST, returnAST);
			stateField_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = stateField_AST;
	}
	
	public final void newValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST newValue_AST = null;
		
		try {      // for error handling
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			newValue_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = newValue_AST;
	}
	
	public final void path() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST path_AST = null;
		
		try {      // for error handling
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop110:
			do {
				if ((LA(1)==DOT)) {
					AST tmp25_AST = null;
					tmp25_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp25_AST);
					match(DOT);
					weakKeywords();
					identifier();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop110;
				}
				
			} while (true);
			}
			path_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = path_AST;
	}
	
	public final void concatenation() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concatenation_AST = null;
		Token  c = null;
		AST c_AST = null;
		
		try {      // for error handling
			additiveExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case CONCAT:
			{
				c = LT(1);
				c_AST = astFactory.create(c);
				astFactory.makeASTRoot(currentAST, c_AST);
				match(CONCAT);
				c_AST.setType(EXPR_LIST); c_AST.setText("concatList");
				additiveExpression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop44:
				do {
					if ((LA(1)==CONCAT)) {
						match(CONCAT);
						additiveExpression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop44;
					}
					
				} while (true);
				}
				concatenation_AST = currentAST.root;
				concatenation_AST = astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL,"||")).add(astFactory.make( (new ASTArray(1)).add(astFactory.create(IDENT,"concat")))).add(c_AST));
				currentAST.root = concatenation_AST;
				currentAST.child = concatenation_AST!=null &&concatenation_AST.getFirstChild()!=null ?
					concatenation_AST.getFirstChild() : concatenation_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case EOF:
			case AND:
			case BETWEEN:
			case ESCAPE:
			case IN:
			case IS:
			case LIKE:
			case ILIKE:
			case NOT:
			case OR:
			case THEN:
			case MEMBER:
			case COMMA:
			case EQ:
			case NE:
			case SQL_NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case CLOSE:
			case CLOSE_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			concatenation_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = concatenation_AST;
	}
	
	public final void withClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST withClause_AST = null;
		
		try {      // for error handling
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp27_AST);
			match(WITH);
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			withClause_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = withClause_AST;
	}
	
	public final void logicalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpression_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			logicalExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = logicalExpression_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			logicalOrExpression();
			astFactory.addASTChild(currentAST, returnAST);
			expression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = expression_AST;
	}
	
	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;
		
		try {      // for error handling
			logicalAndExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop13:
			do {
				if ((LA(1)==OR)) {
					AST tmp28_AST = null;
					tmp28_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp28_AST);
					match(OR);
					logicalAndExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop13;
				}
				
			} while (true);
			}
			logicalOrExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = logicalOrExpression_AST;
	}
	
	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;
		
		try {      // for error handling
			negatedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop16:
			do {
				if ((LA(1)==AND)) {
					AST tmp29_AST = null;
					tmp29_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp29_AST);
					match(AND);
					negatedExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop16;
				}
				
			} while (true);
			}
			logicalAndExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = logicalAndExpression_AST;
	}
	
	public final void negatedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negatedExpression_AST = null;
		AST x_AST = null;
		AST y_AST = null;
		weakKeywords();
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				/*
				AST tmp30_AST = null;
				tmp30_AST = astFactory.create(LT(1));
				*/
				match(NOT);
				negatedExpression();
				x_AST = returnAST;
				negatedExpression_AST = currentAST.root;
				negatedExpression_AST = negateNode(x_AST);
				currentAST.root = negatedExpression_AST;
				currentAST.child = negatedExpression_AST!=null &&negatedExpression_AST.getFirstChild()!=null ?
					negatedExpression_AST.getFirstChild() : negatedExpression_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case ALL:
			case ANY:
			case AVG:
			case COUNT:
			case ELEMENTS:
			case EXISTS:
			case FALSE:
			case INDICES:
			case MAX:
			case MIN:
			case NULL:
			case SOME:
			case SUM:
			case TRUE:
			case CASE:
			case EMPTY:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case PLUS:
			case MINUS:
			case OPEN:
			case COLON:
			case PARAM:
			case NUM_INT:
			case QUOTED_STRING:
			case IDENT:
			{
				equalityExpression();
				y_AST = returnAST;
				negatedExpression_AST = currentAST.root;
				negatedExpression_AST = y_AST;
				currentAST.root = negatedExpression_AST;
				currentAST.child = negatedExpression_AST!=null &&negatedExpression_AST.getFirstChild()!=null ?
					negatedExpression_AST.getFirstChild() : negatedExpression_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		returnAST = negatedExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		//AST x_AST = null;
		Token  is = null;
		AST is_AST = null;
		Token  ne = null;
		AST ne_AST = null;
		//AST y_AST = null;
		
		try {      // for error handling
			relationalExpression();
			//x_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop22:
			do {
				if ((_tokenSet_9.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case EQ:
					{
						AST tmp31_AST = null;
						tmp31_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp31_AST);
						match(EQ);
						break;
					}
					case IS:
					{
						is = LT(1);
						is_AST = astFactory.create(is);
						astFactory.makeASTRoot(currentAST, is_AST);
						match(IS);
						is_AST.setType(IS);
						{
						switch ( LA(1)) {
						case NOT:
						{
							match(NOT);
							is_AST.setType(NE);
							break;
						}
						case ALL:
						case ANY:
						case AVG:
						case COUNT:
						case ELEMENTS:
						case EXISTS:
						case FALSE:
						case INDICES:
						case MAX:
						case MIN:
						case NULL:
						case SOME:
						case SUM:
						case TRUE:
						case CASE:
						case EMPTY:
						case NUM_DOUBLE:
						case NUM_FLOAT:
						case NUM_LONG:
						case PLUS:
						case MINUS:
						case OPEN:
						case COLON:
						case PARAM:
						case NUM_INT:
						case QUOTED_STRING:
						case IDENT:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						break;
					}
					case NE:
					{
						AST tmp33_AST = null;
						tmp33_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp33_AST);
						match(NE);
						break;
					}
					case SQL_NE:
					{
						ne = LT(1);
						ne_AST = astFactory.create(ne);
						astFactory.makeASTRoot(currentAST, ne_AST);
						match(SQL_NE);
						ne_AST.setType(NE);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					relationalExpression();
					//y_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop22;
				}
				
			} while (true);
			}
			equalityExpression_AST = currentAST.root;
			
						// Post process the equality expression to clean up 'is null', etc.
						equalityExpression_AST = processEqualityExpression(equalityExpression_AST);
					
			currentAST.root = equalityExpression_AST;
			currentAST.child = equalityExpression_AST!=null &&equalityExpression_AST.getFirstChild()!=null ?
				equalityExpression_AST.getFirstChild() : equalityExpression_AST;
			currentAST.advanceChildToEnd();
			equalityExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		returnAST = equalityExpression_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		Token  n = null;
		//AST n_AST = null;
		Token  i = null;
		AST i_AST = null;
		Token  b = null;
		AST b_AST = null;
		Token  l = null;
		AST l_AST = null;
		Token  l2 = null;
		AST l2_AST = null;
		AST p_AST = null;
		
		try {      // for error handling
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EOF:
			case AND:
			case IS:
			case OR:
			case THEN:
			case COMMA:
			case EQ:
			case NE:
			case SQL_NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case CLOSE:
			case CLOSE_BRACKET:
			{
				{
				{
				_loop28:
				do {
					if (((LA(1) >= LT && LA(1) <= GE))) {
						{
						switch ( LA(1)) {
						case LT:
						{
							AST tmp34_AST = null;
							tmp34_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp34_AST);
							match(LT);
							break;
						}
						case GT:
						{
							AST tmp35_AST = null;
							tmp35_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp35_AST);
							match(GT);
							break;
						}
						case LE:
						{
							AST tmp36_AST = null;
							tmp36_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp36_AST);
							match(LE);
							break;
						}
						case GE:
						{
							AST tmp37_AST = null;
							tmp37_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp37_AST);
							match(GE);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						additiveExpression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop28;
					}
					
				} while (true);
				}
				}
				break;
			}
			case BETWEEN:
			case IN:
			case LIKE:
			case ILIKE:
			case NOT:
			case MEMBER:
			{
				{
				switch ( LA(1)) {
				case NOT:
				{
					n = LT(1);
//					n_AST = astFactory.create(n);
					match(NOT);
					break;
				}
				case BETWEEN:
				case IN:
				case LIKE:
				case ILIKE:
				case MEMBER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case IN:
				{
					{
					i = LT(1);
					i_AST = astFactory.create(i);
					astFactory.makeASTRoot(currentAST, i_AST);
					match(IN);
					
										i_AST.setType( (n == null) ? IN : NOT_IN);
										i_AST.setText( (n == null) ? "in" : "not in");
									
					inList();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case BETWEEN:
				{
					{
					b = LT(1);
					b_AST = astFactory.create(b);
					astFactory.makeASTRoot(currentAST, b_AST);
					match(BETWEEN);
					
										b_AST.setType( (n == null) ? BETWEEN : NOT_BETWEEN);
										b_AST.setText( (n == null) ? "between" : "not between");
									
					betweenList();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case LIKE:
				{
					{
					l = LT(1);
					l_AST = astFactory.create(l);
					astFactory.makeASTRoot(currentAST, l_AST);
					match(LIKE);
					
										l_AST.setType( (n == null) ? LIKE : NOT_LIKE);
										l_AST.setText( (n == null) ? "like" : "not like");
									
					concatenation();
					astFactory.addASTChild(currentAST, returnAST);
					likeEscape();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case ILIKE:
				{
					{
					l2 = LT(1);
					l2_AST = astFactory.create(l2);
					astFactory.makeASTRoot(currentAST, l2_AST);
					match(ILIKE);
					
										l2_AST.setType( (n == null) ? ILIKE : NOT_ILIKE);
										l2_AST.setText( (n == null) ? "ilike" : "not ilike");
									
					concatenation();
					astFactory.addASTChild(currentAST, returnAST);
					likeEscape();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case MEMBER:
				{
					{
					match(MEMBER);
					{
					switch ( LA(1)) {
					case OF:
					{
						match(OF);
						break;
					}
					case IDENT:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					path();
					p_AST = returnAST;
					
									processMemberOf(n,p_AST,currentAST);
								
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			relationalExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = relationalExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		try {      // for error handling
			multiplyExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop48:
			do {
				if ((LA(1)==PLUS||LA(1)==MINUS)) {
					{
					switch ( LA(1)) {
					case PLUS:
					{
						AST tmp40_AST = null;
						tmp40_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp40_AST);
						match(PLUS);
						break;
					}
					case MINUS:
					{
						AST tmp41_AST = null;
						tmp41_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp41_AST);
						match(MINUS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					multiplyExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop48;
				}
				
			} while (true);
			}
			additiveExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = additiveExpression_AST;
	}
	
	public final void inList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inList_AST = null;
		//AST x_AST = null;
		
		try {      // for error handling
			compoundExpr();
			//x_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			inList_AST = currentAST.root;
			inList_AST = astFactory.make( (new ASTArray(2)).add(astFactory.create(IN_LIST,"inList")).add(inList_AST));
			currentAST.root = inList_AST;
			currentAST.child = inList_AST!=null &&inList_AST.getFirstChild()!=null ?
				inList_AST.getFirstChild() : inList_AST;
			currentAST.advanceChildToEnd();
			inList_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = inList_AST;
	}
	
	public final void betweenList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST betweenList_AST = null;
		
		try {      // for error handling
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			match(AND);
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			betweenList_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = betweenList_AST;
	}
	
	public final void likeEscape() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST likeEscape_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ESCAPE:
			{
				AST tmp43_AST = null;
				tmp43_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp43_AST);
				match(ESCAPE);
				concatenation();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case AND:
			case IS:
			case OR:
			case THEN:
			case COMMA:
			case EQ:
			case NE:
			case SQL_NE:
			case CLOSE:
			case CLOSE_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			likeEscape_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = likeEscape_AST;
	}
	
	public final void compoundExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compoundExpr_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ELEMENTS:
			case INDICES:
			{
				collectionExpr();
				astFactory.addASTChild(currentAST, returnAST);
				compoundExpr_AST = currentAST.root;
				break;
			}
			case IDENT:
			{
				path();
				astFactory.addASTChild(currentAST, returnAST);
				compoundExpr_AST = currentAST.root;
				break;
			}
			case OPEN:
			{
				{
				match(OPEN);
				{
				{
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop103:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						expression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop103;
					}
					
				} while (true);
				}
				}
				}
				match(CLOSE);
				}
				compoundExpr_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = compoundExpr_AST;
	}
	
	public final void multiplyExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplyExpression_AST = null;
		
		try {      // for error handling
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop52:
			do {
				if ((LA(1)==STAR||LA(1)==DIV)) {
					{
					switch ( LA(1)) {
					case STAR:
					{
						AST tmp47_AST = null;
						tmp47_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp47_AST);
						match(STAR);
						break;
					}
					case DIV:
					{
						AST tmp48_AST = null;
						tmp48_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp48_AST);
						match(DIV);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					unaryExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop52;
				}
				
			} while (true);
			}
			multiplyExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = multiplyExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				AST tmp49_AST = null;
				tmp49_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp49_AST);
				match(MINUS);
				tmp49_AST.setType(UNARY_MINUS);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = currentAST.root;
				break;
			}
			case PLUS:
			{
				AST tmp50_AST = null;
				tmp50_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp50_AST);
				match(PLUS);
				tmp50_AST.setType(UNARY_PLUS);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = currentAST.root;
				break;
			}
			case CASE:
			{
				caseExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = currentAST.root;
				break;
			}
			case ALL:
			case ANY:
			case EXISTS:
			case SOME:
			{
				quantifiedExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = currentAST.root;
				break;
			}
			case AVG:
			case COUNT:
			case ELEMENTS:
			case FALSE:
			case INDICES:
			case MAX:
			case MIN:
			case NULL:
			case SUM:
			case TRUE:
			case EMPTY:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case COLON:
			case PARAM:
			case NUM_INT:
			case QUOTED_STRING:
			case IDENT:
			{
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void caseExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseExpression_AST = null;
		
		try {      // for error handling
			if ((LA(1)==CASE) && (LA(2)==WHEN)) {
				AST tmp51_AST = null;
				tmp51_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp51_AST);
				match(CASE);
				{
				int _cnt56=0;
				_loop56:
				do {
					if ((LA(1)==WHEN)) {
						whenClause();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt56>=1 ) { break _loop56; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt56++;
				} while (true);
				}
				{
				switch ( LA(1)) {
				case ELSE:
				{
					elseClause();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case END:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(END);
				caseExpression_AST = currentAST.root;
			}
			else if ((LA(1)==CASE) && (_tokenSet_13.member(LA(2)))) {
				AST tmp53_AST = null;
				tmp53_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp53_AST);
				match(CASE);
				tmp53_AST.setType(CASE2);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				int _cnt59=0;
				_loop59:
				do {
					if ((LA(1)==WHEN)) {
						altWhenClause();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt59>=1 ) { break _loop59; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt59++;
				} while (true);
				}
				{
				switch ( LA(1)) {
				case ELSE:
				{
					elseClause();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case END:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(END);
				caseExpression_AST = currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = caseExpression_AST;
	}
	
	public final void quantifiedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST quantifiedExpression_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case SOME:
			{
				AST tmp55_AST = null;
				tmp55_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp55_AST);
				match(SOME);
				break;
			}
			case EXISTS:
			{
				AST tmp56_AST = null;
				tmp56_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp56_AST);
				match(EXISTS);
				break;
			}
			case ALL:
			{
				AST tmp57_AST = null;
				tmp57_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp57_AST);
				match(ALL);
				break;
			}
			case ANY:
			{
				AST tmp58_AST = null;
				tmp58_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp58_AST);
				match(ANY);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case IDENT:
			{
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ELEMENTS:
			case INDICES:
			{
				collectionExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			quantifiedExpression_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = quantifiedExpression_AST;
	}
	
	public final void atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atom_AST = null;
		Token  op = null;
		AST op_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		try {      // for error handling
			primaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop74:
			do {
				switch ( LA(1)) {
				case DOT:
				{
					AST tmp59_AST = null;
					tmp59_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp59_AST);
					match(DOT);
					identifier();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case OPEN:
					{
						{
						op = LT(1);
						op_AST = astFactory.create(op);
						astFactory.makeASTRoot(currentAST, op_AST);
						match(OPEN);
						op_AST.setType(METHOD_CALL);
						exprList();
						astFactory.addASTChild(currentAST, returnAST);
						match(CLOSE);
						}
						break;
					}
					case EOF:
					case AND:
					case BETWEEN:
					case DOT:
					case ESCAPE:
					case IN:
					case IS:
					case LIKE:
					case ILIKE:
					case NOT:
					case OR:
					case END:
					case ELSE:
					case THEN:
					case WHEN:
					case MEMBER:
					case COMMA:
					case EQ:
					case NE:
					case SQL_NE:
					case LT:
					case GT:
					case LE:
					case GE:
					case CONCAT:
					case PLUS:
					case MINUS:
					case STAR:
					case DIV:
					case CLOSE:
					case OPEN_BRACKET:
					case CLOSE_BRACKET:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					break;
				}
				case OPEN_BRACKET:
				{
					lb = LT(1);
					lb_AST = astFactory.create(lb);
					astFactory.makeASTRoot(currentAST, lb_AST);
					match(OPEN_BRACKET);
					lb_AST.setType(INDEX_OP);
					expression();
					astFactory.addASTChild(currentAST, returnAST);
					match(CLOSE_BRACKET);
					break;
				}
				default:
				{
					break _loop74;
				}
				}
			} while (true);
			}
			atom_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = atom_AST;
	}
	
	public final void whenClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whenClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp62_AST = null;
			tmp62_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp62_AST);
			match(WHEN);
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(THEN);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			}
			whenClause_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = whenClause_AST;
	}
	
	public final void elseClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp64_AST = null;
			tmp64_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp64_AST);
			match(ELSE);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			}
			elseClause_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = elseClause_AST;
	}
	
	public final void altWhenClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST altWhenClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp65_AST = null;
			tmp65_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp65_AST);
			match(WHEN);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(THEN);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			}
			altWhenClause_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = altWhenClause_AST;
	}
	
	public final void identifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifier_AST = null;
		
		try {      // for error handling
			AST tmp67_AST = null;
			tmp67_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp67_AST);
			match(IDENT);
			identifier_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			
					identifier_AST = handleIdentifierError(LT(1),ex);
				
		}
		returnAST = identifier_AST;
	}
	
	public final void collectionExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST collectionExpr_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ELEMENTS:
			{
				AST tmp68_AST = null;
				tmp68_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp68_AST);
				match(ELEMENTS);
				break;
			}
			case INDICES:
			{
				AST tmp69_AST = null;
				tmp69_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp69_AST);
				match(INDICES);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(OPEN);
			path();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			collectionExpr_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = collectionExpr_AST;
	}
	
	public final void primaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case AVG:
			case COUNT:
			case ELEMENTS:
			case INDICES:
			case MAX:
			case MIN:
			case SUM:
			case IDENT:
			{
				identPrimary();
				astFactory.addASTChild(currentAST, returnAST);
				{
				if ((LA(1)==DOT) && (LA(2)==CLASS)) {
					AST tmp72_AST = null;
					tmp72_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp72_AST);
					match(DOT);
					AST tmp73_AST = null;
					tmp73_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp73_AST);
					match(CLASS);
				}
				else if ((_tokenSet_16.member(LA(1))) && (_tokenSet_17.member(LA(2)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				primaryExpression_AST = currentAST.root;
				break;
			}
			case FALSE:
			case NULL:
			case TRUE:
			case EMPTY:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_INT:
			case QUOTED_STRING:
			{
				constant();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = currentAST.root;
				break;
			}
			case COLON:
			{
				AST tmp74_AST = null;
				tmp74_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp74_AST);
				match(COLON);
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = currentAST.root;
				break;
			}
			case OPEN:
			{
				match(OPEN);
				{
				expressionOrVector();
				astFactory.addASTChild(currentAST, returnAST);
				}
				match(CLOSE);
				primaryExpression_AST = currentAST.root;
				break;
			}
			case PARAM:
			{
				AST tmp77_AST = null;
				tmp77_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp77_AST);
				match(PARAM);
				{
				switch ( LA(1)) {
				case NUM_INT:
				{
					AST tmp78_AST = null;
					tmp78_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp78_AST);
					match(NUM_INT);
					break;
				}
				case EOF:
				case AND:
				case BETWEEN:
				case DOT:
				case ESCAPE:
				case IN:
				case IS:
				case LIKE:
				case ILIKE:
				case NOT:
				case OR:
				case END:
				case ELSE:
				case THEN:
				case WHEN:
				case MEMBER:
				case COMMA:
				case EQ:
				case NE:
				case SQL_NE:
				case LT:
				case GT:
				case LE:
				case GE:
				case CONCAT:
				case PLUS:
				case MINUS:
				case STAR:
				case DIV:
				case CLOSE:
				case OPEN_BRACKET:
				case CLOSE_BRACKET:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				primaryExpression_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = primaryExpression_AST;
	}
	
	public final void exprList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprList_AST = null;
		Token  t = null;
		AST t_AST = null;
		Token  l = null;
		AST l_AST = null;
		Token  b = null;
		AST b_AST = null;
		
		AST trimSpec = null;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case TRAILING:
			{
				t = LT(1);
				t_AST = astFactory.create(t);
				astFactory.addASTChild(currentAST, t_AST);
				match(TRAILING);
				trimSpec = t_AST;
				break;
			}
			case LEADING:
			{
				l = LT(1);
				l_AST = astFactory.create(l);
				astFactory.addASTChild(currentAST, l_AST);
				match(LEADING);
				trimSpec = l_AST;
				break;
			}
			case BOTH:
			{
				b = LT(1);
				b_AST = astFactory.create(b);
				astFactory.addASTChild(currentAST, b_AST);
				match(BOTH);
				trimSpec = b_AST;
				break;
			}
			case ALL:
			case ANY:
			case AVG:
			case COUNT:
			case ELEMENTS:
			case EXISTS:
			case FALSE:
			case INDICES:
			case MAX:
			case MIN:
			case NOT:
			case NULL:
			case SOME:
			case SUM:
			case TRUE:
			case CASE:
			case EMPTY:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case PLUS:
			case MINUS:
			case OPEN:
			case CLOSE:
			case COLON:
			case PARAM:
			case NUM_INT:
			case QUOTED_STRING:
			case IDENT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if(trimSpec != null) trimSpec.setType(IDENT);
			{
			switch ( LA(1)) {
			case ALL:
			case ANY:
			case AVG:
			case COUNT:
			case ELEMENTS:
			case EXISTS:
			case FALSE:
			case INDICES:
			case MAX:
			case MIN:
			case NOT:
			case NULL:
			case SOME:
			case SUM:
			case TRUE:
			case CASE:
			case EMPTY:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case PLUS:
			case MINUS:
			case OPEN:
			case COLON:
			case PARAM:
			case NUM_INT:
			case QUOTED_STRING:
			case IDENT:
			{
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CLOSE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			exprList_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = exprList_AST;
	}
	
	public final void identPrimary() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identPrimary_AST = null;
		Token  o = null;
		AST o_AST = null;
		Token  op = null;
		AST op_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
				handleDotIdent();
				{
				_loop87:
				do {
					if ((LA(1)==DOT) && (LA(2)==ELEMENTS||LA(2)==OBJECT||LA(2)==IDENT) && (_tokenSet_19.member(LA(3)))) {
						AST tmp79_AST = null;
						tmp79_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp79_AST);
						match(DOT);
						{
						switch ( LA(1)) {
						case IDENT:
						{
							identifier();
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case ELEMENTS:
						{
							AST tmp80_AST = null;
							tmp80_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp80_AST);
							match(ELEMENTS);
							break;
						}
						case OBJECT:
						{
							o = LT(1);
							o_AST = astFactory.create(o);
							astFactory.addASTChild(currentAST, o_AST);
							match(OBJECT);
							o_AST.setType(IDENT);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
					}
					else {
						break _loop87;
					}
					
				} while (true);
				}
				{
				switch ( LA(1)) {
				case OPEN:
				{
					{
					op = LT(1);
					op_AST = astFactory.create(op);
					astFactory.makeASTRoot(currentAST, op_AST);
					match(OPEN);
					op_AST.setType(METHOD_CALL);
					exprList();
					astFactory.addASTChild(currentAST, returnAST);
					match(CLOSE);
					}
					break;
				}
				case EOF:
				case AND:
				case BETWEEN:
				case DOT:
				case ESCAPE:
				case IN:
				case IS:
				case LIKE:
				case ILIKE:
				case NOT:
				case OR:
				case END:
				case ELSE:
				case THEN:
				case WHEN:
				case MEMBER:
				case COMMA:
				case EQ:
				case NE:
				case SQL_NE:
				case LT:
				case GT:
				case LE:
				case GE:
				case CONCAT:
				case PLUS:
				case MINUS:
				case STAR:
				case DIV:
				case CLOSE:
				case OPEN_BRACKET:
				case CLOSE_BRACKET:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				identPrimary_AST = currentAST.root;
				break;
			}
			case AVG:
			case COUNT:
			case ELEMENTS:
			case INDICES:
			case MAX:
			case MIN:
			case SUM:
			{
				aggregate();
				astFactory.addASTChild(currentAST, returnAST);
				identPrimary_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = identPrimary_AST;
	}
	
	public final void constant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUM_INT:
			{
				AST tmp82_AST = null;
				tmp82_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp82_AST);
				match(NUM_INT);
				constant_AST = currentAST.root;
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp83_AST = null;
				tmp83_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp83_AST);
				match(NUM_FLOAT);
				constant_AST = currentAST.root;
				break;
			}
			case NUM_LONG:
			{
				AST tmp84_AST = null;
				tmp84_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp84_AST);
				match(NUM_LONG);
				constant_AST = currentAST.root;
				break;
			}
			case NUM_DOUBLE:
			{
				AST tmp85_AST = null;
				tmp85_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp85_AST);
				match(NUM_DOUBLE);
				constant_AST = currentAST.root;
				break;
			}
			case QUOTED_STRING:
			{
				AST tmp86_AST = null;
				tmp86_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp86_AST);
				match(QUOTED_STRING);
				constant_AST = currentAST.root;
				break;
			}
			case NULL:
			{
				AST tmp87_AST = null;
				tmp87_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp87_AST);
				match(NULL);
				constant_AST = currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp88_AST = null;
				tmp88_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp88_AST);
				match(TRUE);
				constant_AST = currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp89_AST);
				match(FALSE);
				constant_AST = currentAST.root;
				break;
			}
			case EMPTY:
			{
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp90_AST);
				match(EMPTY);
				constant_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = constant_AST;
	}
	
	public final void expressionOrVector() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expressionOrVector_AST = null;
		AST e_AST = null;
		AST v_AST = null;
		
		try {      // for error handling
			expression();
			e_AST = returnAST;
			{
			switch ( LA(1)) {
			case COMMA:
			{
				vectorExpr();
				v_AST = returnAST;
				break;
			}
			case CLOSE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expressionOrVector_AST = currentAST.root;
			
					// If this is a vector expression, create a parent node for it.
					if (v_AST != null)
						expressionOrVector_AST = astFactory.make( (new ASTArray(3)).add(astFactory.create(VECTOR_EXPR,"{vector}")).add(e_AST).add(v_AST));
					else
						expressionOrVector_AST = e_AST;
				
			currentAST.root = expressionOrVector_AST;
			currentAST.child = expressionOrVector_AST!=null &&expressionOrVector_AST.getFirstChild()!=null ?
				expressionOrVector_AST.getFirstChild() : expressionOrVector_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = expressionOrVector_AST;
	}
	
	public final void vectorExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST vectorExpr_AST = null;
		
		try {      // for error handling
			match(COMMA);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop83:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					expression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop83;
				}
				
			} while (true);
			}
			vectorExpr_AST = currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = vectorExpr_AST;
	}
	
	public final void aggregate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aggregate_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case AVG:
			case MAX:
			case MIN:
			case SUM:
			{
				{
				switch ( LA(1)) {
				case SUM:
				{
					AST tmp93_AST = null;
					tmp93_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp93_AST);
					match(SUM);
					break;
				}
				case AVG:
				{
					AST tmp94_AST = null;
					tmp94_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp94_AST);
					match(AVG);
					break;
				}
				case MAX:
				{
					AST tmp95_AST = null;
					tmp95_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp95_AST);
					match(MAX);
					break;
				}
				case MIN:
				{
					AST tmp96_AST = null;
					tmp96_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp96_AST);
					match(MIN);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(OPEN);
				additiveExpression();
				astFactory.addASTChild(currentAST, returnAST);
				match(CLOSE);
				aggregate_AST = currentAST.root;
				aggregate_AST.setType(AGGREGATE);
				aggregate_AST = currentAST.root;
				break;
			}
			case COUNT:
			{
				AST tmp99_AST = null;
				tmp99_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp99_AST);
				match(COUNT);
				match(OPEN);
				{
				switch ( LA(1)) {
				case STAR:
				{
					AST tmp101_AST = null;
					tmp101_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp101_AST);
					match(STAR);
					tmp101_AST.setType(ROW_STAR);
					break;
				}
				case ALL:
				case DISTINCT:
				case ELEMENTS:
				case INDICES:
				case IDENT:
				{
					{
					{
					switch ( LA(1)) {
					case DISTINCT:
					{
						AST tmp102_AST = null;
						tmp102_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp102_AST);
						match(DISTINCT);
						break;
					}
					case ALL:
					{
						AST tmp103_AST = null;
						tmp103_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp103_AST);
						match(ALL);
						break;
					}
					case ELEMENTS:
					case INDICES:
					case IDENT:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					{
					switch ( LA(1)) {
					case IDENT:
					{
						path();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case ELEMENTS:
					case INDICES:
					{
						collectionExpr();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(CLOSE);
				aggregate_AST = currentAST.root;
				break;
			}
			case ELEMENTS:
			case INDICES:
			{
				collectionExpr();
				astFactory.addASTChild(currentAST, returnAST);
				aggregate_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = aggregate_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"all\"",
		"\"any\"",
		"\"and\"",
		"\"as\"",
		"\"asc\"",
		"\"avg\"",
		"\"between\"",
		"\"class\"",
		"\"count\"",
		"\"delete\"",
		"\"desc\"",
		"DOT",
		"\"distinct\"",
		"\"elements\"",
		"\"escape\"",
		"\"exists\"",
		"\"false\"",
		"\"fetch\"",
		"\"from\"",
		"\"full\"",
		"\"group\"",
		"\"having\"",
		"\"in\"",
		"\"indices\"",
		"\"inner\"",
		"\"insert\"",
		"\"into\"",
		"\"is\"",
		"\"join\"",
		"\"left\"",
		"\"like\"",
		"\"ilike\"",
		"\"max\"",
		"\"min\"",
		"\"new\"",
		"\"not\"",
		"\"null\"",
		"\"or\"",
		"\"order\"",
		"\"outer\"",
		"\"properties\"",
		"\"right\"",
		"\"select\"",
		"\"set\"",
		"\"some\"",
		"\"sum\"",
		"\"true\"",
		"\"union\"",
		"\"update\"",
		"\"versioned\"",
		"\"where\"",
		"\"case\"",
		"\"end\"",
		"\"else\"",
		"\"then\"",
		"\"when\"",
		"\"on\"",
		"\"with\"",
		"\"both\"",
		"\"empty\"",
		"\"leading\"",
		"\"member\"",
		"\"object\"",
		"\"of\"",
		"\"trailing\"",
		"AGGREGATE",
		"ALIAS",
		"CONSTRUCTOR",
		"CASE2",
		"EXPR_LIST",
		"FILTER_ENTITY",
		"IN_LIST",
		"INDEX_OP",
		"IS_NOT_NULL",
		"IS_NULL",
		"METHOD_CALL",
		"NOT_BETWEEN",
		"NOT_IN",
		"NOT_LIKE",
		"NOT_ILIKE",
		"ORDER_ELEMENT",
		"QUERY",
		"RANGE",
		"ROW_STAR",
		"SELECT_FROM",
		"UNARY_MINUS",
		"UNARY_PLUS",
		"VECTOR_EXPR",
		"WEIRD_IDENT",
		"CONSTANT",
		"NUM_DOUBLE",
		"NUM_FLOAT",
		"NUM_LONG",
		"JAVA_CONSTANT",
		"COMMA",
		"EQ",
		"NE",
		"SQL_NE",
		"LT",
		"GT",
		"LE",
		"GE",
		"CONCAT",
		"PLUS",
		"MINUS",
		"STAR",
		"DIV",
		"OPEN",
		"CLOSE",
		"OPEN_BRACKET",
		"CLOSE_BRACKET",
		"COLON",
		"PARAM",
		"NUM_INT",
		"QUOTED_STRING",
		"IDENT",
		"ID_START_LETTER",
		"ID_LETTER",
		"ESCqs",
		"WS",
		"HEX_DIGIT",
		"EXPONENT",
		"FLOAT_SUFFIX",
		"NOT_IS"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	}

	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2L, 17179869184L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 0L, 34359738368L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 288232577322451010L, 1407632581591040L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 288233178685244482L, 1411755750195202L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 288230376151711746L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 288230376151711746L, 1407392063422464L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 288232575174967298L, 1407392063422464L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 288232575174967362L, 1407392063422464L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 2147483648L, 240518168576L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 288233178685244482L, 1416153796706306L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 288233178685244482L, 1442542075772930L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1080866713102451778L, 1548095192039426L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { -9185371609192852944L, 69972927507857408L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 792633534417207296L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 72057594037927936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 1080866713102484546L, 2111045145460738L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { -8104504896090368398L, 72057584374251530L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 0L, 281474976710656L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 1080866713102484546L, 2251782633816066L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	
	}
