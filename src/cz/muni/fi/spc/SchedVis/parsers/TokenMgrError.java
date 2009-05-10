/* Generated By:JavaCC: Do not edit this line. TokenMgrError.java Version 4.1 */
/* JavaCCOptions: */
/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.muni.fi.spc.SchedVis.parsers;

/** Token Manager Error. */
@SuppressWarnings("serial")
public final class TokenMgrError extends Error {

	/*
	 * Ordinals for various reasons why an Error of this type can be thrown.
	 */

	/**
	 * Lexical error occurred.
	 */
	public static final int LEXICAL_ERROR = 0;

	/**
	 * An attempt was made to create a second instance of a static token manager.
	 */
	static final int STATIC_LEXER_ERROR = 1;

	/**
	 * Tried to change to an invalid lexical state.
	 */
	public static final int INVALID_LEXICAL_STATE = 2;

	/**
	 * Detected (and bailed out of) an infinite loop in the token manager.
	 */
	static final int LOOP_DETECTED = 3;

	/**
	 * Replaces unprintable characters by their escaped (or unicode escaped)
	 * equivalents in the given string
	 */
	protected static final String addEscapes(final String str) {
		final StringBuffer retval = new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case 0:
					continue;
				case '\b':
					retval.append("\\b");
					continue;
				case '\t':
					retval.append("\\t");
					continue;
				case '\n':
					retval.append("\\n");
					continue;
				case '\f':
					retval.append("\\f");
					continue;
				case '\r':
					retval.append("\\r");
					continue;
				case '\"':
					retval.append("\\\"");
					continue;
				case '\'':
					retval.append("\\\'");
					continue;
				case '\\':
					retval.append("\\\\");
					continue;
				default:
					if (((ch = str.charAt(i)) < 0x20) || (ch > 0x7e)) {
						final String s = "0000" + Integer.toString(ch, 16);
						retval.append("\\u" + s.substring(s.length() - 4, s.length()));
					} else {
						retval.append(ch);
					}
					continue;
			}
		}
		return retval.toString();
	}

	/**
	 * Returns a detailed message for the Error when it is thrown by the token
	 * manager to indicate a lexical error. Parameters : EOFSeen : indicates if
	 * EOF caused the lexical error curLexState : lexical state in which this
	 * error occurred errorLine : line number when the error occurred errorColumn
	 * : column number when the error occurred errorAfter : prefix that was seen
	 * before this error occurred curchar : the offending character Note: You can
	 * customize the lexical error message by modifying this method.
	 */
	protected static String lexicalError(final boolean EOFSeen,
	    final int errorLine, final int errorColumn, final String errorAfter,
	    final char curChar) {
		return ("Lexical error at line "
		    + errorLine
		    + ", column "
		    + errorColumn
		    + ".  Encountered: "
		    + (EOFSeen ? "<EOF> " : ("\""
		        + TokenMgrError.addEscapes(String.valueOf(curChar)) + "\"")
		        + " (" + (int) curChar + "), ") + "after : \""
		    + TokenMgrError.addEscapes(errorAfter) + "\"");
	}

	/**
	 * Indicates the reason why the exception is thrown. It will have one of the
	 * above 4 values.
	 */
	int errorCode;

	/** Full Constructor. */
	public TokenMgrError(final boolean EOFSeen, final int errorLine,
	    final int errorColumn, final String errorAfter, final char curChar,
	    final int reason) {
		this(TokenMgrError.lexicalError(EOFSeen, errorLine, errorColumn,
		    errorAfter, curChar), reason);
	}

	/** Constructor with message and reason. */
	public TokenMgrError(final String message, final int reason) {
		super(message);
		this.errorCode = reason;
	}

}
/*
 * JavaCC - OriginalChecksum=81b2bddec7fd9d3af8f192ae1453b663 (do not edit this
 * line)
 */
