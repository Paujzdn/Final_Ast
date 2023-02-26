package practica4;

import ast.protocols.tcp.TCPSegment;

/**
 * @author AST's teachers
 */
public class TSocketSend extends TSocketBase {

  protected int sndMSS;       // Send maximum segment size

  /**
   * Create an endpoint bound to the local IP address and the given TCP port. The local IP address is determined by the
   * networking system.
   *
   * @param //ch sirve para comentar
   */
  protected TSocketSend(ProtocolSend p, int localPort, int remotePort) {
    super(p, localPort, remotePort);
    sndMSS = p.channel.getMSS() - TCPSegment.HEADER_SIZE; // IP maximum message size - TCP header size
  }

  public void sendData(byte[] data, int offset, int length) {
    TCPSegment seg;
    while(length>sndMSS){
      seg=this.segmentize(data, offset, sndMSS);
      this.sendSegment(seg);
      offset=offset+sndMSS;
      length=length-sndMSS;
    }
    seg=this.segmentize(data, offset, length);
    this.sendSegment(seg);
    //byte realData[]=new byte[length];
    //System.arraycopy(data, offset, realData, 0, length);

    //int nsegm = length/sndMSS;

    //for(int i=0; i<nsegm; i++){
      //this.sendSegment(this.segmentize(realData, i*sndMSS, sndMSS));
    //}
    //int l=length-sndMSS*nsegm;
    //f(l!=0) {
      //this.sendSegment(this.segmentize(realData, nsegm * sndMSS, l));
    //}
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    TCPSegment seg = new TCPSegment();
    byte[] newData=new byte[length];
    System.arraycopy(data, offset, newData, 0, length);
    seg.setData(newData, 0, length);
    seg.setPorts(super.localPort, super.remotePort);
    return seg;
  }

  protected void sendSegment(TCPSegment segment) {
    proto.channel.send(segment);
  }
}
