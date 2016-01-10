

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

class AppSpec extends PlaySpecification {
	val app: FakeApplication =
    FakeApplication(
      withRoutes = TestRoute
    )

    "run in firefox" in new WithBrowser(webDriver = WebDriverFactory(FIREFOX), app = app) {
     browser.goTo("/testing")
     browser.$("#title").getTexts().get(0) must equalTo("Test Page")

     browser.$("b").click()

     browser.$("#title").getTexts().get(0) must equalTo("testing")
    }
}