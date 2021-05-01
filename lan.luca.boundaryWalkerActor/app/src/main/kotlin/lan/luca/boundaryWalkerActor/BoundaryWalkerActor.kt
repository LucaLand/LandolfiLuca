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
import lan.luca.iss2021_resumablebw.consolegui.ConsoleGui
import lan.luca.iss2021_resumablebw.wenv.RobotMovesInfo
import org.json.JSONObject
import kotlin.system.exitProcess


class BoundaryWalkerActor(name:String, scope: CoroutineScope) : ActorBasicKotlin(name, scope) {
    enum class State{START, DOBOUNDARY, STOP, start, walking, obstacle, end, paused, pausedObstacle}
    enum class WenvAddr(private val addr:String){
        LOCAL("localhost"), WENV("wenv");
        fun getAddr() :String{ return addr }
    }

    private val TIME_UNIT = "350"
    private var wenvAddr = WenvAddr.WENV
    private var state = State.start
    private var currentBasicMove  = "";

    private val stepper = BasicStepRobotActor("stepper", this,scope, wenvAddr.getAddr())
    private var moves = RobotMovesInfo(true)

    private val stepMsg = MsgUtil.buildDispatch(name, ApplMsgs.stepId, ApplMsgs.stepMsg.replace("TIME", TIME_UNIT), "stepRobot")
    private val leftMsg = MsgUtil.buildDispatch(name, ApplMsgs.robotMoveId, ApplMsgs.turnLeftMsg.replace("TIME", TIME_UNIT), "stepRobot")
    private val rightMsg = MsgUtil.buildDispatch(name, ApplMsgs.robotMoveId, ApplMsgs.turnRightMsg.replace("TIME", TIME_UNIT), "stepRobot")

    private var stepNum = 1

    protected suspend fun fsm(key: String, msg: String) {
        println("$name | fsm state=$state stepNum=$stepNum move=$key endmove=$msg")
        when (state) {
            State.start -> {
                moves.showRobotMovesRepresentation()
                if (key == "RESUME") {
                    doStep()
                    state = State.walking
                }
            }

            State.walking -> {
                if (key == "stepDone") {
                    //curState = State.walk;
                    moves.updateRobotMovesRepresentation("w")
                    doStep()
                } else if (key == "stepFail") {
                    state = State.obstacle
                    turnLeft()
                } else if (key == "STOP") {
                    state = State.paused
                    delay((TIME_UNIT.toLong()+50))
                    turnRight() //to force state transition
                } else {
                    println("IGNORE answer of turnLeft")
                }
            } //walk

            State.obstacle -> if ((key == "turnLeft" || key == "turnRight") && msg == "true") {
                if (stepNum < 4) {
                    stepNum++
                    moves.updateRobotMovesRepresentation("l")
                    moves.showRobotMovesRepresentation()
                    state = State.walking
                    doStep()
                } else {  //at home again
                    state = State.end
                    turnLeft() //to force state transition
                }
            } else if (key == "STOP") {
                state = State.pausedObstacle
                turnLeft() //to force state transition
            }

            State.paused -> {
                if (key == "stepDone") {
                    moves.updateRobotMovesRepresentation("w")
                } else if (key == "turnRight" && msg == "true") {
                    turnLeft() //to compensate last turnRight //STEPPER DO NOT EXECUTE THE TURNRIGHT without a delay()
                } else if (key == "RESUME") {
                    state = State.walking
                    doStep()
                }
            }

            State.pausedObstacle -> {
                if (key == "RESUME") {
                    state = State.obstacle
                    turnRight()
                }
            }

            State.end -> {
                if (key == "turnLeft") {
                    println("BOUNDARY WALK END")
                    moves.updateRobotMovesRepresentation("l")
                    moves.showRobotMovesRepresentation()
                    turnRight() //to compensate last turnLeft
                } else {
                    //reset();
                    stepNum = 1
                    state = State.start
                    moves.movesRepresentationAndClean
                }
            }
            else -> {
                System.out.println("error - curState = $state")
            }
        }
    }




    override suspend fun handleInput(msg: ApplMessage) {
        colorPrint(  "$name BasicStepRobotActor|  handleInput $msg currentBasicMove=$currentBasicMove")
        val infoJsonStr = msg.msgContent
        val infoJson    = JSONObject(infoJsonStr)

        msgDriven(infoJson)
    }

    protected suspend fun msgDriven(infoJson: JSONObject) {
        when{
            infoJson.has("endmove") -> fsm(infoJson.getString("move"), infoJson.getString("endmove"));
            //infoJson.has("sonarName") -> handleSonar(infoJson);
            //infoJson.has("collision") -> handleCollision(infoJson);
            infoJson.has("robotcmd") -> handleRobotCmd(infoJson);
            infoJson.has("stepDone") -> fsm("stepDone", infoJson.getString("stepDone"))
            infoJson.has("stepFail") -> fsm("stepFail", infoJson.getString("stepFail"))
            infoJson.has("robotcmd") -> fsm("robotcmd",infoJson.getString("robotcmd"))
        }
    }

    private suspend fun handleRobotCmd(infoJson: JSONObject) {
        var msg = infoJson.getString("robotcmd")

        when(msg){
            "RESUME" -> fsm("RESUME","RESUME")
            "STOP" -> fsm("STOP", "STOP")
        }
    }

    private fun fsmv1(key: String, msg: String) {
        colorPrint(  "$name | State: $state - key: $key - msg: $msg", Color.GREEN)
        when(state){
            State.START -> {
                if(msg== "Start") {
                    doStep()
                    state = State.DOBOUNDARY
                }
            }

            State.DOBOUNDARY -> {
                if(stepNum >= 4){
                    state = State.START
                    stepNum=0
                    //finish()
                    return
                }
                when{
                    key=="stepFail" -> {
                        turnLeft()
                        stepNum++
                    }
                    key=="stepDone" -> doStep()
                    key=="endmove" -> {if(msg=="true") doStep()}
                }

            }

            State.STOP ->{
                state = State.START
            }
        }
    }

    private suspend fun finish() {
        print("Repeat or Finish?\n(R/F) -> ")

        val enteredString = readLine()
        println("You have entered this: $enteredString")
        if(enteredString == "R"){
            stepNum = 0
            state = State.START
            fsm("Start","Start")
        }else if(enteredString == "F"){
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

    private fun turnRight() {
        stepper.send(rightMsg)
    }

    private fun doStep() {
        stepper.send(stepMsg)
    }



}

fun main() {
    println("BEGINS CPU=${sysUtil.cpus} ${sysUtil.curThread()}")
    runBlocking {

        val boundaryActor = BoundaryWalkerActor("tiger", this)
        val gui = ConsoleGui(GuiObserver(boundaryActor))
        boundaryActor.send(MsgUtil.startDefaultMsg)
        delay(5000)
        println("ENDS runBlocking ${sysUtil.curThread()}")
        //boundaryActor.terminate()
        //delay(1000)
        //exitProcess(5)
    }
    //println("ENDS main ${sysUtil.curThread()}")

}