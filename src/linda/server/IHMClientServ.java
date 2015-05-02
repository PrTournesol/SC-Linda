package linda.server;

/* ------------------------------------------------------- 
		Les packages Java qui doivent etre importes.
*/
import linda.shm.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import linda.Tuple;
import linda.shm.CentralizedLinda;



/* ------------------------------------------------------- 
		Implementation de l'application
*/

public class IHMClientServ extends JApplet {
	private static final long serialVersionUID = 1;
	TextField textfield;
	Choice carnets;
	Label message;
        Label Abonnement;          //display
	Label AbonnementReponse;   //display
    LindaClient client;

    public IHMClientServ(LindaClient c){
        this.client=c;
    }

    @Override
	public void init() {


		setSize(600,800);
		setLayout(new GridLayout(13,1));
		add(new Label("  Tuple : "));
		textfield = new TextField(30);
		add(textfield);

		Button Writebutton = new Button("Write");
		Writebutton.addActionListener(new WriteButtonAction());
		add(Writebutton);

		Button Readbutton = new Button("Read");
		Readbutton.addActionListener(new ReadButtonAction());
		add(Readbutton);

		Button Takebutton = new Button("Take");
		Takebutton.addActionListener(new TakeButtonAction());
		add(Takebutton);

		Button TryTakebutton = new Button("Try Take");
		TryTakebutton.addActionListener(new TryTakeButtonAction());
		add(TryTakebutton);

		Button TryReadbutton = new Button("Try Read");
		TryReadbutton.addActionListener(new TryReadButtonAction());
		add(TryReadbutton);

		Button TakeAllbutton = new Button("Take All");
		TakeAllbutton.addActionListener(new TakeAllButtonAction());
		add(TakeAllbutton);

		Button ReadAllbutton = new Button("Read All");
		ReadAllbutton.addActionListener(new ReadAllButtonAction());
		add(ReadAllbutton);
		
		Button EventRegisterButton = new Button("EventRegister");
		EventRegisterButton.addActionListener(new EventRegisterButtonAction());
		add(EventRegisterButton);

		message = new Label();
		add(message);
                
		Abonnement = new Label();
		add(Abonnement);
                Abonnement.setText("vous etes abonné a : ");
                
		AbonnementReponse = new Label();
		add(AbonnementReponse);
                AbonnementReponse.setText("tuples commandés : ");
	}


	// La reaction au bouton Write
	class WriteButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            client.write(Tuple.valueOf(textfield.getText()));

		}
	}
	// La reaction au bouton Read
	class ReadButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            message.setText("En attente d'un tuple correspondant...   ");
            client.read(Tuple.valueOf(textfield.getText()));
		}
	}
	// La reaction au bouton Take
	class TakeButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            message.setText("En attente d'un tuple correspondant...   ");
            client.take(Tuple.valueOf(textfield.getText()));
        }
	}
	// La reaction au bouton TryTake
	class TryTakeButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            client.tryTake(Tuple.valueOf(textfield.getText()));		}
	}
	// La reaction au bouton TryRead
	class TryReadButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            client.tryRead(Tuple.valueOf(textfield.getText()));		}
	}
	// La reaction au bouton TakeAll
	class TakeAllButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            client.takeAll(Tuple.valueOf(textfield.getText()));		}
	}
	// La reaction au bouton ReadAll
	class ReadAllButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            client.readAll(Tuple.valueOf(textfield.getText()));		}
	}
	// La reaction au bouton ReadAll
	class EventRegisterButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
            Tuple tuple=Tuple.valueOf(textfield.getText());
            newAbonnement(tuple.toString());
            MyCallback cb;
                cb = new MyCallback(client, tuple);
                client.eventRegister(tuple,cb);	

	}
	}

public String getTextField(){
    return textfield.getText();
}

public void setText(String s) {
    message.setText(s);
}

        
public void setAbonnementRepText(String s) {
    System.out.println("IHM receive");
     AbonnementReponse.setText(AbonnementReponse.getText()+ s +" ");
}
        
public void newAbonnement(String s) {
    Abonnement.setText(Abonnement.getText()+ s+" ");            
}

}


