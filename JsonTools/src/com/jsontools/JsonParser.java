package com.jsontools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Stack;

/**
 * 
 * @author Calvin Withun 09-05-2021
 * 
 * <p>
 * The <code>JsonParser</code> class is used to load <code>JsonObject
 * </code> objects from .json files and from Strings containing valid
 * json data. This process ignores whitespace (spaces, tabs, newlines,
 * and carriage returns) which is not located within a String value.
 * </p>
 *
 */
public final class JsonParser {
	
	public static JsonObject parseFile(String filepath) throws FileNotFoundException, Exception {
		return parseFile(new File(filepath));
	}
	
	public static JsonObject parseFile(File file) throws FileNotFoundException, Exception {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		
		String data = "";
		while (scanner.hasNextLine()) {
			data += scanner.nextLine();
		}
		scanner.close();
		return parseString(data);
	}
	
	public static JsonObject parseString(String data) throws Exception {
		return constructJsonObject(removeWhitespace(data));
	}
	
	private static String removeWhitespace(String line) throws StringIndexOutOfBoundsException {
		Stack<Character> stack = new Stack<Character>();
		int currentIndex = 0;
		while (currentIndex < line.length()) {
			char currentChar = line.charAt(currentIndex);
			
			if (currentChar == '"') {
				// check if character is inside a string
				try {
					if (stack.peek() == '"') {
						if (line.charAt(currentIndex - 1) != '\\') {
							// ending a string (quotes are not escaped)
							stack.pop();
						}
					} else {	
						// starting a string
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r'){
				// verify whitespace is not in a string
				if (stack.size() == 0) {
					line = line.substring(0, currentIndex) + line.substring(currentIndex + 1, line.length());
					continue;
				}
			}
			currentIndex++;
		}
		return line;
	}
	
	private static JsonObject constructJsonObject(String data) throws Exception {
		char[] dataArray = data.toCharArray();
		int length = dataArray.length;
		if (dataArray[0] != '{') {
			throw new Exception("JSON formatting error: json object does not begin with '{'");
		}
		if (dataArray[length - 1] != '}') {
			throw new Exception("JSON formatting error: json object does not end with '}'");
		}
			
		Stack<Character> stack = new Stack<Character>();
		JsonObject jobj = new JsonObject();
		
		int beginIndex = 1;
		int currentIndex = beginIndex;
		while (currentIndex < length - 1) {
			char currentChar = dataArray[currentIndex];
			
			if (currentChar == '{') {
				// starting a json object
				// verify character is not inside a string
				try {
					if (stack.peek() != '"') {
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == '[') {
				// starting a json list
				// verify character is not inside a string
				try {
					if (stack.peek() != '"') {
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == '"') {
				// starting or ending a string
				// check if character is inside a string
				try {
					if (stack.peek() == '"') {
						if (dataArray[currentIndex - 1] != '\\') {
							// ending a string (quotes are not escaped)
							stack.pop();
						}
					} else {
						// starting a string
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == '}') {
				// ending a json object
				// check if character is in a string
				try {
					if (stack.peek() == '"') {
						currentIndex++;
						continue;
					}
				} catch (EmptyStackException ex) {
					throw new Exception("JSON formatting error: unexpected token '}'");
				}
				// check for valid nesting
				if (stack.peek() == '{') {
					// exiting a json object
					stack.pop();
				} else {
					throw new Exception("JSON formatting error: unexpected token '}'");
				}
				// check if json object is at root level
				if (stack.size() == 0) {
					String entry = data.substring(beginIndex, currentIndex + 1);
					String key = validateKey(entry);
					jobj.put(key, constructJsonObject(entry.substring(key.length() + 3, entry.length())));
					beginIndex = currentIndex + 2;
					if (dataArray[currentIndex + 1] == ',') {
						currentIndex++;
					}
				}
			} else if (currentChar == ']') {
				// ending a json list
				// check if character is in a string
				try {
					if (stack.peek() == '"') {
						currentIndex++;
						continue;
					}
				} catch (EmptyStackException ex) {
					throw new Exception("JSON formatting error: unexpected token ']'");
				}
				// check for valid nesting
				if (stack.peek() == '[') {
					// exiting a json object
					stack.pop();
				} else {
					throw new Exception("JSON formatting error: unexpected token ']'");
				}
				// check if json object is at root level
				if (stack.size() == 0) {
					String entry = data.substring(beginIndex, currentIndex + 1);
					String key = validateKey(entry);
					jobj.put(key, constructJsonList(entry.substring(key.length() + 3, entry.length())));
					beginIndex = currentIndex + 2;
					if (dataArray[currentIndex + 1] == ',') {
						currentIndex++;
					}
				}
			} else if (currentChar == ',') {
				// ending an entry
				// verify comma is ending a primitive and if primitive is at root level
				char prev = dataArray[currentIndex - 1];
				if (prev != '}' && prev != ']' && stack.size() == 0) {
					String entry = data.substring(beginIndex, currentIndex);
					String key = validateKey(entry);
					jobj.put(key, constructJsonPrimitive(entry.substring(key.length() + 3, entry.length())));
					beginIndex = currentIndex + 1;
				}
				
			}
			currentIndex++;
		}
		if (beginIndex < currentIndex) {
			// there is an unrecognized primitive at the end
			// of the json file if this code is hit
			String entry = data.substring(beginIndex, currentIndex);
			String key = validateKey(entry);
			jobj.put(key, constructJsonPrimitive(entry.substring(key.length() + 3, entry.length())));
		}
		return jobj;
	}
	
	private static JsonList constructJsonList(String data) throws Exception {
		char[] dataArray = data.toCharArray();
		int length = dataArray.length;
		if (dataArray[0] != '[') {
			throw new Exception("JSON formatting error: json list does not begin with '['");
		}
		if (dataArray[length - 1] != ']') {
			throw new Exception("JSON formatting error: json list does not end with ']'");
		}
		
		Stack<Character> stack = new Stack<Character>();
		JsonList jlist = new JsonList();
		
		int beginIndex = 1;
		int currentIndex = beginIndex;
		while (currentIndex < length - 1) {
			char currentChar = dataArray[currentIndex];
			
			if (currentChar == '{') {
				// starting a json object
				// verify character is not inside a string
				try {
					if (stack.peek() != '"') {
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == '[') {
				// starting a json list
				// verify character is not inside a string
				try {
					if (stack.peek() != '"') {
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == '"') {
				// starting or ending a string
				// check if character is inside a string
				try {
					if (stack.peek() == '"') {
						if (dataArray[currentIndex - 1] != '\\') {
							// ending a string (quotes are not escaped)
							stack.pop();
						}
					} else {
						// starting a string
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == '}') {
				// ending a json object
				// check if character is in a string
				try {
					if (stack.peek() == '"') {
						currentIndex++;
						continue;
					}
				} catch (EmptyStackException ex) {
					throw new Exception("JSON formatting error: unexpected token '}'");
				}
				// check for valid nesting
				if (stack.peek() == '{') {
					// exiting a json object
					stack.pop();
				} else {
					throw new Exception("JSON formatting error: unexpected token '}'");
				}
				// check if json object is at root level
				if (stack.size() == 0) {
					jlist.add(constructJsonObject(data.substring(beginIndex, currentIndex + 1)));
					beginIndex = currentIndex + 2;
				}
			} else if (currentChar == ']') {
				// ending a json list
				// check if character is in a string
				try {
					if (stack.peek() == '"') {
						currentIndex++;
						continue;
					}
				} catch (EmptyStackException ex) {
					throw new Exception("JSON formatting error: unexpected token ']'");
				}
				// check for valid nesting
				if (stack.peek() == '[') {
					// exiting a json object
					stack.pop();
				} else {
					throw new Exception("JSON formatting error: unexpected token ']'");
				}
				// check if json object is at root level
				if (stack.size() == 0) {
					jlist.add(constructJsonList(data.substring(beginIndex, currentIndex + 1)));
					beginIndex = currentIndex + 2;
					if (dataArray[currentIndex + 1] == ',') {
						currentIndex++;
					}
				}
			} else if (currentChar == ',') {
				// ending an entry
				// verify comma is ending a primitive and if primitive is at root level
				char prev = dataArray[currentIndex - 1];
				if (prev != '}' && prev != ']' && stack.size() == 0) {
					jlist.add(constructJsonPrimitive(data.substring(beginIndex, currentIndex)));
					beginIndex = currentIndex + 1;
				}
				
			}
			currentIndex++;
		}
		if (beginIndex < currentIndex) {
			// there is an unrecognized primitive at the end
			// of the json list if this code is hit
			jlist.add(constructJsonPrimitive(data.substring(beginIndex, currentIndex)));
		}
		return jlist;
	}
	
	private static Object constructJsonPrimitive(String data) throws Exception {
		if (data.charAt(0) == '"' && data.charAt(data.length() - 1) == '"') {
			// check for string
			return data.substring(1, data.length() - 1);
		} else if (data.equals("true")) {
			// check for bool (true)
			return true;
		} else if (data.equals("false")) {
			// check for bool (false)
			return false;
		} else {
			try {
				return Long.parseLong(data);
			} catch (NumberFormatException ex1) {
				// not a Long
				try {
					return Double.parseDouble(data);
				} catch (NumberFormatException ex2) {
					// not a Double
					throw new Exception("JSON formatting error: (" + data + ") is not a valid value");
				}
			}
		}
	}
	
	private static String validateKey(String entry) throws Exception {
		String[] split = entry.split(":");
		String key = split[0];
		if (key.charAt(0) == '"' && key.charAt(key.length() - 1) == '"') {
			key = key.substring(1, key.length() - 1);
		} else {
			throw new Exception("Json formatting exception: (" + key + ") is not a valid key");
		}
		return key;
	}

}
