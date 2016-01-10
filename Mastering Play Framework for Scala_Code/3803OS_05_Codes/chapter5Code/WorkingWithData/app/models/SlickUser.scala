package models

import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._

case class SlickUser(id: Long, loginId: String, name: String)

class SlickUserTable(tag: Tag) extends Table[SlickUser](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def loginId = column[String]("login_id")

  def name = column[String]("name")

  def dob = column[Long]("dob")

  def password = column[String]("password")

  def * = (id, loginId, name) <>(SlickUser.tupled, SlickUser.unapply)
}

object SlickUserHelper {
  val users = TableQuery[SlickUserTable]

  def add(loginId: String,
          password: String,
          name: String = "anonymous",
          dateOfBirth: DateTime): Long = {

    play.api.db.slick.DB.withSession { implicit session =>
      users.map(p => (p.loginId, p.name, p.dob, p.password))
        .returning(users.map(_.id))
        .insert((loginId, name, dateOfBirth.getMillis, password))
    }
  }

  def updatePassword(userId: Long,
                     password: String) = {

    play.api.db.slick.DB.withSession { implicit session =>
      users.filter(_.id === userId)
        .map(u => u.password)
        .update(password)
    }
  }

  def getAll: Seq[SlickUser] = {
    play.api.db.slick.DB.withSession { implicit session =>
      users.run
    }
  }
}
