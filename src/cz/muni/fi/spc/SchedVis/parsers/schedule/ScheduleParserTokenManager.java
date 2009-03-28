/* Generated By:JavaCC: Do not edit this line. ScheduleParserTokenManager.java */
/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

 */
package cz.muni.fi.spc.SchedVis.parsers.schedule;
import cz.muni.fi.spc.SchedVis.parsers.Parser;

/** Token Manager. */
public class ScheduleParserTokenManager implements ScheduleParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 10:
         return jjStopAtPos(0, 9);
      case 59:
         return jjStopAtPos(0, 13);
      case 60:
         return jjStopAtPos(0, 10);
      case 62:
         return jjStopAtPos(0, 12);
      case 124:
         return jjStopAtPos(0, 11);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 154;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAddStates(0, 2);
                  }
                  else if ((0x100000200L & l) != 0L)
                  {
                     if (kind > 5)
                        kind = 5;
                     jjCheckNAdd(2);
                  }
                  else if (curChar == 45)
                     jjAddStates(3, 4);
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 2:
                  if ((0x100000200L & l) == 0L)
                     break;
                  kind = 5;
                  jjCheckNAdd(2);
                  break;
               case 7:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 15:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 27:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 35:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 45:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 53:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 52;
                  break;
               case 66:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 65;
                  break;
               case 69:
                  if (curChar == 45)
                     jjAddStates(3, 4);
                  break;
               case 70:
                  if (curChar == 49 && kind > 1)
                     kind = 1;
                  break;
               case 71:
                  if (curChar == 49)
                     jjCheckNAdd(72);
                  break;
               case 72:
                  if (curChar == 44)
                     jjCheckNAddTwoStates(74, 75);
                  break;
               case 73:
                  if (curChar != 49)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAdd(72);
                  break;
               case 74:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 73;
                  break;
               case 75:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAddTwoStates(72, 75);
                  break;
               case 76:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 2);
                  break;
               case 77:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAdd(77);
                  break;
               case 78:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(78, 72);
                  break;
               case 84:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 83;
                  break;
               case 89:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 88;
                  break;
               case 97:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 96;
                  break;
               case 106:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 105;
                  break;
               case 111:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 110;
                  break;
               case 119:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 118;
                  break;
               case 133:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 132;
                  break;
               case 147:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 146;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 4)
                        kind = 4;
                     jjCheckNAdd(1);
                  }
                  if (curChar == 109)
                     jjAddStates(5, 8);
                  else if (curChar == 106)
                     jjAddStates(9, 12);
                  else if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 17;
                  else if (curChar == 103)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(1);
                  break;
               case 3:
                  if (curChar == 101 && kind > 6)
                     kind = 6;
                  break;
               case 4:
               case 12:
                  if (curChar == 118)
                     jjCheckNAdd(3);
                  break;
               case 5:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 6:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 8:
                  if (curChar == 100)
                     jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 9:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 11:
                  if (curChar == 103)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 13:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 14:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 16:
                  if (curChar == 100)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 17:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 106)
                     jjAddStates(9, 12);
                  break;
               case 20:
                  if (curChar == 108 && kind > 8)
                     kind = 8;
                  break;
               case 21:
                  if (curChar == 97)
                     jjCheckNAdd(20);
                  break;
               case 22:
                  if (curChar == 118)
                     jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 23:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 24:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 25:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 26:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 28:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 27;
                  break;
               case 29:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 30:
                  if (curChar == 116 && kind > 8)
                     kind = 8;
                  break;
               case 31:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 30;
                  break;
               case 32:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 31;
                  break;
               case 33:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 36:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 37:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 38:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 39:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 38;
                  break;
               case 40:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 39;
                  break;
               case 41:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 40;
                  break;
               case 42:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 41;
                  break;
               case 43:
                  if (curChar == 120)
                     jjstateSet[jjnewStateCnt++] = 42;
                  break;
               case 44:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 43;
                  break;
               case 46:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 45;
                  break;
               case 47:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 46;
                  break;
               case 48:
                  if (curChar == 101)
                     jjCheckNAdd(20);
                  break;
               case 49:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 48;
                  break;
               case 50:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 49;
                  break;
               case 51:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 50;
                  break;
               case 52:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 51;
                  break;
               case 54:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 53;
                  break;
               case 55:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 54;
                  break;
               case 56:
                  if (curChar == 110 && kind > 8)
                     kind = 8;
                  break;
               case 57:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 56;
                  break;
               case 58:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 57;
                  break;
               case 59:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 58;
                  break;
               case 60:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 59;
                  break;
               case 61:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 60;
                  break;
               case 62:
                  if (curChar == 112)
                     jjstateSet[jjnewStateCnt++] = 61;
                  break;
               case 63:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 62;
                  break;
               case 64:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 63;
                  break;
               case 65:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 64;
                  break;
               case 67:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 66;
                  break;
               case 68:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 67;
                  break;
               case 79:
                  if (curChar == 109)
                     jjAddStates(5, 8);
                  break;
               case 80:
                  if (curChar == 100 && kind > 6)
                     kind = 6;
                  break;
               case 81:
                  if (curChar == 111)
                     jjCheckNAdd(80);
                  break;
               case 82:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 81;
                  break;
               case 83:
                  if (curChar == 103)
                     jjstateSet[jjnewStateCnt++] = 82;
                  break;
               case 85:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 84;
                  break;
               case 86:
                  if (curChar == 118)
                     jjstateSet[jjnewStateCnt++] = 85;
                  break;
               case 87:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 86;
                  break;
               case 88:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 87;
                  break;
               case 90:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 89;
                  break;
               case 91:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 90;
                  break;
               case 92:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 91;
                  break;
               case 93:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 92;
                  break;
               case 94:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 93;
                  break;
               case 95:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 94;
                  break;
               case 96:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 95;
                  break;
               case 98:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 97;
                  break;
               case 99:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 98;
                  break;
               case 100:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 99;
                  break;
               case 101:
                  if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 100;
                  break;
               case 102:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 101;
                  break;
               case 103:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 102;
                  break;
               case 104:
                  if (curChar == 97)
                     jjCheckNAdd(80);
                  break;
               case 105:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 104;
                  break;
               case 107:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 106;
                  break;
               case 108:
                  if (curChar == 118)
                     jjstateSet[jjnewStateCnt++] = 107;
                  break;
               case 109:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 108;
                  break;
               case 110:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 109;
                  break;
               case 112:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 111;
                  break;
               case 113:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 112;
                  break;
               case 114:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 113;
                  break;
               case 115:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 114;
                  break;
               case 116:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 115;
                  break;
               case 117:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 116;
                  break;
               case 118:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 117;
                  break;
               case 120:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 119;
                  break;
               case 121:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 120;
                  break;
               case 122:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 121;
                  break;
               case 123:
                  if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 122;
                  break;
               case 124:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 123;
                  break;
               case 125:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 124;
                  break;
               case 126:
                  if (curChar == 101 && kind > 7)
                     kind = 7;
                  break;
               case 127:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 126;
                  break;
               case 128:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 127;
                  break;
               case 129:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 128;
                  break;
               case 130:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 129;
                  break;
               case 131:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 130;
                  break;
               case 132:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 131;
                  break;
               case 134:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 133;
                  break;
               case 135:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 134;
                  break;
               case 136:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 135;
                  break;
               case 137:
                  if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 136;
                  break;
               case 138:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 137;
                  break;
               case 139:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 138;
                  break;
               case 140:
                  if (curChar == 116 && kind > 7)
                     kind = 7;
                  break;
               case 141:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 140;
                  break;
               case 142:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 141;
                  break;
               case 143:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 142;
                  break;
               case 144:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 143;
                  break;
               case 145:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 144;
                  break;
               case 146:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 145;
                  break;
               case 148:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 147;
                  break;
               case 149:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 148;
                  break;
               case 150:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 149;
                  break;
               case 151:
                  if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 150;
                  break;
               case 152:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 151;
                  break;
               case 153:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 152;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 154 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   77, 78, 72, 70, 71, 103, 125, 139, 153, 29, 47, 55, 68, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, "\12", "\74", "\174", 
"\76", "\73", };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT", 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[154];
private final int[] jjstateSet = new int[308];
protected char curChar;
/** Constructor. */
public ScheduleParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public ScheduleParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 154; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
         matchedToken = jjFillToken();
         return matchedToken;
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
