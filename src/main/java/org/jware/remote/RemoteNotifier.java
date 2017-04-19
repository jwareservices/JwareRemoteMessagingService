package org.jware.remote;

/*
 * Copyright (C) 2014 J. Paul Jackson <jwareservices@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.jware.notify.Notifiable;
import org.jware.notify.Notification;
import org.jware.util.NotificationEvent;

/*
 * File: RemoteNotifier.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 1.3 June 4, 1998
 */

/*
 * Provides an interface for JWNotifiable objects. Objects that implement this
 * interface can use a JWNotifier object to post and receive notifications
 * between each other.<p>
 */
public class RemoteNotifier implements Runnable {

    /**
     * Client socket.
     */
    protected Socket clientSocket;
    /**
     * Client input stream.
     */
    protected DataInputStream in;
    /**
     * Client output stream.
     */
    protected DataOutputStream out;
    Thread runThread;
    boolean running = false;
    Notifiable owner;
    String host;
    int port;

    /**
     * Override to use a JWNotifier object to call a JWNotifiable objects
     * instance method.
     *
     * @param	notifyMessage is used to store a notification.<p>
     */
    public RemoteNotifier(Notifiable owner) {
        this.owner = owner;
    }

    public void connect(String hst, int prt) throws IOException {
        this.host = hst;
        this.port = prt;

        clientSocket = new Socket(host, port);

        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        if (!running) {
            startThread();
        } else {
            if (runThread != null) {
                runThread.stop();
                running = false;
            }
            startThread();
        }

    }

    void startThread() {
        runThread = new Thread(this);
        runThread.setDaemon(true);
        runThread.start();
        running = true;
    }

    public void run() {
        while (true) {
            try {
                NotificationEvent notification = new NotificationEvent(this, in.readUTF());

                notification.readArgs(in);
                Notification msg = (Notification)notification.getSource();
                owner.receiveNotify(msg.sourceObj, msg.targetObj, msg.notifyMessage);

            } catch (IOException e) {
            }
        }
    }

    public void postNotification(NotificationEvent i_notification) {
        try {
            out.writeUTF(i_notification.getID());
        } catch (IOException e) {
        }

        i_notification.writeArgs(out);
    }
}
