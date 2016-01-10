package controllers

import play.api.libs.oauth._
import play.api.libs.openid.OpenID
import play.api.libs.ws.WS
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Play.current

object Application extends Controller {

  def index = Action.async {
    implicit request =>
      OpenID.verifiedId.map(info => Ok(views.html.main(info.attributes)))
        .recover {
        case t: Throwable =>
          Redirect(routes.Application.login)
      }
  }

  def login = Action.async {
    implicit request =>
      val openId = "https://www.google.com/accounts/o8/id"
      OpenID.redirectURL(
        openId,
        routes.Application.index.absoluteURL(),
        Seq("email" -> "http://schema.openid.net/contact/email",
          "name" -> "http://openid.net/schema/namePerson/first"))
        .map(url => Redirect(url))
        .recover { case t: Throwable => Ok(t.getMessage)}
  }


  val KEY = ConsumerKey("myAppKey", "myAppSecret")

  val TWITTER = OAuth(ServiceInfo(
    "https://api.twitter.com/oauth/request_token",
    "https://api.twitter.com/oauth/access_token",
    "https://api.twitter.com/oauth/authorize", KEY),
    true)

  def authenticate = Action { request =>
    request.getQueryString("oauth_verifier").map { verifier =>
      val tokenPair = sessionTokenPair(request).get
      TWITTER.retrieveAccessToken(tokenPair, verifier) match {
        case Right(t) => {
          Redirect(routes.Application.welcome).withSession("token" -> t.token, "secret" -> t.secret)
        }
        case Left(e) => throw e
      }
    }.getOrElse(
        TWITTER.retrieveRequestToken("http://localhost:9000/twitterLogin") match {
          case Right(rt) =>
            Redirect(TWITTER.redirectUrl(rt.token)).withSession("token" -> rt.token, "secret" -> rt.secret)
          case Left(e) => throw e
        })
  }

  private def sessionTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get("token")
      secret <- request.session.get("secret")
    } yield {
      RequestToken(token, secret)
    }
  }

  def welcome = Action.async {
    implicit request =>
      sessionTokenPair match {
        case Some(credentials) => {
          WS.url("https://api.twitter.com/1.1/statuses/home_timeline.json")
            .sign(OAuthCalculator(KEY, credentials))
            .get
            .map(result => Ok(result.json))
        }
        case _ => Future.successful(Redirect(routes.Application.authenticate).withNewSession)
      }
  }
}
