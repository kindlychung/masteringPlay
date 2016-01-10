package controllers

import actions.{TrackingAction, TrackAction}
import org.joda.time.DateTime
import play.api.mvc.{Action, WrappedRequest, Request, Controller}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DemoCustom extends Controller {

  class TimedRequest[A](val time: DateTime, request: Request[A]) extends WrappedRequest[A](request)

  def timedAction[A](action: Action[A]) = Action.async(action.parser) {
    request =>
      val time = new DateTime()
      val newRequest = new TimedRequest(time, request)
      action(newRequest)
  }

  private def isCompatibleBrowser(agent: String): Boolean = {
    !agent.contains("MSIE")
  }

  /*def timedAction[A](action: Action[A]) = Action.async(action.parser) {
    request =>
      val time = new DateTime()
      val newRequest = new TimedRequest[A](time, request)
      request.headers.get(USER_AGENT).collect {
        case agent if isCompatibleBrowser(agent) =>
          action(newRequest)
      }.getOrElse {
        Future.successful(Ok(views.html.main()))
      }
  }

  def timedAction[A](action: Action[A]) = Action.async(action.parser) {
    request =>
      val time = new DateTime()
      val newRequest = new TimedRequest(time, request)
      action(newRequest).map(_.withHeaders("processTime" -> new DateTime().minus(time.getMillis).getMillis.toString()))
  }*/

  def viewAdminProfile(id: Long) = TrackAction {
    request =>
    Ok(views.html.main())
  }

  def updateAdminProfile(id: Long) = TrackAction(parse.json) {
    request =>
      Ok(views.html.main())
  }

 /* def viewAdminProfile(id: Long) = TrackAction {
    Action { request =>
      Ok(views.html.main())
    }
  }

  def updateAdminProfile(id: Long) = TrackAction {
    Action(parse.json) { request =>
      Ok(views.html.main())
    }
  }*/

  /*def viewAdminProfile(id: Long) = TrackingAction {
    request =>
      Ok(views.html.main())
  }

  def updateAdminProfile(id: Long) = TrackingAction(parse.json) {
    request =>
      Ok(views.html.main())
  }*/
}
