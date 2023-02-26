package practica1.CircularQ;

import java.util.Iterator;
import utils.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    private int nElements;
    private int w;
    private int r;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
        this.nElements=0;
        w=0;
        r=0;
    }

    @Override
    public int size() {
        return this.nElements;
    }

    @Override
    public int free() {
        return (this.N)-(this.nElements);
    }

    @Override
    public boolean hasFree(int n) {
        return this.free()>=n;
    }

    @Override
    public boolean empty() {
        return this.free()==N;
    }

    @Override
    public boolean full() {
        return this.free()==0;
    }

    @Override
    public E peekFirst() {
        return this.queue[r];
    }

    @Override
    public E peekLast() {
        if(this.w==0){
            return (this.queue[N-1]);
        }
        return this.queue[w-1];
    }

    @Override
    public E get() {
        E obj=this.queue[r];
        r=(r+1)%N;
        nElements=nElements-1;
        return obj;
    }

    @Override
    public void put(E e) {
        this.queue[w]=e;
        w=(w+1)%N;
        nElements=nElements+1;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();

    }

    class MyIterator implements Iterator {

        private int pos=r;
        private int nElemIt=nElements;

        @Override
        public boolean hasNext() {
            return nElemIt!=0;
        }

        @Override
        public E next() {
            E obj=queue[pos];
            pos=(pos+1)%N;
            nElemIt=nElemIt-1;
            return obj;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}