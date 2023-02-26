package practica1.Protocol;

import ast.protocols.tcp.TCPSegment;
import utils.Channel;

public class TSocketRecv {

    private final Channel channel;

    public TSocketRecv(Channel channel) {
        this.channel = channel;
    }

    public int receiveData(byte[] data, int offset, int length){
        TCPSegment segm= null;

        segm = channel.receive();

        int dataLength=segm.getDataLength();
        int dataOffset=segm.getDataOffset();
        byte[] recievedData=segm.getData();

        int realLength=length;
        if(realLength>dataLength)
            realLength=dataLength;

        for(int i=0; i<realLength; i++){
            data[offset+i]=recievedData[dataOffset+i];
        }

        return realLength;
    }
}
