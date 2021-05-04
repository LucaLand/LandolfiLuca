package it.unibo.webspring.demo

import com.andreapivetta.kolor.Color
import it.unibo.actor0.sysUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*


@Controller
class HumanInterfaceController {
    @Value("\${human.logo}")
    var appName: String?    = null
    var applicationModelRep = "waiting"

    var roomMap = ""
    val gui = "simpleGui"

    init{
        RobotResource.initRobotResource(true) //we want a local BasicStepRobotActorCaller
    }

    @GetMapping("/")    //defines that the method handles GET requests.
    fun entry(model: Model): String {
        model.addAttribute("arg", appName)
        roomMap = RobotResource.getRoomMap()
        model.addAttribute("map", roomMap)
        println("TIGER $roomMap")
        model["map"] = roomMap
        println("HumanInterfaceController | entry model=$model")
        return gui
    }

    /*
    Spring provides a Model object which can be passed into the controller.
    You can configure this model object via the addAttribute(key, value) method.
     */

    @GetMapping("/model")
    @ResponseBody   //With this annotation, the String returned by the methods is sent to the browser as plain text.
    fun  homePage( model: Model) : String{
        model.addAttribute("arg", appName)

        roomMap = RobotResource.getRoomMap()
        model.addAttribute("map", roomMap)
        println("TIGER $roomMap")
        model["map"] = roomMap

        sysUtil.colorPrint("HumanInterfaceController | homePage model=$model", Color.GREEN)
        return String.format("HumanInterfaceController text normal state= $applicationModelRep"  );
    }

    //@RequestMapping methods assume @ResponseBody semantics by default.
    //https://springframework.guru/spring-requestmapping-annotation/

    //@RequestMapping( "/move") //, method = RequestMethod.POST,  MediaType.APPLICATION_FORM_URLENCODED_VALUE
    @PostMapping("/move")  //signals that this method handles POST requests
    //@ResponseBody
    fun move( model: Model, @RequestParam(name = "movetodo") move:String ) : String {
        sysUtil.colorPrint("HumanInterfaceController | doMove  $move", Color.GREEN)

        roomMap = RobotResource.getRoomMap()
        model.addAttribute("map", roomMap)
        println("TIGER $roomMap")
        model["map"] = roomMap

        RobotResource.execMove(move)
        return  gui
    }

    @PostMapping("/domove")
    fun do_move(model: Model, @RequestParam(name = "movetodo") move:String) : String {
        sysUtil.colorPrint("HumanInterfaceController | do_move $move ", Color.GREEN)

        //if(move=="SHOW"){
            roomMap = RobotResource.getRoomMap()
            //println("TIGER $roomMap")
            model.addAttribute("map", roomMap)
            model["map"] = roomMap
        //}else
            RobotResource.execMove(move)
        return  gui
    }

}