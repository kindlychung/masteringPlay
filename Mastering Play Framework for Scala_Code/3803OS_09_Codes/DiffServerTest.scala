
import play.api.test._
import org.scalatest._
import play.api.{Play, Application}

class DiffServerTest extends PlaySpec with OneServerPerTest {

  private val colors = Seq("red", "blue", "yellow")

  private var code = 0

  override def newAppForTest(testData: TestData): FakeApplication = {
    val currentCode = code
    code += 1
    FakeApplication(additionalConfiguration = Map("foo" -> "bar",
      "ehcacheplugin" -> "disabled",
      "color" -> colors(currentCode)
    ))
  }

  override lazy val port = 1234

  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  "The OneServerPerTest trait" must {
    "provide a FakeApplication" in {
      app.configuration.getString("color") mustBe Some("red")
    }
    "make another FakeApplication available implicitly" in {
      getConfig("color") mustBe Some("blue")
    }
    "start server at specified port" in {
      port mustBe 1234
    }
  }
}