
package practica3;

import ast.logging.Log;
import ast.logging.LogFactory;

import utils.Channel;


public class TestLab3 {

    public static void main(String[] args){
        Channel channel = new MonitorChannel(5,0);
        new Thread(new Sender(channel)).start();
        new Thread(new Receiver(channel)).start();
    }
}

class Sender implements Runnable {
    public static Log log = LogFactory.getLog(Sender.class);

    protected TSocketSend output;
    protected int sendNum, sendSize, sendInterval;


    public Sender(Channel channel, int sendNum, int sendSize, int sendInterval) {
        this.output = new TSocketSend(channel);
        this.sendNum = sendNum;
        this.sendSize = sendSize;
        this.sendInterval = sendInterval;
    }

    public Sender(Channel channel) {
        this(channel, 20, 50, 100);
    }

    public void run() {
        try {
            byte n = 0;
            byte[] buf = new byte[sendSize];
            for (int i = 0; i < sendNum; i++) {
                Thread.sleep(sendInterval*10);
                // stamp data to send
                for (int j = 0; j < sendSize; j++) {
                    buf[j] = n;
                    n = (byte) (n + 1);
                }
                output.sendData(buf, 0, buf.length);
            }
            log.info("Sender: transmission finished");
        } catch (Exception e) {
            log.error("Excepcio a Sender: %s", e);
            e.printStackTrace(System.err);
        }
    }

}

class Receiver implements Runnable {
    public static Log log = LogFactory.getLog(Receiver.class);

    protected TSocketRecv input;
    protected int recvBuf, recvInterval;

    public Receiver(Channel channel, int recvBuf, int recvInterval) {
        this.input = new TSocketRecv(channel, true);
        this.recvBuf = recvBuf;
        this.recvInterval = recvInterval;
    }

    public Receiver(Channel channel) {
        this(channel, 25, 10);
    }

    public void run() {
        try {
            byte n = 0;
            byte[] buf = new byte[recvBuf];
            while (true) {
                int r = input.receiveData(buf, 0, buf.length);
                // check received data stamps
                for (int j = 0; j < r; j++) {
                    if (buf[j] != n) {
                        throw new Exception("Receiver: Received data is corrupted");
                    }
                    n = (byte) (n + 1);
                }
                log.info("Receiver: received %d bytes", r);
                Thread.sleep(recvInterval);
            }
        } catch (Exception e) {
            log.error("Excepcio a Receiver: %s", e);
            e.printStackTrace(System.err);
        }
    }
}


