package controllers

import models.{CustomerRepo, AccessTokenRepo, DeviceTokenRepo}

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}

/**
  * Created by Rachel on 2017. 7. 13..
  */
class GetUserDetailSpec extends PlaySpecification with Mockito{

  private val controllerComponents = stubControllerComponents()
  private implicit val ec = controllerComponents.executionContext

  private val mockDeviceTokenRepo = mock[DeviceTokenRepo]
  private val mockAccessTokenRepo = mock[AccessTokenRepo]
  private val mockCustomerRepo = mock[CustomerRepo]

  private val PATH = "/v1/user/detail"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"

  "getUserDetail" should {

    "return error response if access token does not exist" in new WithApplication() {

      mockAccessTokenRepo.findByAccessToken(anyString) returns Left("There is no access token")

      // given
      val controller = new UserController(controllerComponents, mockDeviceTokenRepo, mockAccessTokenRepo, mockCustomerRepo)
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getUserDetail(request)

      // then
      var expectedResult = """{"status":"error","message":"There is no access token"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }
  }

}
