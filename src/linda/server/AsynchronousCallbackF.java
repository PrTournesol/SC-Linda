package linda.server;

import linda.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;



/** The class helps to transform a callback to behave asynchronously.
 * The callback fires exactly once.
 * The callback fires asynchronously with other threads and may do whatever it wants (it may block).
 */
public class AsynchronousCallbackF implements Callback {
	
	private RemoteCallBack rcb;

	public AsynchronousCallbackF (RemoteCallBack _rcb) { this.rcb = _rcb; }
	
	/** Asynchronous call: the associated callback is concurrently run and this one immediately returns false.
	 * @return false always
	 * */
	public boolean call(final Tuple t) {
		new Thread() {
			public void run() {
                        try {
                                boolean callR = rcb.callR(t); // ignore return value
                        } catch (RemoteException ex) {
                            Logger.getLogger(AsynchronousCallbackF.class.getName()).log(Level.SEVERE, null, ex);
                        }
             
			}
		}.start();
		return false;
	}
}
