package practica2.Protocol;

import practica1.Protocol.TSocketRecv;
import utils.Channel;

public class Receiver extends Thread {

    protected TSocketRecv input;
    protected int recvBuf;
    protected byte n = 0;

    public Receiver(Channel c, int recvBuf) {
        this.input = new TSocketRecv(c);
        this.recvBuf = recvBuf;

    }

    public Receiver(Channel c) {
        this(c, 5);
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[2 * recvBuf];
            int totalBytes = 0;
            while (totalBytes < Sender.numBytes || Sender.numBytes == 0) {
                int offset = (int) (Math.random() * recvBuf);
                int r = input.receiveData(buf, offset, recvBuf);
                for (int j = 0; j < r; j++) {
                    if (buf[j + offset] != n) {
                        throw new Exception("ReceiverTask: Recieved data is corrupted");
                    }
                    System.out.println(n);
                    n = (byte) (n + 1);
                }
                totalBytes = totalBytes + r;
            }
        } catch (Exception e) {
            System.err.println("Receiver exception: " + e.getMessage());
        } finally {

        }
    }

}
