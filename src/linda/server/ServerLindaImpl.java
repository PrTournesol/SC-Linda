/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import linda.AsynchronousCallback;
import linda.Callback;
import linda.Tuple;
import linda.shm.CentralizedLinda;
import linda.shm.MyCallback;

/**
 *
 * @author dkleiber
 */
public class ServerLindaImpl extends UnicastRemoteObject implements ServerLinda {

    private CentralizedLinda cl;

public ServerLindaImpl() throws RemoteException {
    this.cl=new CentralizedLinda();

}


    public void writeLinda(Tuple t) throws RemoteException {
        cl.write(t);
    }

    public Tuple readLinda(Tuple t) throws RemoteException {
        return cl.read(t);
    }

    public Tuple takeLinda(Tuple t) throws RemoteException {
        return cl.take(t);
    }


    public Tuple try_Take_Read_Linda(Tuple t, boolean take) throws RemoteException {
        Tuple rep = null;
        if (take) {
        rep= cl.tryTake(t);            
        } else {
            rep = cl.tryRead(t);
        }
        return rep;
    }

    public Collection<Tuple> Take_read_All_Linda(Tuple t, boolean take) throws RemoteException {
        if (take) {
        return cl.takeAll(t);            
        } else {
        return cl.readAll(t);
        }
    }

    public void abonnementLinda(Tuple t,RemoteCallBack callback) throws RemoteException {
        Callback acb;
        if (callback.getCallback() instanceof MyCallback ) {
            acb = new AsynchronousCallback_MyCB(callback);
        } else {
            acb = new AsynchronousCallbackF(callback);
        }
        cl.eventRegister(t, acb);
    }

////////////////////////////////////////////////////////////////////////////////////////////
    /*    Les fonctions qui suivent ne sonts pas utilisés dans le cadre d'un mono server
     */
////////////////////////////////////////////////////////////////////////////////////////////

    public void receive(MessageServer message)throws RemoteException {
    }

    public void send(MessageServer message)throws RemoteException {
    }

    public void Take_Read_All_Linda_Serv(MessageServer ms, boolean take) throws RemoteException {
        
    }

    public void try_Take_Read_Linda_Serv(MessageServer ms, boolean take) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
