package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSync {

    private final int N;
    private int ultim;
    ReentrantLock lock;
    private Condition torn;

    public MonitorSync(int N) {
        this.N = N;
        lock=new ReentrantLock();
        this.torn=lock.newCondition();
    }

    public void waitForTurn(int id) {
        lock.lock();
        try {
            if(ultim==id) {
                torn.await();
            }
            ultim=id;
        } catch (InterruptedException e) {
         e.printStackTrace();
        }
    }


    public void transferTurn() {
        torn.signal();
        lock.unlock();
    }
}
