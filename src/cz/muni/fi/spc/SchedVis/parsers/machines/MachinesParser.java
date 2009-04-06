/* Generated By:JavaCC: Do not edit this line. MachinesParser.java */
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
package cz.muni.fi.spc.SchedVis.parsers.machines;

import cz.muni.fi.spc.SchedVis.parsers.ParseException;
import cz.muni.fi.spc.SchedVis.parsers.Parser;
import cz.muni.fi.spc.SchedVis.parsers.SimpleCharStream;
import cz.muni.fi.spc.SchedVis.parsers.Token;

public class MachinesParser extends Parser implements MachinesParserConstants {
	static final class JJCalls {
		int gen;
		Token first;
		int arg;
		JJCalls next;
	}

	static private final class LookaheadSuccess extends java.lang.Error {

		/**
     * 
     */
		private static final long serialVersionUID = 3101750162306828666L;
	}

	/** Generated Token Manager. */
	public MachinesParserTokenManager token_source;

	SimpleCharStream jj_input_stream;

	/** Current token. */
	public Token token;

	/** Next token. */
	public Token jj_nt;

	private Token jj_scanpos, jj_lastpos;

	private int jj_la;

	private int jj_gen;

	final private int[] jj_la1 = new int[0];
	static private int[] jj_la1_0;
	static {
		MachinesParser.jj_la1_init_0();
	}

	private static void jj_la1_init_0() {
		MachinesParser.jj_la1_0 = new int[] {};
	}

	final private JJCalls[] jj_2_rtns = new JJCalls[3];
	private boolean jj_rescan = false;
	private int jj_gc = 0;
	final private LookaheadSuccess jj_ls = new LookaheadSuccess();
	private final java.util.List jj_expentries = new java.util.ArrayList();
	private int[] jj_expentry;
	private int jj_kind = -1;
	private final int[] jj_lasttokens = new int[100];
	private int jj_endpos;

	/** Constructor with InputStream. */
	public MachinesParser(final java.io.InputStream stream) {
		this(stream, null);
	}

	/** Constructor with InputStream and supplied encoding */
	public MachinesParser(final java.io.InputStream stream, final String encoding) {
		try {
			this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		this.token_source = new MachinesParserTokenManager(this.jj_input_stream);
		this.token = new Token();
		this.token.next = this.jj_nt = this.token_source.getNextToken();
		this.jj_gen = 0;
		for (int i = 0; i < 0; i++) {
			this.jj_la1[i] = -1;
		}
		for (int i = 0; i < this.jj_2_rtns.length; i++) {
			this.jj_2_rtns[i] = new JJCalls();
		}
	}

	/** Constructor. */
	public MachinesParser(final java.io.Reader stream) {
		this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
		this.token_source = new MachinesParserTokenManager(this.jj_input_stream);
		this.token = new Token();
		this.token.next = this.jj_nt = this.token_source.getNextToken();
		this.jj_gen = 0;
		for (int i = 0; i < 0; i++) {
			this.jj_la1[i] = -1;
		}
		for (int i = 0; i < this.jj_2_rtns.length; i++) {
			this.jj_2_rtns[i] = new JJCalls();
		}
	}

	/** Constructor with generated Token Manager. */
	public MachinesParser(final MachinesParserTokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.token.next = this.jj_nt = this.token_source.getNextToken();
		this.jj_gen = 0;
		for (int i = 0; i < 0; i++) {
			this.jj_la1[i] = -1;
		}
		for (int i = 0; i < this.jj_2_rtns.length; i++) {
			this.jj_2_rtns[i] = new JJCalls();
		}
	}

	/** Disable tracing. */
	final public void disable_tracing() {
	}

	/** Enable tracing. */
	final public void enable_tracing() {
	}

	/** Generate ParseException. */
	public ParseException generateParseException() {
		this.jj_expentries.clear();
		boolean[] la1tokens = new boolean[6];
		if (this.jj_kind >= 0) {
			la1tokens[this.jj_kind] = true;
			this.jj_kind = -1;
		}
		for (int i = 0; i < 0; i++) {
			if (this.jj_la1[i] == this.jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((MachinesParser.jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			if (la1tokens[i]) {
				this.jj_expentry = new int[1];
				this.jj_expentry[0] = i;
				this.jj_expentries.add(this.jj_expentry);
			}
		}
		this.jj_endpos = 0;
		this.jj_rescan_token();
		this.jj_add_error_token(0, 0);
		int[][] exptokseq = new int[this.jj_expentries.size()][];
		for (int i = 0; i < this.jj_expentries.size(); i++) {
			exptokseq[i] = (int[]) this.jj_expentries.get(i);
		}
		return new ParseException(this.token, exptokseq,
		    MachinesParserConstants.tokenImage);
	}

	/** Get the next Token. */
	final public Token getNextToken() {
		if ((this.token = this.jj_nt).next != null) {
			this.jj_nt = this.jj_nt.next;
		} else {
			this.jj_nt = this.jj_nt.next = this.token_source.getNextToken();
		}
		this.jj_gen++;
		return this.token;
	}

	/** Get the specific Token. */
	final public Token getToken(final int index) {
		Token t = this.token;
		for (int i = 0; i < index; i++) {
			if (t.next != null) {
				t = t.next;
			} else {
				t = t.next = this.token_source.getNextToken();
			}
		}
		return t;
	}

	private boolean jj_2_1(final int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;
		try {
			return !this.jj_3_1();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			this.jj_save(0, xla);
		}
	}

	private boolean jj_2_2(final int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;
		try {
			return !this.jj_3_2();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			this.jj_save(1, xla);
		}
	}

	private boolean jj_2_3(final int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;
		try {
			return !this.jj_3_3();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			this.jj_save(2, xla);
		}
	}

	private boolean jj_3_1() {
		if (this.jj_3R_4()) {
			return true;
		}
		return false;
	}

	private boolean jj_3_2() {
		if (this.jj_scan_token(MachinesParserConstants.EOL)) {
			return true;
		}
		return false;
	}

	private boolean jj_3_3() {
		if (this.jj_scan_token(MachinesParserConstants.EOL)) {
			return true;
		}
		return false;
	}

	private boolean jj_3R_4() {
		if (this.jj_scan_token(MachinesParserConstants.STRING)) {
			return true;
		}
		if (this.jj_scan_token(5)) {
			return true;
		}
		return false;
	}

	private void jj_add_error_token(final int kind, final int pos) {
		if (pos >= 100) {
			return;
		}
		if (pos == this.jj_endpos + 1) {
			this.jj_lasttokens[this.jj_endpos++] = kind;
		} else if (this.jj_endpos != 0) {
			this.jj_expentry = new int[this.jj_endpos];
			for (int i = 0; i < this.jj_endpos; i++) {
				this.jj_expentry[i] = this.jj_lasttokens[i];
			}
			jj_entries_loop: for (java.util.Iterator it = this.jj_expentries
			    .iterator(); it.hasNext();) {
				int[] oldentry = (int[]) (it.next());
				if (oldentry.length == this.jj_expentry.length) {
					for (int i = 0; i < this.jj_expentry.length; i++) {
						if (oldentry[i] != this.jj_expentry[i]) {
							continue jj_entries_loop;
						}
					}
					this.jj_expentries.add(this.jj_expentry);
					break jj_entries_loop;
				}
			}
			if (pos != 0) {
				this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
			}
		}
	}

	private Token jj_consume_token(final int kind) throws ParseException {
		Token oldToken = this.token;
		if ((this.token = this.jj_nt).next != null) {
			this.jj_nt = this.jj_nt.next;
		} else {
			this.jj_nt = this.jj_nt.next = this.token_source.getNextToken();
		}
		if (this.token.kind == kind) {
			this.jj_gen++;
			if (++this.jj_gc > 100) {
				this.jj_gc = 0;
				for (JJCalls c : this.jj_2_rtns) {
					while (c != null) {
						if (c.gen < this.jj_gen) {
							c.first = null;
						}
						c = c.next;
					}
				}
			}
			return this.token;
		}
		this.jj_nt = this.token;
		this.token = oldToken;
		this.jj_kind = kind;
		throw this.generateParseException();
	}

	private void jj_rescan_token() {
		this.jj_rescan = true;
		for (int i = 0; i < 3; i++) {
			try {
				JJCalls p = this.jj_2_rtns[i];
				do {
					if (p.gen > this.jj_gen) {
						this.jj_la = p.arg;
						this.jj_lastpos = this.jj_scanpos = p.first;
						switch (i) {
							case 0:
								this.jj_3_1();
								break;
							case 1:
								this.jj_3_2();
								break;
							case 2:
								this.jj_3_3();
								break;
						}
					}
					p = p.next;
				} while (p != null);
			} catch (LookaheadSuccess ls) {
			}
		}
		this.jj_rescan = false;
	}

	private void jj_save(final int index, final int xla) {
		JJCalls p = this.jj_2_rtns[index];
		while (p.gen > this.jj_gen) {
			if (p.next == null) {
				p = p.next = new JJCalls();
				break;
			}
			p = p.next;
		}
		p.gen = this.jj_gen + xla - this.jj_la;
		p.first = this.token;
		p.arg = xla;
	}

	private boolean jj_scan_token(final int kind) {
		if (this.jj_scanpos == this.jj_lastpos) {
			this.jj_la--;
			if (this.jj_scanpos.next == null) {
				this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source
				    .getNextToken();
			} else {
				this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
			}
		} else {
			this.jj_scanpos = this.jj_scanpos.next;
		}
		if (this.jj_rescan) {
			int i = 0;
			Token tok = this.token;
			while ((tok != null) && (tok != this.jj_scanpos)) {
				i++;
				tok = tok.next;
			}
			if (tok != null) {
				this.jj_add_error_token(kind, i);
			}
		}
		if (this.jj_scanpos.kind != kind) {
			return true;
		}
		if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
			throw this.jj_ls;
		}
		return false;
	}

	final public MachinesList machine_data(final MachinesList machines)
	    throws ParseException {
		if (this.getImporter() != null) {
			this.getImporter().nextLineParsed();
		}
		Token name;
		Token numCPUs;
		Token speed;
		Token arch;
		Token os;
		Token mem;
		Token space;
		name = this.jj_consume_token(MachinesParserConstants.STRING);
		this.jj_consume_token(5);
		numCPUs = this.jj_consume_token(MachinesParserConstants.CONSTANT);
		this.jj_consume_token(5);
		speed = this.jj_consume_token(MachinesParserConstants.CONSTANT);
		this.jj_consume_token(5);
		arch = this.jj_consume_token(MachinesParserConstants.STRING);
		this.jj_consume_token(5);
		os = this.jj_consume_token(MachinesParserConstants.STRING);
		this.jj_consume_token(5);
		mem = this.jj_consume_token(MachinesParserConstants.CONSTANT);
		this.jj_consume_token(5);
		space = this.jj_consume_token(MachinesParserConstants.CONSTANT);
		label_3: while (true) {
			this.jj_consume_token(MachinesParserConstants.EOL);
			if (this.jj_2_3(2)) {
				;
			} else {
				break label_3;
			}
		}
		machines.add(new MachineData(name, numCPUs, speed, arch, os, mem, space));
		if (this.getImporter() != null) {
			this.getImporter().nextLineParsed();
		}
		{
			if (true) {
				return machines;
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public MachinesList read() throws ParseException {
		MachinesList machines = new MachinesList();
		label_1: while (true) {
			machines = this.machine_data(machines);
			if (this.jj_2_1(2)) {
				;
			} else {
				break label_1;
			}
		}
		label_2: while (true) {
			if (this.jj_2_2(2)) {
				;
			} else {
				break label_2;
			}
			this.jj_consume_token(MachinesParserConstants.EOL);
		}
		this.jj_consume_token(0);
		{
			if (true) {
				return machines;
			}
		}
		throw new Error("Missing return statement in function");
	}

	/** Reinitialise. */
	public void ReInit(final java.io.InputStream stream) {
		this.ReInit(stream, null);
	}

	/** Reinitialise. */
	public void ReInit(final java.io.InputStream stream, final String encoding) {
		try {
			this.jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.token.next = this.jj_nt = this.token_source.getNextToken();
		this.jj_gen = 0;
		for (int i = 0; i < 0; i++) {
			this.jj_la1[i] = -1;
		}
		for (int i = 0; i < this.jj_2_rtns.length; i++) {
			this.jj_2_rtns[i] = new JJCalls();
		}
	}

	/** Reinitialise. */
	public void ReInit(final java.io.Reader stream) {
		this.jj_input_stream.ReInit(stream, 1, 1);
		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.token.next = this.jj_nt = this.token_source.getNextToken();
		this.jj_gen = 0;
		for (int i = 0; i < 0; i++) {
			this.jj_la1[i] = -1;
		}
		for (int i = 0; i < this.jj_2_rtns.length; i++) {
			this.jj_2_rtns[i] = new JJCalls();
		}
	}

	/** Reinitialise. */
	public void ReInit(final MachinesParserTokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.token.next = this.jj_nt = this.token_source.getNextToken();
		this.jj_gen = 0;
		for (int i = 0; i < 0; i++) {
			this.jj_la1[i] = -1;
		}
		for (int i = 0; i < this.jj_2_rtns.length; i++) {
			this.jj_2_rtns[i] = new JJCalls();
		}
	}

}
