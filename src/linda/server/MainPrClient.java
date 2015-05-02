/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linda.server;

/**Cree un client connecté a un server.
 * Dans le cas d'un mono server, l'adresse est "//localhost:6000/Linda"
 * sinon dans le cas d'un multi serveur "//localhost:6000/Linda"+ id du server
 * ("//localhost:6000/Linda3" pour le server 3)
 *
 * @author dkleiber
 */
public class MainPrClient {


    public static void main(String args[]) {        
          new LindaClient("//localhost:6000/Linda3");
    }
}
