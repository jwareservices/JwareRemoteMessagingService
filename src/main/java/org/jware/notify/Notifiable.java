
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

/*
 * File: Notifiable.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 1.2 Feb. 4, 1997
 */

/*
 * Provides an interface for Notifiable objects. Objects that implement this
 * interface can use a Notifier object to post and receive notifications
 * between each other.<p>
 */
public interface Notifiable {

    /**
     * For use on return.
     */
    public static final boolean RECEIVE_ACKNOWLEDGED = true;
    /**
     * For use on return.
     */
    public static final boolean RECEIVE_FAILED = false;

    /**
     * Override to use a Notifier object to call a Notifiable objects instance
     * method. The parameters mirror a Notification wrapper.<p>
     *
     * @param	sourceObj can be a source for the notification.<p>
     * @param	notifyMessage an integer useful for passing switches.<p>
     * @param	targetObh can be used to store a target for a notification.<p>
     */
    public boolean receiveNotify(Object sourceObj, Object targetObj, int notifyMessage);
}
