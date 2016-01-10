package plugin

import java.util.Date
import play.api.Play.current
import play.api.{Application, Play, Plugin}

class NotifierPlugin(app:Application) extends Plugin{ 
  private def notify(status:String, time:Date):Unit = { 
    val msg = s"The app has been $status at $time" 
    log.info(msg) 
  } 

  override def onStart() { 
    val time = new Date() 
    val emailId = app.configuration.getString("notify.admin.id").get 
    nofity(emailId,"started",time) 
  } 

  override def onStop() { 
    val time = new Date() 
    val emailId = app.configuration.getString("notify.admin.id").get 
    nofity(emailId,"stopped",time) 
  } 

  override def enabled: Boolean = true 
}
