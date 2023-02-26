package practica1.LinkedQ;

import java.util.Iterator;
import utils.Queue;

public class LinkedQueue<E> implements Queue<E> {

    private int nElements=0;
    private Node lastNode, firstNode;


    @Override
    public int size() {
        return this.nElements;
    }

    @Override
    public int free() {
        throw new UnsupportedOperationException("Not applies");
    }

    @Override
    public boolean hasFree(int n) {
        return true;
    }

    @Override
    public boolean empty() {
        return this.size()==0;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public E peekFirst() {
        return (E) this.firstNode.getValue();
    }

    @Override
    public E peekLast() {
        return (E) this.lastNode.getValue();
    }

    @Override
    public E get() {
        E obj= (E) this.firstNode.getValue();
        if(nElements==1){
            this.lastNode=null;
        }
        this.firstNode=this.firstNode.getNext();
        this.nElements=this.nElements-1;
        return obj;
    }

    @Override
    public void put(E value) {
        Node nod=new Node();
        nod.setValue(value);
        if(this.empty()){
            this.lastNode=nod;
            this.firstNode=nod;
        }
        else{
            this.lastNode.setNext(nod);
            this.lastNode=nod;
        }
        this.nElements=this.nElements+1;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        int nElemsIt=nElements;
        Node nod=firstNode;
        @Override
        public boolean hasNext() {
            return nElemsIt!=0;
        }

        @Override
        public E next() {
            Node<E> nodnext = nod;
            nod=this.nod.getNext();
            nElemsIt=nElemsIt-1;
            return nodnext.getValue();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}