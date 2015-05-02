package linda.shm;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import linda.Callback;
import linda.Client;
import linda.Tuple;

public class MyCallback implements Callback{

	private Client client;
	private Tuple tuple;
	private int countdown;
	
	public MyCallback (Client _client, Tuple _tuple) {
                System.out.print("new myCallback");
		this.client=_client;
		this.tuple=_tuple;
		this.countdown=4;
                System.out.println("...done");
	}
	
	@Override
	public boolean call(Tuple t) {
            System.out.println("call"+t.toString());
		Boolean result=true;
		//String oldMessage=client.getIhm().getText();
            //    System.out.println("Par abonnement vous gagnez "+t.toString()+"    ");
                client.receiveAbonement(t);
	    //client.getIhm().setText("Par abonnement vous gagnez "+t.toString()+"    ");
		//client.getIhm().setText(oldMessage);
		this.countdown--;
		if (countdown==0){
			result=false;
		}
		return result;
	}
	
	public Client getClient()  {
		return this.client;
	}
	
	public Tuple getTuple() {
		return this.tuple;
	}

}
