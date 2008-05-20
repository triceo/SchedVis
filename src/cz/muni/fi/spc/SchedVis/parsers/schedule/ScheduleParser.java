/* Generated By:JavaCC: Do not edit this line. ScheduleParser.java */
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import cz.muni.fi.spc.SchedVis.parsers.Parser;

public @SuppressWarnings("all") class ScheduleParser extends Parser implements ScheduleParserConstants {
  public static void main(String args[]) throws ParseException {
      try {
        ScheduleParser parser = new ScheduleParser(System.in);
        ScheduleEventsList events = parser.read();
        System.out.println("Successfully read " + events.size() + " events!");
      } catch (Exception e) {
        System.out.println("NOK.");
        System.out.println(e.getMessage());
      } catch (Error e) {
        System.out.println("Oops.");
        System.out.println(e.getMessage());
      }
  }

  final public ScheduleEventsList read() throws ParseException {
        ScheduleEventsList events = new ScheduleEventsList();
    label_1:
    while (true) {
      events = event_data(events);
      if (jj_2_1(2)) {
        ;
      } else {
        break label_1;
      }
    }
    label_2:
    while (true) {
      if (jj_2_2(2)) {
        ;
      } else {
        break label_2;
      }
      jj_consume_token(9);
    }
    jj_consume_token(0);
   {if (true) return events;}
    throw new Error("Missing return statement in function");
  }

  final public ScheduleEventsList event_data(ScheduleEventsList events) throws ParseException {
  if (this.getImporter() != null) this.getImporter().nextLineParsed();
    if (jj_2_6(2)) {
      events = machine_event(events);
      label_3:
      while (true) {
        if (jj_2_3(2)) {
          ;
        } else {
          break label_3;
        }
        jj_consume_token(WHITESPACE);
      }
      jj_consume_token(9);
                                                      {if (true) return events;}
    } else if (jj_2_7(2)) {
      events = move_event(events);
      label_4:
      while (true) {
        if (jj_2_4(2)) {
          ;
        } else {
          break label_4;
        }
        jj_consume_token(WHITESPACE);
      }
      jj_consume_token(9);
                                                    {if (true) return events;}
    } else if (jj_2_8(2)) {
      events = IO_event(events);
      label_5:
      while (true) {
        if (jj_2_5(2)) {
          ;
        } else {
          break label_5;
        }
        jj_consume_token(WHITESPACE);
      }
      jj_consume_token(9);
                                                  {if (true) return events;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public ScheduleEventsList machine_event(ScheduleEventsList list) throws ParseException {
    Token event;
    Token clock;
    Token machine;
    event = jj_consume_token(MACHINE_EVENT_FLAG);
    jj_consume_token(WHITESPACE);
    clock = jj_consume_token(CONSTANT);
    jj_consume_token(WHITESPACE);
    machine = jj_consume_token(STRING);
        list.add(new ScheduleEventMachine(event, clock, machine));
        {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public ScheduleEventsList move_event(ScheduleEventsList list) throws ParseException {
        Token event;
        Token clock;
        Token job;
        Token origMachine;
        Token newMachine;
        ScheduleMachineDataList data;
    event = jj_consume_token(MOVE_EVENT_FLAG);
    jj_consume_token(WHITESPACE);
    clock = jj_consume_token(CONSTANT);
    jj_consume_token(WHITESPACE);
    job = jj_consume_token(CONSTANT);
    jj_consume_token(WHITESPACE);
    origMachine = jj_consume_token(STRING);
    jj_consume_token(WHITESPACE);
    newMachine = jj_consume_token(STRING);
    data = schedule_data();
        list.add(new ScheduleEventMove(event, clock, job, origMachine, newMachine, data));
        {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public ScheduleEventsList IO_event(ScheduleEventsList list) throws ParseException {
    Token event;
    Token clock;
    Token job;
    ScheduleMachineDataList data;
    event = jj_consume_token(IO_EVENT_FLAG);
    jj_consume_token(WHITESPACE);
    clock = jj_consume_token(CONSTANT);
    jj_consume_token(WHITESPACE);
    job = jj_consume_token(CONSTANT);
    data = schedule_data();
        list.add(new ScheduleEventIO(event, clock, job, data));
        {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public ScheduleMachineDataList schedule_data() throws ParseException {
        ScheduleMachineDataList data = new ScheduleMachineDataList();
    label_6:
    while (true) {
      jj_consume_token(WHITESPACE);
      data = machine_data(data);
      if (jj_2_9(2)) {
        ;
      } else {
        break label_6;
      }
    }
         {if (true) return data;}
    throw new Error("Missing return statement in function");
  }

  final public ScheduleMachineDataList machine_data(ScheduleMachineDataList list) throws ParseException {
        ScheduleJobDataList data = new ScheduleJobDataList();
        Token machineId;
    jj_consume_token(10);
    machineId = jj_consume_token(STRING);
    label_7:
    while (true) {
      if (jj_2_10(2)) {
        ;
      } else {
        break label_7;
      }
      jj_consume_token(11);
      data = job_data(data);
    }
    jj_consume_token(12);
                list.add(new ScheduleMachineData(machineId, data));
                {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public ScheduleJobDataList job_data(ScheduleJobDataList list) throws ParseException {
        Token job;
        Token numCPUs;
        Token assignedCPUs;
        Token arch;
        Token memory;
        Token space;
        Token start;
    Token end;
    Token deadline;
    ScheduleJobData data = new ScheduleJobData();
    job = jj_consume_token(CONSTANT);
    jj_consume_token(13);
    numCPUs = jj_consume_token(CONSTANT);
    jj_consume_token(13);
    if (jj_2_11(2)) {
      assignedCPUs = jj_consume_token(CONSTANT_LIST);
    } else if (jj_2_12(2)) {
      assignedCPUs = jj_consume_token(CONSTANT);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(13);
    arch = jj_consume_token(STRING);
    jj_consume_token(13);
    memory = jj_consume_token(CONSTANT);
    jj_consume_token(13);
    space = jj_consume_token(CONSTANT);
    jj_consume_token(13);
    start = jj_consume_token(CONSTANT);
    jj_consume_token(13);
    end = jj_consume_token(CONSTANT);
    jj_consume_token(13);
    deadline = jj_consume_token(CONSTANT);
    label_8:
    while (true) {
      if (jj_2_13(2)) {
        ;
      } else {
        break label_8;
      }
      jj_consume_token(13);
    }
                data.setId(job);
                data.setNeedsCPUs(numCPUs);
                data.assignCPUs(assignedCPUs);
                data.setArch(arch);
                data.setNeedsMemory(memory);
                data.setNeedsSpace(space);
                data.setStarts(start);
                data.setEnds(end);
                data.setDeadline(deadline);
                list.add(data);
                {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  final private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  final private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  final private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  final private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  final private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  final private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  final private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  final private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  final private boolean jj_3R_13() {
    if (jj_scan_token(10)) return true;
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_scan_token(WHITESPACE)) return true;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_scan_token(WHITESPACE)) return true;
    return false;
  }

  final private boolean jj_3_9() {
    if (jj_scan_token(WHITESPACE)) return true;
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_10() {
    if (jj_scan_token(MACHINE_EVENT_FLAG)) return true;
    if (jj_scan_token(WHITESPACE)) return true;
    return false;
  }

  final private boolean jj_3_13() {
    if (jj_scan_token(13)) return true;
    return false;
  }

  final private boolean jj_3_10() {
    if (jj_scan_token(11)) return true;
    if (jj_3R_14()) return true;
    return false;
  }

  final private boolean jj_3_11() {
    if (jj_scan_token(CONSTANT_LIST)) return true;
    return false;
  }

  final private boolean jj_3_8() {
    if (jj_3R_12()) return true;
    return false;
  }

  final private boolean jj_3R_14() {
    if (jj_scan_token(CONSTANT)) return true;
    return false;
  }

  final private boolean jj_3_7() {
    if (jj_3R_11()) return true;
    return false;
  }

  final private boolean jj_3R_12() {
    if (jj_scan_token(IO_EVENT_FLAG)) return true;
    if (jj_scan_token(WHITESPACE)) return true;
    return false;
  }

  final private boolean jj_3_6() {
    if (jj_3R_10()) return true;
    return false;
  }

  final private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_6()) {
    jj_scanpos = xsp;
    if (jj_3_7()) {
    jj_scanpos = xsp;
    if (jj_3_8()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_scan_token(9)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_9()) return true;
    return false;
  }

  final private boolean jj_3_12() {
    if (jj_scan_token(CONSTANT)) return true;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_scan_token(WHITESPACE)) return true;
    return false;
  }

  final private boolean jj_3R_11() {
    if (jj_scan_token(MOVE_EVENT_FLAG)) return true;
    if (jj_scan_token(WHITESPACE)) return true;
    return false;
  }

  public ScheduleParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[13];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public ScheduleParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public ScheduleParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ScheduleParserTokenManager(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public ScheduleParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ScheduleParserTokenManager(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public ScheduleParser(ScheduleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(ScheduleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken = token;
    if ((token = jj_nt).next != null) jj_nt = jj_nt.next;
    else jj_nt = jj_nt.next = token_source.getNextToken();
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    jj_nt = token;
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if ((token = jj_nt).next != null) jj_nt = jj_nt.next;
    else jj_nt = jj_nt.next = token_source.getNextToken();
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private java.util.Vector<int[]> jj_expentries = new java.util.Vector<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[14];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 14; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 13; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
