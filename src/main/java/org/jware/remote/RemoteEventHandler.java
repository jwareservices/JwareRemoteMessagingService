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


import java.io.*;
import java.net.Socket;
import java.util.*;

/*
 * File: RemoteEventHandler.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 1.2 July 1, 1998
 */

/**
 *
 * RemoteEventHandler is an extension of the EventDispatcher class. It is used
 * on the remote (client) side of the JWare remote event model. It receives
 * event objects from a server and post them to any registered (client)
 * EventListeners. Typically the client itself.
 *
 */
public class RemoteEventHandler extends EventDispatcher implements Runnable {

    //
    // Members
    //
    private InputStream in;
    private OutputStream out;
    private Thread connectionThread;
    private boolean running = false;

    /**
     *
     * Default constructor. Creates this classes event dispatcher and thread.
     *
     */
    public RemoteEventHandler() {
        super();

        connectionThread = new Thread(this);
        connectionThread.setDaemon(true);
    }

    /**
     *
     * Create a connection to a host for receiving events.
     *
     * @param	host the string containing the host name.
     * @param	port the port to use.
     *
     */
    public void connectTo(String host, int port) //throws IOException
    {
        Socket clientSocket;

        //
        // Create the socket
        //
        try {
            clientSocket = new Socket(host, port);
            setStreams(clientSocket.getInputStream(), clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RemoteException("Invalid connection");
        }

        //
        // Set up streams
        //
    }

    /**
     *
     * Set up private member streams for reading and writing.
     *
     * @param in The output stream.
     * @param out The input stream.
     *
     */
    public void setStreams(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;

        //
        // A flag used to allow the streams to be set up before the thread starts, avoids
        // a whole bunch of nullpointer exceptions and a dead program.
        //
        if (!running) {
            connectionThread.start();
            running = true;
        }

    }

    /**
     *
     * Send a remote event by creating an ObjectOutputStream from the member
 OutputStream out. Write the event to the stream.
     *
     * @param	event The EventObject to send.
     *
     */
    public boolean sendRemoteEvent(EventObject event) {
        boolean returnState = false;

        if (event != null) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(this.out);
                out.writeObject(event);
                out.flush();
                returnState = true;
            } catch (Throwable t) {
                throw new RemoteException("Unable send event.");
            }
        }

        return returnState;
    }

    /**
     *
     * Block read and return an incoming EventObject.
     *
     */
    private EventObject getNextEvent() throws IOException {
        EventObject returnEvent = null;
        ObjectInputStream in = new ObjectInputStream(this.in);

        try {
            returnEvent = (EventObject) in.readObject();
        } catch (ClassCastException e) {
            throw new RemoteException("Stream returned non-event object.");
        } catch (ClassNotFoundException e2) {
            throw new RemoteException("Unable to resolve class from stream.");
        }

        return returnEvent;
    }

    /**
     *
     * Run forever getting and posting events.
     *
     */
    public void run() {
        while (true) {
            try {
                EventObject theEvent = getNextEvent();
                if (theEvent != null) {
                    postEvent(theEvent); // from parent
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }
}
