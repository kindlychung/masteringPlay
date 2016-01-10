object Global extends GlobalSettings{
override def onStart(app: play.api.Application) = { 

    val delay = Duration.create(delayForNextExecution(18, 0), TimeUnit.SECONDS) 
    val frequency = Duration.create(24, TimeUnit.HOURS) 

    val subscriptionHandler = Akka.system.actorOf(SubscriptionActor.props) 
    Akka.system(app).scheduler.schedule(delay, frequency, subscriptionHandler, "sendMsgs") 

   //using runnable
    val noDelay = FiniteDuration(0, TimeUnit.SECONDS) 

    Akka.system(app).scheduler.schedule(noDelay, freq, new Runnable { 
      def run() { 
        ResourceHandler.update(resourceName) 
      } 
    })
  } 

  private def delayForNextExecution(hour: Int, minute: Int) = { 
    Seconds.secondsBetween(new DateTime(), nextExecution(hour, minute)).getSeconds 
  } 

  private def nextExecution(hour: Int, minute: Int) = { 
    val next = new DateTime() 
      .withHourOfDay(hour) 
      .withMinuteOfHour(minute) 
      .withSecondOfMinute(0) 
      .withMillisOfSecond(0) 

    if (next.isBeforeNow) { 
      next.plusHours(24) 
    } else { 
      next 
    } 
  }

  override def onError(request: RequestHeader, ex: Throwable) = { 
    log.error(ex)
    InternalServerError(ex.getMessage) 
  }

import play.api.mvc.{Result, RequestHeader,Results}
 override def onBadRequest(request: RequestHeader,
                           error: String): Future[Result] = { 
    Future{ 
      Results.BadRequest(error) 
    } 
  }

override def onRouteRequest(requestHeader: RequestHeader) = { 
    val path = requestHeader.path 

    val pathConditions = path.equals("/") || 
      path.startsWith("/register") || 
      path.startsWith("/login") || 
      path.startsWith("/forgot") 

   if (!pathConditions) { 
      val tokenId = requestHeader.headers.get("Auth-Token") 
      val userId = requestHeader.headers.get("Auth-User") 
      if (tokenId.isDefined && userId.isDefined) { 
        val isValidSession = SessionDetails.validateSession(SessionDetails(userId.get.toLong, tokenId.get)) 
        if (isValidSession) { 
          super.onRouteRequest(request) 
        } 
        else Some(controllers.SessionController.invalidSession) 
      } 
      else { 
        Some(controllers.SessionController.invalidSession) 
      } 
    } 
    else { 
      super.onRouteRequest(request) 
    } 
  }

override def onRouteRequest(requestHeader: RequestHeader) = { 
  val path = requestHeader.path 

  val actualPath = getSupportedPath(path) 
  val customRequestHeader = requestHeader.copy(path = actualPath) 
   
  super.onRouteRequest(customRequestHeader) 
}

override def doFilter(action: EssentialAction): EssentialAction = HeadersFilter.noCache(action) 

override def onRequestCompletion(requestHeader: RequestHeader) {
  if(requestHeader.path.startsWith("/search")){
    //code to persist request parameters, time, etc
  }
}
} 
