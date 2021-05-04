package it.unibo.webspring.demo

import it.unibo.actor0.MsgUtil
import it.unibo.actor0.sysUtil
import it.unibo.actorAppl.BasicStepRobotActorCaller
import it.unibo.actorAppl.NaiveActorKotlinObserver
import it.unibo.robotService.ApplMsgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import lan.luca.boundaryWalkerActor.BoundaryWalkerActor
import mapRoomKotlin.mapUtil
import org.springframework.stereotype.Component


@Component
object RobotResource {
     lateinit var myscope     : CoroutineScope
     lateinit var obs         : NaiveActorKotlinObserver
     lateinit var robot       : BasicStepRobotActorCaller
     lateinit var robotBw       : BoundaryWalkerActor
     var configured           = false
    var map = ""


    fun initRobotResource(local: Boolean=false){
        if( configured ){
            sysUtil.colorPrint("RobotResource | already configured localRobot=$local - ${sysUtil.curThread()} " )
            return
        }
        //Not already condfigured
         configured = true
         myscope    = CoroutineScope(Dispatchers.Default)
         obs        = NaiveActorKotlinObserver("obs", myscope)

            //TODO: define an observer that updates the HTML page
         robot   = BasicStepRobotActorCaller("robot", myscope )
         robotBw = BoundaryWalkerActor("BwActor", myscope)
         /*if( local ){
            BasicStepRobotActor("stepRobot", ownerActor=obs, myscope, "localhost")
         }
          */
         sysUtil.colorPrint("RobotResource | configured localRobot=$local - ${sysUtil.curThread()} " )
    }

    fun execMove( move : String ) {
        when (move) {
            "l" -> robot.send(ApplMsgs.stepRobot_l("spring"))
            "r" -> robot.send(ApplMsgs.stepRobot_r("spring"))
            "w" -> robot.send(ApplMsgs.stepRobot_w("spring"))
            "s" -> robot.send(ApplMsgs.stepRobot_s("spring"))
            "p" -> robot.send(ApplMsgs.stepRobot_step("spring", "350"))
            "RESUME" -> robotBw.send(MsgUtil.buildDispatch("spring", "GuiId", "{\"robotcmd\":\"RESUME\"}", "BwActor"))
            "STOP" -> robotBw.send(MsgUtil.buildDispatch("spring", "GuiId", "{\"robotcmd\":\"STOP\"}", "BwActor"))
        }

    }

    fun getRoomMap() : String{
        return "${mapUtil.getMapAndClean()} \n\t ${mapUtil.state}"
    }


}
