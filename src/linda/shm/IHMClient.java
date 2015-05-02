package linda.shm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/** Client interface for the application
 * 
 * @author dkleiber
 * @version 1.3
 */
public class IHMClient extends JApplet {

	private static final long serialVersionUID = 1;
	TextField textfield;    //entry field for the tuples
	Label message;          //display
	Label Abonnement;          //display
	Label AbonnementReponse;   //display
	ProcessusClient client; //client associated to the interface

	/** Constructs an interface with its client as parameter
	 * @param c the client associated
	 */
	public IHMClient(ProcessusClient c){
		this.client=c;
	}

	/** Initialize the interface
	 */
	@Override
	public void init() {
		setSize(600,1000);
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

	/** Implementation of the ActionListeners :
	 * sets the state of the client with the state according to the action
	 */
		// La reaction au bouton Write
		class WriteButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
		    		client.setEtat(ClientEtat.write);
			}
		}
		// La reaction au bouton Read
		class ReadButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
					message.setText("En attente d'un tuple correspondant...   ");
		    		client.setEtat(ClientEtat.read);
			}
		}
		// La reaction au bouton Take
		class TakeButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
		    		message.setText("En attente d'un tuple correspondant...   ");
		    		client.setEtat(ClientEtat.take);
			}
		}
		// La reaction au bouton TryTake
		class TryTakeButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
		    		client.setEtat(ClientEtat.trytake);
			}
		}
		// La reaction au bouton TryRead
		class TryReadButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				client.setEtat(ClientEtat.tryread);
			}
		}
		// La reaction au bouton TakeAll
		class TakeAllButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
		    		client.setEtat(ClientEtat.takeall);
			}
		}
		// La reaction au bouton ReadAll
		class ReadAllButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
		    		client.setEtat(ClientEtat.readall);
			}
		}
		// La reaction au bouton EventRegister
		class EventRegisterButtonAction implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
		    		client.setEtat(ClientEtat.register);
			}
		}

	/** return the content of the textField
	 *@param
	 *@return a String content of the textField
	 */
	public String getTextField(){
	    return textfield.getText();
	}

	/** get the string s into the Label message
	 */
	public String getText() {
	    return message.getText();
	}

	/** set the string s into the Label message
	 * @param s the String containing the text to display
	 */
	public void setText(String s) {
	    message.setText(s);
	}

        
        public void setAbonnementRepText(String s) {
            AbonnementReponse.setText(AbonnementReponse.getText()+ s +" "); 
        }
        
        public void newAbonnement(String s) {
            Abonnement.setText(Abonnement.getText()+ s+" "); 
           
        }
}


