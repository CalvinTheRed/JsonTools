package com.jsontools;

import java.util.ArrayList;

public class JsonList extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5861632984184717533L;

	@Override
	public String toString() {
		String jsonString = "[";
		int i = 0;
		for (Object item : this) {
			if (item instanceof String) {
				jsonString += "\"" + item + "\"";
			} else {
				jsonString += item;
			}
			if (i < size() - 1) {
				jsonString += ",";
			}
			i++;
		}
		return jsonString + "]";
	}
	
	public boolean subsetOf(JsonList other) {
		ArrayList<Integer> referencedIndices = new ArrayList<Integer>();
		boolean subset = true;
		for (Object thisItem : this) {
			if (thisItem instanceof JsonObject) {
				boolean foundMatch = false;
				for (int i = 0; i < other.size(); i++) {
					Object otherItem = other.get(i);
					if (thisItem.getClass() == otherItem.getClass()) {
						if (((JsonObject) thisItem).subsetOf((JsonObject) otherItem)) {
							if (referencedIndices.contains(i)) {
								continue;
							}
							referencedIndices.add(i);
							foundMatch = true;
							break;
						}
					}
				}
				subset = foundMatch;
			} else if (thisItem instanceof JsonList) {
				boolean foundMatch = false;
				for (int i = 0; i < other.size(); i++) {
					Object otherItem = other.get(i);
					if (thisItem.getClass() == otherItem.getClass()) {
						if (((JsonList) thisItem).subsetOf((JsonList) otherItem)) {
							if (referencedIndices.contains(i)) {
								continue;
							}
							referencedIndices.add(i);
							foundMatch = true;
							break;
						}
					}
				}
				subset = foundMatch;
			} else {
				if (!other.contains(thisItem)) {
					return false;
				}
			}
			if (!subset) {
				return subset;
			}
		}
		return subset;
	}
}
