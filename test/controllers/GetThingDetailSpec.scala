package controllers

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test.PlaySpecification
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


}
