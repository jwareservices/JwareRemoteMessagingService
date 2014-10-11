package org.jware.remote.event;/* * Copyright (C) 2014 J. Paul Jackson <jwareservices@gmail.com> * * This program is free software: you can redistribute it and/or modify * it under the terms of the GNU General Public License as published by * the Free Software Foundation, either version 3 of the License, or * (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License * along with this program.  If not, see <http://www.gnu.org/licenses/>. */import java.util.*;/* * File: GetAllUsersRemoteEvent.java * * @author J. Paul Jackson <jwareservices@gmail.com> *  * @version 1.2 Feb. 4, 1997 *//*  *This is a support class for the test project.*/public class GetAllUsersRemoteEvent extends RemoteEvent {    private static final long serialVersionUID = 1L;    public Vector allUsers;    public GetAllUsersRemoteEvent(Object source, Vector users) {        super(source);        allUsers = users;    }}