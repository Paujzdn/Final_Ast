package practica3;

import ast.protocols.tcp.TCPSegment;
import java.util.Random;
public class MonitorChannel extends practica2.Protocol.MonitorChannel {

   
    private double lossRatio;

   
    public MonitorChannel(){
        super(5);
        this.lossRatio=0;
    }

    public MonitorChannel(int N, double lossRatio) {
        super(N);
        this.lossRatio = lossRatio;
    }

    @Override
    public void send(TCPSegment seg) {
        int max=100;
        int min=0;
        Random randomNum=new Random();
        int num=min+randomNum.nextInt(max);
        if(num>=lossRatio*100){
            super.send(seg);
        }
    }

    @Override
    public int getMSS() {
        return 1480;
    }

}
