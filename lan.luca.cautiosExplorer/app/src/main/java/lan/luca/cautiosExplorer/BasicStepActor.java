package lan.luca.cautiosExplorer;

import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;

public class BasicStepActor extends ActorBasicJava {

    private final IssWsHttpJavaSupport support;

    public BasicStepActor(String name, IssWsHttpJavaSupport supp) {
        super(name);
        this.support = supp;
    }

    public static String getMsgName(){
        return "Stepper";
    }

    @Override
    protected void handleInput(String msg) {
        System.out.println(myname() + " || " + msg);
        if(msg.equals("w"))
            doStep();
        else
            handleMsg(new JSONObject(msg));
    }

    private void handleMsg(JSONObject jsonObject) {
        if( jsonObject.has("endmove") )
            handleEndmove(jsonObject);


    }

    private void handleEndmove(JSONObject jsonObject) {
        if(jsonObject.getString("move").equals(RobotMovesMsg.forwardMsg.getMove())){
            if(jsonObject.getString("endmove").equals("true"))
                updateObservers("{\"Stepper\":\"true\"}");
            else
                handleObstacle();
        }
    }

    private void handleObstacle() {
        updateObservers("{\"Stepper\":\"false\"}");
    }

    private void doStep() {
        support.forward(RobotMovesMsg.forwardMsg.getMsg());
    }
}
