class CountControllerSpec extends PlaySpecification with BeforeExample {

  override def before: Any = {
    TestHelper.clearDB
  }

  """Counter query""" should {
    """fetch count of visits grouped by browser names""" in new WithApplication {
      TestHelper.post("/sampleData.json")
      val queryString =
        """applicationId=39&perspective=browser&from=1389949200000&till=1399145400000""".stripMargin
      val request = FakeRequest(GET, "/query/count?" + queryString)
      val response = route(request)
      val result = response.get
      status(result) must equalTo(OK)
      contentAsJson(result) must equalTo(TestHelper.browserCount)
    }
  }
