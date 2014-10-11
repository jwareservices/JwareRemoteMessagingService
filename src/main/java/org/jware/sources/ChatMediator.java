package org.jware.sources;/* * Copyright (C) 2014 J. Paul Jackson <jwareservices@gmail.com> * * This program is free software: you can redistribute it and/or modify * it under the terms of the GNU General Public License as published by * the Free Software Foundation, either version 3 of the License, or * (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License * along with this program.  If not, see <http://www.gnu.org/licenses/>. */import java.io.DataInputStream;import java.io.DataOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.OutputStream;import java.net.ServerSocket;import java.net.Socket;import java.util.*;import org.jware.notify.Notifiable;import org.jware.notify.Notification;import org.jware.notify.Notifier;import org.jware.util.NotificationEvent;import org.jware.util.Queue;/* * File: ChatMediator.java * * @author J. Paul Jackson <jwareservices@gmail.com> *  * @version 1.2 Feb. 4, 1997 *//** These classes are support classes for the test project.*/class PeerConnection implements Runnable, Notifiable {    ChatMediator mediator;    DataInputStream in;    DataOutputStream out;    public String id;    static int currentID = 1;    public PeerConnection(ChatMediator m, InputStream in, OutputStream out) {        mediator = m;        this.in = new DataInputStream(in);        this.out = new DataOutputStream(out);        new Thread(this).start();    }    public void run() {        while (true) {            try {                NotificationEvent note = new NotificationEvent(this, in.readUTF());                note.readArgs(in);                mediator.receiveNotification(note);            } catch (IOException e) {            }        }    }        public boolean receiveNotify( Object src, Object trg, int id) {        receiveNotification((NotificationEvent)src);        return true;    }        public boolean receiveNotification(NotificationEvent note) {        try {            out.writeUTF(note.getID());        } catch (IOException e) {            return false;        }        note.writeArgs(out);        return true;    }}	public class ChatMediator implements Runnable, Notifiable {    public static final int PORT = 5073;    Notifier serverNotifier;    ServerSocket serverSocket ;    Vector currentUsers;    public ChatMediator() {        serverNotifier = new Notifier();        try {            serverSocket = new ServerSocket(PORT);        } catch (IOException e) {        }        currentUsers = new Vector();        new Thread(this).start();    }    public boolean receiveNotify( Object src, Object trg, int id) {        receiveNotification((NotificationEvent)src);        return true;    }        public boolean receiveNotification(NotificationEvent note) {        serverNotifier.postNotification((Notification)note.getSource());        return true;    }    public void updatePeer() {        for (int i = 0; i < currentUsers.size() - 1; i++) {        }    }    public void run() {        while (true) {            try {                Socket s = serverSocket.accept();                PeerConnection peer = new PeerConnection(this, s.getInputStream(), s.getOutputStream());                currentUsers.addElement(peer);                serverNotifier.registerNotifiable(peer);  //              ChatMediator();            } catch (IOException e) {            }        }    }    static public void main(String args[]) {        new ChatMediator();    }}