package controllers

import models.Credentials
import play.api.mvc._

import play.api._
import data.Form
import data.Forms._
import play.twirl.api.Html
import play.api.data.validation.{Invalid, Valid, ValidationError, Constraint}

object Application extends Controller {

  def index = Action {
    implicit request =>
      val content: play.twirl.api.Html = Html("Hello WOrld")
      Ok(views.html.main("home") {
        content
      })
  }

  def newUser = Action(parse.multipartFormData) {
    implicit request =>
      signupForm.bindFromRequest().fold(
        formWithErrors => BadRequest(views.html.register(formWithErrors)),
        credentials => Ok("Welcome!!!!")
      )
  }

  val signupForm = Form(
    mapping(
      "loginId" -> email,
      "password" -> nonEmptyText
    )(Credentials.apply)(Credentials.unapply) verifying("Username already in use",
      result => result match {
        case loginCredentials =>
          !loginCredentials.loginId.equals("testUser@app.com")
      })
  )

  def register = Action {
    implicit request =>
      Ok(views.html.register(signupForm)).withNewSession
  }

  def login = Action {
    Ok("login")
  }

  val validUserName = """[A-Za-z0-9]*""".r
  val userNameCheck: Constraint[String] = Constraint("userName")({
    text =>
      val error = text match {
        case validUserName() => Nil
        case _ => Seq(ValidationError("only digits and alphabet ar allowed in userName"))
      }
      if (error.isEmpty) Valid else Invalid(error)
  })
}
