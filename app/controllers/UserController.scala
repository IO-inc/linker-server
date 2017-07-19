package controllers

import javax.inject._

import models._
import data._
import services.{UserService, SwitcherService}
import common.Request

import play.api.libs.json.{JsValue, Writes, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

class UserController @Inject()(cc: ControllerComponents,
                               deviceTokenRepo: DeviceTokenRepo,
                               switcherService: SwitcherService,
                               userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

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

  case class response(macAddress: String)

  implicit val responseWrites = new Writes[Linker] {
    def writes(l: Linker) = Json.obj(
      "macAddress" -> l.macAddress
    )
  }

  def createDeviceToken = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Option[JsValue] = request.body.asJson
    val token = (jsonBody.get \ "token").as[String]

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    deviceTokenRepo.createDeviceToken(token, accessToken) match {
      case Right(id) => Future.successful(Ok(Json.toJson(SuccessResponse(data = Option(Json.toJson(CreateDeviceTokenResponse(id)))))))
      case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
    }
  }

  def getUserDetail = Action.async { implicit request: Request[AnyContent] =>

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    userService.checkAccessToken(accessToken) match {
      case Right(accessToken) => {
        val (customer, linkerList) = userService.getUserDetail(accessToken)
        // TODO: replace "Ra2uLPm0CTRQYXdzMglpbs+N7eLv4svrXEjd9YLACEI=" to access token parameter after modifying Switcher Server
        val switcherDetail = switcherService.getSwitcherDetail("Ra2uLPm0CTRQYXdzMglpbs+N7eLv4svrXEjd9YLACEI=")
        val userDetail = GetUserDetailResponse(customer, linkerList.map(m => m.macAddress.get), switcherDetail._1, switcherDetail._2)

        Future.successful(Ok(Json.toJson(SuccessResponse(data = Option(Json.toJson(userDetail))))))
      }
      case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
    }
  }

  def getAuthSMS = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Option[JsValue] = request.body.asJson
    val phoneNumber = (jsonBody.get \ "phoneNumber").as[String]
    val parameterMap = Map("phoneNumber" -> phoneNumber)

    Request.checkRequestParameters(parameterMap) match {
      case Right(_) =>
        Future.successful(Ok(Json.toJson(ErrorResponse(message = "error"))))
      case Left(parameter) =>
        Future.successful(Ok(Json.toJson(ErrorResponse(message = parameter + ErrorMessage.NO_REQUEST_PARAMETER))))
    }
  }

}
