package practica1.Protocol;

import utils.Channel;

public class TestProtocol {

    public static void main(String[] args) {
        Channel channel = new QueueChannel(2);
        int I = 10;
        Sender s = new Sender(channel);
        Receiver r = new Receiver(channel);

        for (int i = 0; i < I; i++) {
            s.run();
            r.run();
        }
        System.out.println("\nSimulation end.");
    }
}
