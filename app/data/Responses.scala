package data

import java.sql.Timestamp

import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Created by mijeongpark on 2017. 7. 10..
  */
case class SuccessResponse(status: String = "success", data: Option[JsValue])
case class ErrorResponse(status: String = "error", message: String)
case class CreateDeviceTokenResponse(id: Long)
case class GetUserDetailResponse(
                                id: Long,
                                name: Option[String],
                                phoneNumber: Option[String],
                                authNumber: Option[String],
                                postNo: Option[String],
                                addr1: Option[String],
                                addr2: Option[String],
                                email: Option[String],
                                createdAt: Timestamp,
                                updatedAt: Timestamp,
                                linkerList: Option[List[String]],
                                switcherList: Option[List[String]],
                                requestList: Option[List[String]]
                                )

object successResponse {

  implicit val implicitSuccessResponse = new Writes[SuccessResponse] {
    def writes(response: SuccessResponse): JsValue = {
      Json.obj(
        "status" -> response.status,
        "data" -> response.data
      )
    }
  }
}

object errorResponse {

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
        "id" -> response.id,
        "phoneNumber" -> response.phoneNumber,
        "name" -> response.name,
        "authNumber" -> response.authNumber,
        "postNo" -> response.postNo,
        "addr1" -> response.addr1,
        "addr2" -> response.addr2,
        "email" -> response.email,
        "createdAt" -> response.createdAt,
        "updatedAt" -> response.updatedAt,
        "linkerList" -> response.linkerList,
        "switcherList" -> response.switcherList,
        "requestList" -> response.requestList
      )
    }
  }
}






