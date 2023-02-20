package com.ravingarinc.api.concurrent;

import com.ravingarinc.api.I;

import java.util.logging.Level;

/**
 * Defines an error which is related to the asynchronous execution of code
 */
public class AsynchronousException extends Exception {
    public AsynchronousException(final String message, final Throwable invocation) {
        super(message, invocation);
    }

    public AsynchronousException(final String message) {
        super(message);
    }

    public void report() {
        I.log(Level.SEVERE, "AsynchronousException! " + getMessage(), getCause());
    }
}
