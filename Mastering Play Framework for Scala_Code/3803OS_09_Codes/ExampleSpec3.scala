//Code from ScalaTestPlus-Play examples

package org.scalatestplus.play.examples.onebrowserpersuite

import play.api.test._
import org.scalatest._
import org.scalatest.tags.FirefoxBrowser
import org.scalatestplus.play._
import play.api.{Play, Application}

import play.api.mvc.{Handler, Action, Results}

object TestRoute extends PartialFunction[(String, String), Handler] {

  def apply(v1: (String, String)): Handler = v1 match {
    case ("GET", "/testing") =>
      Action(
        Results.Ok(
          "<html>" +
            "<head><title>Test Page</title></head>" +
            "<body>" +
            "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
            "</body>" +
            "</html>"
        ).as("text/html")
      )
    case ("GET", "/hello") =>
      Action(
        Results.Ok(
          "<html>" +
            "<head><title>Hello</title></head>" +
            "<body>" +
            "<input type='button' name='b' value='Click Me' onclick='document.title=\"helloUser\"' />" +
            "</body>" +
            "</html>"
        ).as("text/html")
      )
  }

  def isDefinedAt(x: (String, String)): Boolean = x._1 == "GET" && (x._2 == "/testing" || x._2 == "/hello")

}

@FirefoxBrowser
class ExampleSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory {

  // Override app if you need a FakeApplication with other than non-default parameters.
  implicit override lazy val app: FakeApplication =
    FakeApplication(
      additionalConfiguration = Map("ehcacheplugin" -> "disabled"),
      withRoutes = TestRoute
    )

  "The OneBrowserPerSuite trait" must {
    "navigate to testing" in {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }

    "navigate to hello in same window" in {
      go to ("http://localhost:" + port + "/hello")
      pageTitle mustBe "Hello"
      click on find(name("b")).value
      eventually { pageTitle mustBe "helloUser" }
    }
  }
}

