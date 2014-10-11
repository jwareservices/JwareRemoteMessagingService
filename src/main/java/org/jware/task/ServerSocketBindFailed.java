package org.jware.task;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * File: ServerSocketBindFailed.java Created On: 11/00/2014
 *
 * @author J. Paul Jackson <jwareservices@gmail.com>
 *
 * Purpose:
 */
public class ServerSocketBindFailed extends RuntimeException {

    public ServerSocketBindFailed(String e) {
        super(e);
    }

}
