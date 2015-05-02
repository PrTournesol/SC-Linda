package linda;



import java.util.Collection;

/** Public interface to a Linda implementation. */
public interface Linda {
	
	/** Adds a tuple t to the tuplespace. */
	public void write(Tuple t);
	
	/** Returns a tuple matching the template and removes it from the tuplespace.
	 * Blocks if no corresponding tuple is found. */
	public Tuple take(Tuple template);
	
	/** Returns a tuple matching the template and leaves it in the tuplespace.
	 * Blocks if no corresponding tuple is found. */
	public Tuple read(Tuple template);

	/** Returns a tuple matching the template and removes it from the tuplespace.
	 * Returns null if none found. */
	public Tuple tryTake(Tuple template);

	/** Returns a tuple matching the template and leaves it in the tuplespace.
	 * Returns null if none found. */
	public Tuple tryRead(Tuple template);

	/** Returns all the tuples matching the template and removes them from the tuplespace.
	 * Returns an empty collection if none found (never blocks).
	 * Note: there is no atomicity or consistency constraints between takeAll and other methods;
	 * for instance two concurrent takeAll with similar templates may split the tuples between the two results. 
	 */
	public Collection<Tuple> takeAll(Tuple template);
	
	/** Returns all the tuples matching the template and leaves them in the tuplespace.
	 * Returns an empty collection if none found (never blocks).
	 * Note: there is no atomicity or consistency constraints between readAll and other methods;
	 * for instance (write([1]);write([2])) || readAll([?Integer]) may return only [2].
	 */
	public Collection<Tuple> readAll(Tuple template);
	
	/** Registers a callback which will be called when a tuple matching the template appears.
	 * The found tuple is removed from the tuplespace.
	 * The callback is kept if it returns true, and is deregistered if it returns false. This is the only way to deregister a callback.
	 * Note that the callback may immediately fire if a matching tuple is already present. And as long as it returns true, it immediately fires multiple times.
	 * Beware: as the firing must wait for the return value of the callback, the callback must never block (see {@link AsynchronousCallback} class). 
	 * Callbacks are not ordered: if more than one may be fired, the chosen one is arbitrary.
	 * 
	 * @param template the filtering template.
	 * @param callback the callback to call if a matching tuple appears.
	 */
	public void eventRegister(Tuple template, Callback callback);	
	
	/** To debug, prints any information it wants (e.g. the tuples in tuplespace or the registered callbacks), prefixed by <code>prefix</code. */
	public void debug(String prefix);

}
