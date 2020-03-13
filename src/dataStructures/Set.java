package dataStructures;

//Code taken from lab 1
public interface Set<E> extends Iterable<E> {

	public boolean add(E obj);
	public boolean isMember(E obj);	
	public boolean remove(E obj);
	public boolean isEmpty();
	public int size();
	public void clear();
	public Set<E> union(Set<E> S2);
	public Set<E> difference(Set<E> S2);
	public Set<E> intersection(Set<E> S2);
	public boolean isSubSet(Set<E> S2);
	public boolean equals(Set<E> S2);
	public Set<Set<E>> singletonSets();
}