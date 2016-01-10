
import play.api.test._
import org.scalatest._
import play.api.{Play, Application}

class DiffAppTest extends UnitSpec with OneAppPerTest {

  private val colors = Seq("red", "blue", "yellow")

  private var colorCode = 0

  override def newAppForTest(testData: TestData): FakeApplication = {
    val currentCode = colorCode
    colorCode+=1
    FakeApplication(additionalConfiguration = Map("foo" -> "bar",
      "ehcacheplugin" -> "disabled",
      "color" -> colors(currentCode)
    ))
  }

  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  "The OneAppPerTest trait" must {
    "provide a FakeApplication" in {
      app.configuration.getString("color") mustBe Some("red")
    }
    "make another FakeApplication available implicitly" in {
      getConfig("color") mustBe Some("blue")
    }
    "make the third FakeApplication available implicitly" in {
      getConfig("color") mustBe Some("yellow")
    }
  }
}