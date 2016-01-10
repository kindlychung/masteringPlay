package models

import java.security.MessageDigest

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current

object AnormUserRepo extends UserRepo {

  private def simple = {
    get[Option[Long]]("id") ~
      get[String]("login_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("contact_no") ~
      get[Option[Long]]("dob") ~
      get[Option[String]]("address") map {
      case id ~ login_id ~ name ~ contact_no ~ dob ~ address =>
        User(id, login_id, name, contact_no, dob, address)
    }
  }

  private def md5Hash(secret: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    val byteData = secret.getBytes("UTF-8")
    md5.digest(byteData).map("%02x".format(_)).mkString
  }

  def authenticate(loginId: String, password: String): Boolean = {
    DB.withConnection {
      implicit connection =>
        val pwd = SQL("SELECT password FROM user WHERE login_id={loginId} and is_active=true"
        ).on(
            'loginId -> loginId
          ).as(scalar[String].singleOpt)
        if (pwd.isDefined) {
          pwd.get.equals(md5Hash(password))
        } else false
    }
  }

  def create(u: User, host: String, password: String): Option[Long] = {

    DB.withConnection {
      implicit connection =>
        val userId = SQL("INSERT INTO user(login_id,password,name,contact_no,dob,address,is_active) " +
          "VALUES({loginId},{password},{name},{contactNo},{dob},{address},{isActive})"
        ).on(
            'loginId -> u.loginId,
            'password -> md5Hash(password),
            'name -> u.name,
            'contactNo -> u.contactNo,
            'dob -> u.dob,
            'address -> u.address,
            'isActive -> true
          ).executeInsert()
        userId
    }
  }

  def update(u: User): Boolean = {
    var updated = false
    DB.withConnection {
      implicit connection =>
        val rowsUpdated = SQL("UPDATE user SET " +
          "name={name},contact_no={contactNo},dob={dob},address={address} WHERE id={userId}")
          .on(
            'userId -> u.id,
            'name -> u.name,
            'contactNo -> u.contactNo,
            'dob -> u.dob,
            'address -> u.address
          ).executeUpdate()
        if (rowsUpdated > 0) {
          updated = true
        }
        updated
    }
  }

  def findByLogin(loginId: String): Option[User] = {
    DB.withConnection {
      implicit connection =>
        val user = SQL("SELECT id,login_id,name,contact_no,dob,address" +
          " FROM user WHERE login_id={loginId}")
          .on(
            'loginId -> loginId
          ).as(simple.singleOpt)
        user
    }
  }

  def delete(userId: Long): Boolean = {
    var deleted = false
    DB.withConnection {
      implicit connection =>
        val rows = SQL("DELETE FROM user WHERE id={userId}")
          .on(
            'userId -> userId
          ).executeUpdate()
        if (rows > 0) {
          deleted = true
        }
    }
    deleted
  }

  def find(userId: Long): Option[User] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT id,login_id,name,contact_no,dob,address" +
          " FROM user WHERE id={userId}")
          .on(
            'userId -> userId
          ) as (simple.singleOpt)
    }
  }

  def getAll: Seq[User] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT id,login_id,name,contact_no,dob,address" +
          " FROM user")
          .as(simple.*)
    }
  }

  def newUser(loginId: String): Boolean = {
    if (findByLogin(loginId).isEmpty) {
      true
    }
    else
      false
  }


  def updateStatus(userId: Long, isActive: Boolean): Int = {
    DB.withConnection {
      implicit connection =>
        val query = """UPDATE user SET is_active={isActive} WHERE id={userId}"""
        SQL(query)
          .on(
            'userId -> userId,
            'isActive -> isActive
          ).executeUpdate()
    }
  }

  def updatePassword(userId: Long, password: String): Int = {
    DB.withConnection {
      implicit connection =>
        val query = """UPDATE user SET password={password} WHERE id={userId}"""
        SQL(query)
          .on(
            'userId -> userId,
            'password -> md5Hash(password)
          ).executeUpdate()
    }
  }
}
