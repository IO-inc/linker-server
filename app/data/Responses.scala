package data

import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Created by mijeongpark on 2017. 7. 10..
  */
case class successResponse(status: String = "success", data: Option[JsValue])
case class errorResponse(status: String = "error", message: String)
case class CreateDeviceTokenResponse(id: Long)

object successResponse {

  implicit val implicitSuccessResponse = new Writes[successResponse] {
    def writes(response: successResponse): JsValue = {
      Json.obj(
        "status" -> response.status,
        "data" -> response.data
      )
    }
  }
}

object errorResponse {

  implicit val implicitErrorResponse = new Writes[errorResponse] {
    def writes(response: errorResponse): JsValue = {
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






