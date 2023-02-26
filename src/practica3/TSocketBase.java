package practica3;

import ast.logging.Log;
import ast.logging.LogFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import utils.Channel;

public class TSocketBase {

    public static Log log = LogFactory.getLog(TSocketBase.class);

    protected Lock lock;
    protected Condition appCV;
    protected Channel channel;

    protected TSocketBase(Channel channel) {
        lock = new ReentrantLock();
        appCV = lock.newCondition();
        this.channel = channel;
    }
}
