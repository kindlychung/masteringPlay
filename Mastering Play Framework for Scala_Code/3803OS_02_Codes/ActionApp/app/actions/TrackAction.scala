package actions

import play.api.mvc._
import scala.concurrent.Future

object TrackAction extends ActionBuilder[Request] {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    persistReq(request)
    block(request)
  }

  private def persistReq[A](request: Request[A]) = {
    println(s"received another request ${request.headers}")
  }
}

case class TrackAction[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[Result] = {
    persistReq(request)
    action(request)
  }

  private def persistReq(request: Request[A]) = {
    println(s"received another request ${request.headers}")
  }

  lazy val parser = action.parser
}

object TrackingAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    block(request)
  }
  override def composeAction[A](action: Action[A]) = new TrackAction(action)
}
