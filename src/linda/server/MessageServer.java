/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda.server;

import java.io.Serializable;
import java.util.ArrayList;
import linda.Tuple;

/**Cette classe represente les messages entre server. elle est doté d'un Objet
 * reponse, qui peut etre un tuple ou une liste;
 * et d'une liste comportant en premiere position l'id du server qui a envoye le
 * message puis les id des serveurs ayants traité la demande et enfin la nature
 * du message 
 * 
 *
 * @author DaD
 */
public class MessageServer implements Serializable{
    private Object reponse;
    private ArrayList<Integer> list;
    private NatureM nature;
    private int iddemande;

    
    public MessageServer(Object t, int id,int iddemande, NatureM nat){
        this.reponse=t;
        list= new ArrayList<Integer>();
        list.add(id);
        this.nature=nat;
        this.iddemande=iddemande;
    }
    
    /**
     * @return the reponse
     */
    public Object getT() {
        return reponse;
    }

    /**
     * @param reponse the reponse to set
     */
    public void setT(Object t) {
        this.reponse = t;
    }

    /**
     * @return the list
     */
    public ArrayList<Integer> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<Integer> list) {
        this.list = list;
    }

    /**
     * @return the nature
     */
    public NatureM getNature() {
        return nature;
    }

    /**
     * @param nature the nature to set
     */
    public void setNature(NatureM nature) {
        this.nature = nature;
    }

    public int getIdDemande() {
        return this.iddemande;
    }

    
}
