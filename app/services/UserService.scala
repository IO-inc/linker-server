package services

import com.google.inject.{Inject, Singleton}
import data.ErrorMessage
import models._
import scala.async.Async._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Rachel on 2017. 7. 17..
  */
@Singleton
class UserService @Inject()(
                             accessTokenRepo: AccessTokenRepo,
                             customerRepo: CustomerRepo) {

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

    Await.result(userDetailFuture, Duration(3000, "millis"))
  }

}
