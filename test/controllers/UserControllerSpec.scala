package controllers

import models.{CustomerRepo, AccessTokenRepo, DeviceTokenRepo}

import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.mvc.{ControllerComponents, Results, RequestHeader}
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }

/**
  * Created by Rachel on 2017. 7. 12..
  */
class UserControllerSpec extends PlaySpecification with Mockito {


  private val controllerComponents = stubControllerComponents()
  private implicit val ec = controllerComponents.executionContext

  private val mockDeviceTokenRepo = mock[DeviceTokenRepo]
  private val mockAccessTokenRepo = mock[AccessTokenRepo]
  private val mockCustomerRepo = mock[CustomerRepo]

  private val PATH = "/v1/user/token"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"
  private val TOKEN = "fsdhfhjwehjfh"

  private val ID = 10

  "createDeviceToken" should {

    "return error response if access token does not exist" in new WithApplication() {

      // given
      mockDeviceTokenRepo.createDeviceToken(anyString, anyString) returns Left("There is no access token")

      val controller = new UserController(controllerComponents, mockDeviceTokenRepo, mockAccessTokenRepo, mockCustomerRepo)
      val request = FakeRequest(POST, PATH)
                  .withJsonBody(Json.parse(s"""{ "token": "${TOKEN}" }"""))
                  .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createDeviceToken(request)

      // then
      var expectedResult = """{"status":"error","message":"There is no access token"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return response if device token is successfully created" in new WithApplication() {

      // given
      mockDeviceTokenRepo.createDeviceToken(anyString, anyString) returns Right(ID)

      val controller = new UserController(controllerComponents, mockDeviceTokenRepo, mockAccessTokenRepo, mockCustomerRepo)
      val request = FakeRequest(POST, PATH)
        .withJsonBody(Json.parse(s"""{ "token": "${TOKEN}" }"""))
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createDeviceToken(request)

      // then
      var expectedResult = s"""{"status":"success","data":{"id":${ID}}}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)

    }
  }

}