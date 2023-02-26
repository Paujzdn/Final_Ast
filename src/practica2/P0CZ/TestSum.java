package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {
        CounterThread ct1, ct2;
        ct1= new CounterThread();
        ct2= new CounterThread();
        ct1.start();
        ct2.start();
        ct1.join();
        ct2.join();

        System.out.println("El numero total d'elements es: " + CounterThread.x);
    }
}

