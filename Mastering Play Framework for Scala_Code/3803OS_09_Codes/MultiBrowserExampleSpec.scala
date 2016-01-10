//Code from ScalaTestPlus-Play examples

package org.scalatestplus.play.examples.onebrowserpersuite

import play.api.test._
import org.scalatest._
import tags._
import org.scalatestplus.play._
import play.api.{Play, Application}

// Place your tests in an abstract class
abstract class MultiBrowserExampleSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite {

  // Override app if you need a FakeApplication with other than non-default parameters.
  implicit override lazy val app: FakeApplication =
    FakeApplication(
      additionalConfiguration = Map("ehcacheplugin" -> "disabled"),
      withRoutes = TestRoute
    )

  "The OneBrowserPerSuite trait" must {
    "provide a FakeApplication" in {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the FakeApplication available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the FakeApplication" in {
      Play.maybeApplication mustBe Some(app)
    }
    "provide the port number" in {
      port mustBe Helpers.testServerPort
    }
    "provide an actual running server" in {
      import Helpers._
      import java.net._
      val url = new URL("http://localhost:" + port + "/boum")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
}

// Then make a subclass that mixes in the factory for each
// Selenium driver you want to test with.
@FirefoxBrowser class FirefoxExampleSpec extends MultiBrowserExampleSpec with FirefoxFactory
@ChromeBrowser class ChromeExampleSpec extends MultiBrowserExampleSpec with ChromeFactory
@HtmlUnitBrowser class HtmlUnitExampleSpec extends MultiBrowserExampleSpec with HtmlUnitFactory

