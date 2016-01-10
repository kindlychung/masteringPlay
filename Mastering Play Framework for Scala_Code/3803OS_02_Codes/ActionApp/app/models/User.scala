package models

import scala.util.Random

case class User(name: String, email: String) {
  def save: Long = {
    Random.nextLong()
  }
}

object User{
  val existingUsers = Seq(User("dummy", "user@email.com"))
  def findById(id: String) = {
    existingUsers.find(user => user.email == id)
  }
}
