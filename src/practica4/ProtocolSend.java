package practica4;

import utils.Channel;
import java.util.ArrayList;


/**
 *
 * @author upcnet
 */
public class ProtocolSend extends ProtocolBase {

  protected ArrayList<TSocketSend> sockets;

  public ProtocolSend(Channel ch) {
    super(ch);
    sockets = new ArrayList<TSocketSend>();
  }

  public TSocketSend openForOutput(int localPort, int remotePort) {
    lk.lock();
    try {
      TSocketSend tss=this.getMatchingTSocket(localPort, remotePort);
      if(tss==null){
        TSocketSend tssNou=new TSocketSend(this, localPort, remotePort);
        sockets.add(tssNou);
        return tssNou;
      }
      return null;
    } finally {
      lk.unlock();
    }
  }

  public TSocketSend getMatchingTSocket(int local, int remote){
    lk.lock();
    try {
      for (TSocketSend tss : sockets) {
        if (tss.localPort == local && tss.remotePort == remote) {
          return tss;
        }
      }
      return null;
    }
    finally{
      lk.unlock();
    }
  }
}
