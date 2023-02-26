package practica1.Protocol;

import ast.protocols.tcp.TCPSegment;
import utils.Channel;

public class TSocketSend {

    private final Channel channel;

    public TSocketSend(Channel channel) {
        this.channel = channel;
    }

    public void sendData(byte[] data, int offset, int length){
        byte[] realData = new byte[length];
        System.arraycopy(data, offset, realData, 0, length);

        TCPSegment segm=new TCPSegment();
        segm.setData(realData, 0, length);

        channel.send(segm);

    }
}
