package com.jsontools;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * @author Calvin Withun 09-05-2021
 * 
 * <p>
 * The <code>JsonObject</code> class represents json objects. All virtual
 * representations of json objects, such as <code>{"key":"value"}</code>,
 * are implemented as instances of this class. <code>JsonObject</code> is
 * a class derived from <code>HashMap&ltString,Object&gt</code>.
 * </p>
 * <p>
 * JsonObject objects are mutable; after they are constructed, JsonObject
 * objects can be given new key-value pairs, they can have keys deleted
 * from their contents, and they can have their key-value values modified.
 * </p>
 *
 */
public class JsonObject extends HashMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1586787919457232015L;

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			return super.get(key);
		}
		return null;
	}
	
	@Override
	public Object put(String key, Object value) {
		if (value instanceof String
				|| value instanceof Boolean
				|| value instanceof Long
				|| value instanceof Double
				|| value instanceof JsonObject
				|| value instanceof JsonArray) {
			return super.put(key, value);
		}
		return null;
	}
	
	@Override
	public String toString() {
		String jsonString = "{";
		int i = 0;
		Set<String> keySet = keySet();
		for (String key : keySet) {
			jsonString += "\"" + key + "\":";
			Object value = get(key);
			if (value instanceof String) {
				jsonString += "\"" + value + "\"";
			} else {
				jsonString += value;
			}
			if (i < keySet.size() - 1) {
				jsonString += ",";
			}
			i++;
		}
		return jsonString + "}";
	}
	
	public boolean subsetOf(JsonObject other) {
		boolean subset = true;
		for (String key : keySet()) {
			try {
				Object thisValue = get(key);
				Object otherValue = other.get(key);
				if (thisValue.getClass() != otherValue.getClass()) {
					// return false if identical keys map to different value types
					return false;
				}
				if (thisValue instanceof JsonObject) {
					// recursively check if the value in "this"
					// is a subset of the value in "other"
					subset &= ((JsonObject) thisValue).subsetOf((JsonObject) otherValue);
				} else if (thisValue instanceof JsonArray) {
					// return true iff all elements from "this"
					// are equal to or subsets of items in "other"
					subset &= ((JsonArray) thisValue).subsetOf((JsonArray) otherValue);
				} else {
					// key maps to a primitive
					subset &= thisValue.equals(otherValue);
				}
			} catch (NullPointerException ex) {
				// other did not have matching key
				return false;
			}
			if (!subset) {
				return subset;
			}
		}
		return subset;
	}
	
	public Object seek(String keypath) throws Exception {
		Object currentData = this;
		Stack<Character> stack = new Stack<Character>();
		char[] keypathArray = keypath.toCharArray();
		int length = keypathArray.length;
		int beginIndex = 0;
		int currentIndex = beginIndex;
		
		while (currentIndex < length){
			char currentChar = keypathArray[currentIndex];
			if (currentChar == '[') {
				try {
					if (stack.peek() != '"') {
						stack.push(currentChar);
					}
				} catch (EmptyStackException ex) {
					stack.push(currentChar);
				}
			} else if (currentChar == ']') {
				try {
					char top = stack.peek();
					if (top == '"') {
						currentIndex++;
						continue;
					} else if (top == '[') {
						stack.pop();
					} else {
						throw new Exception("JSON keypath formatting error: unexpected token ']'");
					}	
				} catch (EmptyStackException ex) {
					throw new Exception("JSON keypath formatting error: unexpected token ']'");
				}
			} else if (currentChar == '.') {
				if (stack.size() == 0) {
					// identify key
					String key = keypath.substring(beginIndex, currentIndex);
					// determine if key includes index data
					if (key.charAt(key.length() - 1) == ']') {
						int indexDataStart = key.indexOf('[');
						String indexData = key.substring(indexDataStart + 1, key.length() - 1);
						key = key.substring(0, indexDataStart);
						currentData = ((JsonObject) currentData).get(key);
						
						// apply index specification to currentData
						JsonArray currentArray = (JsonArray) currentData;
						try {
							int index = Integer.parseInt(indexData);
							currentData = currentArray.get(index);
						} catch (NumberFormatException ex){
							// index specified by contents rather than by number
							JsonObject subset = JsonParser.parseObjectString(indexData);
							for (Object item : currentArray) {
								if (item instanceof JsonObject && subset.subsetOf((JsonObject) item)) {
									currentData = item;
									break;
								}
							}
						}
					} else {
						currentData = ((JsonObject) currentData).get(key);
					}
					beginIndex = currentIndex + 1;
				}
			}
			currentIndex++;
		}
		// catch final segment of keypath
		if (beginIndex < length) {
			// identify key
			String key = keypath.substring(beginIndex, currentIndex);
			// determine if key includes index data
			if (key.charAt(key.length() - 1) == ']') {
				int indexDataStart = key.indexOf('[');
				String indexData = key.substring(indexDataStart + 1, key.length() - 1);
				key = key.substring(0, indexDataStart);
				currentData = ((JsonObject) currentData).get(key);
				
				// apply index specification to currentData
				JsonArray currentArray = (JsonArray) currentData;
				try {
					int index = Integer.parseInt(indexData);
					currentData = currentArray.get(index);
				} catch (NumberFormatException ex){
					// index specified by contents rather than by number
					JsonObject subset = JsonParser.parseObjectString(indexData);
					for (Object item : currentArray) {
						if (item instanceof JsonObject) {
							if (subset.subsetOf((JsonObject) item)) {
								currentData = item;
								break;
							}
						}
					}
				}
			} else {
				currentData = ((JsonObject) currentData).get(key);
			}
		}
		return currentData;
	}
	
}
