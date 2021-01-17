package net.mixednutz.app.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class CollectionDifference<T> {
	
	private final Iterable<T> original;
	
	public CollectionDifference(Collection<T> original) {
		//copy collection into LinkedList
		this.original = new LinkedList<>(original);
	}
	
	/**
	 * Get the missing elements in a new list
	 * 
	 * @param original
	 * @param another
	 * @return
	 */
	public Collection<T> missing(Collection<T> another) {
		Collection<T> ret = new ArrayList<T>();
		for (T t: original) {
			if (!another.contains(t)) {
				ret.add(t);
			}
		}
		return ret;
	}
	
}
