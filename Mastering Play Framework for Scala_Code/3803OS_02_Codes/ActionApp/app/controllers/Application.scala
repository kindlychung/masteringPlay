package controllers

import java.util.Date

import models.{Artist, Subscription, User}
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.{Node, NodeSeq}

object Application extends Controller {

  def index = Action {
    Ok(views.html.main())
  }

  def listArtist = Action {
    Ok(views.html.home(Artist.fetch))
  }

  def fetchArtistByName(name: String) = Action {
    Ok(views.html.home(Artist.fetchByName(name)))
  }

  def search(name: String, country: String) = Action {
    val result = Artist.fetchByNameOrCountry(name, country)
    if (result.isEmpty) {
      NoContent
    }
    else {
      Ok(views.html.home(result))
    }
  }

  def search2(name: Option[String], country: String) = Action {
    val result = name match {
      case Some(n) => Artist.fetchByNameOrCountry(n, country)
      case None => Artist.fetchByCountry(country)
    }
    if (result.isEmpty) {
      NoContent
    }
    else {
      Ok(views.html.home(result))
    }
  }

  def subscribe = Action {
    request =>
      Ok("received " + request.body)
  }

  /*def subscribe = Action {
    request =>
      val reqBody: AnyContent = request.body
      val textContent: Option[String] = reqBody.asText
      textContent.map {
        emailId =>
          Ok("added " + emailId + " to subscriber's list")
      }.getOrElse {
        BadRequest("improper request body")
      }
  }*/

  /*def subscribe = Action(parse.text) {
    request =>
      Ok("added " + request.body + " to subscriber's list")
  }*/

  /*def subscribe = Action(parse.json) {
    request =>
      val reqData: JsValue = request.body
      val emailId = (reqData \ "emailId").as[String]
      val interval = (reqData \ "interval").as[String]
      Ok(s"added $emailId to subscriber's list and will send updates every $interval")
  }*/

  import java.io.File

  def createProfile = Action(parse.multipartFormData) {
    request =>
      val formData = request.body.asFormUrlEncoded
      val email: String = formData.get("email").get(0)
      val name: String = formData.get("name").get(0)
      val userId: Long = User(email, name).save
      request.body.file("displayPic").map {
        picture =>
          val path = "/socialize/user/"
          if (!picture.filename.isEmpty) {
            picture.ref.moveTo(new File(path + userId + ".jpeg"))
          }
          Ok("successfully added user")
      }.getOrElse {
        BadRequest("failed to add user")
      }
  }

  val parseAsSubscription = parse.using {
    request =>
      parse.json.map {
        body =>
          val emailId: String = (body \ "emailId").as[String]
          val interval: String = (body \ "interval").as[String]
          Subscription(emailId, interval)
      }
  }

  implicit val subWrites = Json.writes[Subscription]

  def getSub = Action(parseAsSubscription) {
    request =>
      val subscription: Subscription = request.body
      Ok(Json.toJson(subscription))
  }

  /*def subscribe = Action(parse.tolerantJson) {
    request =>
      val reqData: JsValue = request.body
      val emailId = (reqData \ "email").as[String]
      val interval = (reqData \ "interval").as[String]
      Ok(s"added $emailId to subscriber's list and will send updates every $interval")
  }*/

  def plainResult = Action {
    Result(
      header = ResponseHeader(200, Map(CONTENT_TYPE -> "text/plain")),
      body = Enumerator("This is the response from plainResult method".getBytes())
    )
  }

  def getUserImage(userId: Long) = Action {
    val path: String = s"/socialize/user/$userId.jpeg"
    val img = new File(path)
    if (img.exists()) {
      Ok.sendFile(
        content = img,
        inline = true
      )
    }
    else
      NoContent
  }

  def save = Action(parse.text) {
    request =>
      Status(200)("Got: " + request.body)
  }

  def getReport(fileName: String) = Action.async {
    Future {
      val file: File = new File(fileName)
      if (file.exists()) {
        val info = file.lastModified()
        Ok(s"lastModified on ${new Date(info)}")
      }
      else
        NoContent
    }
  }

  /*def getReport(fileName: String) = Action.async {

    val mayBeFile = Future {
      new File(fileName)
    }
    val timeout = play.api.libs.concurrent.Promise.timeout("Past max time", 10, TimeUnit.SECONDS)
    Future.firstCompletedOf(Seq(mayBeFile, timeout)).map {
      case f: File =>
        if (f.exists()) {
          val info = f.lastModified()
          Ok(s"lastModified on ${new Date(info)}")
        }
        else
          NoContent
      case t: String => InternalServerError(t)
    }
  }*/

  def getConfig = Action {
    implicit request =>
      val xmlResponse: Node = <metadata>
        <company>TinySensors</company>
        <batch>md2907</batch>
      </metadata>

      val jsonResponse = Json.obj("metadata" -> Json.arr(
        Json.obj("company" -> "TinySensors"),
        Json.obj("batch" -> "md2907"))
      )
      render {
        case Accepts.Xml() => Ok(xmlResponse)
        case Accepts.Json() => Ok(jsonResponse)
      }
  }

  def fooBar = Action {
    implicit request =>
      val xmlResponse: Node = <metadata>
        <company>TinySensors</company>
        <batch>md2907</batch>
      </metadata>

      val jsonResponse = Json.obj("metadata" -> Json.arr(
        Json.obj("company" -> "TinySensors"),
        Json.obj("batch" -> "md2907"))
      )

      render {
        case Accepts.Xml() => Ok(xmlResponse)
        case Accepts.Json() & Accepts.JavaScript() => Ok(jsonResponse)
      }
  }
}
