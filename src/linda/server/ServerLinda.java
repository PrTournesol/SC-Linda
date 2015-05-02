/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import linda.Callback;
import linda.Tuple;
import linda.shm.CentralizedLinda;

/**
 *
 * @author dkleiber
 */
public interface ServerLinda extends Remote {
        public void writeLinda(Tuple t)throws RemoteException ;
        public Tuple readLinda(Tuple t)throws RemoteException ;
        public Tuple takeLinda(Tuple t)throws RemoteException ;
        public Tuple try_Take_Read_Linda(Tuple t,boolean take)throws RemoteException ;
        public Collection<Tuple> Take_read_All_Linda(Tuple t,boolean take)throws RemoteException ;
        public void abonnementLinda(Tuple t, RemoteCallBack rc)throws RemoteException ;       
        public void send(MessageServer message) throws RemoteException;
        public void receive(MessageServer message) throws RemoteException;
        public void try_Take_Read_Linda_Serv(MessageServer ms, boolean take)throws RemoteException ;
        public void Take_Read_All_Linda_Serv(MessageServer ms, boolean take)throws RemoteException ;        

}
