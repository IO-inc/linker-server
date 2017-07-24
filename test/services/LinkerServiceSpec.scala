package services

import java.sql.Timestamp

import data.ErrorMessage
import models.{LinkerDetail, LinkerDetailRepo, PurchaseRepo, ThingRepo}
import org.specs2.mock.Mockito
import play.api.test.{PlaySpecification, WithApplication}

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
class LinkerServiceSpec extends PlaySpecification with Mockito {

  import scala.concurrent.ExecutionContext.Implicits.global

  val mockLinkerDetailRepo = mock[LinkerDetailRepo]
  val mockPurchaseRepo = mock[PurchaseRepo]
  val mockThingRepo = mock[ThingRepo]

  val service = new LinkerService(mockLinkerDetailRepo, mockPurchaseRepo, mockThingRepo)

  private val MAC_ADDRESS = "10:12:34:12:34:11"
  private val CUSTOMER_ID = 1L
  private val LINKER_ID = 2L
  private val LINKER_DETAIL_ID = 3L
  private val TIMESTAMP = new Timestamp(System.currentTimeMillis())

  "getLinkerDetail" should {

    "return Left(error) if there is no linker detail by mac address" in new WithApplication() {

      mockLinkerDetailRepo.findLinkerDetailByMacAddress(anyString) returns None

      // given
      val macAddress = MAC_ADDRESS
      val customerId = CUSTOMER_ID

      // when
      val result = service.getLinkerDetail(macAddress, customerId)

      // then
      val expectedResult = Left(ErrorMessage.NO_LINKER)

      result mustEqual(expectedResult)
    }

    /*"return Left(error) if there is no host by customer id and linker id" in new WithApplication() {

      val linkerDetailList = Seq(
        LinkerDetail(
          id = LINKER_DETAIL_ID,
          linkerId = Some(LINKER_ID),
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )
      mockLinkerDetailRepo.findLinkerDetailByMacAddress(anyString) returns Some(linkerDetailList)
      mockPurchaseRepo.findHost(anyLong, anyLong) returns None

      // given
      val macAddress = MAC_ADDRESS
      val customerId = CUSTOMER_ID

      // when
      val result = service.getLinkerDetail(macAddress, customerId)

      // then
      val expectedResult = Left(ErrorMessage.NO_HOST)

      result mustEqual(expectedResult)
    }*/

  }

}
