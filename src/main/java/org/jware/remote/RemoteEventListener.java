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

/*
 * File: RemoteEventListerner.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 2.0 July 1, 1998
 */


/*
 * RemoteEventListener is the EventListener for the jware remote event model.
 */
public interface RemoteEventListener extends java.util.EventListener {

    /**
     * For use on return.
     */
    public static final boolean HANDLE_GOOD = true;
    /**
     * For use on return.
     */
    public static final boolean HANDLE_FAILED = false;

    /**
     * Override to use a Notifier object to call a Notifiable objects instance
     * method.
     *
     * @param	theEvent the event to be handled.<p>
     */
    public boolean handleEvent(java.util.EventObject theEvent);
}
