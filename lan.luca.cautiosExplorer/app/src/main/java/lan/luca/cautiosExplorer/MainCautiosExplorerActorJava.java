/*
===============================================================
MainRobotActorJava.java
Use the aril language and the support specified in the
configuration file IssProtocolConfig.txt


===============================================================
*/
package lan.luca.cautiosExplorer;


import it.unibo.supports2021.IssWsHttpJavaSupport;

public class MainCautiosExplorerActorJava {

    //Constructor
    public MainCautiosExplorerActorJava( ){
        IssWsHttpJavaSupport support = IssWsHttpJavaSupport.createForWs("localhost:8091" );
        CautiosExplorerActor cea = new CautiosExplorerActor("Cautios", support);
        BasicStepActor basicStepActor = new BasicStepActor("Step", support);
        support.registerActor(basicStepActor);
        cea.registerActor(basicStepActor);
        basicStepActor.registerActor(cea);
        cea.send("Start");

    }

    /*
    IssWsHttpJavaSupport support = IssWsHttpJavaSupport.createForWs("localhost:8091" );
        //while( ! support.isOpen() ) ActorBasicJava.delay(100);
        ResumableBoundaryWalkerActor ra = new ResumableBoundaryWalkerActor("rwa", support);
        support.registerActor(ra);
        //console.registerActor(new NaiveObserverActor("naiveObs") );
        ra.send("Start");
        System.out.println("MainRobotActorJava | CREATED  n_Threads=" + Thread.activeCount());
     */


    public static void main(String args[]){
        try {
            System.out.println("MainRobotActorJava | main start n_Threads=" + Thread.activeCount());
            new MainCautiosExplorerActorJava();
            //System.out.println("MainRobotActorJava  | appl n_Threads=" + Thread.activeCount());
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
