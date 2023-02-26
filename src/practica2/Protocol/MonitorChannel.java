package practica2.Protocol;

import ast.protocols.tcp.TCPSegment;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import practica1.CircularQ.CircularQueue;
import utils.Channel;

public class MonitorChannel implements Channel {

    protected ReentrantLock lock;
    private CircularQueue cq;
    protected Condition notEmpty, notFull;

    public MonitorChannel(int N) {
        cq=new CircularQueue(N);
        lock=new ReentrantLock();
        notEmpty=lock.newCondition();
        notFull=lock.newCondition();
    }

    @Override
    public void send(TCPSegment seg){
        lock.lock();
        while (cq.full()) {
            try {
                notFull.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cq.put(seg);
        notEmpty.signal();
        lock.unlock();
    }

    @Override
    public TCPSegment receive(){
        lock.lock();
        try {
            while (cq.empty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Object seg = cq.get();
            notFull.signal();
            return (TCPSegment) seg;
        }
        finally{
            lock.unlock();
        }
    }

    @Override
    public int getMSS() {
        return 0;
    }

}
