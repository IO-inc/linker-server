package services

import com.google.inject.{Inject, Singleton}
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

  def checkAccessToken(accessToken: String): Either[String, AccessToken] = {
    accessTokenRepo.findByAccessToken(accessToken) match {
      case Some(accessToken) => Right(accessToken)
      case None => Left("There is no access token")
    }
  }

  def getUserDetail(accessToken: AccessToken): (Customer, Option[List[Linker]]) = {

   val userDetailFuture = async {
      val Some(customerDevice) = await(customerRepo.findCustomerDeviceId(accessToken.customerDeviceId.getOrElse(0)))
      val Some(customer) = await(customerRepo.findById(customerDevice.customerId.getOrElse(0)))
      val linkerList = await(customerRepo.findLinkerListByCustomerId(customerDevice.customerId.get))

      (customer, linkerList)
    }

    Await.result(userDetailFuture, Duration(3000, "millis"))
  }

}