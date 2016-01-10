package controllers

import org.joda.time.DateTime
import play.api.Routes
import play.api.mvc._

object AppController extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def isValidSession(sessionId: String): Boolean = false


  def home = Action {
    implicit request =>
      request.headers.get("sessionId") match {
        case Some(sId) if isValidSession(sId) =>
          Ok(views.html.home(request))
        case _ => Redirect(routes.AppController.login())
      }
  }

  def login = Action {
    Ok(views.html.login())
  }

  def fetchGreeting = Action{
    val hourOfDay = new DateTime().getHourOfDay
    val message = hourOfDay match{
      case x if x>=9 && x<12 => "Good Morning!!!"
      case y if y>=12 && y<17 => "Good Afternoon!!"
      case z if z>=17 && z<=20 => "Good Evening !!"
      case _ => "Sorry, application is available between 9:00 - 20:00 hrs only. We request you to try again during working hours."
    }
    Ok(message)
  }

  def jsDemo = Action {
    implicit request =>
      Ok(views.html.jsDemo(request))
  }

  def jsRouter = Action{ implicit request=>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.AppController.fetchGreeting
      )
    ).as("text/javascript")
  }
}