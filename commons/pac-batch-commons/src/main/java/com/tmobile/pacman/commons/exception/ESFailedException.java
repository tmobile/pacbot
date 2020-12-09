package com.tmobile.pacman.commons.exception;

public class ESFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Integer responseCode;
    
    public ESFailedException(int responseCode) {
        super();
        this.responseCode = responseCode;
    }
    
    public ESFailedException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }
    
    public ESFailedException(Throwable cause, int responseCode) {
        super(cause);
        this.responseCode = responseCode;
    }
   
    public ESFailedException(String message, Throwable cause, int responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }

	public Integer getResponseCode() {
		return responseCode;
	}
}
