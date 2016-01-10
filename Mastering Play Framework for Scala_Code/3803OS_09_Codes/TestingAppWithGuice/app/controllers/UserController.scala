package controllers

import javax.inject.{Inject, Singleton}

import models.{User, UserRepo}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}


@Singleton
class UserController @Inject()(userRepo: UserRepo) extends Controller {

  implicit val userWrites = Json.writes[User]

  /* GET a specific user's details */
  def getUser(userId: Long) = Action {
    val u = userRepo.find(userId)
    if (u.isEmpty) {
      NoContent
    }
    else {
      Ok(Json.toJson(u))
    }
  }

}
