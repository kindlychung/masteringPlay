import com.google.inject.{AbstractModule, Guice}
import models.{AnormUserRepo, UserRepo}
import play.api.GlobalSettings

object Global extends GlobalSettings {

  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[UserRepo]).to(classOf[AnormUserRepo])
    }
  })

  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)
}