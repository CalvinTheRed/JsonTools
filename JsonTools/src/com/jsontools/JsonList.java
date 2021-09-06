package com.jsontools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Calvin Withun 09-05-2021
 * 
 * <p>
 * The <code>JsonList</code> class represents json lists (arrays). All
 * virtual representations of json lists, such as <code>[1,2,3]</code>,
 * are implemented as instances of this class. <code>JsonList</code> is
 * a class derived from <code>ArrayList&ltObject&gt</code>.
 * </p>
 * <p>
 * JsonList objects are mutable; after they are constructed, JsonList
 * objects can be given new items and they can have their items removed.
 * </p>
 *
 */
public class JsonList extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4436681852335062648L;

	public JsonList() {
		super();
	}
	
	public JsonList(Collection<? extends Object> c) {
		super(c);
		for (Object obj : c) {
			if (!(obj instanceof String)
					&& !(obj instanceof Boolean)
					&& !(obj instanceof Long)
					&& !(obj instanceof Double)
					&& !(obj instanceof JsonObject)
					&& !(obj instanceof JsonList)) {
				clear();
				break;
			}
		}
	}

	@Override
	public boolean add(Object e) {
		if (e instanceof String
				|| e instanceof Boolean
				|| e instanceof Long
				|| e instanceof Double
				|| e instanceof JsonObject
				|| e instanceof JsonList) {
			return super.add(e);
		}
		return false;
	}
	
	@Override
	public void add(int index, Object element) {
		if (element instanceof String
				|| element instanceof Boolean
				|| element instanceof Long
				|| element instanceof Double
				|| element instanceof JsonObject
				|| element instanceof JsonList) {
			super.add(index, element);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends Object> e) {
		for (Object obj : e) {
			if (!(obj instanceof String)
					&& !(obj instanceof Boolean)
					&& !(obj instanceof Long)
					&& !(obj instanceof Double)
					&& !(obj instanceof JsonObject)
					&& !(obj instanceof JsonList)) {
				return false;
			}
		}
		return super.addAll(e);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Object> e) {
		for (Object obj : e) {
			if (!(obj instanceof String)
					&& !(obj instanceof Boolean)
					&& !(obj instanceof Long)
					&& !(obj instanceof Double)
					&& !(obj instanceof JsonObject)
					&& !(obj instanceof JsonList)) {
				return false;
			}
		}
		return super.addAll(index, e);
	}
	
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
