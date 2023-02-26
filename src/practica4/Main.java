
package practica4;

import practica3.MonitorChannel;
import utils.Channel;


public class Main {

    public static void main(String[] args){
            Channel c = new MonitorChannel();

            ProtocolRecv proto1 = new ProtocolRecv(c);
            new Thread(new Host1(proto1)).start();

            ProtocolSend proto2 = new ProtocolSend(c);
            new Thread(new Host2(proto2)).start();
    }
}


class Host1 implements Runnable {

    public static final int PORT = 10;

    protected ProtocolRecv proto;

    public Host1(ProtocolRecv proto) {
        this.proto = proto;
    }

    public void run() {
      //arranca dos fils receptors, cadascun amb el seu socket de recepcio
      //fes servir els ports apropiats
      //...
        TSocketRecv tsr1= proto.openForInput(PORT, Host2.PORT1);
        Receiver r1=new Receiver(tsr1);
        Thread th1=new Thread(r1);
        th1.start();

        TSocketRecv tsr2=proto.openForInput(PORT, Host2.PORT2);
        Receiver r2=new Receiver(tsr2);
        Thread th2=new Thread(r2);
        th2.start();

    }
}


class Host2 implements Runnable {

    public static final int PORT1 = 10;
    public static final int PORT2 = 50;

    protected ProtocolSend proto;
    
    public Host2(ProtocolSend proto) {
        this.proto = proto;
    }
    
    public void run() {
      TSocketSend tss1=proto.openForOutput(PORT1, Host1.PORT);
      Sender s1=new Sender(tss1);
      Thread th1=new Thread(s1);
      th1.start();


      TSocketSend tss2=proto.openForOutput(PORT2, Host1.PORT);
      Sender s2=new Sender(tss2);
      Thread th2=new Thread(s2);
      th2.start();
    }
    
}


