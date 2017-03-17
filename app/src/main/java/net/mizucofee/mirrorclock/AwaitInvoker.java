package net.mizucofee.mirrorclock;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

public final class AwaitInvoker implements Runnable {
    private Runnable[] runnables;
    private CountDownLatch signal;

    @Override
    public void run() {
        for ( Runnable r : this.runnables ) {
            try {
                r.run();
            } finally {
                this.signal.countDown();
            }
        }
    }

    public void invokeAndWait(Handler handler, Runnable... runnables) {
        this.runnables = runnables;

        if( Looper.myLooper() == handler.getLooper() ){
            for ( Runnable r : this.runnables ) {
                r.run();
            }
            return;
        }
        this.signal = new CountDownLatch(runnables.length); 
        handler.post(this);
        try {
            this.signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
}