package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import java.security.MessageDigest
import org.joda.time.DateTime
import play.api.libs.json.Json

case class User(id: Option[Long], loginId: String, name: Option[String],
                contactNo: Option[String], dob: Option[Long], address: Option[String])

object User{
    implicit val userWrites = Json.writes[User]
}

trait UserRepo {
  def authenticate(loginId: String, password: String): Boolean

  def create(u: User, host: String, password: String): Option[Long]

  def update(u: User): Boolean

  def findByLogin(loginId: String): Option[User]

  def delete(userId: Long): Boolean

  def find(userId: Long): Option[User]

  def getAll: Seq[User]

  def updateStatus(userId: Long, isActive: Boolean): Int

  def updatePassword(userId: Long, password: String): Int
}