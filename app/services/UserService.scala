package services

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import common.{Common, ThirdParty}
import data.ErrorMessage
import models._

import scala.async.Async._
import scala.concurrent.{Await}

/**
  * Created by Rachel on 2017. 7. 17..
  */
@Singleton
class UserService @Inject()(
                             accessTokenRepo: AccessTokenRepo,
                             customerRepo: CustomerRepo,
                             thirdParty: ThirdParty) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def checkAccessToken(accessToken: String): Either[String, AccessToken] = {
    accessTokenRepo.findByAccessToken(accessToken) match {
      case Some(accessToken) => Right(accessToken)
      case None => Left(ErrorMessage.NO_ACCESS_TOKEN)
    }
  }

  def getUserDetail(accessToken: AccessToken): (Customer, Seq[Linker]) = {

   val userDetailFuture = async {
      val Some(customerDevice) = await(customerRepo.findCustomerDeviceId(accessToken.customerDeviceId.getOrElse(0)))
      val Some(customer) = await(customerRepo.findById(customerDevice.customerId.getOrElse(0)))
      val linkerList = customerRepo.findLinkerListByCustomerId(customerDevice.customerId.get)

      (customer, linkerList)
    }

    Await.result(userDetailFuture, Common.COMMON_ASYNC_DURATION)
  }

  def sendAuthSMS(phoneNumber: String): Either[String, String] = {

    val authNumber = Common.createAuthSMSNumber
    val now = new Timestamp(System.currentTimeMillis())

    thirdParty.sendSMS(List(phoneNumber), authNumber) match {
      case "success" => {
        checkExistingUser(phoneNumber) match {
          case Some(customer) => {
            customer.authNumber = Option(authNumber)
            customerRepo.updateCustomer(customer)
          }
          case None => {
            val customer = Customer(
              phoneNumber = Option(phoneNumber),
              authNumber = Option(authNumber),
              createdAt = now,
              updatedAt = now
            )
            customerRepo.createCustomer(customer)
          }
        }
        Right("success")
      }
      case _ => Left(ErrorMessage.FAIL_SMS_SEND)
    }
  }

  def checkExistingUser(phoneNumber: String): Option[Customer] = {
    Await.result(customerRepo.findByPhoneNumber(phoneNumber), Common.COMMON_ASYNC_DURATION)
  }

}
