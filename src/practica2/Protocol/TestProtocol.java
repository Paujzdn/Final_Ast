package practica2.Protocol;

import utils.Channel;

public class TestProtocol {

    public static void main(String[] args) throws InterruptedException {
        //Channel channel = new QueueChannel(4);
        Channel channel = new MonitorChannel(4);

        Sender sender = new Sender(channel, 5, 2);
        Receiver receiver = new Receiver(channel, 10);

        sender.start();
        receiver.start();

        sender.join();
        receiver.join();
        System.out.println("\nSimulation end.");
    }
}
