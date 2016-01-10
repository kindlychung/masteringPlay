package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime

case class User(id: Long,
                loginId: String,
                name: String)

object User {
  def add(loginId: String,
          password: String,
          name: String = "anonymous",
          dateOfBirth: DateTime): Option[Long] = {

    val dob = dateOfBirth.getMillis
    DB.withConnection {
      implicit connection =>
        val userId = SQL"""INSERT INTO user(login_id,password,name,
			  dob) VALUES($loginId,$password,$name,$dob)""".executeInsert()

        userId
    }
  }

  def updatePassword(userId: Long,
                     password: String) = {
    DB.withConnection {
      implicit connection =>
        SQL"""UPDATE user SET password=$password WHERE id = $userId""".executeUpdate()
    }
  }

  def userRow: RowParser[User] = {
    get[Long]("id") ~
      get[String]("login_id") ~
      get[String]("name") map {
      case id ~ login_id ~ name => User(id, login_id, name)
    }
  }

  def getAll = {
    DB.withConnection {
      implicit connection =>
        val query = "SELECT id, login_id, name FROM user"
        SQL(query).as(userRow.*)
    }
  }
}