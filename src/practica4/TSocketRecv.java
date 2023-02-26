
package practica4;

// declareu imports

import ast.protocols.tcp.TCPSegment;
import practica1.CircularQ.CircularQueue;

/**
 * Socket for receiving endpoint.
 *
 * @author upcnet
 */
public class TSocketRecv extends TSocketBase {

    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcvSegUnc;

    /**
     * Create an endpoint bound to the local IP address and the given TCP port.
     * The local IP address is determined by the networking system.
     * //@param ch
     */
    protected TSocketRecv(ProtocolRecv p, int localPort, int remotePort) {
        super(p, localPort, remotePort);
        rcvQueue = new CircularQueue<TCPSegment>(20);
        rcvSegUnc = 0;
    }

    /**
     * Places received data in buf
     */
    public int receiveData(byte[] buf, int offset, int length) {
        lk.lock();
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
            lk.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcvQueue.peekFirst();
        // getCnd data from seg and copy to receiveData's buffer
        int n = seg.getDataLength() - rcvSegUnc;
        if (n > length) {
            // receiveData's buffer is small. Consume a fragment of the received segment
            n = length;
        }
        // n == min(length, seg.getDataLength() - rcvSegConsumedBytes, es a dir, el que et queda per consumir
        System.arraycopy(seg.getData(), seg.getDataOffset() + rcvSegUnc, buf, offset, n);
        rcvSegUnc += n;
        if (rcvSegUnc == seg.getDataLength()) {
            // seg is totally consumed. Remove from rcvQueue
            rcvQueue.get();
            rcvSegUnc = 0;
        }
        return n;
    }

    /**
     * Segment arrival.
     * @param rseg segment of received packet
     */
    protected void processReceivedSegment(TCPSegment rseg) {
        lk.lock();
        try {
            if(!rcvQueue.full()){
                rcvQueue.put(rseg);
                appCV.signal();
            }
            else
                log.info("Segment LOST");
        } finally {
            lk.unlock();
        }
    }
}




