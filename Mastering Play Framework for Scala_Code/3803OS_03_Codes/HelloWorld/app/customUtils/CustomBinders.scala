package customUtils

import java.util.UUID

import models.SearchParams
import play.api.mvc.{QueryStringBindable, PathBindable}

object CustomBinders {
  implicit def uuidPathBinder = new PathBindable[UUID] {
    override def bind(key: String, value: String): Either[String, UUID] = {
      Right(UUID.fromString(value))
    }

    override def unbind(key: String, id: UUID): String = {
      id.toString
    }
  }

  implicit def searchParamQueryStringBinder(implicit doubleBinder: QueryStringBindable[Double],
                                            stringBinder: QueryStringBindable[String]) = {
    new QueryStringBindable[SearchParams] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SearchParams]] = {
        for {
          latitude <- doubleBinder.bind("latitude", params)
          longitude <- doubleBinder.bind("longitude", params)
          category <- stringBinder.bind("category", params)
        } yield {
          (latitude, longitude, category) match {
            case (Right(lat), Right(long), Right(cat)) => Right(SearchParams(cat, lat, long))
            case _ => Left("Invalid SearchParams")
          }
        }
      }

      override def unbind(key: String, params: SearchParams): String = {
        val lat = doubleBinder.unbind("latitude", params.latitude)
        val long = doubleBinder.unbind("longitude", params.longitude)
        val cat = stringBinder.unbind("category", params.category)

        s"$lat&$long&$cat"
      }
    }
  }
}
