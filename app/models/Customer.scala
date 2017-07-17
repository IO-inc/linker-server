package models

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

class CustomerRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

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

  private[models] class CustomersTable(tag: Tag) extends Table[Customer](tag, "Customers") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def phoneNumber = column[Option[String]]("phoneNumber")
    def name = column[Option[String]]("name")
    def authNumber = column[Option[String]]("authNumber")
    def postNo = column[Option[String]]("postNo")
    def addr1 = column[Option[String]]("addr1")
    def addr2 = column[Option[String]]("addr2")
    def email = column[Option[String]]("email")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, phoneNumber, name, authNumber, postNo, addr1, addr2, email, createdAt, updatedAt, deletedAt) <> (Customer.tupled, Customer.unapply)

  }

  private[models] class AccessTokensTable(tag: Tag) extends Table[AccessToken](tag, "AccessTokens") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def accessToken = column[Option[String]]("accessToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, accessToken, createdAt, updatedAt, deletedAt) <> (AccessToken.tupled, AccessToken.unapply)

  }

  private[models] class LinkersTable(tag: Tag) extends Table[Linker](tag, "Linkers") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def macAddress = column[Option[String]]("macAddress")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, macAddress, createdAt, updatedAt, deletedAt) <> (Linker.tupled, Linker.unapply)

  }

  private[models] class CustomerDevicesTable(tag: Tag) extends Table[CustomerDevice](tag, "CustomerDevices") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerId = column[Option[Long]]("customerId")
    def uuid = column[Option[String]]("uuid")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerId, uuid, createdAt, updatedAt, deletedAt) <> (CustomerDevice.tupled, CustomerDevice.unapply)

  }

  private[models] class PurchasesTable(tag: Tag) extends Table[Purchase](tag, "Purchases") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def linkerId = column[Option[Long]]("linkerId")
    def customerId = column[Option[Long]]("customerId")
    def price = column[Option[String]]("price")
    def warrantyDate = column[Option[Timestamp]]("warrantyDate")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, linkerId, customerId, price, warrantyDate, createdAt, updatedAt, deletedAt) <> (Purchase.tupled, Purchase.unapply)

  }

  private[models] class PurchaseOwnersTable(tag: Tag) extends Table[PurchaseOwner](tag, "PurchaseOwners") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def purchaseId = column[Option[Long]]("purchaseId")
    def customerId = column[Option[Long]]("customerId")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, purchaseId, customerId, createdAt, updatedAt, deletedAt) <> (PurchaseOwner.tupled, PurchaseOwner.unapply)

  }
}

