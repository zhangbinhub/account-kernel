
package OLink.bpm.core.dynaform.document.dql;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.ANTLRHashString;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

@SuppressWarnings("all")
public class DqlBaseLexer extends antlr.CharScanner implements DqlTokenTypes,
		TokenStream {

	// NOTE: The real implementations are in the subclass.
	protected void setPossibleID(boolean possibleID) {
	}

	public DqlBaseLexer(InputStream in) {
		this(new ByteBuffer(in));
	}

	public DqlBaseLexer(Reader in) {
		this(new CharBuffer(in));
	}

	public DqlBaseLexer(InputBuffer ib) {
		this(new LexerSharedInputState(ib));
	}

	public DqlBaseLexer(LexerSharedInputState state) {
		super(state);
		caseSensitiveLiterals = false;
		setCaseSensitive(false);
		literals = new Hashtable();
		literals.put(new ANTLRHashString("between", this), 10);
		literals.put(new ANTLRHashString("case", this), 55);
		literals.put(new ANTLRHashString("delete", this), 13);
		literals.put(new ANTLRHashString("new", this), 38);
		literals.put(new ANTLRHashString("end", this), 56);
		literals.put(new ANTLRHashString("object", this), 66);
		literals.put(new ANTLRHashString("insert", this), 29);
		literals.put(new ANTLRHashString("distinct", this), 16);
		literals.put(new ANTLRHashString("where", this), 54);
		literals.put(new ANTLRHashString("trailing", this), 68);
		literals.put(new ANTLRHashString("then", this), 58);
		literals.put(new ANTLRHashString("select", this), 46);
		literals.put(new ANTLRHashString("and", this), 6);
		literals.put(new ANTLRHashString("outer", this), 43);
		literals.put(new ANTLRHashString("not", this), 39);
		literals.put(new ANTLRHashString("fetch", this), 21);
		literals.put(new ANTLRHashString("from", this), 22);
		literals.put(new ANTLRHashString("null", this), 40);
		literals.put(new ANTLRHashString("count", this), 12);
		literals.put(new ANTLRHashString("like", this), 34);
		literals.put(new ANTLRHashString("when", this), 59);
		literals.put(new ANTLRHashString("class", this), 11);
		literals.put(new ANTLRHashString("inner", this), 28);
		literals.put(new ANTLRHashString("leading", this), 64);
		literals.put(new ANTLRHashString("with", this), 61);
		literals.put(new ANTLRHashString("set", this), 47);
		literals.put(new ANTLRHashString("escape", this), 18);
		literals.put(new ANTLRHashString("join", this), 32);
		literals.put(new ANTLRHashString("elements", this), 17);
		literals.put(new ANTLRHashString("of", this), 67);
		literals.put(new ANTLRHashString("is", this), 31);
		literals.put(new ANTLRHashString("member", this), 65);
		literals.put(new ANTLRHashString("or", this), 41);
		literals.put(new ANTLRHashString("any", this), 5);
		literals.put(new ANTLRHashString("full", this), 23);
		literals.put(new ANTLRHashString("min", this), 37);
		literals.put(new ANTLRHashString("as", this), 7);
		literals.put(new ANTLRHashString("all", this), 4);
		literals.put(new ANTLRHashString("union", this), 51);
		literals.put(new ANTLRHashString("order", this), 42);
		literals.put(new ANTLRHashString("both", this), 62);
		literals.put(new ANTLRHashString("some", this), 48);
		literals.put(new ANTLRHashString("properties", this), 44);
		literals.put(new ANTLRHashString("false", this), 20);
		literals.put(new ANTLRHashString("exists", this), 19);
		literals.put(new ANTLRHashString("asc", this), 8);
		literals.put(new ANTLRHashString("left", this), 33);
		literals.put(new ANTLRHashString("desc", this), 14);
		literals.put(new ANTLRHashString("max", this), 36);
		literals.put(new ANTLRHashString("ilike", this), 35);
		literals.put(new ANTLRHashString("empty", this), 63);
		literals.put(new ANTLRHashString("sum", this), 49);
		literals.put(new ANTLRHashString("on", this), 60);
		literals.put(new ANTLRHashString("into", this), 30);
		literals.put(new ANTLRHashString("else", this), 57);
		literals.put(new ANTLRHashString("right", this), 45);
		literals.put(new ANTLRHashString("versioned", this), 53);
		literals.put(new ANTLRHashString("in", this), 26);
		literals.put(new ANTLRHashString("avg", this), 9);
		literals.put(new ANTLRHashString("update", this), 52);
		literals.put(new ANTLRHashString("true", this), 50);
		literals.put(new ANTLRHashString("group", this), 24);
		literals.put(new ANTLRHashString("having", this), 25);
		literals.put(new ANTLRHashString("indices", this), 27);
	}

	public Token nextToken() throws TokenStreamException {
		Token theRetToken = null;
		tryAgain: for (;;) {
			Token _token = null;
			int _ttype = Token.INVALID_TYPE;
			resetText();
			try { // for char stream error handling
				try { // for lexical error handling
					switch (LA(1)) {
					case '=': {
						mEQ(true);
						theRetToken = _returnToken;
						break;
					}
					case '!':
					case '^': {
						mNE(true);
						theRetToken = _returnToken;
						break;
					}
					case ',': {
						mCOMMA(true);
						theRetToken = _returnToken;
						break;
					}
					case '(': {
						mOPEN(true);
						theRetToken = _returnToken;
						break;
					}
					case ')': {
						mCLOSE(true);
						theRetToken = _returnToken;
						break;
					}
					case '[': {
						mOPEN_BRACKET(true);
						theRetToken = _returnToken;
						break;
					}
					case ']': {
						mCLOSE_BRACKET(true);
						theRetToken = _returnToken;
						break;
					}
					case '|': {
						mCONCAT(true);
						theRetToken = _returnToken;
						break;
					}
					case '+': {
						mPLUS(true);
						theRetToken = _returnToken;
						break;
					}
					case '-': {
						mMINUS(true);
						theRetToken = _returnToken;
						break;
					}
					case '*': {
						mSTAR(true);
						theRetToken = _returnToken;
						break;
					}
					case '/': {
						mDIV(true);
						theRetToken = _returnToken;
						break;
					}
					case ':': {
						mCOLON(true);
						theRetToken = _returnToken;
						break;
					}
					case '?': {
						mPARAM(true);
						theRetToken = _returnToken;
						break;
					}
					case '\'': {
						mQUOTED_STRING(true);
						theRetToken = _returnToken;
						break;
					}
					case '\t':
					case '\n':
					case '\r':
					case ' ': {
						mWS(true);
						theRetToken = _returnToken;
						break;
					}
					case '.':
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9': {
						mNUM_INT(true);
						theRetToken = _returnToken;
						break;
					}
					default:
						if ((LA(1) == '<') && (LA(2) == '>')) {
							mSQL_NE(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '<') && (LA(2) == '=')) {
							mLE(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '>') && (LA(2) == '=')) {
							mGE(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '<') && (true)) {
							mLT(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '>') && (true)) {
							mGT(true);
							theRetToken = _returnToken;
						} else if ((_tokenSet_0.member(LA(1)))) {
							mIDENT(true);
							theRetToken = _returnToken;
						} else {
							if (LA(1) == EOF_CHAR) {
								uponEOF();
								_returnToken = makeToken(Token.EOF_TYPE);
							} else {
								throw new NoViableAltForCharException(
										(char) LA(1), getFilename(), getLine(),
										getColumn());
							}
						}
					}
					if (_returnToken == null)
						continue tryAgain; // found SKIP token
					_ttype = _returnToken.getType();
					_returnToken.setType(_ttype);
					return _returnToken;
				} catch (RecognitionException e) {
					throw new TokenStreamRecognitionException(e);
				}
			} catch (CharStreamException cse) {
				if (cse instanceof CharStreamIOException) {
					throw new TokenStreamIOException(
							((CharStreamIOException) cse).io);
				} else {
					throw new TokenStreamException(cse.getMessage());
				}
			}
		}
	}

	public final void mEQ(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = EQ;
		int _saveIndex;

		match('=');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mLT(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LT;
		int _saveIndex;

		match('<');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mGT(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GT;
		int _saveIndex;

		match('>');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mSQL_NE(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SQL_NE;
		int _saveIndex;

		match("<>");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mNE(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NE;
		int _saveIndex;

		switch (LA(1)) {
		case '!': {
			match("!=");
			break;
		}
		case '^': {
			match("^=");
			break;
		}
		default: {
			throw new NoViableAltForCharException((char) LA(1), getFilename(),
					getLine(), getColumn());
		}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mLE(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LE;
		int _saveIndex;

		match("<=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mGE(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GE;
		int _saveIndex;

		match(">=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mCOMMA(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = COMMA;
		int _saveIndex;

		match(',');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mOPEN(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = OPEN;
		int _saveIndex;

		match('(');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mCLOSE(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CLOSE;
		int _saveIndex;

		match(')');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mOPEN_BRACKET(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = OPEN_BRACKET;
		int _saveIndex;

		match('[');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mCLOSE_BRACKET(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CLOSE_BRACKET;
		int _saveIndex;

		match(']');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mCONCAT(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CONCAT;
		int _saveIndex;

		match("||");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mPLUS(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = PLUS;
		int _saveIndex;

		match('+');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mMINUS(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MINUS;
		int _saveIndex;

		match('-');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mSTAR(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = STAR;
		int _saveIndex;

		match('*');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mDIV(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DIV;
		int _saveIndex;

		match('/');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mCOLON(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = COLON;
		int _saveIndex;

		match(':');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mPARAM(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = PARAM;
		int _saveIndex;

		match('?');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mIDENT(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = IDENT;
		int _saveIndex;

		mID_START_LETTER(false);
		{
			_loop133: do {
				if ((_tokenSet_1.member(LA(1)))) {
					mID_LETTER(false);
				} else if ((LA(1) == '.')) {
					match('.');
				} else {
					break _loop133;
				}

			} while (true);
		}
		if (inputState.guessing == 0) {

			// Setting this flag allows the grammar to use keywords as
			// identifiers, if necessary.
			setPossibleID(true);

		}
		_ttype = testLiteralsTable(_ttype);
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	protected final void mID_START_LETTER(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ID_START_LETTER;
		int _saveIndex;

		switch (LA(1)) {
		case '_': {
			match('_');
			break;
		}
		case '$': {
			match('$');
			break;
		}
		case '#': {
			match('#');
			break;
		}
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z': {
			matchRange('a', 'z');
			break;
		}
		default:
			if (((LA(1) >= '' && LA(1) <= '￾'))) {
				matchRange('', '￾');
			} else {
				throw new NoViableAltForCharException((char) LA(1),
						getFilename(), getLine(), getColumn());
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	protected final void mID_LETTER(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ID_LETTER;
		int _saveIndex;

		if ((_tokenSet_0.member(LA(1)))) {
			mID_START_LETTER(false);
		} else if (((LA(1) >= '0' && LA(1) <= '9'))) {
			matchRange('0', '9');
		} else {
			throw new NoViableAltForCharException((char) LA(1), getFilename(),
					getLine(), getColumn());
		}

		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mQUOTED_STRING(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = QUOTED_STRING;
		int _saveIndex;

		match('\'');
		{
			_loop140: do {
				boolean synPredMatched139 = false;
				if (((LA(1) == '\'') && (LA(2) == '\''))) {
					int _m139 = mark();
					synPredMatched139 = true;
					inputState.guessing++;
					try {
						{
							mESCqs(false);
						}
					} catch (RecognitionException pe) {
						synPredMatched139 = false;
					}
					rewind(_m139);
					inputState.guessing--;
				}
				if (synPredMatched139) {
					mESCqs(false);
				} else if ((_tokenSet_2.member(LA(1)))) {
					matchNot('\'');
				} else {
					break _loop140;
				}

			} while (true);
		}
		match('\'');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	protected final void mESCqs(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ESCqs;
		int _saveIndex;

		match('\'');
		match('\'');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mWS(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = WS;

		int _saveIndex;

		{
			switch (LA(1)) {
			case ' ': {
				match(' ');
				break;
			}
			case '\t': {
				match('\t');
				break;
			}
			case '\n': {
				match('\n');
				if (inputState.guessing == 0) {
					newline();
				}
				break;
			}
			default:
				if ((LA(1) == '\r') && (LA(2) == '\n')) {
					match('\r');
					match('\n');
					if (inputState.guessing == 0) {
						newline();
					}
				} else if ((LA(1) == '\r') && (true)) {
					match('\r');
					if (inputState.guessing == 0) {
						newline();
					}
				} else {
					throw new NoViableAltForCharException((char) LA(1),
							getFilename(), getLine(), getColumn());
				}
			}
		}
		if (inputState.guessing == 0) {
			_ttype = Token.SKIP;
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	public final void mNUM_INT(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NUM_INT;
		int _saveIndex;
		Token f1 = null;
		Token f2 = null;
		Token f3 = null;
		Token f4 = null;
		boolean isDecimal = false;
		Token t = null;

		switch (LA(1)) {
		case '.': {
			match('.');
			if (inputState.guessing == 0) {
				_ttype = DOT;
			}
			{
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					{
						int _cnt147 = 0;
						_loop147: do {
							if (((LA(1) >= '0' && LA(1) <= '9'))) {
								matchRange('0', '9');
							} else {
								if (_cnt147 >= 1) {
									break _loop147;
								} else {
									throw new NoViableAltForCharException(
											(char) LA(1), getFilename(),
											getLine(), getColumn());
								}
							}

							_cnt147++;
						} while (true);
					}
					{
						if ((LA(1) == 'e')) {
							mEXPONENT(false);
						} else {
						}

					}
					{
						if ((LA(1) == 'd' || LA(1) == 'f')) {
							mFLOAT_SUFFIX(true);
							f1 = _returnToken;
							if (inputState.guessing == 0) {
								t = f1;
							}
						} else {
						}

					}
					if (inputState.guessing == 0) {

						if (t != null
								&& t.getText().toUpperCase().indexOf('F') >= 0) {
							_ttype = NUM_FLOAT;
						} else {
							_ttype = NUM_DOUBLE; // assume double
						}

					}
				} else {
				}

			}
			break;
		}
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9': {
			{
				switch (LA(1)) {
				case '0': {
					match('0');
					if (inputState.guessing == 0) {
						isDecimal = true;
					}
					{
						switch (LA(1)) {
						case 'x': {
							{
								match('x');
							}
							{
								int _cnt154 = 0;
								_loop154: do {
									if ((_tokenSet_3.member(LA(1))) && (true)) {
										mHEX_DIGIT(false);
									} else {
										if (_cnt154 >= 1) {
											break _loop154;
										} else {
											throw new NoViableAltForCharException(
													(char) LA(1),
													getFilename(), getLine(),
													getColumn());
										}
									}

									_cnt154++;
								} while (true);
							}
							break;
						}
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7': {
							{
								int _cnt156 = 0;
								_loop156: do {
									if (((LA(1) >= '0' && LA(1) <= '7'))) {
										matchRange('0', '7');
									} else {
										if (_cnt156 >= 1) {
											break _loop156;
										} else {
											throw new NoViableAltForCharException(
													(char) LA(1),
													getFilename(), getLine(),
													getColumn());
										}
									}

									_cnt156++;
								} while (true);
							}
							break;
						}
						default: {
						}
						}
					}
					break;
				}
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9': {
					{
						matchRange('1', '9');
					}
					{
						_loop159: do {
							if (((LA(1) >= '0' && LA(1) <= '9'))) {
								matchRange('0', '9');
							} else {
								break _loop159;
							}

						} while (true);
					}
					if (inputState.guessing == 0) {
						isDecimal = true;
					}
					break;
				}
				default: {
					throw new NoViableAltForCharException((char) LA(1),
							getFilename(), getLine(), getColumn());
				}
				}
			}
			{
				if ((LA(1) == 'l')) {
					{
						match('l');
					}
					if (inputState.guessing == 0) {
						_ttype = NUM_LONG;
					}
				} else if (((_tokenSet_4.member(LA(1)))) && (isDecimal)) {
					{
						switch (LA(1)) {
						case '.': {
							match('.');
							{
								_loop164: do {
									if (((LA(1) >= '0' && LA(1) <= '9'))) {
										matchRange('0', '9');
									} else {
										break _loop164;
									}

								} while (true);
							}
							{
								if ((LA(1) == 'e')) {
									mEXPONENT(false);
								} else {
								}

							}
							{
								if ((LA(1) == 'd' || LA(1) == 'f')) {
									mFLOAT_SUFFIX(true);
									f2 = _returnToken;
									if (inputState.guessing == 0) {
										t = f2;
									}
								} else {
								}

							}
							break;
						}
						case 'e': {
							mEXPONENT(false);
							{
								if ((LA(1) == 'd' || LA(1) == 'f')) {
									mFLOAT_SUFFIX(true);
									f3 = _returnToken;
									if (inputState.guessing == 0) {
										t = f3;
									}
								} else {
								}

							}
							break;
						}
						case 'd':
						case 'f': {
							mFLOAT_SUFFIX(true);
							f4 = _returnToken;
							if (inputState.guessing == 0) {
								t = f4;
							}
							break;
						}
						default: {
							throw new NoViableAltForCharException((char) LA(1),
									getFilename(), getLine(), getColumn());
						}
						}
					}
					if (inputState.guessing == 0) {

						if (t != null
								&& t.getText().toUpperCase().indexOf('F') >= 0) {
							_ttype = NUM_FLOAT;
						} else {
							_ttype = NUM_DOUBLE; // assume double
						}

					}
				} else {
				}

			}
			break;
		}
		default: {
			throw new NoViableAltForCharException((char) LA(1), getFilename(),
					getLine(), getColumn());
		}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	protected final void mEXPONENT(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = EXPONENT;
		int _saveIndex;

		{
			match('e');
		}
		{
			switch (LA(1)) {
			case '+': {
				match('+');
				break;
			}
			case '-': {
				match('-');
				break;
			}
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': {
				break;
			}
			default: {
				throw new NoViableAltForCharException((char) LA(1),
						getFilename(), getLine(), getColumn());
			}
			}
		}
		{
			int _cnt174 = 0;
			_loop174: do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0', '9');
				} else {
					if (_cnt174 >= 1) {
						break _loop174;
					} else {
						throw new NoViableAltForCharException((char) LA(1),
								getFilename(), getLine(), getColumn());
					}
				}

				_cnt174++;
			} while (true);
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	protected final void mFLOAT_SUFFIX(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = FLOAT_SUFFIX;
		int _saveIndex;

		switch (LA(1)) {
		case 'f': {
			match('f');
			break;
		}
		case 'd': {
			match('d');
			break;
		}
		default: {
			throw new NoViableAltForCharException((char) LA(1), getFilename(),
					getLine(), getColumn());
		}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	protected final void mHEX_DIGIT(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = HEX_DIGIT;
		int _saveIndex;

		{
			switch (LA(1)) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': {
				matchRange('0', '9');
				break;
			}
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f': {
				matchRange('a', 'f');
				break;
			}
			default: {
				throw new NoViableAltForCharException((char) LA(1),
						getFilename(), getLine(), getColumn());
			}
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(String.valueOf(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	private static final long[] mk_tokenSet_0() {
		long[] data = new long[3072];
		data[0] = 103079215104L;
		data[1] = 576460745860972544L;
		for (int i = 2; i <= 1022; i++) {
			data[i] = -1L;
		}
		data[1023] = 9223372036854775807L;
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

	private static final long[] mk_tokenSet_1() {
		long[] data = new long[3072];
		data[0] = 287949004254216192L;
		data[1] = 576460745860972544L;
		for (int i = 2; i <= 1022; i++) {
			data[i] = -1L;
		}
		data[1023] = 9223372036854775807L;
		return data;
	}

	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

	private static final long[] mk_tokenSet_2() {
		long[] data = new long[2048];
		data[0] = -549755813889L;
		for (int i = 1; i <= 1022; i++) {
			data[i] = -1L;
		}
		data[1023] = 9223372036854775807L;
		return data;
	}

	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());

	private static final long[] mk_tokenSet_3() {
		long[] data = new long[1025];
		data[0] = 287948901175001088L;
		data[1] = 541165879296L;
		return data;
	}

	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());

	private static final long[] mk_tokenSet_4() {
		long[] data = new long[1025];
		data[0] = 70368744177664L;
		data[1] = 481036337152L;
		return data;
	}

	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());

}
