package controllers

import models._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

trait BaseUserController extends Controller {
this: Controller =>

  val userRepo:UserRepo

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

object UserController extends BaseUserController{
  val userRepo = AnormUserRepo
}