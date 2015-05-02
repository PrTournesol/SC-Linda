/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import linda.Callback;
import linda.Tuple;
import linda.shm.BookList;
import linda.shm.MyCallback;

/**
 *
 * @author DaD
 */
public class ServerLindaImplMult extends UnicastRemoteObject implements ServerLinda {

    //Liste des tuples du server
    private ConcurrentLinkedQueue<Tuple> list;
    //Liste des id des serveurs voisins
    private ArrayList<Integer> idVoisins;
    //Liste des paire (callback-tuple) du server
    private ArrayList<DemandeCB> booklist;
    //Hash map ou l'on insere les reponse aux requetes des clients
    private ConcurrentHashMap<Integer, Object> solutionClient;
    int id;
    //Adresse commune a tout les server
    String adress;
    private Lock moniteur;
    private Condition reveil;
    //Compteur pour differencier les differentes requete des clients
    private int id_demande = 0;

    public ServerLindaImplMult(int[] voisins, int idd, String adress) throws RemoteException {
        this.id = idd;
        this.adress = adress;
        idVoisins = new ArrayList<Integer>();
        booklist = new ArrayList<DemandeCB>();
        solutionClient = new ConcurrentHashMap<Integer, Object>();
        for (int i : voisins) {
            idVoisins.add(i);
        }
        list = new ConcurrentLinkedQueue<Tuple>();
        System.out.println("creation du server n " + id);

        this.moniteur = new ReentrantLock();
        this.reveil = moniteur.newCondition();
    }

    public void writeLinda(Tuple t) throws RemoteException {
        boolean add = true;
        moniteur.lock();
        System.out.println("ajout de " + t.toString() + " sur server n " + id);
        //On verifie si le tuple peut interreser l'un des callback enregistré
        for (int i = 0; i < booklist.size(); i++) {
            if (t.matches(booklist.get(i).getTuple())) {
                add = false;
                if (!booklist.get(i).call(t)) {
                    booklist.remove(i);
                }
            }
        }
        //Si aucun callback n'as pris le tuple
        if (add) {
            list.add(t);
            reveil.signalAll();
        }
        moniteur.unlock();
        send(new MessageServer(t.deepclone(), id, -1, NatureM.signal));
    }

    public Tuple readLinda(Tuple template) throws RemoteException {

        int iddemande = id_demande;
        id_demande++;
        moniteur.lock();
        System.out.println("read sur server" + id);
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Tuple element = (Tuple) itr.next();
            if (element.matches(template)) {
                return element.deepclone();
            }
        }
        moniteur.unlock();
        while (!solutionClient.containsKey(iddemande)) {

            send(new MessageServer(template, id, iddemande, NatureM.read));
            try {
                moniteur.lock();
                System.out.println("client en attente, id=" + iddemande);
                if ((!solutionClient.containsKey(iddemande))) {
                    reveil.await();
                }
                System.out.println("client debloqué");
                moniteur.unlock();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerLindaImplMult.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return (Tuple) solutionClient.get(iddemande);

    }

    public Tuple takeLinda(Tuple template) throws RemoteException {
        int iddemande = id_demande;
        id_demande++;
        moniteur.lock();
        System.out.println("take sur server" + id);
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Tuple element = (Tuple) itr.next();
            if (element.matches(template)) {
                Tuple rep = element.deepclone();
                element.remove();
                return rep;
            }
        }
        moniteur.unlock();
        while (!solutionClient.containsKey(iddemande)) {
            send(new MessageServer(template, id, iddemande, NatureM.take));
            try {
                moniteur.lock();
                System.out.println("client en attente, id=" + iddemande);
                if ((!solutionClient.containsKey(iddemande))) {
                    reveil.await();
                }
                System.out.println("client debloqué");
                moniteur.unlock();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerLindaImplMult.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return (Tuple) solutionClient.get(iddemande);
    }

    /**try_Take_Read_Linda(Tuple t, boolean take) appelé par un client lorsqu'il
     * souhaite faire un trytake ou un tryread. On cherche d'abbord une tuple
     * correspondante dans sa propre liste, puis on fait une requete aux servers 
     * adjacents si on n'en a pas trouvé.
     * 
     * @param t le tuple template
     * @param take savoir si on effectue un take ou un read
     * @return
     * @throws RemoteException 
     */
    public Tuple try_Take_Read_Linda(Tuple t, boolean take) throws RemoteException {

        int iddemande = id_demande;
        id_demande++;
        Tuple reponse = null;
        System.out.println("try read sur server" + id);
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Tuple element = (Tuple) itr.next();
            if (element.matches(t)) {
                reponse = element.deepclone();
                if (take) {
                    element.remove();
                }
            }
        }
        if (reponse == null) {
            MessageServer ms;
            if (take) {
                ms = new MessageServer(t.deepclone(), id, iddemande, NatureM.trytake);
            } else {
                ms = new MessageServer(t.deepclone(), id, iddemande, NatureM.tryread);
            }
            send(ms);
        }
        if (solutionClient.containsKey(iddemande)) {
            reponse = (Tuple) solutionClient.get(iddemande);
        }
        return reponse;
    }

    /**Take_read_All_Linda(Tuple t, boolean take) appelé par un client lorsqu'il
     * souhaite faire un takeAll ou un readAll. On cherche d'abbord les tuples
     * correspondant dans sa propre liste, puis on fait une requete aux servers 
     * adjacents.
     * 
     * @param t le tuple template
     * @param take savoir si on effectue un takeAll ou un readAll
     * @return
     * @throws RemoteException 
     */
    public Collection<Tuple> Take_read_All_Linda(Tuple t, boolean take) throws RemoteException {

        ArrayList<Tuple> rep = new ArrayList<Tuple>();
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Tuple element = (Tuple) itr.next();
            if (element.matches(t)) {
                Tuple aux = element.deepclone();
                rep.add(aux);
                if (take) {
                    element.remove();
                }
            }
        }
        int iddemande = id_demande;
        id_demande++;
        //On insere la liste dans la hashtab contenant les données clients.
        solutionClient.put(iddemande, rep);
        if (take) {
            send(new MessageServer(t, id, iddemande, NatureM.takeall));
        } else {
            send(new MessageServer(t, id, iddemande, NatureM.readall));
        }
        return (Collection<Tuple>) solutionClient.get(iddemande);

    }

    /**try_Take_Read_Linda_Serv(MessageServer ms, boolean take) est un take ou 
     * un read non bloquant dans le cadre d'un echange entre server. Soit on 
     * envoit la reponse au serveur ayant fait la requete, soit on transmet
     * la demande aux servers adjacents.
     * 
     * @param ms le message server
     * @param take pour savoir si on doit retirer le tuple ou pas
     * @throws RemoteException 
     */
    public void try_Take_Read_Linda_Serv(MessageServer ms, boolean take) throws RemoteException {
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Tuple element = (Tuple) itr.next();
            if (element.matches((Tuple) ms.getT())) {

                ms.setNature(NatureM.reponse);
                Tuple rep = element.deepclone();
                ms.setT(rep);
                if (take) {
                    element.remove();
                }
                System.out.println("solution trouvé par server " + id + " : " + rep.toString());
                send(ms);
                return;

            }
        }
        send(ms);  //Ce server n'as pas la reponse
    }

    /**Take_Read_All_Linda_Serv() effectue un take ou un read all dans le cadre
     * d'une requete d'un autre server. Ainsi le reponse est envoyé au serveur
     * ayant cree la requete (message type reponse liste), et la requete est transmise
     * aux serveur ne l'ayant pas traité ( send(ms) ) .
     * 
     * @param ms le message server
     * @param take pour savoir si on doit retirer le tuple ou pas
     * @throws RemoteException 
     */
    public void Take_Read_All_Linda_Serv(MessageServer ms, boolean take) throws RemoteException {
        Tuple t = (Tuple) ms.getT();
        ArrayList<Tuple> rep = new ArrayList<Tuple>();
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Tuple element = (Tuple) itr.next();
            if (element.matches(t)) {
                Tuple aux = element.deepclone();
                rep.add(aux);
                if (take) {
                    element.remove();
                }
            }
        }
        send(ms);

        send(new MessageServer(rep, ms.getList().get(0), ms.getIdDemande(), NatureM.reponse_List));

    }
    /**enregistre un callback, en verifiant par avance si un ou plusieurs tuples
     * l'interesse
     * 
     * @param t le tuple
     * @param rc le remote callback du client
     * @throws RemoteException 
     */
    public void abonnementLinda(Tuple t, RemoteCallBack rc) throws RemoteException {
        Callback acb;
        boolean add = true;
        if (rc.getCallback() instanceof MyCallback) {
            acb = new AsynchronousCallback_MyCB(rc);
        } else {
            acb = new AsynchronousCallbackF(rc);
        }
        Tuple rep = try_Take_Read_Linda(t, true);
        while ((rep != null) && add) {
            add = acb.call(rep);
            rep = try_Take_Read_Linda(t, true);
        }
        if (add) {
            booklist.add(new DemandeCB(acb, t));
        }

    }

    
    /**Procedure qui traite l'echange de message entre les servers.
     * Si le message est de type reponse celui ci est transmi au server ayant
     * creer le message.
     * Sinon le message est transmit aux servers adjacents n'ayant pas encore 
     * traité le message
     * 
     * @param message
     * @throws RemoteException 
     */
    public void send(MessageServer message) throws RemoteException {

        ServerLinda sl;
        try {
            //Le message est de type reponse
            if (message.getNature().equals(NatureM.reponse) || message.getNature().equals(NatureM.reponse_List)) {
                sl = (ServerLinda) Naming.lookup(adress + message.getList().get(0));
                //message.getList().get(0) contient l'id du server ayant créer le message
                sl.receive(message);
            } else {

                List<Integer> nextserver = new ArrayList<Integer>();
                //On retient les servers a qui l'on doit envoyer le message
                //(qui n'ont pas encore traité le message).
                //On ajoute leurs id au la liste pour marquer que le message leurs est transmit
                for (int i : idVoisins) {
                    if (!message.getList().contains(i)) {
                        nextserver.add(i);
                        message.getList().add(i);
                    }
                }

                for (int i : nextserver) {
                    //Envoi du message aux servers
                    System.out.print("server" + id + " send request to server(s) " + i);
                    sl = (ServerLinda) Naming.lookup(adress + i);
                    sl.receive(message);
                }
            }


        } catch (NotBoundException ex) {
            Logger.getLogger(ServerLindaImplMult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerLindaImplMult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerLindaImplMult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**receive traite un message. Selon sa nature le server va effectuer l'operation
     * correspondante.
     * 
     * @param message
     * @throws RemoteException 
     */
    public void receive(MessageServer message) throws RemoteException {

        moniteur.lock();
        try {
            System.out.println();
            System.out.println("server " + id + " receive requete type " + message.getNature().name());

            /*Si le message est de type reponse on insere le tuple correspondant
             * a la cle de la demande et on reveille les clients en attente.
             * 
             */
            if (message.getNature().equals(NatureM.reponse)) {
                if (message.getList().get(0) == id) {
                    int iddemande = message.getIdDemande();
                    Object t = message.getT();
                    {
                        solutionClient.put(iddemande, t);
                        System.out.println("solution a la clé : " + message.getIdDemande());
                        this.reveil.signalAll();
                    }

                }
            }
            /*Si le message est de type reponse liste, on concatenne la liste
             * reponse a la liste que l'on a deja
             * 
             */
            if (message.getNature().equals(NatureM.reponse_List)) {
                if (message.getList().get(0) == id) {
                    int iddemande = message.getIdDemande();
                    Object list_aux = message.getT();
                    ((ArrayList) solutionClient.get(iddemande)).addAll((ArrayList) list_aux);
                }
            }

            /*Si le message est de type read, take , tryread ou trytake, le
             * server va faire appel a try_take_read_linda_serv(), afin
             * de ne pas etre bloquant.
             * 
             */
            if (message.getNature().equals(NatureM.read)) {
                this.try_Take_Read_Linda_Serv(message, false);
            }
            if (message.getNature().equals(NatureM.take)) {
                this.try_Take_Read_Linda_Serv(message, true);
            }
            if (message.getNature().equals(NatureM.tryread)) {
                this.try_Take_Read_Linda_Serv(message, false);
            }
            if (message.getNature().equals(NatureM.trytake)) {
                this.try_Take_Read_Linda_Serv(message, true);
            }
            if (message.getNature().equals(NatureM.readall)) {
                Take_Read_All_Linda_Serv(message, false);
            }
            if (message.getNature().equals(NatureM.takeall)) {
                Take_Read_All_Linda_Serv(message, true);
            }
            
            /*Signale aux different serveur qu'un nouveau tuple a été 
             * inséré dans l'un des servers, tout les clients sonts alors réveillé
             * pour voir si le tuple les interesse.
             * 
             */
            if (message.getNature().equals(NatureM.signal)) {
                this.reveil.signalAll();
                send(message);
                /*On verifie ici si le tuple peut etre interessant pour les
                 * differents callback enregistré. Le callback effectue alors
                 * un take, le tuple est encore present apres.
                 * 
                 */
                for (int i = 0; i < booklist.size(); i++) {
                    if (((Tuple) message.getT()).matches(booklist.get(i).getTuple())) {
                        if (!booklist.get(i).call((Tuple) message.getT())) {
                            booklist.remove(i);
                        }
                    }
                }
            }


        } finally {
            moniteur.unlock();
        }
    }
}
