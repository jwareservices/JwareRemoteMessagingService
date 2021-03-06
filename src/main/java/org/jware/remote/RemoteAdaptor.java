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
 * File: RemoteAdaptor.java
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 * 
 * @version 1.3 June. 4, 1998
 */

public class RemoteAdaptor implements RemoteEventListener {

    /**
     * Override to use a Notifier object to call a Notifiable objects instance
     * method.
     *
     * @param notifyMessage is used to store a notification.<p>
     */
    public boolean handleEvent(java.util.EventObject remoteEvent) {
        return HANDLE_GOOD;
    }
}

