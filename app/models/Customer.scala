package models

import java.time.LocalDateTime

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.JdbcProfile



/**
  * Created by mijeongpark on 2017. 7. 5..
  */

case class Customer(
                     id: Long,
                     name: String,
                     authNumber: String,
                     postNo: String,
                     addr1: String,
                     addr2: String,
                     createdAt: LocalDateTime = LocalDateTime.now(),
                     updatedAt: LocalDateTime = LocalDateTime.now(),
                     deletedAt: LocalDateTime )

class CustomerRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val Customers = TableQuery[CustomersTable]

  private[models] class CustomersTable(tag: Tag) extends Table[Customer](tag, "CUSTOMER") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def authNumber = column[String]("authNumber")

    def postNo = column[String]("postNo")

    def addr1 = column[String]("addr1")

    def addr2 = column[String]("addr2")

    def createdAt = column[LocalDateTime]("createdAt")

    def updatedAt = column[LocalDateTime]("updatedAt")

    def deletedAt = column[LocalDateTime]("deletedAt")

    def * = (id, name, authNumber, postNo, addr1, addr2, createdAt, updatedAt, deletedAt) <> (Customer.tupled. Customer.unapply)
  }
}

