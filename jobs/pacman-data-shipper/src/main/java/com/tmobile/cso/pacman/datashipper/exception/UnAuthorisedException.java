package com.tmobile.cso.pacman.datashipper.exception;

public class UnAuthorisedException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public UnAuthorisedException() {
    }
    
    /**
     * 
     */
    public UnAuthorisedException(String msg) {
        super(msg);
    }
    
}
