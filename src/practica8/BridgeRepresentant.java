package practica8;

import java.io.*;
import java.net.Socket;

public class BridgeRepresentant implements IBridge{
    Socket s;
    BufferedReader entradaTxt;
    PrintWriter sortidaTxt;

    public BridgeRepresentant(){
        s=new Socket(); //falten direccions
        try {
            entradaTxt=new BufferedReader(new InputStreamReader(s.getInputStream()));
            sortidaTxt=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void enter(boolean direction) {
        String direccio=Comms.ESQUERRA;
        if(direction){
            direccio=Comms.DRETA;
        }
        sortidaTxt.println(Comms.ENTRAR);
        sortidaTxt.println(direccio);
        try {
            entradaTxt.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exit() {
        sortidaTxt.println(Comms.SORTIR);
    }

    @Override
    public void close() {
        sortidaTxt.println(Comms.TANCAR);

    }
}
