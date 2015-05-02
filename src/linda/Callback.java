package linda;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;



/** Callback when a tuple appears. */
public interface Callback extends Serializable{

	/** Callback when a tuple appears. 
	 * The callback is kept if it returns true, and is deregistered if it returns false.
	 * See Linda.eventRegister for additional constraints.
	 * 
	 * @param t the new tuple
	 * @return true if the callback is kept, false if it is deregistered.
	 */
	boolean call(Tuple t) ;
}
