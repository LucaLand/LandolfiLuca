package lan.luca.cautiosExplorer;

import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;

public class CautiosExplorerActor extends ActorBasicJava {

    private static String msgName = "Cautios";

    private final IssWsHttpJavaSupport support;
    private boolean backHome = false;
    public CautiosExplorerActor(String name, IssWsHttpJavaSupport support) {
        super(name);
        this.support = support;
    }

    public static String getMsgName() {
        return msgName;
    }

    @Override
    protected void handleInput(String msg) {
        System.out.println(myname() + " || " + msg);

        if(msg.equals("Start"))
            updateObservers("w");
        else
            handleMsg(new JSONObject(msg));
    }

    private void handleMsg(JSONObject jsonObject) {
        if(jsonObject.has(BasicStepActor.getMsgName()))
            handleStepper(jsonObject);
    }

    private void handleStepper(JSONObject jsonObject) {
        String endMove = jsonObject.getString(BasicStepActor.getMsgName());
        if(endMove.equals("true"))
            updateObservers("w");
        else
            backHome();
    }

    private void backHome() {
        support.forward(RobotMovesMsg.turnLeftMsg.getMsg());
        delay(1000);
        support.forward(RobotMovesMsg.turnLeftMsg.getMsg());
        delay(1000);
        if(!backHome) {
            backHome = true;
            updateObservers("w");
        }else
            updateObservers("{\""+ msgName +"\":\"in Den\"}");
    }

}
