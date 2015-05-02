/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linda;

import java.io.Serializable;
import linda.Tuple;

/**
 *
 * @author DaD
 */
public interface Client extends Serializable{
 
    
   public void receiveAbonement(Tuple t) ;
}
