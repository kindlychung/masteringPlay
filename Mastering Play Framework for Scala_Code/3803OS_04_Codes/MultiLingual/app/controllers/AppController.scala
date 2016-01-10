package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._


object AppController extends Controller {

  val enquiryForm = Form(
    tuple(
      "emailId" -> email,
      "userName" -> optional(text),
      "question" -> nonEmptyText)
  )

  def index = Action {
    implicit request =>
      Redirect(routes.AppController.askUs)
  }

  def askUs = Action {
    implicit request =>
      Ok(views.html.index(enquiryForm))
  }

  def enquire = Action {
    implicit request =>
      enquiryForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index(errors)),
        query => {
          println(query.toString)
          Redirect(routes.AppController.askUs)
        }
      )
  }

}
