//code from ScalatestPlus-Play examples
package org.scalatestplus.play.examples.onebrowserpersuite

import play.api.test._
import org.scalatest._
import tags.FirefoxBrowser
import org.scalatestplus.play._
import play.api.{Play, Application}

// This is the "master" suite
class NestedExampleSpec extends Suites(
  new OneSpec,
  new TwoSpec,
  new RedSpec,
  new BlueSpec
) with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory {
  // Override app if you need a FakeApplication with other than non-default parameters.
  implicit override lazy val app: FakeApplication =
    FakeApplication(
      additionalConfiguration = Map("ehcacheplugin" -> "disabled"),
      withRoutes = TestRoute
    )
}
 
// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser
@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser
@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser

@DoNotDiscover
class BlueSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser {

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
  }
}

