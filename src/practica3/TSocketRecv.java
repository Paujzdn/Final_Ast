package practica3;

import ast.protocols.tcp.TCPSegment;
import practica1.CircularQ.CircularQueue;
import utils.Channel;

public class TSocketRecv extends TSocketBase {

    protected Thread thread;
    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcvSegConsumedBytes;

    public TSocketRecv(Channel channel) {
        super(channel);
        rcvQueue = new CircularQueue<TCPSegment>(20);
        rcvSegConsumedBytes = 0;

    }

    public TSocketRecv(Channel channel, boolean task) {
        this(channel);
        thread = new Thread(new ReceiverTask());
        thread.start();
    }

    /**
     * Places received data in buf Veure descripció detallada en Exercici 3!!
     */
   public int receiveData(byte[] buf, int offset, int length) {
        lock.lock();
        try {
            while(rcvQueue.empty()) {
                try {
                    appCV.await();
                } catch (java.lang.InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            int util = consumeSegment(buf, offset, length);
            return util;
        }
        finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcvQueue.peekFirst();
        // getCnd data from seg and copy to receiveData's buffer
        int n = seg.getDataLength() - rcvSegConsumedBytes;
        if (n > length) {
            // receiveData's buffer is small. Consume a fragment of the received segment
            n = length;
        }
        // n == min(length, seg.getDataLength() - rcvSegConsumedBytes, es a dir, el que et queda per consumir
        System.arraycopy(seg.getData(), seg.getDataOffset() + rcvSegConsumedBytes, buf, offset, n);
        rcvSegConsumedBytes += n;
        if (rcvSegConsumedBytes == seg.getDataLength()) {
            // seg is totally consumed. Remove from rcvQueue
            rcvQueue.get();
            rcvSegConsumedBytes = 0;
        }
        return n;
    }

    /**
     * TCPSegment arrival.
     *
     * @param rseg segment of received packet
     */
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            if(!rcvQueue.full()){
                rcvQueue.put(rseg);
                appCV.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    class ReceiverTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                TCPSegment rseg = channel.receive();
                processReceivedSegment(rseg);
            }
        }
    }
}
