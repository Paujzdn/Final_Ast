package excThreads;
import java.lang.Runnable;
public class MatrixMult implements Runnable{

    public static int[][] mx1, mx2, mxr;
    private int N, M;
    public static int fila=0;

    public MatrixMult(int n, int m){
        N=n;
        M=m;
        mx1=new int[N][M];
        mx2=new int[N][M];
        mxr=new int[M][M];
    }

    @Override
    public void run(){
        int myFila=fila;
        fila=fila+1;
        for(int i=0; i<M; i++){
            for(int j=0; j<M; j++){
                mxr[myFila][i]=mxr[myFila][i]+(mx1[myFila][j]*mx2[i][j]);
            }
        }
    }

}
