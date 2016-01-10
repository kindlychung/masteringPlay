package models

import java.security.MessageDigest
import javax.inject.Singleton

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._
import play.api.libs.json._

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

