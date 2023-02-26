package practica1.Protocol;

import practica1.CircularQ.CircularQueue;
import ast.protocols.tcp.TCPSegment;
import utils.Channel;

public class QueueChannel implements Channel {

    private CircularQueue cua;
    //private int nElements;


    public QueueChannel(int N) {
        cua=new CircularQueue(N);
    }

    @Override
    public void send(TCPSegment s) {
        cua.put(s);
    }

    @Override
    public TCPSegment receive() {
        return (TCPSegment) cua.get();
    }

    @Override
    public int getMSS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
