JwareRemoteMessagingService
===========================
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
