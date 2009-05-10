/* Generated By:JavaCC: Do not edit this line. MachinesParserTokenManager.java */
/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.muni.fi.spc.SchedVis.parsers.machines;

import cz.muni.fi.spc.SchedVis.parsers.SimpleCharStream;
import cz.muni.fi.spc.SchedVis.parsers.Token;
import cz.muni.fi.spc.SchedVis.parsers.TokenMgrError;

/** Token Manager. */
public class MachinesParserTokenManager implements MachinesParserConstants {

	/** Debug output. */
	public java.io.PrintStream debugStream = System.out;
	static final int[] jjnextStates = {};
	/** Token literal values. */
	public static final String[] jjstrLiteralImages = { "", null, null, null,
	    null, "\73", };
	/** Lexer state names. */
	public static final String[] lexStateNames = { "DEFAULT", };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[8];
	private final int[] jjstateSet = new int[16];
	protected char curChar;

	int curLexState = 0;

	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	/** Constructor. */
	public MachinesParserTokenManager(final SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag) {
			throw new Error(
			    "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		}
		this.input_stream = stream;
	}

	/** Constructor. */
	public MachinesParserTokenManager(final SimpleCharStream stream,
	    final int lexState) {
		this(stream);
		this.SwitchTo(lexState);
	}

	/** Get the next Token. */
	public Token getNextToken() {
		Token matchedToken;
		int curPos = 0;

		for (;;) {
			try {
				this.curChar = this.input_stream.BeginToken();
			} catch (final java.io.IOException e) {
				this.jjmatchedKind = 0;
				matchedToken = this.jjFillToken();
				return matchedToken;
			}

			this.jjmatchedKind = 0x7fffffff;
			this.jjmatchedPos = 0;
			curPos = this.jjMoveStringLiteralDfa0_0();
			if (this.jjmatchedKind != 0x7fffffff) {
				if (this.jjmatchedPos + 1 < curPos) {
					this.input_stream.backup(curPos - this.jjmatchedPos - 1);
				}
				matchedToken = this.jjFillToken();
				return matchedToken;
			}
			int error_line = this.input_stream.getEndLine();
			int error_column = this.input_stream.getEndColumn();
			String error_after = null;
			boolean EOFSeen = false;
			try {
				this.input_stream.readChar();
				this.input_stream.backup(1);
			} catch (final java.io.IOException e1) {
				EOFSeen = true;
				error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
				if ((this.curChar == '\n') || (this.curChar == '\r')) {
					error_line++;
					error_column = 0;
				} else {
					error_column++;
				}
			}
			if (!EOFSeen) {
				this.input_stream.backup(1);
				error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
			}
			throw new TokenMgrError(EOFSeen, error_line, error_column, error_after,
			    this.curChar, TokenMgrError.LEXICAL_ERROR);
		}
	}

	protected Token jjFillToken() {
		final Token t;
		final String curTokenImage;
		final int beginLine;
		final int endLine;
		final int beginColumn;
		final int endColumn;
		final String im = MachinesParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
		curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
		beginLine = this.input_stream.getBeginLine();
		beginColumn = this.input_stream.getBeginColumn();
		endLine = this.input_stream.getEndLine();
		endColumn = this.input_stream.getEndColumn();
		t = Token.newToken(this.jjmatchedKind, curTokenImage);

		t.beginLine = beginLine;
		t.endLine = endLine;
		t.beginColumn = beginColumn;
		t.endColumn = endColumn;

		return t;
	}

	private void jjCheckNAdd(final int state) {
		if (this.jjrounds[state] != this.jjround) {
			this.jjstateSet[this.jjnewStateCnt++] = state;
			this.jjrounds[state] = this.jjround;
		}
	}

	private int jjMoveNfa_0(final int startState, int curPos) {
		int startsAt = 0;
		this.jjnewStateCnt = 8;
		int i = 1;
		this.jjstateSet[0] = startState;
		int kind = 0x7fffffff;
		for (;;) {
			if (++this.jjround == 0x7fffffff) {
				this.ReInitRounds();
			}
			if (this.curChar < 64) {
				final long l = 1L << this.curChar;
				do {
					switch (this.jjstateSet[--i]) {
						case 0:
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 2) {
									kind = 2;
								}
								this.jjCheckNAdd(5);
							} else if ((0x2400L & l) != 0L) {
								if (kind > 1) {
									kind = 1;
								}
							} else if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 3;
							}
							if (this.curChar == 13) {
								this.jjstateSet[this.jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((this.curChar == 10) && (kind > 1)) {
								kind = 1;
							}
							break;
						case 2:
							if (this.curChar == 13) {
								this.jjstateSet[this.jjnewStateCnt++] = 1;
							}
							break;
						case 3:
							if ((this.curChar == 49) && (kind > 2)) {
								kind = 2;
							}
							break;
						case 4:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 3;
							}
							break;
						case 5:
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 2) {
								kind = 2;
							}
							this.jjCheckNAdd(5);
							break;
						case 7:
							if ((0x3ff100100000000L & l) == 0L) {
								break;
							}
							if (kind > 4) {
								kind = 4;
							}
							this.jjstateSet[this.jjnewStateCnt++] = 7;
							break;
						default:
							break;
					}
				} while (i != startsAt);
			} else if (this.curChar < 128) {
				final long l = 1L << (this.curChar & 077);
				do {
					switch (this.jjstateSet[--i]) {
						case 0:
							if ((0x7fffffe07fffffeL & l) == 0L) {
								break;
							}
							if (kind > 4) {
								kind = 4;
							}
							this.jjCheckNAdd(7);
							break;
						case 7:
							if ((0x7fffffe87fffffeL & l) == 0L) {
								break;
							}
							if (kind > 4) {
								kind = 4;
							}
							this.jjCheckNAdd(7);
							break;
						default:
							break;
					}
				} while (i != startsAt);
			} else {
				do {
					switch (this.jjstateSet[--i]) {
						default:
							break;
					}
				} while (i != startsAt);
			}
			if (kind != 0x7fffffff) {
				this.jjmatchedKind = kind;
				this.jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = this.jjnewStateCnt) == (startsAt = 8 - (this.jjnewStateCnt = startsAt))) {
				return curPos;
			}
			try {
				this.curChar = this.input_stream.readChar();
			} catch (final java.io.IOException e) {
				return curPos;
			}
		}
	}

	private int jjMoveStringLiteralDfa0_0() {
		switch (this.curChar) {
			case 59:
				return this.jjStopAtPos(0, 5);
			default:
				return this.jjMoveNfa_0(0, 0);
		}
	}

	private int jjStopAtPos(final int pos, final int kind) {
		this.jjmatchedKind = kind;
		this.jjmatchedPos = pos;
		return pos + 1;
	}

	/** Reinitialise parser. */
	public void ReInit(final SimpleCharStream stream) {
		this.jjmatchedPos = this.jjnewStateCnt = 0;
		this.curLexState = this.defaultLexState;
		this.input_stream = stream;
		this.ReInitRounds();
	}

	/** Reinitialise parser. */
	public void ReInit(final SimpleCharStream stream, final int lexState) {
		this.ReInit(stream);
		this.SwitchTo(lexState);
	}

	private void ReInitRounds() {
		int i;
		this.jjround = 0x80000001;
		for (i = 8; i-- > 0;) {
			this.jjrounds[i] = 0x80000000;
		}
	}

	/** Set debug output. */
	public void setDebugStream(final java.io.PrintStream ds) {
		this.debugStream = ds;
	}

	/** Switch to specified lex state. */
	public void SwitchTo(final int lexState) {
		if ((lexState >= 1) || (lexState < 0)) {
			throw new TokenMgrError("Error: Ignoring invalid lexical state : "
			    + lexState + ". State unchanged.",
			    TokenMgrError.INVALID_LEXICAL_STATE);
		}
		this.curLexState = lexState;
	}

}
