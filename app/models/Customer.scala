package models

import java.sql.{Date, Time, Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneOffset}
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

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
                     createdAt: Timestamp,
                     updatedAt: Timestamp,
                     deletedAt: Timestamp )

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

    def createdAt = column[Timestamp]("createdAt")

    def updatedAt = column[Timestamp]("updatedAt")

    def deletedAt = column[Timestamp]("deletedAt")

    def * = (id, name, authNumber, postNo, addr1, addr2, createdAt, updatedAt, deletedAt) <> (Customer.tupled. Customer.unapply)
  }

  def find(id: Long) =
    db.run(Customers.filter(_.id === id).result.headOption)

}

