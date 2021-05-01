package lan.luca.boundaryWalkerActor

import com.andreapivetta.kolor.Color
import it.unibo.actor0.MsgUtil
import it.unibo.actor0.sysUtil
import lan.luca.iss2021_resumablebw.interaction.IssObserver
import org.json.JSONObject
import java.util.*

class GuiObserver(boundaryActor: BoundaryWalkerActor) : IssObserver {

    var boundaryWalkerActor = boundaryActor

    override fun handleInfo(p0: String?) {
        handleInfo(JSONObject(p0))
        sysUtil.colorPrint("GuiObserver | msg= ${p0.toString()}", Color.BLUE)
    }

    override fun handleInfo(p0: JSONObject?) {
        if (p0 != null) {
            if(p0.has("robotcmd")) {
                var cmd = p0.getString("robotcmd")
                sysUtil.colorPrint("GuiObserver | command= $cmd", Color.BLUE)
                boundaryWalkerActor.send(MsgUtil.buildDispatch("GuiObserver", "GuiId", p0.toString(), "tiger"))
            }
        }
    }

}