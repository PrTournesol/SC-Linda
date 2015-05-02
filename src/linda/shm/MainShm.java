package linda.shm;

/** Main class of the memory shared implementation
 *
 * @author dkleiber
 * @version 1.2
 */
public class MainShm {

	/** Creation of 2 clients and a Linda server
	 * 
	 * @param args none
	 */
	public static void main(String args[]) {
        CentralizedLinda linda= new CentralizedLinda();
	Thread a = new Thread(new ProcessusClient(linda,1));
        Thread b = new Thread(new ProcessusClient(linda,2));
        a.start();
        System.out.println("client 1 lancé");
	b.start();
        System.out.println("client 2 lancé");
	}


}
