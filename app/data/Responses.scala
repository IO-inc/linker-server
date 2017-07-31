package data

import common.Common
import models.{Command, Customer, Thing}
import play.api.libs.json._

/**
  * Created by mijeongpark on 2017. 7. 10..
  */
case class SuccessResponse(status: String = Common.SUCCESS, data: Option[JsValue] = None)
case class ErrorResponse(status: String = "error", message: String)
case class CreateDeviceTokenResponse(id: Long)
case class GetUserDetailResponse(
                                customer: Customer,
                                linkerList: Seq[String],
                                switcherList: List[String],
                                requestList: List[String]
                                )
case class GetAuthInfoResponse(accessToken: String)
case class GetLinkerDetailResponse(
                                  active: Boolean,
                                  host: Host,
                                  things: Seq[Thing]
                                  )
case class Host(phoneNumber: String, name: String)
case class SwitcherDetail(
                         status: String,
                         switcherId: Long,
                         modelCode: String,
                         macAddress: String,
                         startAt: String,
                         endAt: String,
                         paymentPlanId: Int,
                         firstPayAt: String,
                         nextPayAt: String,
                         shareCode: String,
                         hashingShareCode: String,
                         pKey: String,
                         freeYN: String,
                         userName: String,
                         customerId: Long,
                         duration: String,
                         `type`: String,
                         currentTime: String)
case class GetThingDetailResponse(
                                 switcherDetail: SwitcherDetail,
                                 commands: Seq[Command],
                                 thing: Thing)

object SuccessResponse {

  implicit val implicitSuccessResponse = new Writes[SuccessResponse] {
    def writes(response: SuccessResponse): JsValue = {
      Json.obj(
        "status" -> response.status,
        "data" -> response.data
      )
    }
  }
}

object ErrorResponse {

  implicit val implicitErrorResponse = new Writes[ErrorResponse] {
    def writes(response: ErrorResponse): JsValue = {
      Json.obj(
        "status" -> response.status,
        "message" -> response.message
      )
    }
  }
}

object CreateDeviceTokenResponse {

  implicit val implicitCreateDeviceTokenResponse = new Writes[CreateDeviceTokenResponse] {
    def writes(response: CreateDeviceTokenResponse): JsValue = {
      Json.obj(
        "id" -> response.id
      )
    }
  }
}

object GetUserDetailResponse {

  implicit val implicitGetUserDetailResponse = new Writes[GetUserDetailResponse] {
    def writes(response: GetUserDetailResponse): JsValue = {
      Json.obj(
        "id" -> response.customer.id,
        "phoneNumber" -> response.customer.phoneNumber,
        "name" -> response.customer.name,
        "authNumber" -> response.customer.authNumber,
        "postNo" -> response.customer.postNo,
        "addr1" -> response.customer.addr1,
        "addr2" -> response.customer.addr2,
        "email" -> response.customer.email,
        "createdAt" -> response.customer.createdAt,
        "updatedAt" -> response.customer.updatedAt,
        "linkerList" -> response.linkerList,
        "switcherList" -> response.switcherList,
        "requestList" -> response.requestList
      )
    }
  }
}

object GetAuthInfoResponse {

  implicit val implicitGetAuthInfoResponse = new Writes[GetAuthInfoResponse] {
    def writes(response: GetAuthInfoResponse): JsValue = {
      Json.obj(
        "accessToken" -> response.accessToken
      )
    }
  }
}

object GetLinkerDetailResponse {

  implicit val implicitGetLinkerDetailResponse = new Writes[GetLinkerDetailResponse] {
    def writes(response: GetLinkerDetailResponse): JsValue = {
      Json.obj(
        "active" -> response.active,
        "host" -> Json.obj(
          "phoneNumber" -> response.host.phoneNumber,
          "name" -> response.host.name
        ),
        "things" -> JsArray(response.things.map(thing =>
        Json.obj(
          "macAddress" -> thing.macAddress,
          "type" -> thing.`type`,
          "active" -> thing.active
        )))
      )
    }
  }
}

object SwitcherDetail {

  implicit val implicitSwitcherDetail = new Reads[SwitcherDetail] {
    def reads(response: JsValue): JsResult[SwitcherDetail] = {
      for {
        status <- (response \ "status").validate[String]
        switcherId <- (response \ "switcherId").validate[Long]
        modelCode <- (response \ "modelCode").validate[String]
        macAddress <- (response \ "macaddress").validate[String]
        startAt <- (response \ "startDate").validate[String]
        endAt <- (response \ "endDate").validate[String]
        paymentPlanId <- (response \ "payPlanCode").validate[Int]
        firstPatyAt <- (response \ "firstPayDate").validate[String]
        nextPayAt <- (response \ "nextPayDate").validate[String]
        shareCode <- (response \ "shareCode").validate[String]
        hashingShareCode <- (response \ "hashingShareCode").validate[String]
        pKey <- (response \ "pKey").validate[String]
        freeYN <- (response \ "freeYN").validate[String]
        userName <- (response \ "username").validate[String]
        customerId <- (response \ "customerId").validate[Long]
        duration <- (response \ "duration").validate[String]
        `type` <- (response \ "type").validate[String]
        currentTime <- (response \ "currentTime").validate[String]

      } yield SwitcherDetail(
        status, switcherId, modelCode,
        macAddress, startAt, endAt,
        paymentPlanId, firstPatyAt, nextPayAt,
        shareCode, hashingShareCode, pKey,
        freeYN, userName, customerId,
        duration, `type`, currentTime)
    }
  }
}

object GetThingDetailResponse {

  implicit val implicitGetThingDetailResponse = new Writes[GetThingDetailResponse] {
    def writes(response: GetThingDetailResponse): JsValue = {
      Json.obj(
        "status" -> response.switcherDetail.status,
        "switcherId" -> response.switcherDetail.switcherId,
        "modelCode" -> response.switcherDetail.modelCode,
        "macAddress" -> response.switcherDetail.macAddress,
        "startAt" -> response.switcherDetail.startAt,
        "endAt" -> response.switcherDetail.endAt,
        "paymentPlanId" -> response.switcherDetail.paymentPlanId,
        "firstPayAt" -> response.switcherDetail.firstPayAt,
        "nextPayAt" -> response.switcherDetail.nextPayAt,
        "shareCode" -> response.switcherDetail.shareCode,
        "hashingShareCode" -> response.switcherDetail.hashingShareCode,
        "pKey" -> response.switcherDetail.pKey,
        "freeYN" -> response.switcherDetail.freeYN,
        "userName" -> response.switcherDetail.userName,
        "customerId" -> response.switcherDetail.customerId,
        "duration" -> response.switcherDetail.duration,
        "type" -> response.switcherDetail.`type`,
        "currentTime" -> response.switcherDetail.currentTime,
        "commands" -> JsArray(response.commands.map(command =>
        Json.obj(
          "command" -> command.command,
          "detail" -> command.detail
        ))),
        "active" -> response.thing.active
      )
    }
  }
}




