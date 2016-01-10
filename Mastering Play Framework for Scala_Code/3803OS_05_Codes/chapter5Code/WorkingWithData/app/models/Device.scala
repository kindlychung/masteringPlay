package models

import play.api.Play.current
import play.api.libs.json.Writes._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.LastError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Device {

  def db = ReactiveMongoPlugin.db

  def collection = db.collection[JSONCollection]("devices")

  def registerDevice(deviceId: String,
                     ownerId: String,
                     deviceDetails: JsObject): Future[LastError] = {

    var newDevice = Json.obj("deviceId" -> deviceId, "ownerId" -> ownerId.trim)
    val config = (deviceDetails \ "configuration").asOpt[JsObject]
    val metadata = (deviceDetails \ "metadata").asOpt[JsObject]
    if (!config.isDefined)
      newDevice = newDevice ++ Json.obj("configuration" -> Json.parse("{}"))
    if (!metadata.isDefined)
      newDevice = newDevice ++ Json.obj("metadata" -> Json.parse("{}"))

    collection.insert[JsValue](newDevice)
  }

  def fetchDevice(deviceId: String): Future[Option[JsObject]] = {
    val findDevice = Json.obj("deviceId" -> deviceId)
    collection.find(findDevice).one[JsObject]
  }

  def removeDeviceById(deviceId: String): Future[LastError] = {
    val removeDoc = Json.obj("deviceId" -> deviceId)
    collection.remove[JsValue](removeDoc)
  }

  def updateConfiguration(deviceId: String,
                          ownerId: String,
                          updatedField: JsObject) = {
    val property = updatedField.keys.head
    val propertyValue = updatedField.values.head
    val toUpdate = Json.obj(s"configuration.$property" -> propertyValue)
    val setData = Json.obj("$set" -> toUpdate)
    val documentToUpdate = Json.obj("deviceId" -> deviceId, "ownerId" -> ownerId)
    collection.update[JsValue, JsValue](documentToUpdate, setData)
  }

}