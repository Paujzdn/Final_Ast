package practica8;

public interface IBridge {
    void enter(boolean direction); //Entering in the bridge
    void exit(); //Leaving the bridge
    void close(); //Client (i.e car) quitting;
}
