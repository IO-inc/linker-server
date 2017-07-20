package controllers

import common.ThirdParty
import data.{ErrorMessage, ErrorResponse}
import models.DeviceTokenRepo
import org.specs2.mock.Mockito
import play.api.libs.json.{JsValue, Writes, Json}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}
import services.{UserService, SwitcherService}

/**
  * Created by Rachel on 2017. 7. 19..
  */
class GetAuthSMSSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val ec = controllerComponents.executionContext

  private val mockDeviceTokenRepo = mock[DeviceTokenRepo]
  private val mockSwitcherService = mock[SwitcherService]
  private val mockUserService = mock[UserService]
  private val mockThirdParty = mock[ThirdParty]

  val controller = new UserController(controllerComponents, mockDeviceTokenRepo, mockSwitcherService, mockThirdParty, mockUserService)

  private val PATH = "/v1/user/auth"

  private val PHONE_NUMBER = "01028688487"

  "getAuthSMS" should {

    "return error response if phone number is empty" in new WithApplication() {

      // given
      val fakeJson = Json.obj(
        "phoneNumber" -> ""
      )
      val request = FakeRequest(POST, PATH)
          .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthSMS(request)

      // then
      val expectedResult = s"""{"status":"error","message":"phoneNumber${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if sending auth sms is failed" in new WithApplication() {

      // given
      val fakeJson = Json.obj(
        "phoneNumber" -> PHONE_NUMBER
      )
      val request = FakeRequest(POST, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthSMS(request)

      // then
      val expectedResult = s"""{"status":"error","message":"phoneNumber${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }
  }
}
