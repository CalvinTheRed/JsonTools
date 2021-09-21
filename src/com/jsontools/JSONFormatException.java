package com.jsontools;

public class JSONFormatException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5240789095640930508L;

	public JSONFormatException(String msg) {
		super("JSON formatting exception: " + msg);
	}

}
