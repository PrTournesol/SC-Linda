package linda.shm;

import java.util.ArrayList;
import java.util.List;
import linda.Callback;
import linda.Tuple;

public class BookList {
    	private ArrayList<Callback> bookList; //set containing all the callBacks registered
    	private ArrayList<Tuple> list_tuples; //set containing all the tuple coresponding

    	public BookList () {
    		this.bookList=new ArrayList<Callback>();
                this.list_tuples = new ArrayList<Tuple>();
    	}
    	
    	public void register(Tuple template, List<Tuple> list, Callback callback) {
                System.out.print("register");
               
	    	Boolean registerCallback=true;
	    	Boolean templatePresent=true;
	    	while(registerCallback&&templatePresent) {  //ptetre plutot en recursif
                   templatePresent=false;  //Sinon on en sort pas au premier tour...
			    for (int index=list.size()-1;index>=0;index--) {

			        if (list.get(index).matches(template)) {
			        	//templatePresent=(((MyCallback)callback).getClient().getLinda().tryRead(template)!=null); //si try take y arrive le tuple ny est plus
                                        templatePresent=true;
			        	//if (templatePresent) {
			        		//registerCallback=callback.call(t); //pas template
			        		registerCallback=callback.call(list.remove(index)); //pas template
                                                //list.remove(t);
			        	//}
			        }
			    }
	    	}
                System.out.print("...done");
	    	if (registerCallback){
                System.out.print("...& add CB");
	    		bookList.add(callback);
	    		list_tuples.add(template);
	    	}
                System.out.println();
    	}	
	    
    	public boolean notify(Tuple t) {
                System.out.print("notify");
    		Boolean result=false;
 
    		for(int i=0;i<list_tuples.size();i++) {
    			if (t.matches(list_tuples.get(i))) { //On est dans ce sens, le new tuple match le template du callback
                                System.out.print("...true");
	    			result=true;
	    			if (!bookList.get(i).call(t)){
                                    bookList.remove(i);
                                    list_tuples.remove(i);
                                }
	    			break;
    			}

    		}
                System.out.println("...fini");    
    		return result;
    	}
}