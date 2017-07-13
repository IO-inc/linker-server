package controllers

import javax.inject._

import models._
import data._

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Writes, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

class UserController @Inject()(cc: ControllerComponents,
                               deviceTokenRepo: DeviceTokenRepo)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // TODO: separate implicit writers
  implicit val implicitCommandDataWrites = new Writes[Customer] {
    def writes(model: Customer): JsValue = {
      Json.obj(
        "id" -> model.id,
        "name" -> model.name,
        "postNo" -> model.postNo
      )
    }
  }

  def createDeviceToken = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Option[JsValue] = request.body.asJson
    val token = (jsonBody.get \ "token").as[String]

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    deviceTokenRepo.createDeviceToken(token, accessToken) match {
      case Right(id) => Future.successful(Ok(Json.toJson(successResponse("success", Option(Json.toJson(CreateDeviceTokenResponse(id)))))))
      case Left(message) => Future.successful(Ok(Json.toJson(errorResponse("error", message))))
    }
  }

}
