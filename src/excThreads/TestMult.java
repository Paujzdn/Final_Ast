package excThreads;
import java.lang.Runnable;
import java.lang.Thread;

public class TestMult {
    public static void main(String[] args) throws java.lang.InterruptedException{
        int n=10;
        int m=10;

        Runnable myRunnable = new MatrixMult(n,m);

        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                MatrixMult.mx1[i][j]=i+j;
                MatrixMult.mx2[i][j]=i+j;
            }
        }
        Thread thread1 = new Thread(myRunnable);
        Thread thread2 = new Thread(myRunnable);
        Thread thread3 = new Thread(myRunnable);
        Thread thread4 = new Thread(myRunnable);
        Thread thread5 = new Thread(myRunnable);
        Thread thread6 = new Thread(myRunnable);
        Thread thread7 = new Thread(myRunnable);
        Thread thread8 = new Thread(myRunnable);
        Thread thread9 = new Thread(myRunnable);
        Thread thread10 = new Thread(myRunnable);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
        thread10.start();



        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();
        thread6.join();
        thread7.join();
        thread8.join();
        thread9.join();
        thread10.join();

        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                System.out.print(MatrixMult.mxr[i][j]+" ");
            }
            System.out.println(" ");
        }
        System.out.println("El nombre de files es: " +MatrixMult.fila);
    }
}

