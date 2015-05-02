package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. 
 * data are stored in an ArrayList
 * the data access is managed with monitors
 *
 * @author  Damien Kleiber and Philippe Leleux
 * @version 1.42
 */
public class CentralizedLinda implements Linda {

    private ArrayList<Tuple> list;		  //set containing all of the server's Tuple
    private BookList bookList;			  //object containing all tha callbacks registered
    private Lock moniteur;                //moniteur ensuring exclusive access to list
    private Condition newTupleTake;       //condition (signal, await) : signal [add of a new Tuple] to [waiting take]
    private Condition newTupleRead;       //condition (signal, await) : signal [add of a new Tuple] to [waiting read]
    private int takeCount;                //number of take waiting
    private int readCount;                //number of read waiting

	/** Constructs a server Linda in shared memory by initializing
	 * the monitor (and a newTuple condition) 
	 * the ArrayList of Tuple with null
	 */
    public CentralizedLinda() {
        list= new ArrayList<Tuple>();
        this.moniteur = new ReentrantLock();
        this.newTupleTake = moniteur.newCondition ();
        this.newTupleRead = moniteur.newCondition ();
        this.bookList = new BookList();
    }

	/** Adds a tuple t to the tuplespace.
	 * @param t the tuple to add
	 */
    public void write(Tuple t) {   //verif notifie ds moniteur
           new ThreadWrite(t, this).start();
    }

	/** Returns a tuple matching the template and removes it from the tuplespace.
	 * Blocks if no corresponding tuple is found. 
	 * @param template an object Tuple model of the tuple searched
	 * @return a Tuple matching the template
	 */
    public  synchronized Tuple take(Tuple template) {
        getMoniteur().lock();
        Boolean loopCondition = true;
    	Tuple result = null;
        int i =0;
        try {
            ++takeCount;
            while (loopCondition) {
                System.out.println("boucle take");
		        while(i<list.size()&&loopCondition) {
		            if (getList().get(i).matches(template)) {
		                result = getList().remove(i);
                        --takeCount;
		                loopCondition = false;
		            }
                    i++;
		        }
				if (loopCondition) {
            		newTupleTake.await();
				}
			}
        } catch (InterruptedException ex) {
            Logger.getLogger(CentralizedLinda.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            getMoniteur().unlock();
        }
        return result;
    }

	/** Returns a tuple matching the template and leaves it in the tuplespace.
	 * Blocks if no corresponding tuple is found. 
	 * @param template an object Tuple model of the tuple searched
	 * @return a copy of the tuple matching the template if it exists or else null
	 */
    public  synchronized Tuple read(Tuple template) {
        Boolean loopCondition = true;
        Tuple result = null;
        getMoniteur().lock();
        int i = 0;
        try {
        	++readCount;
		while (loopCondition) {
			System.out.println("boucle read");
				while(i<list.size()&&loopCondition) {
					if (list.get(i).matches(template)&&loopCondition) {
						result = list.get(i).deepclone();
						--readCount;
						loopCondition = false;
					}
					i++;
				}
		        if (loopCondition) {
		            newTupleRead.await();
		        }
		}
	} catch (InterruptedException ex) {
		    Logger.getLogger(CentralizedLinda.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
            getMoniteur().unlock();
        }
	return result;
    }

	/** Returns a tuple matching the template and removes it from the tuplespace.
	 * Returns null if none found. 
	 * @param template an object Tuple model of the tuple searched
	 * @return a Tuple matching the template if it exists or else null
	 */
public Tuple tryTake(Tuple template) {
        Tuple result=null;
        moniteur.lock();
        try {
            int i=0;
            boolean trouve=false;
            while(!trouve && i<list.size()){
                if (list.get(i).matches(template)) { // On vient de trouver.
                    result=list.remove(i);
                    trouve=true; // Pour sortir de la boucle.
                }
                i++;
            }
        } finally {
            moniteur.unlock();
        }
        return result;
}

	/** Returns a tuple matching the template and leaves it in the tuplespace.
	 * Returns null if none found. 
	 * @param template an object Tuple model of the tuple searched
	 * @return a copy of the tuple matching the template if it exists or else null
	 */
    public Tuple tryRead(Tuple template) {
    	Tuple result = null;
        getMoniteur().lock();
        try {
		for (Tuple t : getList()) {
			if (t.matches(template)) result=t.deepclone();
		}
	} finally {
		getMoniteur().unlock();
	}
	return result;
    }

	/** Returns all the tuples matching the template and removes them from the tuplespace.
	 * Returns an empty collection if none found (never blocks).
	 * Note: there is no atomicity or consistency constraints between takeAll and other methods;
	 * for instance two concurrent takeAll with similar templates may split the tuples between the two results.
	 * @param template an object Tuple model of the tuple searched
	 * @return a collection of all the Tuple matching the template if there is any or else an empty collection 
	 */
    public synchronized Collection<Tuple> takeAll(Tuple template) {
	getMoniteur().lock();
	Collection<Tuple> collect= new ArrayList<Tuple>();
	try {
		for (int i = getList().size()-1; i>=0;i--) {
			if (getList().get(i).matches(template)) {
				collect.add(getList().remove(i));
			}
		}
	} finally {
		getMoniteur().unlock();
	}
	return collect;
    }
    
	/** Returns all the tuples matching the template and leaves them in the tuplespace.
	 * Returns an empty collection if none found (never blocks).
	 * Note: there is no atomicity or consistency constraints between readAll and other methods;
	 * for instance (write([1]);write([2])) || readAll([?Integer]) may return only [2].
	 * @param template an object Tuple model of the tuple searched
	 * @return a collection of copies of all the Tuple matching the template if there is any or else an empty collection 
	 */
    public  synchronized Collection<Tuple> readAll(Tuple template) {
        getMoniteur().lock();
        Collection<Tuple> collect=new ArrayList<Tuple>();
        try {
		    for (Tuple t : getList()) {
		        if (t.matches(template)) collect.add(t.deepclone());
		    }
        } finally {
            getMoniteur().unlock();
        }
         return collect;
    }

	/** Registers a callback which will be called when a tuple matching the template appears.
	 * The found tuple is removed from the tuplespace.
	 * The callback is kept if it returns true, and is deregistered if it returns false. This is the only way to deregister a callback.
	 * Note that the callback may immediately fire if a matching tuple is already present. And as long as it returns true, it immediately fires multiple times.
	 * Beware: as the firing must wait for the return value of the callback, the callback must never block (see {@link AsynchronousCallback} class). 
	 * Callbacks are not ordered: if more than one may be fired, the chosen one is arbitrary.
	 * 
	 * @param template the filtering template.
	 * @param callback the callback to call if a matching tuple appears.
	 * @return
	 */
    public void eventRegister(Tuple template, Callback callback) {
		getMoniteur().lock();
        try {
        	getBookList().register(template,this.getList(), ( callback));
        } finally {
		getMoniteur().unlock();
        }	
    }

	/** To debug, prints any information it wants (e.g. the tuples in tuplespace or the registered callbacks), prefixed by <code>prefix</code.
	 * @param prefix
	 */
    public void debug(String prefix) {
    	System.out.println("debug : " + prefix);
    }

    /**
     * @return the list
     */
    public ArrayList<Tuple> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(Tuple t) {
        this.list.add(t);
    }

    /**
     * @return the bookList
     */
    public BookList getBookList() {
        return bookList;
    }

    /**
     * @param bookList the bookList to set
     */
    public void setBookList(BookList bookList) {
        this.bookList = bookList;
    }

    /**
     * @return the moniteur
     */
    public Lock getMoniteur() {
        return moniteur;
    }

    /**
     * @return the newtuple
     */
    public Condition getNewTupleTake() {
        return newTupleTake;
    }

    /**
     * @return the newtuple
     */
    public Condition getNewTupleRead() {
        return newTupleRead;
    }

    /**
     * @return the takeCount
     */
    public int getTakeCount() {
        return takeCount;
    }

    /**
     * @param takeCount the takeCount to set
     */
    public void setTakeCount(int takeCount) {
        this.takeCount = takeCount;
    }

    /**
     * @return the readCount
     */
    public int getReadCount() {
        return readCount;
    }

    /**
     * @param readCount the readCount to set
     */
    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }



}
