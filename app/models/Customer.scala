package models

import Tables._

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

class CustomerRepo @Inject()(
                              protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val Customers = TableQuery[CustomersTable]
  private[models] val AccessTokens = TableQuery[AccessTokensTable]
  private[models] val Linkers = TableQuery[LinkersTable]
  private[models] val CustomerDevices = TableQuery[CustomerDevicesTable]
  private[models] val Purchases = TableQuery[PurchasesTable]
  private[models] val PurchaseOwners = TableQuery[PurchaseOwnersTable]

  private def __findByAccessToken(accessToken: String): DBIO[Option[AccessToken]] =
    AccessTokens.filter(_.accessToken === accessToken).result.headOption

  private def _findById(id: Long): DBIO[Option[Customer]] =
    Customers.filter(_.id === id).result.headOption

  def findById(id: Long): Future[Option[Customer]] = {
    db.run(_findById(id))
  }

  def findCustomerDeviceId(id: Long): Future[Option[CustomerDevice]] = {
    db.run(CustomerDevices.filter(_.id === id).result.headOption)
  }

  def findLinkerListByCustomerId(customerId: Long): Seq[Linker] = {

    val query = for {
      ((purchaseOwner, purchase), linkers) <- PurchaseOwners.filter(_.customerId === customerId).join(Purchases).on(_.purchaseId === _.id).join(Linkers).on(_._2.linkerId === _.id)
    } yield (linkers)

    Await.result(db.run(query.result), Duration(3000, "millis"))
  }
}

