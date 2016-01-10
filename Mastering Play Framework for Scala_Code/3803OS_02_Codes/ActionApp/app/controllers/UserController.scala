package controllers

import models.User
import play.api.mvc._

import scala.concurrent.Future

object UserController extends Controller{
  class UserRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

  def UserAction(userId: String) = new ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]) = {
      User.findById(userId).map { user:User =>
        block(new UserRequest(user, request))
      } getOrElse {
        Future.successful(Ok(views.html.main()))
      }
    }
  }

}
