/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import linda.Callback;
import linda.Tuple;

/**Implemente Remote callback, fait la partie client du callback dans un modele client
 * server
 * 
 * @author DaD
 */
public class RemoteCallBackImpl extends UnicastRemoteObject implements RemoteCallBack {

    private Callback cb;
    
    
    public RemoteCallBackImpl(Callback _cb) throws RemoteException {
        this.cb=_cb;
    }
    
    public boolean callR(Tuple t) throws RemoteException {
       return cb.call(t);
    }

    public Callback getCallback() throws RemoteException {
        return cb;
    }
    
}
