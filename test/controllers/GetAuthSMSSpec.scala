package controllers

import data.{ErrorMessage}
import models.DeviceTokenRepo
import org.specs2.mock.Mockito
import play.api.libs.json.{Json}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}
import services.{UserService, SwitcherService}

/**
  * Created by Rachel on 2017. 7. 19..
  */
class GetAuthSMSSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockDeviceTokenRepo = mock[DeviceTokenRepo]
  private val mockSwitcherService = mock[SwitcherService]
  private val mockUserService = mock[UserService]

  val controller = new UserController(controllerComponents, mockDeviceTokenRepo, mockSwitcherService, mockUserService)

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
  }

  "return error response if sending sms is failed" in new WithApplication() {

    mockUserService.sendAuthSMS(anyString) returns Left(ErrorMessage.FAIL_SMS_SEND)

    // given
    val fakeJson = Json.obj(
      "phoneNumber" -> PHONE_NUMBER
    )
    val request = FakeRequest(POST, PATH)
      .withJsonBody(fakeJson)

    // when
    val result = controller.getAuthSMS(request)

    // then
    val expectedResult = s"""{"status":"error","message":"${ErrorMessage.FAIL_SMS_SEND}"}"""

    status(result) must beEqualTo(OK)
    contentAsString(result) mustEqual(expectedResult)
  }

  "return response if sending sms is successful" in new WithApplication() {

    mockUserService.sendAuthSMS(anyString) returns Right("success")

    // given
    val fakeJson = Json.obj(
      "phoneNumber" -> PHONE_NUMBER
    )
    val request = FakeRequest(POST, PATH)
      .withJsonBody(fakeJson)

    // when
    val result = controller.getAuthSMS(request)

    // then
    val expectedResult = s"""{"status":"success","data":null}"""

    status(result) must beEqualTo(OK)
    contentAsString(result) mustEqual(expectedResult)
  }

}
