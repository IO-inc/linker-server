package services

import javax.inject.Inject

import com.google.inject.Singleton
import common.Common
import data.ErrorMessage
import models.LinkerDetailRepo
import models._

import scala.async.Async.{async, await}
import scala.concurrent.Await

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
@Singleton
class LinkerService @Inject()(
                             linkerDetailRepo: LinkerDetailRepo,
                             purchaseRepo: PurchaseRepo,
                             thingRepo: ThingRepo) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def getLinkerDetail(macAddress: String, customerId: Long): Either[String, (LinkerDetail, Option[Customer], Option[Seq[Thing]])] = {

    linkerDetailRepo.findLinkerDetailByMacAddress(macAddress) match {
      case Some(linkerDetailList) => {
        val linkerDetail = linkerDetailList.head
        val linkerId = linkerDetail.linkerId.get

        val customerAndThingDetailFuture = async {
          val customerOption = purchaseRepo.findHost(customerId, linkerId)
          val thingListOption = thingRepo.findByLinkerId(linkerId)

          (linkerDetail, customerOption, thingListOption)
        }

//        Await.result(customerAndThingDetailFuture, Common.COMMON_ASYNC_DURATION)
        Left(ErrorMessage.NO_LINKER)
      }
      case None => Left(ErrorMessage.NO_LINKER)
    }



   /* val linkerDetailFuture = async {
      val linkerDetailOption = linkerDetailRepo.findLinkerDetailByMacAddress(macAddress)
      linkerDetailOption match {
        case None => return Left(ErrorMessage.NO_LINKER)
      }
      val customerOption = purchaseRepo.findHost(customerId, linkerDetailOption.get.head.linkerId.get)
      customerOption match {
        case None => return Left(ErrorMessage.NO_HOST)
      }
//      val thingList =

    }

    Await.result(linkerDetailFuture, Common.COMMON_ASYNC_DURATION)*/

    /*linkerDetailRepo.findLinkerDetailByMacAddress(macAddress) match {
      case Some(linkerDetailList) => {
        val linkerDetail = linkerDetailList.head

        purchaseRepo.findHost(customerId, linkerDetail.id) match {
          case Some(customer) => Left("")
          case None => Left(ErrorMessage.NO_HOST)
        }
      }
      case None => Left(ErrorMessage.NO_LINKER)
    }*/

  }


}
