class SubscriptionActor extends Actor with ActorLogging { 
  override protected def receive: Receive = { 
    case "sendMsgs" => 
     // fetch subscribed users and the requested content and 
     //then email if content size is greater than minimal limit
     ...
    case x => log.error(new Exception("Invalid msg sent"), x.toString) 
  } 
} 

object SubscriptionActor { 
  def props: Props = Props(classOf[SubscriptionActor]) 
}

