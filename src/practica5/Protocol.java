
package practica5;

// define imports

import ast.protocols.tcp.TCPSegment;

import ast.logging.Log;
import ast.logging.LogFactory;
import utils.FDuplexChannel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;


/**
 *
 * @author upcnet
 */
public class Protocol {
    public static Log log = LogFactory.getLog(Protocol.class);

    protected ArrayList<TSocket> sockets;
    protected Thread task;
    protected Lock lk;
    protected FDuplexChannel.Peer net;

    public Protocol(FDuplexChannel.Peer ch) {
        sockets = new ArrayList<TSocket>();
        task = new Thread(new ReceiverTask());
        task.start();
        lk = new ReentrantLock();
        net = ch;
    }

    public TSocket openWith(int localPort, int remotePort) {
        lk.lock();
        try {
            TSocket ts= getMatchingTSocket(localPort, remotePort);
            if(ts==null){
                ts=new TSocket(this, localPort, remotePort);
                sockets.add(ts);
                return ts;
            }
            return null;
        } finally {
            lk.unlock();
        }
    }

    protected void ipInput(TCPSegment segment) {
        TSocket ts= this.getMatchingTSocket(segment.getDestinationPort(), segment.getSourcePort());
        if(ts!=null){
            ts.processReceivedSegment(segment);
        }
        else
            log.info("Socket not found");
    }

    protected TSocket getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for(TSocket ts:sockets){
                if(ts.localPort==localPort && ts.remotePort==remotePort){
                    return ts;
                }
            }
            return null;
        } finally {
            lk.unlock();
        }
    }

    class ReceiverTask implements Runnable {
        public void run() {
            while (true) {
                TCPSegment rseg = net.receive();
                ipInput(rseg);
            }
        }
    }


}
