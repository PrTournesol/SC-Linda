/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda.server;

import linda.Callback;
import linda.Tuple;

/**Regroupe un callback et le tuple qu'il demande
 *
 * @author DaD
 */
public class DemandeCB {
    
    private Callback cb;
    private Tuple tuple;
    
    public DemandeCB(Callback cb, Tuple t){
        this.cb=cb;
        this.tuple= t;
    }

    /**
     * @return the cb
     */
    public Callback getCb() {
        return cb;
    }

    /**
     * @return the tuple
     */
    public Tuple getTuple() {
        return tuple;
    }
    
    public boolean call(Tuple t){
        return cb.call(t);
    }
}
