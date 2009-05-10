/* Generated By:JavaCC: Do not edit this line. ScheduleParserTokenManager.java */
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
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import cz.muni.fi.spc.SchedVis.parsers.SimpleCharStream;
import cz.muni.fi.spc.SchedVis.parsers.Token;
import cz.muni.fi.spc.SchedVis.parsers.TokenMgrError;

/** Token Manager. */
public class ScheduleParserTokenManager implements ScheduleParserConstants {

	/** Debug output. */
	public java.io.PrintStream debugStream = System.out;
	static final int[] jjnextStates = { 80, 81, 75, 73, 74, 106, 128, 142, 156,
	    32, 50, 58, 71, };
	/** Token literal values. */
	public static final String[] jjstrLiteralImages = { "", null, null, null,
	    null, null, null, null, null, null, "\74", "\174", "\76", "\73", };
	/** Lexer state names. */
	public static final String[] lexStateNames = { "DEFAULT", };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[157];
	private final int[] jjstateSet = new int[314];
	protected char curChar;

	int curLexState = 0;

	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	/** Constructor. */
	public ScheduleParserTokenManager(final SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag) {
			throw new Error(
			    "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		}
		this.input_stream = stream;
	}

	/** Constructor. */
	public ScheduleParserTokenManager(final SimpleCharStream stream,
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

	private void jjAddStates(int start, final int end) {
		do {
			this.jjstateSet[this.jjnewStateCnt++] = ScheduleParserTokenManager.jjnextStates[start];
		} while (start++ != end);
	}

	protected Token jjFillToken() {
		final Token t;
		final String curTokenImage;
		final int beginLine;
		final int endLine;
		final int beginColumn;
		final int endColumn;
		final String im = ScheduleParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
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

	private void jjCheckNAddStates(int start, final int end) {
		do {
			this.jjCheckNAdd(ScheduleParserTokenManager.jjnextStates[start]);
		} while (start++ != end);
	}

	private void jjCheckNAddTwoStates(final int state1, final int state2) {
		this.jjCheckNAdd(state1);
		this.jjCheckNAdd(state2);
	}

	private int jjMoveNfa_0(final int startState, int curPos) {
		int startsAt = 0;
		this.jjnewStateCnt = 157;
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
								this.jjCheckNAddStates(0, 2);
							} else if ((0x100000200L & l) != 0L) {
								if (kind > 6) {
									kind = 6;
								}
								this.jjCheckNAdd(5);
							} else if ((0x2400L & l) != 0L) {
								if (kind > 1) {
									kind = 1;
								}
							} else if (this.curChar == 45) {
								this.jjAddStates(3, 4);
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
						case 4:
							if ((0x3ff100100000000L & l) == 0L) {
								break;
							}
							if (kind > 5) {
								kind = 5;
							}
							this.jjstateSet[this.jjnewStateCnt++] = 4;
							break;
						case 5:
							if ((0x100000200L & l) == 0L) {
								break;
							}
							if (kind > 6) {
								kind = 6;
							}
							this.jjCheckNAdd(5);
							break;
						case 10:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 9;
							}
							break;
						case 18:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 17;
							}
							break;
						case 30:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 29;
							}
							break;
						case 38:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 37;
							}
							break;
						case 48:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 47;
							}
							break;
						case 56:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 55;
							}
							break;
						case 69:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 68;
							}
							break;
						case 72:
							if (this.curChar == 45) {
								this.jjAddStates(3, 4);
							}
							break;
						case 73:
							if ((this.curChar == 49) && (kind > 2)) {
								kind = 2;
							}
							break;
						case 74:
							if (this.curChar == 49) {
								this.jjCheckNAdd(75);
							}
							break;
						case 75:
							if (this.curChar == 44) {
								this.jjCheckNAddTwoStates(77, 78);
							}
							break;
						case 76:
							if (this.curChar != 49) {
								break;
							}
							if (kind > 3) {
								kind = 3;
							}
							this.jjCheckNAdd(75);
							break;
						case 77:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 76;
							}
							break;
						case 78:
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 3) {
								kind = 3;
							}
							this.jjCheckNAddTwoStates(75, 78);
							break;
						case 79:
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 2) {
								kind = 2;
							}
							this.jjCheckNAddStates(0, 2);
							break;
						case 80:
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 2) {
								kind = 2;
							}
							this.jjCheckNAdd(80);
							break;
						case 81:
							if ((0x3ff000000000000L & l) != 0L) {
								this.jjCheckNAddTwoStates(81, 75);
							}
							break;
						case 87:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 86;
							}
							break;
						case 92:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 91;
							}
							break;
						case 100:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 99;
							}
							break;
						case 109:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 108;
							}
							break;
						case 114:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 113;
							}
							break;
						case 122:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 121;
							}
							break;
						case 136:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 135;
							}
							break;
						case 150:
							if (this.curChar == 45) {
								this.jjstateSet[this.jjnewStateCnt++] = 149;
							}
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
							if ((0x7fffffe07fffffeL & l) != 0L) {
								if (kind > 5) {
									kind = 5;
								}
								this.jjCheckNAdd(4);
							}
							if (this.curChar == 109) {
								this.jjAddStates(5, 8);
							} else if (this.curChar == 106) {
								this.jjAddStates(9, 12);
							} else if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 20;
							} else if (this.curChar == 103) {
								this.jjstateSet[this.jjnewStateCnt++] = 13;
							}
							break;
						case 3:
							if ((0x7fffffe07fffffeL & l) == 0L) {
								break;
							}
							if (kind > 5) {
								kind = 5;
							}
							this.jjCheckNAdd(4);
							break;
						case 4:
							if ((0x7fffffe87fffffeL & l) == 0L) {
								break;
							}
							if (kind > 5) {
								kind = 5;
							}
							this.jjCheckNAdd(4);
							break;
						case 6:
							if ((this.curChar == 101) && (kind > 7)) {
								kind = 7;
							}
							break;
						case 7:
						case 15:
							if (this.curChar == 118) {
								this.jjCheckNAdd(6);
							}
							break;
						case 8:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 7;
							}
							break;
						case 9:
							if (this.curChar == 109) {
								this.jjstateSet[this.jjnewStateCnt++] = 8;
							}
							break;
						case 11:
							if (this.curChar == 100) {
								this.jjstateSet[this.jjnewStateCnt++] = 10;
							}
							break;
						case 12:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 11;
							}
							break;
						case 13:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 12;
							}
							break;
						case 14:
							if (this.curChar == 103) {
								this.jjstateSet[this.jjnewStateCnt++] = 13;
							}
							break;
						case 16:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 15;
							}
							break;
						case 17:
							if (this.curChar == 109) {
								this.jjstateSet[this.jjnewStateCnt++] = 16;
							}
							break;
						case 19:
							if (this.curChar == 100) {
								this.jjstateSet[this.jjnewStateCnt++] = 18;
							}
							break;
						case 20:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 19;
							}
							break;
						case 21:
							if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 20;
							}
							break;
						case 22:
							if (this.curChar == 106) {
								this.jjAddStates(9, 12);
							}
							break;
						case 23:
							if ((this.curChar == 108) && (kind > 9)) {
								kind = 9;
							}
							break;
						case 24:
							if (this.curChar == 97) {
								this.jjCheckNAdd(23);
							}
							break;
						case 25:
							if (this.curChar == 118) {
								this.jjstateSet[this.jjnewStateCnt++] = 24;
							}
							break;
						case 26:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 25;
							}
							break;
						case 27:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 26;
							}
							break;
						case 28:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 27;
							}
							break;
						case 29:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 28;
							}
							break;
						case 31:
							if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 30;
							}
							break;
						case 32:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 31;
							}
							break;
						case 33:
							if ((this.curChar == 116) && (kind > 9)) {
								kind = 9;
							}
							break;
						case 34:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 33;
							}
							break;
						case 35:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 34;
							}
							break;
						case 36:
							if (this.curChar == 116) {
								this.jjstateSet[this.jjnewStateCnt++] = 35;
							}
							break;
						case 37:
							if (this.curChar == 115) {
								this.jjstateSet[this.jjnewStateCnt++] = 36;
							}
							break;
						case 39:
							if (this.curChar == 110) {
								this.jjstateSet[this.jjnewStateCnt++] = 38;
							}
							break;
						case 40:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 39;
							}
							break;
						case 41:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 40;
							}
							break;
						case 42:
							if (this.curChar == 116) {
								this.jjstateSet[this.jjnewStateCnt++] = 41;
							}
							break;
						case 43:
							if (this.curChar == 117) {
								this.jjstateSet[this.jjnewStateCnt++] = 42;
							}
							break;
						case 44:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 43;
							}
							break;
						case 45:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 44;
							}
							break;
						case 46:
							if (this.curChar == 120) {
								this.jjstateSet[this.jjnewStateCnt++] = 45;
							}
							break;
						case 47:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 46;
							}
							break;
						case 49:
							if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 48;
							}
							break;
						case 50:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 49;
							}
							break;
						case 51:
							if (this.curChar == 101) {
								this.jjCheckNAdd(23);
							}
							break;
						case 52:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 51;
							}
							break;
						case 53:
							if (this.curChar == 110) {
								this.jjstateSet[this.jjnewStateCnt++] = 52;
							}
							break;
						case 54:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 53;
							}
							break;
						case 55:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 54;
							}
							break;
						case 57:
							if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 56;
							}
							break;
						case 58:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 57;
							}
							break;
						case 59:
							if ((this.curChar == 110) && (kind > 9)) {
								kind = 9;
							}
							break;
						case 60:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 59;
							}
							break;
						case 61:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 60;
							}
							break;
						case 62:
							if (this.curChar == 116) {
								this.jjstateSet[this.jjnewStateCnt++] = 61;
							}
							break;
						case 63:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 62;
							}
							break;
						case 64:
							if (this.curChar == 108) {
								this.jjstateSet[this.jjnewStateCnt++] = 63;
							}
							break;
						case 65:
							if (this.curChar == 112) {
								this.jjstateSet[this.jjnewStateCnt++] = 64;
							}
							break;
						case 66:
							if (this.curChar == 109) {
								this.jjstateSet[this.jjnewStateCnt++] = 65;
							}
							break;
						case 67:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 66;
							}
							break;
						case 68:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 67;
							}
							break;
						case 70:
							if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 69;
							}
							break;
						case 71:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 70;
							}
							break;
						case 82:
							if (this.curChar == 109) {
								this.jjAddStates(5, 8);
							}
							break;
						case 83:
							if ((this.curChar == 100) && (kind > 7)) {
								kind = 7;
							}
							break;
						case 84:
							if (this.curChar == 111) {
								this.jjCheckNAdd(83);
							}
							break;
						case 85:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 84;
							}
							break;
						case 86:
							if (this.curChar == 103) {
								this.jjstateSet[this.jjnewStateCnt++] = 85;
							}
							break;
						case 88:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 87;
							}
							break;
						case 89:
							if (this.curChar == 118) {
								this.jjstateSet[this.jjnewStateCnt++] = 88;
							}
							break;
						case 90:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 89;
							}
							break;
						case 91:
							if (this.curChar == 109) {
								this.jjstateSet[this.jjnewStateCnt++] = 90;
							}
							break;
						case 93:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 92;
							}
							break;
						case 94:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 93;
							}
							break;
						case 95:
							if (this.curChar == 117) {
								this.jjstateSet[this.jjnewStateCnt++] = 94;
							}
							break;
						case 96:
							if (this.curChar == 108) {
								this.jjstateSet[this.jjnewStateCnt++] = 95;
							}
							break;
						case 97:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 96;
							}
							break;
						case 98:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 97;
							}
							break;
						case 99:
							if (this.curChar == 102) {
								this.jjstateSet[this.jjnewStateCnt++] = 98;
							}
							break;
						case 101:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 100;
							}
							break;
						case 102:
							if (this.curChar == 110) {
								this.jjstateSet[this.jjnewStateCnt++] = 101;
							}
							break;
						case 103:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 102;
							}
							break;
						case 104:
							if (this.curChar == 104) {
								this.jjstateSet[this.jjnewStateCnt++] = 103;
							}
							break;
						case 105:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 104;
							}
							break;
						case 106:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 105;
							}
							break;
						case 107:
							if (this.curChar == 97) {
								this.jjCheckNAdd(83);
							}
							break;
						case 108:
							if (this.curChar == 98) {
								this.jjstateSet[this.jjnewStateCnt++] = 107;
							}
							break;
						case 110:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 109;
							}
							break;
						case 111:
							if (this.curChar == 118) {
								this.jjstateSet[this.jjnewStateCnt++] = 110;
							}
							break;
						case 112:
							if (this.curChar == 111) {
								this.jjstateSet[this.jjnewStateCnt++] = 111;
							}
							break;
						case 113:
							if (this.curChar == 109) {
								this.jjstateSet[this.jjnewStateCnt++] = 112;
							}
							break;
						case 115:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 114;
							}
							break;
						case 116:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 115;
							}
							break;
						case 117:
							if (this.curChar == 117) {
								this.jjstateSet[this.jjnewStateCnt++] = 116;
							}
							break;
						case 118:
							if (this.curChar == 108) {
								this.jjstateSet[this.jjnewStateCnt++] = 117;
							}
							break;
						case 119:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 118;
							}
							break;
						case 120:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 119;
							}
							break;
						case 121:
							if (this.curChar == 102) {
								this.jjstateSet[this.jjnewStateCnt++] = 120;
							}
							break;
						case 123:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 122;
							}
							break;
						case 124:
							if (this.curChar == 110) {
								this.jjstateSet[this.jjnewStateCnt++] = 123;
							}
							break;
						case 125:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 124;
							}
							break;
						case 126:
							if (this.curChar == 104) {
								this.jjstateSet[this.jjnewStateCnt++] = 125;
							}
							break;
						case 127:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 126;
							}
							break;
						case 128:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 127;
							}
							break;
						case 129:
							if ((this.curChar == 101) && (kind > 8)) {
								kind = 8;
							}
							break;
						case 130:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 129;
							}
							break;
						case 131:
							if (this.curChar == 117) {
								this.jjstateSet[this.jjnewStateCnt++] = 130;
							}
							break;
						case 132:
							if (this.curChar == 108) {
								this.jjstateSet[this.jjnewStateCnt++] = 131;
							}
							break;
						case 133:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 132;
							}
							break;
						case 134:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 133;
							}
							break;
						case 135:
							if (this.curChar == 102) {
								this.jjstateSet[this.jjnewStateCnt++] = 134;
							}
							break;
						case 137:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 136;
							}
							break;
						case 138:
							if (this.curChar == 110) {
								this.jjstateSet[this.jjnewStateCnt++] = 137;
							}
							break;
						case 139:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 138;
							}
							break;
						case 140:
							if (this.curChar == 104) {
								this.jjstateSet[this.jjnewStateCnt++] = 139;
							}
							break;
						case 141:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 140;
							}
							break;
						case 142:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 141;
							}
							break;
						case 143:
							if ((this.curChar == 116) && (kind > 8)) {
								kind = 8;
							}
							break;
						case 144:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 143;
							}
							break;
						case 145:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 144;
							}
							break;
						case 146:
							if (this.curChar == 116) {
								this.jjstateSet[this.jjnewStateCnt++] = 145;
							}
							break;
						case 147:
							if (this.curChar == 115) {
								this.jjstateSet[this.jjnewStateCnt++] = 146;
							}
							break;
						case 148:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 147;
							}
							break;
						case 149:
							if (this.curChar == 114) {
								this.jjstateSet[this.jjnewStateCnt++] = 148;
							}
							break;
						case 151:
							if (this.curChar == 101) {
								this.jjstateSet[this.jjnewStateCnt++] = 150;
							}
							break;
						case 152:
							if (this.curChar == 110) {
								this.jjstateSet[this.jjnewStateCnt++] = 151;
							}
							break;
						case 153:
							if (this.curChar == 105) {
								this.jjstateSet[this.jjnewStateCnt++] = 152;
							}
							break;
						case 154:
							if (this.curChar == 104) {
								this.jjstateSet[this.jjnewStateCnt++] = 153;
							}
							break;
						case 155:
							if (this.curChar == 99) {
								this.jjstateSet[this.jjnewStateCnt++] = 154;
							}
							break;
						case 156:
							if (this.curChar == 97) {
								this.jjstateSet[this.jjnewStateCnt++] = 155;
							}
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
			if ((i = this.jjnewStateCnt) == (startsAt = 157 - (this.jjnewStateCnt = startsAt))) {
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
				return this.jjStopAtPos(0, 13);
			case 60:
				return this.jjStopAtPos(0, 10);
			case 62:
				return this.jjStopAtPos(0, 12);
			case 124:
				return this.jjStopAtPos(0, 11);
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
		for (i = 157; i-- > 0;) {
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
