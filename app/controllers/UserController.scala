package controllers

import javax.inject._

import models.{CustomerRepo, Customer}

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Writes, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

case class RequestInfo(url: String, method: String, headers: Map[String, String], body: String)

class UserController @Inject()(dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val customerRepo = new CustomerRepo(dbConfigProvider)

  // TODO: saperate implicit writers
  implicit val implicitCommandDataWrites = new Writes[Customer] {
    def writes(model: Customer): JsValue = {
      Json.obj(
        "id" -> model.id,
        "name" -> model.name,
        "postNo" -> model.postNo
      )
    }
  }

  def getCustomer(id: Long) = Action.async {
    val future: Future[Option[Customer]] = customerRepo.findById(id)
    future.map { model =>
      Ok(Json.toJson(model))
    }
  }

  def createDeviceToken = Action.async { implicit request: Request[AnyContent] =>

    println("request body: " + request.body)
    Future.successful(Ok("fail"))
  }

}
