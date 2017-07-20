package services

import java.sql.Timestamp

import common.ThirdParty
import data.ErrorMessage
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
  private val mockThirdParty = mock[ThirdParty]
  private val mockUserService = mock[UserService]

  val service = new UserService(mockAccessTokenRepo, mockCustomerRepo, mockThirdParty)

  private val PHONE_NUMBER = "01028688487"
  private val CUSTOMER_ID = 1L
  private val TIMESTAMP = new Timestamp(System.currentTimeMillis())
  private val AUTH_NUMBER = "1234"


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

  "sendAuthSMS" should {
    "return Left(FAIL_SMS_SEND) if sending sms through 3rd party server is failed" in new WithApplication() {

      mockThirdParty.sendSMS(any, anyString) returns "error"

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.sendAuthSMS(phoneNumber)

      // then
      val expectedResult = Left(ErrorMessage.FAIL_SMS_SEND)

      result mustEqual(expectedResult)
    }

    "return Right if sending sms is successful in case of new customer" in new WithApplication() {

      mockThirdParty.sendSMS(any, anyString) returns "success"
      mockUserService.checkExistingUser(anyString) returns None
      mockCustomerRepo.createCustomer(any) returns 1

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.sendAuthSMS(phoneNumber)

      // then
      val expectedResult = Right("success")

      result mustEqual(expectedResult)
    }
  }

  "return Right if sending sms is successful in case of existing customer" in new WithApplication() {

    val customer = Customer(
      authNumber = Option(AUTH_NUMBER),
      createdAt = TIMESTAMP,
      updatedAt = TIMESTAMP
    )

    mockThirdParty.sendSMS(any, anyString) returns "success"
    mockUserService.checkExistingUser(anyString) returns Some(customer)
    mockCustomerRepo.updateCustomer(any) returns 1

    // given
    val phoneNumber = PHONE_NUMBER

    // when
    val result = service.sendAuthSMS(phoneNumber)

    // then
    val expectedResult = Right("success")

    result mustEqual(expectedResult)
  }
}
