import java.util.Date

import controllers.UserController
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future
import org.mockito.Mockito._
import models._
import controllers._

import play.api.libs.json._
  
class UserControllerTest extends PlaySpec with Results with MockitoSugar { 

  "UserController#getUser" should {
    "be valid" in {
      val userRepository = mock[UserRepo]
      val defaultUser = User(Some(1), "loginId", Some("name"), Some("contact_no"), Some(20L), Some("address"))
      when(userRepository.find(1)) thenReturn Option(defaultUser)

      class TestController extends Controller with BaseUserController{
        val userRepo = userRepository
      }

      val controller = new TestController
      val result: Future[Result] = controller.getUser(1L).apply(FakeRequest())
      
      val userJson: JsValue = contentAsJson(result)
      userJson mustBe Json.toJson(defaultUser)
    }
  }
}