package practica8;

public class Cotxe implements Runnable{
    protected boolean direccio;
    protected IBridge bridge;

    public Cotxe(boolean dir){
        bridge=new BridgeRepresentant();
        direccio=dir;
    }
    @Override
    public void run() {

    }
}

