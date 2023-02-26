package practica3;

import ast.protocols.tcp.TCPSegment;
import utils.Channel;

public class TSocketSend extends TSocketBase {

    protected int sndMSS;       // Send maximum segment size

    public TSocketSend(Channel channel) {
        super(channel);
        sndMSS = channel.getMSS();
    }

    public void sendData(byte[] data, int offset, int length) {
        byte realData[]=new byte[length];
        System.arraycopy(data, offset, realData, 0, length);

        int nsegm = length/sndMSS;

        for(int i=0; i<nsegm; i++){
            this.sendSegment(this.segmentize(realData, i*sndMSS, sndMSS));
        }
        int l=length-sndMSS*nsegm;
        if(l!=0) {
            this.sendSegment(this.segmentize(realData, nsegm * sndMSS, l));
        }
        System.out.println(l);

    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setData(data, offset, length);
        return seg;
    }

    protected void sendSegment(TCPSegment segment) {
        channel.send(segment);
    }
}
