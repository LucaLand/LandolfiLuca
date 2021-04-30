package lan.luca.boundaryWalkerActor

import com.andreapivetta.kolor.Color
import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.actor0.MsgUtil
import it.unibo.actor0.sysUtil
import it.unibo.robotService.ApplMsgs
import it.unibo.robotService.BasicStepRobotActor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import kotlin.system.exitProcess

class BoundaryWalkerActor(name:String, scope: CoroutineScope) : ActorBasicKotlin(name, scope) {
enum class State{START, DOBOUNDARY, STOP}

    private val TIME_UNIT = "350"

    private var state = State.START
    private var currentBasicMove  = "";
    private val stepper = BasicStepRobotActor("stepper", this,scope,"localhost")
    private val stepMsg = MsgUtil.buildDispatch(name, ApplMsgs.stepId, ApplMsgs.stepMsg.replace("TIME", TIME_UNIT), "stepRobot")
    private val leftMsg = MsgUtil.buildDispatch(name, ApplMsgs.robotMoveId, ApplMsgs.turnLeftMsg.replace("TIME", TIME_UNIT), "stepRobot")

    private var boundary = 0

    override suspend fun handleInput(msg: ApplMessage) {
        colorPrint(  "$name BasicStepRobotActor|  handleInput $msg currentBasicMove=$currentBasicMove")
        val infoJsonStr = msg.msgContent
        val infoJson    = JSONObject(infoJsonStr)
        
        if(msg == MsgUtil.startDefaultMsg)
            fsm("", "Start");
        else
            msgDriven(infoJson)

    }

    protected fun msgDriven(infoJson: JSONObject) {


        when{
            infoJson.has("stepDone") -> fsm("stepDone", infoJson.getString("stepDone"))
            infoJson.has("stepFail") -> fsm("stepFail", infoJson.getString("stepFail"))
            infoJson.has("endmove") -> fsm("endmove",infoJson.getString("endmove"))
        }
    }

    private fun fsm(key: String, msg: String) {
        colorPrint(  "$name | State: $state - key: $key - msg: $msg", Color.GREEN)
        when(state){
            State.START -> {
                if(msg== "Start") {
                    doStep()
                    state = State.DOBOUNDARY
                }
            }

            State.DOBOUNDARY -> {
                if(boundary >= 4){
                    state = State.STOP
                    return
                }
                when{
                    key=="stepFail" -> {
                        turnLeft()
                        boundary++
                    }
                    key=="stepDone" -> doStep()
                    key=="endmove" -> {if(msg=="true") doStep()}
                }

            }

            State.STOP ->{
                finish()
            }
        }
    }

    private fun finish() {
        print("Repeat or Finish?\n(R/F) -> ")

        val enteredString = readLine()
        println("You have entered this: $enteredString")
        if(enteredString == "R"){
            state = State.START
            fsm("Start","Start")
        }else if(enteredString == "E"){
            this.terminate()
            exitProcess(5)
        }else{
            finish()
            return
        }

        return
    }

    private fun turnLeft() {
        stepper.send(leftMsg)
    }

    private fun doStep() {
        stepper.send(stepMsg)
    }



}

fun main() {
    println("BEGINS CPU=${sysUtil.cpus} ${sysUtil.curThread()}")
    runBlocking {
        val boundaryActor = BoundaryWalkerActor("tiger", this)
        boundaryActor.send(MsgUtil.startDefaultMsg)
        delay(5000)
        println("ENDS runBlocking ${sysUtil.curThread()}")
        //boundaryActor.terminate()
        //delay(1000)
        //exitProcess(5)
    }
    //println("ENDS main ${sysUtil.curThread()}")

}