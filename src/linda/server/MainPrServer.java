/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dkleiber
 */
public class MainPrServer {

    public static void main(String args[]) {
        try {
            ServerLindaImpl cl = new ServerLindaImpl();
                LocateRegistry.createRegistry(6000);
                Naming.rebind("//localhost:6000/Linda", cl);
        }
         catch (MalformedURLException ex) {
                Logger.getLogger(MainPrServer.class.getName()).log(Level.SEVERE, null, ex);
            }
         catch (RemoteException ex) {
            Logger.getLogger(MainPrServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
