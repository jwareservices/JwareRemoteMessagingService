
package org.jware.notify;

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
 * File: Notifier.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 1.2 Feb. 4, 1997
 */

/*
 * Notifier is the main class in the notification mechanism. It stores
 * Notifiable objects in a Vector and any Notifications in a (FIFO) Queue.
 * Any posted notifications will be sent to all registeredNotifiable objects
 * ReceiveNotify ( ) method, multi-thread safe. This will continue until the
 * queue is empty. All posted messages are dequeued and sent only once. Since
 * the class uses a thread to access its internal notification mechanism, all
 * methods are synchronized. Further, Notifier uses Java's full set of thread
 * safety mechanisms and makes every attempt to prevent starvation or deadlock.
 * <p>
 * 
 * <pre>
 * Usage: Normally this class would only be instantiated once, by some top
 *	level controlling class.  However any number of Notifiers can be
 * 	instantiated.  Once created the embedded thread begins its run ( )
 *	method, waiting for Notifications to be posted.
 *
 *	...
 *
 *	// start the thread and wait for notifications. <p>
 *	Notifier notifier = new Notifier ( );
 *	...
 *
 *	// make some notifiable objects. <p>
 *	SomeNotifiableClass	someObject1 = new SomeNotifiableClass ( );
 *	SomeNotifiableClass	someObject2 = new SomeNotifiableClass ( );
 *	...
 *
 *	// register the above classes with the notifier object. <p>
 *	notifier.registerNotifiable ( someObject1 );
 *	notifier.registerNotifiable ( someObject2 );
 *	...
 *
 *	// create some notifications and post.<p>
 *	for ( int i = 0;  i++ < 5;  )
 *	{
 *		Notification newNotification = new Notification ( );
 *		newNotification.m_sourceObj = "This is Notification #" + i;
 *		notifier.postNotification ( newNotification );
 *	} <p>
 *	...
 * <p>
 *	Note: Registered objects must implement the Notifiable interface
 *	to correctly use this mechanism. Further, a Notification object
 *	is used as a wrapper for the information to be posted.
 * </pre> <p>
 * @see	Notifier
 * @see Notification
 * @see	Queue
 */
public class Notifier implements Runnable {

    /**
     * The default running priority is Thread.NORM_PRIORITY - 1. This ensures
     * that other threads get some cpu time.
     */
    public static final int DEFAULT_PRIORITY = Thread.NORM_PRIORITY - 1;
    /**
     * The notify priority is set to Thread.NORM_PRIORITY + 1
     */
    public static final int NOTIFY_PRIORITY = Thread.NORM_PRIORITY + 1;
    /**
     * Just for ease of program readability, is the queue pending or empty?
     */
    private static final boolean PENDING = true;
    private static final boolean EMPTY = false;
    /**
     * Used as a switch for testing emptiness of the notification queue.
     */
    private boolean messageQ = EMPTY;
    /**
     * Unimplemented for now.
     */
    private Vector threadGroups;
    private ThreadGroup currentThreadGroup;
    /**
     * The runnable thread.
     */
    private Thread theThread;
    /**
     * These members store queue notifiable objects and posted notifications.
     */
    private Vector notifiableObjects;
    private Queue notifyMessageQ;

    /**
     * Default constructor. Creates the internal members, sets the thread
     * priority to its default, and starts the thread.
     */
    public Notifier() {

        messageQ = EMPTY;

        notifiableObjects = new Vector(0, 1);

        notifyMessageQ = new Queue();

        theThread = new Thread(this);

        theThread.setPriority(DEFAULT_PRIORITY);

        theThread.start();

    }

    /**
     * Register a JWNotifiable object.<p>
     *
     * @param i_notifiableObj is the JWNotifiable to be added to the Vector.<p>
     * @exception	JWRuntimeException Thrown if an "parameter" insertion into the
     * internal Vector fails.
     */
    public synchronized void registerNotifiable(Notifiable i_notifiableObj) {

        try {
            notifiableObjects.addElement(i_notifiableObj);
        } catch (Exception e) {
            throw new NotificationException("Registration Failure: " + e.getMessage());
        }

    }

    /**
     * Opposite of registerNotifiable. This method unregisters a JWNotifiable
     * object.<p>
     *
     * @param i_notifiableObj is the JWNotifiable to be removed from the
     * Vector.<p>
     * @exception	JWRuntimeException Thrown if the "param" is not found in the
     * internal Vector.
     */
    public synchronized void dropNotifiable(Notifiable i_notifiableObj) {
        try {
            if (notifiableObjects.contains(i_notifiableObj)) {
                notifiableObjects.removeElement(i_notifiableObj);
            }
        } catch (Exception e) {
            throw new NotificationException("Registration Failure: " + e.getMessage());
        }

    }

    /**
     * Post a notification object to the queue. This method raises the priority
     * level of the internal thread to NOTIFY_PRIORITY.<p>
     *
     * @param i_postedNotification is the JWNotification to be queued.<p>
     * @exception	JWRuntimeException Thrown if the "parameter" insertion into
     * the internal JWQueue or if raising the threads priority fails.
     */
    public synchronized void postNotification(Notification i_postedNotification) {

        //
        //	When a notification is posted, set the switch messageQ 
        //	to PENDING or true, raise the priority and return.
        //
        try {

            notifyMessageQ.enqueue(i_postedNotification);

            messageQ = PENDING;

            theThread.setPriority(NOTIFY_PRIORITY);
        } catch (Exception e) {
            throw new NotificationException("Notification Post Failure: " + e.getMessage());
        }
    }

    /**
     * Used to locate a target object in the Vector.
     */
    private synchronized Notifiable findTarget(Notifiable i_targetObj) {
        return (Notifiable) notifiableObjects.elementAt(notifiableObjects.indexOf(i_targetObj));
    }

    /**
     * Send a notification directly to a targeted notifiable object. The
     * notification is sent immediately.<p>
     *
     * @param	i_targetObj notifiable object to send the urgent notification
     * to.<p>
     * @param	i_notifyMessage notification message to be sent.<p>
     * @exception	JWRuntimeException Thrown if the target object is not found.
     */
    public synchronized void sendUrgentNotify(Notifiable i_targetObj, Notification i_notifyMessage) {

        try {
            findTarget(i_targetObj).receiveNotify(i_notifyMessage.sourceObj,
                    i_notifyMessage.targetObj,
                    i_notifyMessage.notifyMessage);

        } catch (Exception e) {
            throw new NotificationException("Send Target Not Found");
        }

    }

    /**
     * Send out one message to all registered notifiable objects. If this method
     * is called there are notifications to be sent. Check for notifiable
     * objects, if yes, aquire a lock on the queue. Dequeue a notification. Get
     * an enumeration of all notifiable objects. Loop while there elements
     * available, calling-by-cast and yielding through each iteration. Check the
     * message queue. Set it and the threads priority appropriately.
     */
    private synchronized void sendNotify() {
        if (!notifiableObjects.isEmpty()) {

            synchronized (notifyMessageQ) // SF I don't think this is needed because the method is sync'ed, have not tried without it.
            {

                Notification notifyMessage = (Notification) notifyMessageQ.dequeue();

                Enumeration notificationsEnum = notifiableObjects.elements();

                while (notificationsEnum.hasMoreElements()) {

                    ((Notifiable) notificationsEnum.nextElement()).receiveNotify(notifyMessage.sourceObj,
                            notifyMessage.targetObj,
                            notifyMessage.notifyMessage);

                    //
                    // yielding here helps deter starvation during long loops.
                    //
                    theThread.yield();
                }

                if (notifyMessageQ.isEmpty()) {
                    messageQ = EMPTY;

                    theThread.setPriority(DEFAULT_PRIORITY);
                }

            }
        }
    }

    /**
     * This is the Runnable implementation. It is an infinite loop that checks
     * an internal semaphore to see if notifications are pending. This semaphore
     * forces the thread to wait until there are notifications to be sent. Once
     * posted the semaphore is raised to a PENDING state, at which point the
     * thread is allowed to continue and notifications are sent.
     */
    public synchronized void run() {

        while (true) {

            if (messageQ != PENDING && (Thread.currentThread() == theThread)) {
                try {
                    wait(2000);
                } catch (InterruptedException e) {
                };
            } else {
                notify();

                sendNotify();
            }
        }

    }
}
