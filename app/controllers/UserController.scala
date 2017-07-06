package controllers

import javax.inject._

import models.{Customer, CustomerRepo}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.Future

/**
  * Created by mijeongpark on 2017. 7. 5..
  */
@Singleton
class UserController @Inject() (dbConfigProvider: DatabaseConfigProvider,
                                cc: ControllerComponents) extends AbstractController(cc) {

  import scala.concurrent.ExecutionContext.Implicits.global

  val customerRepo = new CustomerRepo(dbConfigProvider)

  implicit val implicitCommandDataWrites = new Writes[Customer] {
    def writes(model: Customer): JsValue = {
      Json.obj(
        "id" -> model.id,
        "name" -> model.name
      )
    }
  }

  def getCustomer(id: Long) = Action.async {
    val future: Future[Option[Customer]] = customerRepo.find(id)
    future.map { model =>
      Ok(Json.toJson(model))
    }
  }

}
