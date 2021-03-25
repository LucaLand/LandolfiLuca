package it.unibo.resumablebw;

import java.sql.Time;
import java.util.Observable;
import java.util.Observer;

public class AnotherObserver implements Observer, Runnable {

    private int stopCount = 0;
    @Override
    public synchronized void update(Observable observable, Object o) {
        /*
        Per constatare che una sleep in questa funzione
        blocca l'interfaccia in quanto l'observer prende
        il controllo del suo Thread
         */
        try {
            System.out.println("AnotherObserver || update: 1 Blocco Interfaccia");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("AnotherObserver || update: 2 Sblocco Interfaccia");
        if(o.toString().equals("STOP")) {
            stopCount++;
            new Thread(this).start();
        }
        /*
        Function return control to the UI Interface
         while the run function is in execution on another Thread

         */
    }

    @Override
    public void run() {
        /*
        This part of the Observer will execute in concurrency not blocking the UI interface
         */

        System.out.println("AnotherObserver || THREAD run: StopCount = " + stopCount
                    + " || Active Threads:" + Thread.activeCount());
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("AnotherObserver || THREAD run : FINISHED! " +
                " || Thread n. " + (Thread.activeCount()-1));
    }
}
