package dataStructures;

import java.util.Iterator;

//Code taken from lab 2
@SuppressWarnings("unchecked")
public class DynamicSet<E> implements Set<E> {

	// static set
	private StaticSet<E> theSet;
	
	// current max capacity
	private int maxCapacity;

	private static final int DEFAULT_SET_SIZE = 10;

	public DynamicSet(int initialCapacity) {
		this.theSet = new StaticSet<E>(initialCapacity);
		this.maxCapacity = initialCapacity;
	}
	@Override
	public Iterator<E> iterator() {
		return theSet.iterator();
	}

	@Override
	public boolean add(E obj) {
		if (this.isMember(obj)) // Avoid extending the set unnecessarily
			return false;
		if (this.maxCapacity == this.theSet.size())
		{
			this.maxCapacity *= 2;
			StaticSet<E> newSet = new StaticSet<E>(this.maxCapacity);
			copySet(theSet, newSet);
			this.theSet = newSet;
		}
		return this.theSet.add(obj);
	}

	private void copySet(Set<E> src, Set<E> dst) {
		for (Object obj : src)
			dst.add((E) obj);
	}

	@Override
	public boolean isMember(E obj) {
		return this.theSet.isMember(obj);
	}

	@Override
	public boolean remove(E obj) {
		return this.theSet.remove(obj);
	}

	@Override
	public boolean isEmpty() {
		return this.theSet.isEmpty();
	}

	@Override
	public int size() {
		return this.theSet.size();
	}

	@Override
	public void clear() {
		this.theSet.clear();
	}

	@Override
	public Set<E> union(Set<E> S2) {
		Set<E> temp = this.theSet.union(S2);
		DynamicSet<E> result = new DynamicSet<E>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public Set<E> difference(Set<E> S2) {
		Set<E> temp = this.theSet.difference(S2);
		DynamicSet<E> result = new DynamicSet<E>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public Set<E> intersection(Set<E> S2) {
		Set<E> temp = this.theSet.intersection(S2);
		DynamicSet<E> result = new DynamicSet<E>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public boolean isSubSet(Set<E> S2) {
		return this.theSet.isSubSet(S2);
	}
	
	@Override
	public boolean equals(Set<E> S2) {
		return this.theSet.equals(S2);
	}
	
	@Override
	public Set<Set<E>> singletonSets() {
		return this.theSet.singletonSets();
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean checkDisjoint(Set[] sets) {
		return StaticSet.checkDisjoint(sets);
	}

}