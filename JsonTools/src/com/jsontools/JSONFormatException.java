package com.jsontools;

public class JSONFormatException extends Exception {
	
	public JSONFormatException(String msg) {
		super("JSON formatting exception: " + msg);
	}

}
