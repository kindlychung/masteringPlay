
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
            "<input type='button' name='b' value='Click Me' onclick='document.title=\"testing\"' />" +
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

class AllBrowsersPerSuiteTest extends PlaySpec with OneServerPerSuite with AllBrowsersPerSuite {

  // Override newAppForTest if you need a FakeApplication with other than non-default parameters.
  override lazy val app: FakeApplication =
    FakeApplication(
      withRoutes = TestRoute
    )

  // Place tests you want run in different browsers in the `sharedTests` method:
  def sharedTests(browser: BrowserInfo) = {

      "navigate to testing "+browser.name in {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click on find(name("b")).value
        eventually { pageTitle mustBe "testing" }
      }

      "navigate to hello in a new window"+browser.name in {
        go to ("http://localhost:" + port + "/hello")
        pageTitle mustBe "Hello"
        click on find(name("b")).value
        eventually { pageTitle mustBe "helloUser" }
      }
  }

  // Place tests you want run just once outside the `sharedTests` method
  // in the constructor, the usual place for tests in a `PlaySpec`

  "The test" must {
    "start the FakeApplication" in {
      Play.maybeApplication mustBe Some(app)
    }
  }

}


