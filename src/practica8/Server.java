package practica8;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    protected ServerSocket ss;
    protected IBridge bridge;

    public Server(BridgeReal br){
        bridge=br;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket s = ss.accept();
                new Thread(new Treballador(s)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Treballador implements Runnable{
        private Socket s;
        private BufferedReader entradaTxt;
        private PrintWriter sortidaTxt;
        public Treballador(Socket _s) throws IOException {
            s=_s;
            entradaTxt=new BufferedReader(new InputStreamReader(s.getInputStream()));
            sortidaTxt=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

        }
        @Override
        public void run() {
            try {

                String accio=entradaTxt.readLine();
                String direccioS=entradaTxt.readLine();
                boolean direccio=false;
                if(direccioS.equals(Comms.DRETA)) {
                    direccio = true;
                }

                if(accio.equals(Comms.ENTRAR)){
                    bridge.enter(direccio);
                    sortidaTxt.println(Comms.OK);
                }else if(accio.equals(Comms.SORTIR)){
                    bridge.exit();
                }else if(accio.equals(Comms.TANCAR)){
                    bridge.close();
                }

                //bridge.close();
                entradaTxt.close();
                sortidaTxt.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
