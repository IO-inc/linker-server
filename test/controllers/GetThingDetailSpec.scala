package controllers

import java.sql.Timestamp
import common.Common
import data.{ErrorMessage, SwitcherDetail}
import models.{Command, Thing}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import org.specs2.mock.Mockito
import services.{SwitcherService, ThingService}

/**
  * Created by Rachel on 2017. 7. 30..
  */
class GetThingDetailSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockThingService = mock[ThingService]
  private val mockSwitcherService = mock[SwitcherService]

  val controller = new ThingController(controllerComponents, mockThingService, mockSwitcherService)

  private val PATH = "/v1/user/linkers"
  private val ACCESS_TOKEN = "Ra2uLPm0CTRQYXdzMglpbs+N7eLv4svrXEjd9YLACEI="

  private val MAC_ADDRESS = "F0:DF:54:57:1D:AA"
  private val `TYPE` = "switcher"
  private val COMMAND = "RESERVATION"
  private val THING_ID = 1L
  private val ACTIVE = true
  private val TIMESTAMP =  new Timestamp(System.currentTimeMillis())
  private val COMMAND_ID = 2L
  private val STATUE = "4"
  private val SWITCHER_ID = 3L
  private val MODEL_CODE = "0003"
  private val TIMESTAMP_STRING = "2016-11-14"
  private val PAYMENT_PLAN_ID = 3
  private val SHARE_CODE = "1123"
  private val HASHING_SHARE_CODE = "2345"
  private val PKEY = "698015E6"
  private val FREE_YN = "N"
  private val USER_NAME = "박미정"
  private val CUSTOMER_ID = 4L
  private val DURATION = "1"
  private val SWITCHER_TYPE = "P"


  "getThingDetail" should {

    "return error response if type is empty" in new WithApplication() {

      // given
      val `type` = ""
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getThingDetail(`type`, macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"type${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if mac address is empty" in new WithApplication() {

      // given
      val `type` = `TYPE`
      val macAddress = ""
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getThingDetail(`type`, macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"macAddress${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if the type of thing is not matched" in new WithApplication() {

      mockThingService.getThingCommandListByType(anyString, anyString) returns Left(ErrorMessage.INVALID_THING_TYPE)

      // given
      val `type` = `TYPE`
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getThingDetail(`type`, macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"${ErrorMessage.INVALID_THING_TYPE}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if there is no thing by mac address" in new WithApplication() {

      mockThingService.getThingCommandListByType(anyString, anyString) returns Left(ErrorMessage.NO_THING)

      // given
      val `type` = `TYPE`
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getThingDetail(`type`, macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"${ErrorMessage.NO_THING}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return response if there is command list for thing by mac address" in new WithApplication() {

      val thing = Thing(
        id = THING_ID,
        `type` = Some(`TYPE`),
        macAddress = Some(MAC_ADDRESS),
        active = Some(ACTIVE),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val commandList = Seq(
        Command(
          id = COMMAND_ID,
          thingId = Some(THING_ID),
          command = Some(COMMAND),
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      val switcherDetail = SwitcherDetail(
        STATUE,
        SWITCHER_ID,
        MODEL_CODE,
        MAC_ADDRESS,
        TIMESTAMP_STRING,
        TIMESTAMP_STRING,
        PAYMENT_PLAN_ID,
        TIMESTAMP_STRING,
        TIMESTAMP_STRING,
        SHARE_CODE,
        HASHING_SHARE_CODE,
        PKEY,
        FREE_YN,
        USER_NAME,
        CUSTOMER_ID,
        DURATION,
        SWITCHER_TYPE,
        TIMESTAMP_STRING
      )

      mockThingService.getThingCommandListByType(anyString, anyString) returns Right((thing, commandList))
      mockSwitcherService.getSwitcherDetail(anyString, anyString) returns switcherDetail

      // given
      val `type` = `TYPE`
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getThingDetail(`type`, macAddress)(request)

      // then
      var expectedResult = s"""{"status":"${Common.SUCCESS}","data""""

      status(result) must beEqualTo(OK)
      contentAsString(result) must contain(expectedResult)
    }

  }
}
