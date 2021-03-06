import java.util.Date

import controllers._
import org.specs2.mock.Mockito
import org.specs2.mutable._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future
import models._

import play.api.libs.json._

class UserControllerSpec extends Specification with Mockito {

  "UserController#getUser" should {
    "be valid" in {
      val userRepository = mock[UserRepo]
      val defaultUser = User(Some(1), "loginId", Some("name"), Some("contact_no"), Some(20L), Some("address"))
      userRepository.find(1) returns Option(defaultUser)

      class TestController extends Controller with BaseUserController{
        val userRepo = userRepository
      }

      val controller = new TestController
      val result: Future[Result] = controller.getUser(1L).apply(FakeRequest())
      val userJson: JsValue = contentAsJson(result)

      userJson should be equalTo(Json.toJson(defaultUser))
    }
  }
}