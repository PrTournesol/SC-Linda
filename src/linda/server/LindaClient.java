package linda.server;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.shm.MyCallback;
import linda.Client;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda, Client {

    String adress;
    IHMClientServ ihm;
    private ServerLinda sl;

    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        sl=null;
        this.adress=serverURI;
        this.ihm= new IHMClientServ(this);
        ihm.init();
		ihm.start();
        JFrame frame = new JFrame("Client ");
		frame.setSize(600,400);
        frame.getContentPane().add(ihm);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            sl = (ServerLinda) Naming.lookup(adress);
        } catch (NotBoundException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void write(Tuple t) {
        try {
            sl.writeLinda(t);
        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Tuple take(Tuple template) {
        try {
                       Tuple t= sl.takeLinda(template);
            System.out.println("dadada"+ t.toString());
            ihm.setText("vous avez gagné :"+t.toString());
            return t;


        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Tuple read(Tuple template) {

        try {
            
            Tuple t= sl.readLinda(template);
            System.out.println("dadada"+ t.toString());
            ihm.setText("vous avez gagné :"+t.toString());
            return t;

        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }

    public Tuple tryTake(Tuple template) {

        try {
            Tuple t = sl.try_Take_Read_Linda(template, true);
            if  (t!=null) {
                ihm.setText("vous avez gagné :"+t.toString());
            } else {
                ihm.setText("pas de tuple correspondant");              
            }          

        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }

    public Tuple tryRead(Tuple template) {

        try {
            Tuple t = sl.try_Take_Read_Linda(template, false);
            if  (t!=null) {
                ihm.setText("vous avez gagné :"+t.toString());
            } else {
                ihm.setText("pas de tuple correspondant");              
            }
            
            return t;

        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }

    public Collection<Tuple> takeAll(Tuple template) {
        Collection<Tuple> colect = null;
        String rep="vous gagnez : ";
        try {
            colect= sl.Take_read_All_Linda(template, true);
            Iterator itr = colect.iterator();
          while (itr.hasNext()) {
                Tuple element = (Tuple) itr.next();
                rep=rep.concat(element.toString());
          }
        ihm.setText(rep);
          
        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            return colect;
    }

    public Collection<Tuple> readAll(Tuple template) {
        Collection<Tuple> colect = null;
        String rep="vous gagnez : ";
        try {
            colect=  sl.Take_read_All_Linda(template, false);
            Iterator itr = colect.iterator();
        while (itr.hasNext()) {
                Tuple element = (Tuple) itr.next();
                rep=rep.concat(element.toString());
          }
        ihm.setText(rep);
        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            return colect;
    }

    public void eventRegister(Tuple template, Callback callback) {
        try {	
            
            RemoteCallBack  rcb = new RemoteCallBackImpl(callback);
                 sl.abonnementLinda(template, rcb);


        } catch (RemoteException ex) {
            Logger.getLogger(LindaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void debug(String prefix) {
    }
    
    // TO BE COMPLETED
    
    
    
    public void receiveAbonement(Tuple t) {
        System.out.println("receive par abbonement "+t.toString());
         ihm.setAbonnementRepText(t.toString()); 
        
    }

}
