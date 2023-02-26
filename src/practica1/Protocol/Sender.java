package practica1.Protocol;

import utils.Channel;

public class Sender {

    public static int numBytes = 0;

    protected TSocketSend output;
    protected int sendNum, sendSize;
    protected byte n = 0;
    protected byte[] buf;

    public Sender(Channel channel, int sendNum, int sendSize) {
        this.output = new TSocketSend(channel);
        this.sendNum = sendNum;
        this.sendSize = sendSize;
        buf = new byte[2 * sendSize];
        numBytes = sendNum * sendSize;
    }

    public Sender(Channel channel) {
        this(channel, 1, 5);
    }

    public void run() {
        try {
            for (int i = 0; i < sendNum; i++) {
                int offset = (int) (Math.random() * sendSize);
                for (int j = 0; j < sendSize; j++) {
                    buf[j + offset] = n;
                    n = (byte) (n + 1);
                }
                output.sendData(buf, offset, sendSize);
            }

        } catch (Exception e) {
            System.err.println("Excepcio a Sender: " + e);
            e.printStackTrace(System.err);
        } finally {

        }
    }

}
