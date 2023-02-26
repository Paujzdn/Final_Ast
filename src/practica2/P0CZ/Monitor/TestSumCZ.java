package practica2.P0CZ.Monitor;

public class TestSumCZ {

    public static void main(String[] args) throws InterruptedException {
        MonitorCZ mon = new MonitorCZ();
        CounterThreadCZ thread1=new CounterThreadCZ(mon);
        CounterThreadCZ thread2=new CounterThreadCZ(mon);
        CounterThreadCZ thread3=new CounterThreadCZ(mon);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println("El número d'increments ha sigut de: "+ mon.getX());
    }
}
