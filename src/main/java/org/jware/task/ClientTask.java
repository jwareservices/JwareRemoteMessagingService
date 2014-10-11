package org.jware.task;/* * Copyright (C) 2014 J. Paul Jackson <jwareservices@gmail.com> * * This program is free software: you can redistribute it and/or modify * it under the terms of the GNU General Public License as published by * the Free Software Foundation, either version 3 of the License, or * (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License * along with this program.  If not, see <http://www.gnu.org/licenses/>. */import java.net.Socket;import java.util.*;/* * File: ClientTask.java * * @author J. Paul Jackson <jwareservices@gmail.com> *  * @version 1.2 July 8, 1998 *//*  * Test class.*/public class ClientTask extends Task {    Socket socket;    RemoteReceiveEventTask re;    public ClientTask() {        super(true,0);        try {            socket = new Socket("192.168.1.2", 8080);            re = new RemoteReceiveEventTask(socket.getInputStream());        } catch (Throwable t) {            t.printStackTrace();        }        try {            for (int i = 0; i < 5; i++) {                PEvent e = new PEvent(this, "# " + i);                new RemoteSendEventTask(e, socket.getOutputStream());            }        } catch (Throwable t) {            t.printStackTrace();        }    }    public void doTask() {        if (re.hasEvent) {            EventObject e = re.getNextEvent();            System.out.println(((PEvent) e).mess);        }    }    static public void main(String args[]) {        new ClientTask();    }}