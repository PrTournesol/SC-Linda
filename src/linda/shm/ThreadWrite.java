package linda.shm;

import linda.Tuple;

public class ThreadWrite extends Thread {
	
	private Tuple t;
    private CentralizedLinda linda;
	
	public ThreadWrite (Tuple _t, CentralizedLinda _linda) {
		this.t = _t;
        this.linda = _linda;
	}
	
    @Override
    public void run() {
		linda.getMoniteur().lock();
        try {
            if (!linda.getBookList().notify(t)) {
                linda.setList(t);
                System.out.println(t.toString()+ " added");
                if (linda.getReadCount()>0) {;
                    linda.getNewTupleRead().signalAll();
                } else if (linda.getTakeCount()>0) {;
                    linda.getNewTupleTake().signalAll();
                }
            }
        } finally {
            linda.getMoniteur().unlock();
        }
	}

}
