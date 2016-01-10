package controllers

import play.api.db.DB
import play.api.mvc._
import play.api.Play.current

object Application extends Controller {

  def index = Action {
    Ok(views.html.main())
  }

  def fetchDBUser = Action {
    var result = "DB User:"
    val conn = DB.getConnection()
    try{
      val rs = conn.createStatement().executeQuery("SELECT USER()")
      while (rs.next()) {
        result += rs.getString(1)
      }
    } finally {
      conn.close()
    }
    Ok(result)
  }

  /* without try blocks
  def fetchDBUser = Action {
    var result = "DB User:"
    DB.withConnection { conn =>
      val rs = conn.createStatement().executeQuery("SELECT USER()")
      while (rs.next()) {
        result += rs.getString(1)
      }
    }
    Ok(result)
  }*/

}
