
package practica5;

import ast.logging.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ast.protocols.tcp.TCPSegment;
import practica1.CircularQ.CircularQueue;

/**
 * @author AST's professors
 */
public class TSocket {
    public static Log log = Protocol.log;
    protected static final int RCV_QUEUE_SIZE = 3;

    protected Protocol proto;
    protected Lock lk;
    protected Condition appCV;

    protected int localPort;
    protected int remotePort;
    
    // Sender variables:
    protected int sndMSS;       // Send maximum segment size
    protected boolean segmentAcknowledged; // segment not yet acknowledged ?
    protected int rcvWindow;

    // Receiver variables:
    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcvSegConsumedBytes;

    //Other atributes (sender or receiver)

    protected Condition ackRebut;
    /**
     * Create an endpoint bound to the given TCP ports.
     */
    protected TSocket(Protocol p, int localPort, int remotePort) {
        lk = new ReentrantLock();
        appCV = lk.newCondition();
        proto = p;
        this.localPort = localPort;
        this.remotePort = remotePort;
        // init sender variables
        sndMSS = p.net.getMMS() - TCPSegment.HEADER_SIZE; // IP maximum message size - TCP header size
        segmentAcknowledged = true;
        // init receiver variables
        rcvQueue = new CircularQueue<TCPSegment>(RCV_QUEUE_SIZE);
        rcvSegConsumedBytes = 0;
        rcvWindow = RCV_QUEUE_SIZE;
        //Other necessary initializations
        //...
        ackRebut=lk.newCondition(); //per quan es reb ack
        //appCV per quan la cua està buida
    }


    // -------------  SENDER PART  ---------------
    public void sendData(byte[] data, int offset, int length) {
        lk.lock();
        try {

            log.debug("%s->sendData(length=%d)", this, length);
            // A completar per l'estudiant:
            int realLength=Math.min(length, sndMSS);
            TCPSegment seg;
            while(length>0){
                seg=this.segmentize(data, offset, realLength);
                while(!segmentAcknowledged || rcvWindow==0){
                    ackRebut.await();
                }
                this.sendSegment(seg);
                segmentAcknowledged=false;
                length=length-realLength;
                offset=offset+realLength;
                realLength=Math.min(length, sndMSS);
            }
            if(length!=0) {
                while (!segmentAcknowledged || rcvWindow == 0) {
                    ackRebut.await();
                }
                seg = this.segmentize(data, offset, length);
                this.sendSegment(seg);
                //appCV.signal();
                segmentAcknowledged = false;
            }
            /*
            int npaq = (int) length/sndMSS; //paquets que necessito
            int lastLength = length % sndMSS; //mida ultim paquet
            for(int i = 0; i < npaq; i++) {
                while(segmentAcknowledged!=true && rcvWindow==0){
                    this.appCV.awaitUninterruptibly();
                }
                this.sendSegment(segmentize(data, offset, lastLength));
                offset = offset + sndMSS;
                appCV.signal();
                segmentAcknowledged=false;
            }
            if(lastLength != 0) {
                while(segmentAcknowledged!=true && rcvWindow==0){
                    this.appCV.awaitUninterruptibly();
                }
                this.sendSegment(segmentize(data, offset, lastLength));
                appCV.signal();
                segmentAcknowledged=false;
            }
            // for each segment to send
            // wait until the sender is not expecting an acknowledgement
            // create a data segment and send it
        */
        }catch(InterruptedException ex){
            ex.printStackTrace();
        } finally {
            lk.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        byte[] newData=new byte[length];
        System.arraycopy(data, offset, newData, 0, length);
        seg.setData(newData, 0, length);
        seg.setPorts(localPort, remotePort);
        return seg;
    }

    protected void sendSegment(TCPSegment segment) {
        log.debug("%s->sendSegment(%s)", this, segment);
        proto.net.send(segment);
    }


    // -------------  RECEIVER PART  ---------------
    /**
     * Places received data in buf
     */
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lk.lock();
        try {
            log.debug("%s->receiveData(maxlen=%d)", this, maxlen);
            while(rcvQueue.empty()) {
                try {
                    appCV.await();
                } catch (java.lang.InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            int util = consumeSegment(buf, offset, maxlen);
            //appCV.signal();
            return util;
        } finally {
            lk.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcvQueue.peekFirst();
        // get data from seg and copy to receiveData's buffer
        int n = seg.getDataLength() - rcvSegConsumedBytes;
        if (n > length) {
            // receiveData's buffer is small. Consume a fragment of the received segment
            n = length;
        }
        // n == min(length, seg.getDataLength() - rcvSegConsumedBytes)
        System.arraycopy(seg.getData(), seg.getDataOffset() + rcvSegConsumedBytes, buf, offset, n);
        rcvSegConsumedBytes += n;
        if (rcvSegConsumedBytes == seg.getDataLength()) {
            // seg is totally consumed. Remove from rcvQueue
            rcvQueue.get();
            if(rcvQueue.free()==1){
                this.sendAck();
            }
            rcvSegConsumedBytes = 0;
        }
        return n;
    }

    protected void sendAck() {
        lk.lock();
        TCPSegment ack=new TCPSegment();
        ack.setAck(true);
        ack.setPorts(localPort, remotePort);
        ack.setWindow(rcvQueue.free());
        this.sendSegment(ack);
        lk.unlock();
    }


    // -------------  SEGMENT ARRIVAL  -------------
    /**
     * Segment arrival.
     * @param rseg segment of received packet
     */
    protected void processReceivedSegment(TCPSegment rseg) {
        lk.lock();
        try {
            // Check ACK
            if (rseg.isAck()) {
                // A completar per l'estudiant:
                rcvWindow=rseg.getWindow();
                segmentAcknowledged=true;
                ackRebut.signal();
                logDebugState();
            } else if (rseg.getDataLength() > 0) {
                // Process segment data
                if (rcvQueue.full()) {
                    log.warn("%s->processReceivedSegment: no free space: %d lost bytes",
                                this, rseg.getDataLength());
                    return;
                }
                // A completar per l'estudiant:
                rcvQueue.put(rseg);
                this.sendAck();
                appCV.signal();
                logDebugState();
            }
        } finally {
            lk.unlock();
        }
    }


    // -------------  LOG SUPPORT  ---------------
    protected void logDebugState() {
        if (log.debugEnabled()) {
            log.debug("%s=> state: %s", this, stateToString());
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(proto.net.getAddr()).append("/{local=").append(localPort);
        buf.append(",remote=").append(remotePort).append("}");
        return buf.toString(); 
    }

    public String stateToString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{segmentAcknowledged=").append(segmentAcknowledged);
        if (rcvQueue.empty()) {
            buf.append("EmptyQueue");
        } else {
            buf.append(",rcvSegment.dataLength=").append(rcvQueue.peekFirst().getDataLength());
            buf.append(",rcvSegConsumedBytes=").append(rcvSegConsumedBytes);
        }
        return buf.append("}").toString();
    }

}
