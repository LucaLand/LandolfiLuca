package it.unibo.resumablebw;

import java.sql.Time;
import java.util.Observable;
import java.util.Observer;

public class AnotherObserver implements Observer, Runnable {

    private int stopCount = 0;
    @Override
    public void update(Observable observable, Object o) {
        /*
        try {
            System.out.println("AnotherObserver || update: 1 Blocco Interfaccia");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        System.out.println("AnotherObserver || update: 2 Sblocco Interfaccia");
        if(o.toString().equals("STOP")) {
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
            stopCount++;
            System.out.println("AnotherObserver || THREAD run: StopCount = " + stopCount
                    + " || Active Threads:" + Thread.activeCount());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("AnotherObserver || THREAD run : FINISHED!");
    }
}
