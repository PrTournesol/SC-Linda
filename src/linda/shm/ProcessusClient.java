package linda.shm;

import linda.Client;
import java.util.Collection;
import javax.swing.JFrame;
import linda.Tuple;
import linda.TupleFormatException;

/** implements the client process
 *
 * @author dkleiber et pleleux
 * @version 3.8
 */
public class ProcessusClient extends Thread implements Client{
    private CentralizedLinda linda; //server shared by the clients
    private IHMClient ihm;			//unique interface of the client
    private ClientEtat etat;		//state of the client
    int id;							//identity of the client

	/** Constructs a client process extending Thread and thus can be executed
	 *
	 * @param _linda the server Linda
	 * @param _id identity of the client
	 */
    public ProcessusClient(CentralizedLinda _linda, int _id){
        id=_id;
        System.out.println("new client");
        this.linda=_linda;
        ihm = new IHMClient(this);
        etat=ClientEtat.nothing;
        ihm.init();
		ihm.start();
        JFrame frame = new JFrame("Client "+id);
		frame.setSize(600,400);
        frame.getContentPane().add(ihm);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

	/** action of the process
	 */ 
    @Override
    public void run() {
        while (true) {
            System.err.print("");
            if (etat==ClientEtat.nothing) {
            } else {
				try {
					if (etat==ClientEtat.write) {
						String n = ihm.getTextField();
						linda.write(Tuple.valueOf(n));
						ihm.setText("ajout de "+Tuple.valueOf(n)+"    "); 
					}
					else if (etat==ClientEtat.read) {
						String n = ihm.getTextField();
						Tuple t = linda.read(Tuple.valueOf(n));
						ihm.setText("vous avez lu "+t.toString()+"    ");        
					}
					else if (etat==ClientEtat.take) {
						String n = ihm.getTextField();
						Tuple t = linda.take(Tuple.valueOf(n));
						ihm.setText("vous avez gagné "+t.toString()+"    ");    
					}
					else if (etat==ClientEtat.tryread) {
						String n = ihm.getTextField();
						Tuple t = linda.tryRead(Tuple.valueOf(n));           
						ihm.setText((t==null)?"pas de tuple corespondente":
								               "vous avez lus "+t.toString());            
					}
					else if (etat==ClientEtat.trytake) {
						String n = ihm.getTextField();
						Tuple t = linda.tryTake(Tuple.valueOf(n));
						ihm.setText((t==null)?"pas de tuple corespondente":
								               "vous avez gagné "+t.toString());            
					}
					else if (etat==ClientEtat.readall) {
						String rep="vous avez lus ";
						String n = ihm.getTextField();
						Collection<Tuple> collect = linda.readAll(Tuple.valueOf(n));
						for (Tuple t : collect) rep = rep.concat(t.toString()+" ");
						ihm.setText(rep);
		
					}
					else if (etat==ClientEtat.takeall) {
						String rep="vous avez gagné ";
						String n = ihm.getTextField();
						Collection<Tuple> collect = linda.takeAll(Tuple.valueOf(n));
						for (Tuple t : collect) rep = rep.concat(t.toString()+" ");
						ihm.setText(rep);       
					}
					else if (etat==ClientEtat.register) {
						String n = ihm.getTextField();
                        ihm.newAbonnement(n);
						linda.eventRegister(Tuple.valueOf(n), new MyCallback(this, Tuple.valueOf(n)));
						ihm.setText("abonnement a "+Tuple.valueOf(n)+"    ");	
					}
				} catch (TupleFormatException e){
					System.out.println("Format du tuple incorrect");
				} finally {	
			    	this.etat=ClientEtat.nothing;
				}		    
			}
        }
    }

	/** changes the state of the client
	 * 
	 * @param _etat new state of the client
	 */
    public void setEtat(ClientEtat _etat){
        this.etat=_etat;
    }
    
    /** returns the server Linda
     * 
     * @return the server Linda of this process
     */
    public CentralizedLinda getLinda() {
    	return this.linda;
    }
    
    /** returns the interface ihm
     * 
     * @return the interface ihm
     */
    public IHMClient getIhm() {
    	return this.ihm;
    }
    
    public void receiveAbonement(Tuple t) {
        ihm.setAbonnementRepText(t.toString());
    }
}
