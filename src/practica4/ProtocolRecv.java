package practica4;


import ast.protocols.tcp.TCPSegment;
import utils.Channel;
import java.util.ArrayList;

/**
 *
 * @author upcnet
 */
public class ProtocolRecv extends ProtocolBase {

  protected Thread task;
  protected ArrayList<TSocketRecv> sockets;

  public ProtocolRecv(Channel ch) {
    super(ch);
    sockets = new ArrayList<TSocketRecv>();
    task = new Thread(new ReceiverTask());
    task.start();
  }

  public TSocketRecv openForInput(int localPort, int remotePort) {
    lk.lock();
    try {
	  TSocketRecv ts= getMatchingTSocket(localPort, remotePort);
	  if(ts==null){
	    TSocketRecv tsr=new TSocketRecv(this, localPort, remotePort);
	    sockets.add(tsr);
	    return tsr;
      }
      return null;
	  
    } finally {
      lk.unlock();
    }
  }

  protected void ipInput(TCPSegment segment) {
    TSocketRecv tsr= this.getMatchingTSocket(segment.getDestinationPort(), segment.getSourcePort());
    if(tsr!=null){
      tsr.processReceivedSegment(segment);
    }
    else
      log.info("Socket not found");
  }

  protected TSocketRecv getMatchingTSocket(int localPort, int remotePort) {
    lk.lock();
    try {
      for(TSocketRecv ts:sockets){
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
        TCPSegment rseg = channel.receive();
        ipInput(rseg);
      }
    }
  }

}
