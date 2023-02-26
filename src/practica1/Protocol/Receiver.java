package practica1.Protocol;

import utils.Channel;

public class Receiver {

    protected TSocketRecv input;
    protected int recvBuf;
    protected byte n = 0;

    public Receiver(Channel channel, int recvBuf) {
        this.input = new TSocketRecv(channel);
        this.recvBuf = recvBuf;

    }

    public Receiver(Channel channel) {
        this(channel, 5);
    }

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
                    n = (byte) (n + 1);
                    System.out.println(buf[j + offset]);
                }
                totalBytes = totalBytes + r;
            }
        } catch (Exception e) {
            System.err.println("Receiver exception: " + e.getMessage());
        } finally {

        }
    }

}
