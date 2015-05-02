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

/**Cette classe cree quatre servers conecté comme suit :
 *   
 *   1
 *  /|
 * 0 |
 *  \|
 *   2-4
 * 
 * @author DaD
 */
public class MainPrMultiServer {
    
    
      public static void main(String args[]) {
        try {
            int id=0;
            int[] d= new int[2];
            d[0]=1;d[1]=2;
                        //creation du premier server
            ServerLindaImplMult cl = new ServerLindaImplMult(d,id,"//localhost:6000/Linda");
                LocateRegistry.createRegistry(6000);
                Naming.rebind("//localhost:6000/Linda"+id, cl);
                
            id=2;
            d[0]=0;d[1]=3;
                        //creation du troisieme server
                Naming.rebind("//localhost:6000/Linda"+id, new ServerLindaImplMult(d,id,"//localhost:6000/Linda"));              
            d= new int[1];           
            id=1;
            d[0]=0;
                        //creation du deuxieme server
                Naming.rebind("//localhost:6000/Linda"+id,new ServerLindaImplMult(d,id,"//localhost:6000/Linda"));
//
            id=3;
            d[0]=2;
                        //creation du quatrieme server
                Naming.rebind("//localhost:6000/Linda"+id, new ServerLindaImplMult(d,id,"//localhost:6000/Linda"));
        }
         catch (MalformedURLException ex) {
                Logger.getLogger(MainPrServer.class.getName()).log(Level.SEVERE, null, ex);
            }
         catch (RemoteException ex) {
            Logger.getLogger(MainPrServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
}
