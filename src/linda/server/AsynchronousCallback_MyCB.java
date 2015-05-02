/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda.server;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import linda.Callback;
import linda.Tuple;

/**Callback asynchrone qui correnspond au callback MyCallback.
 * Ainsi, il va renvoyer faux en meme temps que le Callback du client.
 *
 * @author DaD
 */
public class AsynchronousCallback_MyCB implements Callback {
	
	private RemoteCallBack rcb;
        private int compteur;

	public AsynchronousCallback_MyCB (RemoteCallBack _rcb) {
            this.rcb = _rcb;
            this.compteur=4;
        } 
	
	/** Asynchronous call: the associated callback is concurrently run.
	 * @return false after have return 4 tuples
	 * */
	public boolean call(final Tuple t) {
		new Thread() {
			public void run() {
                        try {
                             rcb.callR(t);
                        } catch (RemoteException ex) {
                            Logger.getLogger(AsynchronousCallbackF.class.getName()).log(Level.SEVERE, null, ex);
                        }
             
			}
		}.start();
		
                compteur--;
                if (compteur==0){
                    return false;
                } else {
                    return true;
                }
	}
}