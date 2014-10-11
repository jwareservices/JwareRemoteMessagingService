package org.jware.util;/* * Copyright (C) 2014 J. Paul Jackson <jwareservices@gmail.com> * * This program is free software: you can redistribute it and/or modify * it under the terms of the GNU General Public License as published by * the Free Software Foundation, either version 3 of the License, or * (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License * along with this program.  If not, see <http://www.gnu.org/licenses/>. *//* * File: HexDecoder.java * * @author J. Paul Jackson <jwareservices@gmail.com> *  * @version 1.2 July. 4, 1998 */public class HexDecoder implements Permutation {    int lastInput = -1;    public HexDecoder() {    }    public void put(String inString) {        int stringLength = inString.length();        int index = 0;        while (stringLength-- > 0) {            int i = hexToInt((byte) inString.charAt(index++));            if (i >= 0) {                if (lastInput >= 0) {                    System.out.println(((lastInput << 4) | i) + " ");                    lastInput = -1;                } else {                    lastInput = i;                }            }        }    }    private int hexToInt(byte inByte) {        if (inByte >= '0' && inByte <= '9') {            return inByte - '0';        }        if (inByte >= 'A' && inByte <= 'F') {            return inByte - 'A' + 10;        }        if (inByte >= 'a' && inByte <= 'f') {            return inByte - 'a' + 10;        }        return -1;    }    public static void main(String[] args) {        HexDecoder enc = new HexDecoder();        enc.put("012345678910111213");    }}