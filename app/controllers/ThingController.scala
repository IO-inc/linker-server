package controllers

import javax.inject.Inject

import common.Request
import data._
import play.api.libs.json.{Json}
import play.api.mvc._
import services.{SwitcherService, ThingService}

import scala.concurrent.{Future, ExecutionContext}

/**
  * Created by Rachel on 2017. 7. 30..
  */
class ThingController @Inject()(cc: ControllerComponents,
                                thingService: ThingService,
                                switcherService: SwitcherService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getThingDetail(`type`: String, macAddress: String) = Action.async { implicit request: Request[AnyContent] =>

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    val parameterMap = Map(
      "macAddress" -> macAddress,
      "type" -> `type`,
      "accessToken" -> accessToken)

    Request.checkRequestParameters(parameterMap) match {
      case Right(_) => {

        thingService.getThingCommandListByType(`type`, macAddress, "RESERVATION") match {
          case Right((thing, commandList)) => {
            val switcherDetail = switcherService.getSwitcherDetail(macAddress, accessToken)
            println("[switcherDetail] " + switcherDetail)

            Future.successful(Ok(Json.toJson(ErrorResponse(message = ""))))
          }
          case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
        }
      }
      case Left(parameter) =>
        Future.successful(Ok(Json.toJson(ErrorResponse(message = parameter + ErrorMessage.NO_REQUEST_PARAMETER))))
    }


  }

}
