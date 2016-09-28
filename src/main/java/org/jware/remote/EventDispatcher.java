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

import java.util.*;
import org.jware.util.Queue;

/*
 * File: EventDispatcher.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 1.2 July. 4, 1998
 */

/**
 *
 * EventDispatcher is a class that facilitates the use of the Java event model.
 * It uses a thread and a queuing mechanism which allows EventListeners to be
 * alerted asynchronously when and event is posted. EventDispatcher can be used
 * as is or extended to include more specialized behavior.
 *
 * @see RemoteEventHandler.
 *
 */
public class EventDispatcher extends Object implements EventPoster {

    /**
     *
     * This helper class is a simple runnable for executing the dispatch loop.
     * Its useful because it allows derivation by other runnables.
     *
     */
    private class Dispatcher implements Runnable {

        public synchronized void run() {
            while (true) {
                try {
                    this.wait();
                    dispatchEvent();
                } catch (InterruptedException i) {
                }
            }

        }
    }
    /**
     *
     * Some members
     *
     */
    private Dispatcher theDispatcher;
    private Thread theThread;
    private Hashtable eventListeners;
    private Queue eventQ;
    /**
     * Key for the global event handlers
     */
    private static final String GLOBAL_LISTENERS = "#GLOBAL_LISTENERS";
    /**
     * Key for the targeted event handlers
     */
    private static final String TARGETED_LISTENERS = "#TARGETED_LISTENERS";
    /**
     * The default running priority is Thread.NORM_PRIORITY - 1. Not used in
     * version 2.0
     */
    public static final int DEFAULT_PRIORITY = Thread.NORM_PRIORITY - 1;
    /**
     * The notify priority is set to Thread.NORM_PRIORITY + 1
     */
    public static final int NOTIFY_PRIORITY = Thread.NORM_PRIORITY + 1;

    /**
     *
     * Default constructor. Creates the internal members, sets the thread
     * priority, and starts it.
     *
     */
    public EventDispatcher() {
        
        // Create the table to hold all the listeners.
        
        eventListeners = new Hashtable();

        
        // Add the initial Vector to store the global event listeners. 
        // These listeners want all events.
        
        eventListeners.put(GLOBAL_LISTENERS, new Vector());
        eventListeners.put(TARGETED_LISTENERS, new Hashtable());

        eventQ = new Queue();
        theDispatcher = new Dispatcher();
        theThread = new Thread(theDispatcher);
        theThread.setPriority(NOTIFY_PRIORITY);
        theThread.setDaemon(true);
        theThread.start();
    }

    /**
     *
     * Register a EventListener object to receive targeted posted events.<p>
     *
     * @param	newListener the event listener to be added to the want all
     * list.<p>
     *
     */
    public synchronized void addTargetedEventListener(Object listenerID, EventListener newListener) {
        ((Hashtable) eventListeners.get(TARGETED_LISTENERS)).put(listenerID, newListener);
    }

    /**
     *
     * Register a EventListener object to receive all posted events.<p>
     *
     * @param	newListener the event listener to be added to the want all
     * list.<p>
     *
     */
    public synchronized void addEventListener(EventListener newListener) {
        ((Vector) eventListeners.get(GLOBAL_LISTENERS)).addElement(newListener);
    }

    /**
     *
     * Register a EventListener object for a specific type of event. Given an
     * instance of an EventObject, this method determines the class type of the
     * EventObject and passes that to the overriden method below.<p>
     *
     * @param	newListener is the EventListener to be added to the Vector.<p>
     * @param	eventObject is the instance of the event type to be added to the
     * typed listeners table.<p>
     *
     */
    public synchronized void addEventListener(EventListener newListener, EventObject eventObject) {
        addEventListener(newListener, eventObject.getClass());
    }

    /**
     *
     * Register a EventListener object for a specific type of event. This method
     * will maintain the table of listeners that want specific events dispatched
     * to them. Given a class type the method will first check the table for
     * previous registered event types of the same class. If none exists, it
     * will create an entry into the table to store the event type and a Vector
     * for the listeners of that type. <p>
     *
     * @param	newListener is the EventListener to be added to the Vector.<p>
     * @param	eventObject is the event type to be added to the typed listeners
     * table.<p>
     *
     */
    public synchronized void addEventListener(EventListener newListener, Class classType) {
        //
        // Get the vector associated with this event type
        //
        Vector classTypeListeners = (Vector) eventListeners.get(classType);

        //
        // If it's not there create it, and store it with the class type
        //
        if (classTypeListeners == null) {
            classTypeListeners = new Vector();
            eventListeners.put(classType, classTypeListeners);
        }

        //
        // If the listener is not already in the vector, store it
        //
        if (!classTypeListeners.contains(newListener)) {
            classTypeListeners.addElement(newListener);
        }

    }

    /**
     *
     * This method removes all references to the EventListener object from the
     * table.<p>
     *
     * @param oldListener the EventListener to be removed from the table.<p>
     *
     */
    public synchronized void removeEventListener(EventListener oldListener) {
        //
        // Get all table entries
        //
        Enumeration listenerKeys = eventListeners.keys();

        while (listenerKeys.hasMoreElements()) {
            //
            // Get next vector
            //
            Vector v = (Vector) eventListeners.get(listenerKeys.nextElement());

            //
            // If vector is there try to remove any reference to the listener
            //
            if (v != null) {
                v.removeElement(oldListener);
            }
        }
    }

    /**
     *
     * Remove an EventListener. <p>
     *
     * @param oldListener the EventListener to be removed from the table.<p>
     *
     */
    public synchronized void removeEventListener(EventListener oldListener, EventObject eventObject) {
        removeEventListener(oldListener, eventObject.getClass());
    }

    /**
     *
     * Remove an EventListener. <p>
     *
     * @param oldListener the EventListener to be removed from the table.<p>
     *
     */
    public synchronized void removeEventListener(EventListener oldListener, Class classType) {
        Vector v = (Vector) eventListeners.get(classType);

        if (v != null) {
            v.removeElement(oldListener);
        }
    }

    /**
     *
     * Post an event object to the queue, notify the thread and return.
     *
     * @param remoteEvent the the event to be queued.<p>
     *
     */
    public synchronized boolean postEvent(EventObject remoteEvent) {
        boolean returnState = false;

        if (remoteEvent != null) {
            //
            //	When a event is posted, raise the priority and alert its thread
            //
            eventQ.enqueue(remoteEvent);

            synchronized (theDispatcher) {
                theDispatcher.notify();
            }

            returnState = true;
        }

        return returnState;
    }

    /**
     *
     * Dispatch any queued events.
     *
     */
    private void dispatchEvent() {
        try {
            EventObject remoteEvent = (EventObject) eventQ.dequeue();

            Enumeration handlersEnum;

            //
            // Take care of global handlers first
            //
            handlersEnum = ((Vector) eventListeners.get(GLOBAL_LISTENERS)).elements();

            while (handlersEnum.hasMoreElements()) {
                ((RemoteEventListener) handlersEnum.nextElement()).handleEvent(remoteEvent);
            }

            //
            // Now find handlers that just want this event type
            //
            Class eventClass = remoteEvent.getClass();

            handlersEnum = ((Vector) eventListeners.get(eventClass)).elements();

            while (handlersEnum.hasMoreElements()) {
                ((RemoteEventListener) handlersEnum.nextElement()).handleEvent(remoteEvent);
            }

            if (eventQ.isEmpty()) {
                synchronized (theDispatcher) {
                    theDispatcher.wait();
                }
            }
        } catch (NullPointerException n) {
        } // This catches a nullpointer exception from the first line
        // dequeue always goes twice before the thread goes the wait condition, which causes a null to return from the queue.
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
