package services

import com.google.inject.{Inject, Singleton}

import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.async.Async._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Rachel on 2017. 7. 17..
  */
@Singleton
class SwitcherService @Inject()(ws: WSClient) {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val HOST = "https://io-switcher-dev.switcher.kr"

  def getSwitcherDetail(accessToken: String): Option[List[String]] = {

    val path = "/v3/mobile/user/me"

    val request = async {
      // TODO: separate API call
      val response = await(ws.url(HOST + path).withHttpHeaders(("Authorization","Bearer " + accessToken)).execute("GET"))
      (((response.json \ "data").as[JsValue]) \ "macaddressList").as[List[String]]
    }

    Option(Await.result(request, Duration(3000, "millis")))
  }

}
