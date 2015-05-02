/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import linda.Callback;
import linda.Tuple;

/**
 *
 * @author DaD
 */
public interface RemoteCallBack extends Remote {

    
    
    
    public boolean callR(Tuple t)  throws RemoteException ;
    public Callback getCallback()  throws RemoteException ;
    
}
