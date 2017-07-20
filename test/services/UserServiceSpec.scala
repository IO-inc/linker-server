package services

import java.sql.Timestamp

import models.{AccessTokenRepo, Customer, CustomerRepo}
import org.specs2.mock.Mockito
import play.api.test.{PlaySpecification, WithApplication}

import scala.concurrent.Future

/**
  * Created by mijeongpark on 2017. 7. 20..
  */
class UserServiceSpec extends PlaySpecification with Mockito {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val mockAccessTokenRepo = mock[AccessTokenRepo]
  private val mockCustomerRepo = mock[CustomerRepo]

  val service = new UserService(mockAccessTokenRepo, mockCustomerRepo)

  private val PHONE_NUMBER = "01028688487"
  private val CUSTOMER_ID = 1L
  private val TIMESTAMP = new Timestamp(System.currentTimeMillis())


  "checkExistingUser" should {

    "return None if there is no customer by phone number" in new WithApplication() {

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(None)

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.checkExistingUser(phoneNumber)

      // then
      val expectedResult = None

      result mustEqual(expectedResult)
    }

    "return Some(customer) there is customer by phone number" in new WithApplication() {

      val customer = Customer(
        CUSTOMER_ID,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        TIMESTAMP,
        TIMESTAMP,
        None
      )

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(Option(customer))

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.checkExistingUser(phoneNumber)

      // then
      val expectedResult = Some(customer)

      result mustEqual(expectedResult)
    }
  }

}
